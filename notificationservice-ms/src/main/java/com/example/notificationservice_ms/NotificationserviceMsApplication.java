package com.example.notificationservice_ms;

import com.example.notificationservice_ms.config.AppJwtProperties;
import com.example.notificationservice_ms.config.AppSecurityProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AppJwtProperties.class, AppSecurityProperties.class})
public class NotificationserviceMsApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationserviceMsApplication.class, args);
	}

}
