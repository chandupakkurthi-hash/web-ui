package com.no_broker_application.Web_UI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WebUiApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebUiApplication.class, args);
	}
}
