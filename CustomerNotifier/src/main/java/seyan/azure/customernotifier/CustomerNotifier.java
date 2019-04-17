package seyan.azure.customernotifier;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.google.gson.Gson;
import com.microsoft.azure.servicebus.ExceptionPhase;
import com.microsoft.azure.servicebus.IMessage;
import com.microsoft.azure.servicebus.IMessageHandler;
import com.microsoft.azure.servicebus.MessageHandlerOptions;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import static java.nio.charset.StandardCharsets.*;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerNotifier {

    static final Gson GSON = new Gson();

    private static final Logger LOG = LoggerFactory.getLogger(CustomerNotifier.class);

    @Value("${HOSTNAME:NOTKNOWN}")
    private String hostname;

    @Value("${SB_QUEUE_CONNECTIONSTRING}")
    private String sb_connectionstring;

    @Value("${QUEUE_NAME}")
    private String queue_name;

    private QueueClient qclient = null;

    @PostConstruct
    public void init() throws Exception {
        qclient = new QueueClient(new ConnectionStringBuilder(sb_connectionstring, queue_name), ReceiveMode.PEEKLOCK);
        registerMessageHandler();
        LOG.info("Reday to receive the result of customer's request and send notification from "+ queue_name);
    }
	private void registerMessageHandler() throws Exception {

        qclient.registerMessageHandler(new IMessageHandler()
        {
            @Override
            public CompletableFuture<Void> onMessageAsync(IMessage message) 
            {
                if(message.getLabel() != null && message.getContentType() != null)
                {
                    LOG.info("Received a message with lable and contentType");
                    byte[] body = message.getBody();

                    String rawString = new String(body, UTF_8);

                    LOG.info("Simulating the sending the following message: ");
                    LOG.info(rawString);

                    try {
                        CompletableFuture<String> result = getSomeWorkOverNetwork(rawString);
                        result.thenRunAsync(
                            () ->{
                                LOG.info("Response Completed"); 
                                qclient.completeAsync(message.getLockToken());                            
                            }
                        );

                    }
                    catch(Exception e) {
                        LOG.info(e.getMessage());
                        qclient.abandonAsync(message.getLockToken());  
                    }
                } else {
                    LOG.info("Unrecogniziable message received. Ignore processing");
                    qclient.abandonAsync(message.getLockToken());  
                }

                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void notifyException(Throwable throwable, ExceptionPhase exceptionPhase) {
                LOG.info(exceptionPhase + "-" + throwable.getMessage());
            }
        },
        new MessageHandlerOptions(1, false, Duration.ofMinutes(5))
        
        );

    } 
    
    @Async
    public CompletableFuture<String> getSomeWorkOverNetwork(String rawString) {
        return CompletableFuture.supplyAsync(
            () ->
                {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "Completed";                   
                }
        );
    }
}