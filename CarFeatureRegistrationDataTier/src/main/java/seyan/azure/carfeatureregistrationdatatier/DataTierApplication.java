package seyan.azure.carfeatureregistrationdatatier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = { "seyan.azure.carfeatureregistrationdatatier.model" }) 
@EnableJpaRepositories(basePackages = { "seyan.azure.carfeatureregistrationdatatier.jpa" })
public class DataTierApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataTierApplication.class, args);
	}

}
