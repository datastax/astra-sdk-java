package com.datastax.tutorial.sdkquickstartspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.cassandra.CassandraAutoConfiguration;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication(exclude = { CassandraDataAutoConfiguration.class, CassandraAutoConfiguration.class })
public class SdkQuickstartSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdkQuickstartSpringApplication.class, args);
	}
	
	@GetMapping("/")
	public String hello() { return "OK"; }

}
