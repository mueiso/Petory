package com.study.petory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PetoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetoryApplication.class, args);
	}

}
