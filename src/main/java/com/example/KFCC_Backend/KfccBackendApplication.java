package com.example.KFCC_Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class KfccBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(KfccBackendApplication.class, args);
	}

}
