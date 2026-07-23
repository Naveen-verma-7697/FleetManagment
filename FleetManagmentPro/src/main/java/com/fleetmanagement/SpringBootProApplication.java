package com.fleetmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages="com.fleetmanagement.*")
@EntityScan(basePackages="com.fleetmanagement.*")
@EnableJpaRepositories(basePackages="com.fleetmanagement.*")
public class SpringBootProApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootProApplication.class, args);
	}

}
