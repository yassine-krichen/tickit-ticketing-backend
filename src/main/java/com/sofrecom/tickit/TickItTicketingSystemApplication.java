package com.sofrecom.tickit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TickItTicketingSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(TickItTicketingSystemApplication.class, args);
	}

}
