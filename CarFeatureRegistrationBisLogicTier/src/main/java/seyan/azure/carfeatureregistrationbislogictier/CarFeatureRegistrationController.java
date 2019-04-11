package seyan.azure.carfeatureregistrationbislogictier;

import seyan.azure.carfeatureregistrationbislogictier.datamodel.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/carfeatureregistry")
public class CarFeatureRegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(CarFeatureRegistrationController.class);

    @Value("${datatier_service_endpoint}")
    private String datatier_service_endpoint;

    @PostMapping("/vins")
    public CarRegistration registervin(@Valid @RequestBody CarRegistration request) throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = datatier_service_endpoint + "/carregistrations";
        LOG.info("Posting a request to " + baseUrl);

        URI uri = new URI(baseUrl);
        ResponseEntity<CarRegistration> result = restTemplate.postForEntity(uri, request, CarRegistration.class);
        return result.getBody();
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
        LOG.info("Putting a request to " + baseUrl);

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

    @PutMapping("/vins/{vinNum}/features")
    public List<FeatureActivation> updatefeatures(@PathVariable String vinNum,
            @Valid @RequestBody FeatureUpdateRequest request) throws Exception {
        // depending on optype (either unit, bulk), call addfeature or
        // bulkupdatefeatures
        if (request.getUpdateType() == FeatureUpdateRequest.UpdateType.append) { // append
            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = datatier_service_endpoint + "/carregistrations/" + vinNum + "/features";
            URI uri = new URI(baseUrl);
            for (FeatureActivation feature : request.getFeatureSet()) {
                LOG.info("Sending a post request (i.e. append new feature to existing featureset ) to " + baseUrl);
                restTemplate.postForEntity(uri, feature, null);
            }
        } else { // overwrite
            overwriteFeatureSet(vinNum, request.getFeatureSet());
        }
        return getfeatures(vinNum);

    }

    @DeleteMapping("/vins/{vinNum}/features")
    public void removefeatures(@PathVariable String vinNum, @Valid @RequestBody FeatureDeleteRequest request)
            throws Exception {
        
        LOG.info("Delete invoked");

        if (request.getDeleteMode() == FeatureDeleteRequest.DeleteMode.selective) {
            // selective delete
            // delete one by one
            // DELETE "/carregistrations/{vinNum}/features"

            RestTemplate restTemplate = new RestTemplate();
            final String baseUrl = datatier_service_endpoint + "/carregistrations/" + vinNum + "/features?featureName={featureName}";

            for (FeatureActivation fa : request.getFeaturesToDelete()) {
                Map<String,Object> uriVariables = new HashMap<>();
                uriVariables.put("featureName", fa.getFeature_name());
                LOG.info("Inovking DELETE to " + baseUrl);
                LOG.info("before invoking restTemplate.delete");
                restTemplate.delete(baseUrl, uriVariables);
                LOG.info("after invoking restTemplate.delete");
            }
        }
        else {
            // delete all features
            List<FeatureActivation> emptyFeature = new ArrayList<FeatureActivation>();
            overwriteFeatureSet(vinNum, emptyFeature);
        }

    }

    private void sendFeatureActivationCode() {
        
    }
    
    private void sendRegisterVinNumResponse() {

    }

    private void sendUpdateFeatureResponse() {

    }



    

}