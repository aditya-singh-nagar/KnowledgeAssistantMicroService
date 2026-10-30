package com.example.MicroserviceProjectTwo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroserviceProjectTwoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroserviceProjectTwoApplication.class, args);
	}

}
