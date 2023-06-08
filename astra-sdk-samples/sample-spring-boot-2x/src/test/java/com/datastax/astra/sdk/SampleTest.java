package com.datastax.astra.sdk;

import com.datastax.astra.sdk.data.FruitRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SampleTest {

    @Autowired
    FruitRepository fruitRepository;

    @Autowired
    AstraClient astraClient;

    @Test
    public void listFruits() {
        System.out.println("Connected to " + astraClient.apiDevops().getOrganization().getName());
        Assertions.assertTrue(fruitRepository.findAll().size() > 0);
    };

}
