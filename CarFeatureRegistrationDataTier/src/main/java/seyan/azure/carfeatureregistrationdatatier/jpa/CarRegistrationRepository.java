package seyan.azure.carfeatureregistrationdatatier.jpa;

import seyan.azure.carfeatureregistrationdatatier.model.*;


import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.rest.core.annotation.RepositoryRestResource;

//@RepositoryRestResource(collectionResourceRel = "carregistration", path = "carregistration")
public interface CarRegistrationRepository extends PagingAndSortingRepository<CarRegistration, Long> {
    CarRegistration findByVinNum(@Param("vinNum") String vinNum);

}