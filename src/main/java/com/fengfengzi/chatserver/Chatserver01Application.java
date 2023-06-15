package com.fengfengzi.chatserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Chatserver01Application {

	public static void main(String[] args) {

		SpringApplication.run(Chatserver01Application.class, args);
		System.out.println("sha bi");
	}

}
