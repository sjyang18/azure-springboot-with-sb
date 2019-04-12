package seyan.azure.sbtopicpublisher;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import static java.nio.charset.StandardCharsets.*;

import java.sql.Timestamp;
import java.time.Duration;

import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import com.google.gson.Gson;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import seyan.azure.sbtopicpublisher.model.CarRegistrationRequest;
import seyan.azure.sbtopicpublisher.model.FeatureChangeRequest;

@RestController
public class SBTopicPubController {
    static final Gson GSON = new Gson();

    private static final Logger LOG = Logger.getLogger(SBTopicPubController.class);

    @Value("${HOSTNAME:NOTKNOWN}")
    private String hostname;

    @Value("${SB_CONNECTIONSTRING}")
    private String sb_connectionstring;

    @Value("${TOPIC_NAME}")
    private String topic_name;

    @Value("${SB_MESSAGE_TIME_TO_LIVE_IN_MIN}")
    private int time_to_live_in_min;

    // private TopicClient carRegistrationClient;

    @PostConstruct
    public void init() {
        LOG.info("Ready to send topic message to " + topic_name);
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> map = new HashMap<>();
        map.put("status", "alive");
        map.put("hostname", hostname);
        return map;
    }

    @PostMapping("/register")
    public CarRegistrationRequest register(@RequestBody CarRegistrationRequest request)
            throws Exception, ServiceBusException {
        // Map<String, Object> response = new HashMap<>();

        LOG.info(GSON.toJson(request, CarRegistrationRequest.class));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Message message = new Message(GSON.toJson(request, CarRegistrationRequest.class).getBytes(UTF_8));
        message.setContentType("application/json");
        message.setLabel("car-registration");
        message.setMessageId("car-registration/" + request.getVinNum() + "/" + timestamp);
        message.setTimeToLive(Duration.ofMinutes(time_to_live_in_min));
        System.out.printf("\nMessage sending: Id = %s", message.getMessageId());
        TopicClient carRegistrationClient = new TopicClient(
                new ConnectionStringBuilder(sb_connectionstring, topic_name));
        carRegistrationClient.sendAsync(message).thenRunAsync(() -> {
            LOG.info("\n\tMessage acknowledged: Id = " + message.getMessageId());
            carRegistrationClient.closeAsync();
        });

        return request;
    }

    @PostMapping("/changefeature")
    public FeatureChangeRequest register(@RequestBody FeatureChangeRequest request)
        throws Exception, ServiceBusException {
        //Map<String, Object> response = new HashMap<>();

        LOG.info(GSON.toJson(request, FeatureChangeRequest.class));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        Message message = new Message(GSON.toJson(request, FeatureChangeRequest.class).getBytes(UTF_8));
        message.setContentType("application/json");
        message.setLabel("changefeature");
        message.setMessageId("changefeature/"+request.getVinNum()+"/"+timestamp);
        message.setTimeToLive(Duration.ofMinutes(time_to_live_in_min));
        System.out.printf("\nMessage sending: Id = %s", message.getMessageId());
        TopicClient carRegistrationClient = new TopicClient(new ConnectionStringBuilder(sb_connectionstring, topic_name));
        carRegistrationClient.sendAsync(message).thenRunAsync(() ->
        {
            LOG.info("\n\tMessage acknowledged: Id = " + message.getMessageId());
            carRegistrationClient.closeAsync();
        });
        return request;
    }


}
