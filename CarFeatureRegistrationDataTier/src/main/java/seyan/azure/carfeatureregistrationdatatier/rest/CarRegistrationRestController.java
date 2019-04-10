package seyan.azure.carfeatureregistrationdatatier.rest;

import seyan.azure.carfeatureregistrationdatatier.jpa.*;
import seyan.azure.carfeatureregistrationdatatier.model.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CarRegistrationRestController {

    @Autowired
    private CarRegistrationRepository carRegistrationRepository;

    @GetMapping("/carregistrations")
    public Page<CarRegistration> getAllCarRegistrations(Pageable pageable) {
        return carRegistrationRepository.findAll(pageable);
    }

    @PostMapping("/carregistrations")
    public CarRegistration createCarRegistration(@Valid @RequestBody CarRegistration carRegistration) {
        return carRegistrationRepository.save(carRegistration);
    }

    /*
    @PutMapping("/carregistrations/{vinNum}")
    public CarRegistration updateCarReistration(@PathVariable String vinNum,
            @RequestBody CarRegistration carRegistrationRequest) {
        // find the data from database
        CarRegistration existingData = carRegistrationRepository.findByVinNum(vinNum);
        Set<String> rSet = new HashSet<String>();
        for (FeatureActivationRepository feature : carRegistrationRequest.getFeatureSet())
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
        return carRegistrationRepository.save(existingData);

    }*/


}