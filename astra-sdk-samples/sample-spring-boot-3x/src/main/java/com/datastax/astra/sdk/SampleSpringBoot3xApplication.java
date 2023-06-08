package com.datastax.astra.sdk;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.cassandra.CassandraDataAutoConfiguration;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@SpringBootApplication
public class SampleSpringBoot3xApplication {

	public static void main(String[] args) {
		SpringApplication.run(SampleSpringBoot3xApplication.class, args);
	}

	@Autowired
	private AstraClient astraClient;

	/**
	 * Index operation.
	 *
	 * @return index.html
	 */
	@GetMapping("/")
	public String hello(HttpServletResponse response) throws IOException {
		return astraClient.apiDevops().getOrganization().getId();
	}


}
