package com.example.backend_monitor;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRabbit
public class BackendMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendMonitorApplication.class, args);
	}

}
