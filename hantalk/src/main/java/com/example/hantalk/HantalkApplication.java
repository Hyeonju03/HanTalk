package com.example.hantalk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HantalkApplication {

	public static void main(String[] args) {
		SpringApplication.run(HantalkApplication.class, args);
		System.out.println("실행 성공");
	}

}
