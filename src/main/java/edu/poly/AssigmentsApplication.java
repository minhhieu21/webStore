package edu.poly;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import edu.poly.config.StorageProperties;
import edu.poly.service.StorageService;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class AssigmentsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssigmentsApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return(args->{
			storageService.init();
		});
	}

}
