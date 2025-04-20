package com.example.Attendence;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class AttendenceApplication {
	@PostConstruct
	void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Dhaka"));
	}

	public static void main(String[] args) {
		SpringApplication.run(AttendenceApplication.class, args);
	}

}
