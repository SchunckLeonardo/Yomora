package com.yomora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class YomoraApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(YomoraApiApplication.class, args);
	}

}
