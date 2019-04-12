package seyan.azure.sbtopicsubscriber;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import static java.nio.charset.StandardCharsets.*;

import java.net.URI;

import com.google.gson.Gson;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import seyan.azure.sbtopicsubscriber.model.CarRegistrationRequest;
import seyan.azure.sbtopicsubscriber.model.FeatureChangeRequest;

@RestController
public class SBTopicSubController {
    static final Gson GSON = new Gson();

    private static final Logger LOG = LoggerFactory.getLogger(SBTopicSubController.class);

    private static final Object Void = null;

    @Value("${HOSTNAME:NOTKNOWN}")
    private String hostname;

    @Value("${SB_CONNECTIONSTRING}")
    private String sb_connectionstring;

    @Value("${TOPIC_NAME}")
    private String topic_name;

    @Value("${SUBSCRIPTION_NAME}")
    private String subscription_name1;

    @Value("${LOGIC_SERVICE_ENDPOINT}")
    private String logic_service_endpoint;

    private SubscriptionClient sbclient1 = null;

    @GetMapping("/health")
    public Map<String, Object> health() {
        LOG.info("Invoking /health endpoint");
        Map<String, Object> map = new HashMap<>();
        map.put("status", "alive");
        map.put("hostname", hostname);
        return map;
    }

    @PostConstruct
    public void init() throws Exception {
        this.sbclient1 = new SubscriptionClient(
                new ConnectionStringBuilder(sb_connectionstring, topic_name + "/subscriptions/" + subscription_name1),
                ReceiveMode.PEEKLOCK);
        registerMessageHandler();

        LOG.info("Ready to receive topic message from " + topic_name);
    }

    @Async
    private CompletableFuture<String> registervin(CarRegistrationRequest request) throws Exception {
        final String baseUrl = logic_service_endpoint + "/carfeatureregistry/vins";
        RestTemplate restTemplate = new RestTemplate();
        LOG.info("Posting a request to " + baseUrl);
        ResponseEntity<String> result = restTemplate.postForEntity(new URI(baseUrl), request, String.class);
        return CompletableFuture.completedFuture(result.getBody());
    }

    @Async
    private CompletableFuture<String> changeFeatures(FeatureChangeRequest request) throws Exception {
        final String baseUrl = logic_service_endpoint + String.format("/carfeatureregistry/vins/%s/features", request.getVinNum());
        RestTemplate restTemplate = new RestTemplate();
        LOG.info("Sending a post request to " + baseUrl);
        ResponseEntity<String> result = restTemplate.postForEntity(new URI(baseUrl), request, String.class);
        return CompletableFuture.completedFuture(result.getBody());
    }

    private void registerMessageHandler() throws Exception {

        // register the RegisterMessageHandler callback
        IMessageHandler messageHandler = new IMessageHandler() {
            // callback invoked when the message handler loop has obtained a message
            public CompletableFuture<Void> onMessageAsync(IMessage message) {

                LOG.info("onMessageAsync invoked");
                // receives message is passed to callback
                if (message.getLabel() != null && message.getContentType() != null
                        && message.getContentType().contentEquals("application/json")) {
                    if (message.getLabel().contentEquals("car-registration")) {
                        byte[] body = message.getBody();
                        CarRegistrationRequest request = GSON.fromJson(new String(body, UTF_8),
                                CarRegistrationRequest.class);
                        LOG.info("CarRegistrationRequest recevied : " + request);
                        LOG.info(" VIN_NUM: " + request.getVinNum());
                        try {
                            CompletableFuture<String> result = registervin(request);

                            result.thenRunAsync(() -> {
                                LOG.info("CarRegistrationRequest processed");
                                try {
                                    LOG.info(String.format("%s", result.get()));
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }

                            });
                        }
                        catch(Exception e) {
                            LOG.info("Failed to foward CarRegistrationRequest to logic layer");
                            LOG.info(e.getMessage());
                            LOG.info(e.getStackTrace().toString());
                            return sbclient1.abandonAsync(message.getLockToken());
                        }
                        
                        
                    }
                    else if (message.getLabel().contentEquals("changefeature")) {
                        byte[] body = message.getBody();
                        FeatureChangeRequest request = GSON.fromJson(new String(body, UTF_8), FeatureChangeRequest.class);
                        LOG.info("FeatureChangeRequest recevied : " + request);
                        LOG.info(" VIN_NUM: " + request.getVinNum());
                        LOG.info(" body : " + new String(body, UTF_8));
                        try {
                            CompletableFuture<String> result = changeFeatures(request);

                            result.thenRunAsync(() -> {
                                LOG.info("FeatureChangeRequest processed");
                                try {
                                    LOG.info(String.format("%s", result.get()));
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }

                            });
                        }
                        catch(Exception e) {
                            LOG.info("Failed to foward changefeature to logic layer");
                            LOG.info(e.getMessage());
                            LOG.info(e.getStackTrace().toString());
                            return sbclient1.abandonAsync(message.getLockToken());
                        } 
                    }
                    

                } else{
                    LOG.info("message.getLabel() " + message.getLabel());
                    LOG.info("message.getContentType() " +message.getContentType());
                }

                return sbclient1.completeAsync(message.getLockToken());
            }
            
            public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                System.out.printf(exceptionPhase + "-" + throwable.getMessage());
            }
        };


        sbclient1.registerMessageHandler(
            messageHandler,
            new MessageHandlerOptions(1, false, Duration.ofMinutes(1))
        );

    }


    

}