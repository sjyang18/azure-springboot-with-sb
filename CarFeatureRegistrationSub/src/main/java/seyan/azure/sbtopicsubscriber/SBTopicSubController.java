package seyan.azure.sbtopicsubscriber;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.annotation.PostConstruct;
import static java.nio.charset.StandardCharsets.*;


import com.google.gson.Gson;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.SubscriptionClient;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import seyan.azure.sbtopicsubscriber.model.CarRegistrationRequest;

@RestController
public class SBTopicSubController {
    static final Gson GSON = new Gson();

    private static final Logger LOG = LoggerFactory.getLogger(SBTopicSubController.class);

    @Value("${HOSTNAME:NOTKNOWN}")
    private String hostname;

    @Value("${SB_CONNECTIONSTRING}")
    private String sb_connectionstring;

    @Value("${TOPIC_NAME}")
    private String topic_name;
    
    @Value("${SUBSCRIPTION_NAME}")
    private String subscription_name1;

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
            new ConnectionStringBuilder(sb_connectionstring, topic_name+"/subscriptions/"+ subscription_name1),
            ReceiveMode.PEEKLOCK
            );
        registerMessageHandler();

        LOG.info("Ready to receive topic message from " + topic_name);
    }

    private void registerMessageHandler() throws Exception
    {

        // register the RegisterMessageHandler callback
    	IMessageHandler messageHandler = new IMessageHandler() {
            // callback invoked when the message handler loop has obtained a message
            public CompletableFuture<Void> onMessageAsync(IMessage message) {

                LOG.info("onMessageAsync invoked");
                // receives message is passed to callback
                if(message.getLabel() != null
                 && message.getLabel().contentEquals("car-registration")
                 && message.getContentType() !=null 
                 && message.getContentType().contentEquals("application/json")
                ) {
                    byte[] body = message.getBody();
                    CarRegistrationRequest request = GSON.fromJson(new String(body, UTF_8), CarRegistrationRequest.class);
                    LOG.info("CarRegistrationRequest recevied : " + request);
                    LOG.info(" VIN_NUM: " + request.getVinNum());
                    LOG.info(" Features: "); 
                    request.getFeatures().forEach(LOG::info);

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