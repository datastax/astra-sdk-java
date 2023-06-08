package com.datastax.astra.sdk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SampleSpringBoot3xApplicationTests {

	@Autowired
	private AstraClient astraClient;

	@Test
	void contextLoads() {
		System.out.println(astraClient.apiDevops().getOrganization().getId());
	}

}
