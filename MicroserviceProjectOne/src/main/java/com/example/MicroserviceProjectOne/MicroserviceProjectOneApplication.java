package com.example.MicroserviceProjectOne;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroserviceProjectOneApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceProjectOneApplication.class, args);
	}

}
