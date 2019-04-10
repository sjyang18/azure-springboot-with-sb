package seyan.azure.carfeatureregistrationdatatier.jpa;

import seyan.azure.carfeatureregistrationdatatier.model.*;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface FeatureActivationRepository extends PagingAndSortingRepository<FeatureActivation, Long> {

}