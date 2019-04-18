package seyan.azure.sbtopicsubscriber;

import java.net.URI;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import seyan.azure.sbtopicsubscriber.model.CarRegistrationRequest;
import seyan.azure.sbtopicsubscriber.model.FeatureChangeRequest;

@Service
public class LogicTierInvokeService {

    
    
    @Value("${LOGIC_SERVICE_ENDPOINT}")
    private String logic_service_endpoint;

    private static final Logger LOG = LoggerFactory.getLogger(LogicTierInvokeService.class);
    private final RestTemplate restTemplate;

    public LogicTierInvokeService(RestTemplateBuilder restTemplateBuilder){
        restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<String> registervin(CarRegistrationRequest request) throws Exception {
        final String baseUrl = logic_service_endpoint + "/carfeatureregistry/vins";
        LOG.info("Posting a request to " + baseUrl);
        ResponseEntity<String> result = restTemplate.postForEntity(new URI(baseUrl), request, String.class);
        return CompletableFuture.completedFuture(result.getBody());
    }

    @Async
    public CompletableFuture<String> changeFeatures(FeatureChangeRequest request) throws Exception {
        final String baseUrl = logic_service_endpoint + String.format("/carfeatureregistry/vins/%s/features", request.getVinNum());
        LOG.info("Sending a post request to " + baseUrl);
        ResponseEntity<String> result = restTemplate.postForEntity(new URI(baseUrl), request, String.class);
        return CompletableFuture.completedFuture(result.getBody());
    }


    
    


}