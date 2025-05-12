package com.sofrecom.tickit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.sofrecom.tickit.config.AutomationProperties;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({AutomationProperties.class})
public class TickItTicketingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickItTicketingSystemApplication.class, args);
	}

}
