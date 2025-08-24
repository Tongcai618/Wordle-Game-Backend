package com.example.springboot_wordle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class SpringbootWordleApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWordleApplication.class, args);
	}

}
