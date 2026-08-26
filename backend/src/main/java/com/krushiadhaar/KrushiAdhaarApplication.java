package com.krushiadhaar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.scheduling.annotation.EnableAsync
public class KrushiAdhaarApplication {

	public static void main(String[] args) {
		SpringApplication.run(KrushiAdhaarApplication.class, args);
	}

}
