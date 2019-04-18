package seyan.azure.carfeatureregistrationdatatier.rest;

import seyan.azure.carfeatureregistrationdatatier.jpa.*;
import seyan.azure.carfeatureregistrationdatatier.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.core.joran.conditional.ElseAction;

@RestController
public class CarRegistrationRestController {

    private static final Logger LOG = LoggerFactory.getLogger(CarRegistrationRestController.class);

    @Autowired
    private CarRegistrationRepository carRegistrationRepository;

    @GetMapping("/carregistrations")
    public Page<CarRegistration> getAllCarRegistrations(Pageable pageable) {
        return carRegistrationRepository.findAll(pageable);
    }

    @PostMapping("/carregistrations")
    public CarRegistration createCarRegistration(@Valid @RequestBody CarRegistration carRegistration) 
        throws Exception {
        // check if this is the existing car with the vin
        CarRegistration dataWithId = carRegistrationRepository.findByVinNum(carRegistration.getVinNum());
        if (dataWithId == null) {
            LOG.info("new registration for a car with vin :" + carRegistration.getVinNum());
            return carRegistrationRepository.save(carRegistration);
        } else {
            LOG.info("updating car registration with vin " + carRegistration.getVinNum());
            return updateCarReistration(carRegistration.getVinNum(), carRegistration);
        }
    }

    @GetMapping("/carregistrations/{vinNum}")
    public CarRegistration getCarRegistrationInfo(@PathVariable String vinNum) {
        CarRegistration existingData = carRegistrationRepository.findByVinNum(vinNum);
        return existingData;
    }

    @PutMapping("/carregistrations/{vinNum}")
    public CarRegistration updateCarReistration(@PathVariable String vinNum,
        @Valid @RequestBody CarRegistration carRegistrationRequest) throws Exception {
        // find the data from database
        CarRegistration existingData = carRegistrationRepository.findByVinNum(vinNum);
        if(existingData != null) {
            Set<String> rSet = new HashSet<String>();
            for (FeatureActivation feature : carRegistrationRequest.getFeatureSet())
            {
                rSet.add(feature.getFeature_name());
            }

            Set<FeatureActivation> eSetToRemove = new HashSet<FeatureActivation>();
            for(FeatureActivation feature : existingData.getFeatureSet())
            {
                if(rSet.contains(feature.getFeature_name())) {
                    rSet.remove(feature.getFeature_name());
                } else {
                    eSetToRemove.add(feature);
                }
            }
    
            for(FeatureActivation feature2remove : eSetToRemove)
            {
                existingData.removeFeature(feature2remove);
            }

            for(String feature2add : rSet)
            {
                existingData.addFeature(feature2add);
            }

            if(eSetToRemove.size() > 0 || rSet.size() > 0) {
                // only when you need update, save.
                LOG.info("saving changes to database");
                return carRegistrationRepository.save(existingData);
            } else 
            {
                LOG.info("no changes to database in this update call");
                return getFeatureSet(vinNum);
            }
        } else {
            throw new Exception("No car found with vin "+ vinNum);
        }    
    }

    @GetMapping("/carregistrations/{vinNum}/features")
    public CarRegistration getFeatureSet(@PathVariable String vinNum) {
        CarRegistration existingData = carRegistrationRepository.findByVinNum(vinNum);
        return existingData;
    }

    @PostMapping("/carregistrations/{vinNum}/features")
    public CarRegistration addFeature(@PathVariable String vinNum, @Valid @RequestBody FeatureActivation featureRequest)
            throws Exception {
        // find the data from database
        CarRegistration existingData = carRegistrationRepository.findByVinNum(vinNum);
        if(existingData != null){
            // only if this is a new feature request, save.
            if(existingData.hasFeature(featureRequest.getFeature_name())){
                return existingData;
            } else {
                existingData.addFeature(featureRequest);
                LOG.info("saving changes to database in addFeature");
                return carRegistrationRepository.save(existingData);
            }
        }else {
            throw new Exception("No car found with vin "+ vinNum);
        }
    }

    @DeleteMapping("/carregistrations/{vinNum}/features")
    public CarRegistration removeFeature(@PathVariable String vinNum, @RequestParam String featureName)
            throws Exception
    {
        CarRegistration existingData = carRegistrationRepository.findByVinNum(vinNum);
        if(existingData != null){
            // only if this is a new feature request, save.
            if(existingData.hasFeature(featureName)){
                existingData.removeFeature(featureName);
                LOG.info("saving changes to database in removeFeature");
                return carRegistrationRepository.save(existingData);
            } else {
                return existingData;            
            }
        }else {
            throw new Exception("No car found with vin "+ vinNum);
        }

    }



}