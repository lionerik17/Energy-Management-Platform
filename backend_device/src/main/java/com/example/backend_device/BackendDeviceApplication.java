package com.example.backend_device;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class BackendDeviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendDeviceApplication.class, args);
	}

}
