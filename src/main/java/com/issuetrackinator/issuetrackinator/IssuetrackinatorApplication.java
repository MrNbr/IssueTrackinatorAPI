package com.issuetrackinator.issuetrackinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
@SpringBootApplication
public class IssuetrackinatorApplication {

	public static void main(final String[] args) {
		SpringApplication.run(IssuetrackinatorApplication.class, args);
	}

}
