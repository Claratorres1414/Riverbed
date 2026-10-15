package com.project.riverbed;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringRiverbedApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringRiverbedApplication.class, args);
	}

}
