package com.yomora;

import org.springframework.boot.SpringApplication;

public class TestYomoraApiApplication {

	public static void main(String[] args) {
		SpringApplication.from(YomoraApiApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
