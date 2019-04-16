package seyan.azure.carfeatureregistrationbislogictier;

import seyan.azure.carfeatureregistrationbislogictier.datamodel.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;
import static java.nio.charset.StandardCharsets.*;

import com.google.gson.Gson;
import com.microsoft.azure.servicebus.Message;
import com.microsoft.azure.servicebus.QueueClient;
import com.microsoft.azure.servicebus.ReceiveMode;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/carfeatureregistry")
public class CarFeatureRegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(CarFeatureRegistrationController.class);
    private static final Gson GSON = new Gson();

    @Value("${datatier_service_endpoint}")
    private String datatier_service_endpoint;

    @Value("${SB_QUEUE_CONNECTIONSTRING}")
    private String sb_connectionstring;

    @Value("${QUEUE_NAME}")
    private String queue_name;

    @PostMapping("/vins")
    public CarRegistration registervin(@Valid @RequestBody CarRegistrationPayload request) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = datatier_service_endpoint + "/carregistrations";
        LOG.info("Posting a request to " + baseUrl);

        URI uri = new URI(baseUrl);
        ResponseEntity<CarRegistration> result = restTemplate.postForEntity(uri, request.composeCarRegistrationRecord(), CarRegistration.class);

        // create a response object out of result
        CarRegistration body = result.getBody();  
        this.sendCarRegistrationPayloadToSB(request);

        return body;
    }

    @GetMapping("/vins/{vinNum}")
    public CarRegistration getCarRegistrationInfo(@PathVariable String vinNum) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = datatier_service_endpoint + "/carregistrations/" + vinNum;
        URI uri = new URI(baseUrl);
        LOG.info("Putting a request to " + baseUrl);

        CarRegistration queryResult = restTemplate.getForObject(uri, CarRegistration.class);
        return queryResult;
    }

    @GetMapping("/vins/{vinNum}/features")
    public List<FeatureActivation> getfeatures(@PathVariable String vinNum) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = datatier_service_endpoint + "/carregistrations/" + vinNum;
        URI uri = new URI(baseUrl);
        LOG.info("Getting a request to " + baseUrl);

        CarRegistration queryResult = restTemplate.getForObject(uri, CarRegistration.class);
        return queryResult.getFeatureSet();
    }

    private CarRegistration overwriteFeatureSet(@PathVariable String vinNum, List<FeatureActivation> request)
            throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = datatier_service_endpoint + "/carregistrations/" + vinNum;
        URI uri = new URI(baseUrl);
        LOG.info("Putting a request to " + baseUrl);

        CarRegistration putRequest = new CarRegistration();
        putRequest.setVinNum(vinNum);
        putRequest.setFeatureSet(request);

        restTemplate.put(uri, putRequest);

        return putRequest;
    }

    @PostMapping("/vins/{vinNum}/features")
    public List<FeatureActivation>  changefeatures(
        @PathVariable String vinNum,
        @Valid @RequestBody FeatureChangePayload request
    )  throws Exception {

        LOG.info("changeType: " + request.getChangeType().toString());

        if(
            request.getChangeType() == FeatureChangePayload.ChangeType.extend ||
            request.getChangeType() == FeatureChangePayload.ChangeType.overwrite
        ){
            LOG.info("calling updatefeatures");
            updatefeatures(vinNum, request);

        }else if(
            request.getChangeType() == FeatureChangePayload.ChangeType.delete_all ||
            request.getChangeType() == FeatureChangePayload.ChangeType.delete_selective
        ) {
            LOG.info("calling removefeatures");
            removefeatures(vinNum, request);
        }
        
        return getfeatures(vinNum);
    }
    
    private List<FeatureActivation> updatefeatures(
        String vinNum,
        FeatureChangePayload request) throws Exception {
        // depending on optype (either unit, bulk), call addfeature or
        // bulkupdatefeatures

        if (request.getChangeType() == FeatureChangePayload.ChangeType.extend) { // extend
            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = datatier_service_endpoint + "/carregistrations/" + vinNum + "/features";
            URI uri = new URI(baseUrl);
            for (String fname : request.getFeatureSet()) {
                LOG.info("Sending a post request (i.e. append new feature to existing featureset ) to " + baseUrl);
                FeatureActivation fa = new FeatureActivation();
                fa.setFeature_name(fname);
                restTemplate.postForEntity(uri, fa, null);
            }
            
        } else { // overwrite
            overwriteFeatureSet(vinNum, request.composeInternalFeatureSetFormat());
        }       
        sendFeatureChangePayloadToSB(request);
        return getfeatures(vinNum);

    }

    @DeleteMapping("/vins/{vinNum}/features")
    public void deletefeatures(@PathVariable String vinNum, @Valid @RequestBody FeatureChangePayload request)
            throws Exception
    {
        if(
            request.getChangeType() == FeatureChangePayload.ChangeType.delete_all ||
            request.getChangeType() == FeatureChangePayload.ChangeType.delete_selective
        ) {
            removefeatures(vinNum, request);
        }

    }

    private void removefeatures(String vinNum, FeatureChangePayload request)
            throws Exception {
        
        LOG.debug("Delete invoked");

        if (request.getChangeType() == FeatureChangePayload.ChangeType.delete_selective) {
            // selective delete
            // delete one by one
            // DELETE "/carregistrations/{vinNum}/features"

            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = datatier_service_endpoint + "/carregistrations/" + vinNum + "/features?featureName={featureName}";

            for (String fa : request.getFeatureSet()) {
                Map<String,Object> uriVariables = new HashMap<>();
                uriVariables.put("featureName", fa);
                LOG.debug("Inovking DELETE to " + baseUrl);
                LOG.debug("before invoking restTemplate.delete");
                restTemplate.delete(baseUrl, uriVariables);
                LOG.debug("after invoking restTemplate.delete");
            }
        }
        else {
            // delete all features
            List<FeatureActivation> emptyFeature = new ArrayList<FeatureActivation>();
            overwriteFeatureSet(vinNum, emptyFeature);
        }
        sendFeatureChangePayloadToSB(request);

    }
    
    private void sendCarRegistrationPayloadToSB(CarRegistrationPayload response) throws Exception {
        
        QueueClient sendClient = 
            new QueueClient(new ConnectionStringBuilder(sb_connectionstring, queue_name), ReceiveMode.PEEKLOCK);
        
        Message message = new Message(GSON.toJson(response, CarRegistrationPayload.class).getBytes(UTF_8));
        message.setContentType("application/json");
        message.setLabel(String.format("%s/%s", queue_name,"CarRegistrationPayload"));
        message.setMessageId(response.toString());
        sendClient.sendAsync(message).thenRunAsync(
            ()-> {
                LOG.info(String.format("Sent response message for %s in sendCarRegistrationResponseToSB", response.getVinNum()));
                sendClient.closeAsync();
            }
        );
    }

    private void sendFeatureChangePayloadToSB(FeatureChangePayload response) throws Exception {
        QueueClient sendClient = 
            new QueueClient(new ConnectionStringBuilder(sb_connectionstring, queue_name), ReceiveMode.PEEKLOCK);
        
        Message message = new Message(GSON.toJson(response, FeatureChangePayload.class).getBytes(UTF_8));
        message.setContentType("application/json");
        message.setLabel(String.format("%s/%s", queue_name,"FeatureChangeResponse"));
        message.setMessageId(response.toString());
        sendClient.sendAsync(message).thenRunAsync(
            ()-> {
                LOG.info(String.format("Sent response message for %s in sendCarRegistrationResponseToSB", response.getVinNum()));
                sendClient.closeAsync();
            }
        );

    }



    

}