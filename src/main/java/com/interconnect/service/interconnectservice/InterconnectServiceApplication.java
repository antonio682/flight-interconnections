package com.interconnect.service.interconnectservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class InterconnectServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InterconnectServiceApplication.class, args);
	}
}
