package com.example.ai_service_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableCaching
public class AiServiceMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(AiServiceMsApplication.class, args);
	}

}
