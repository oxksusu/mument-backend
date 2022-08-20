package com.example.mumentbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class MumentBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(MumentBackendApplication.class, args);
	}

}
