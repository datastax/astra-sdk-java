package com.dstx.astra.sdk;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.stargate.sdk.rest.ApiRestClient;
import io.stargate.sdk.rest.DataCenter;

@TestMethodOrder(OrderAnnotation.class)
public class T05_RestApi_IntegrationTest extends AbstractAstraIntegrationTest {
  
    private static final String WORKING_KEYSPACE   = "astra_sdk_keyspacec_test";
    
    private static ApiRestClient clientApiRest;
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[T05_RestApi_IntegrationTest]" + ANSI_RESET);
        clientApiRest = client.apiRest();
    }
   
    @Test
    @Order(1)
    public void should_create_keyspace() 
    throws InterruptedException {
        if (!clientApiRest.keyspace(WORKING_KEYSPACE).exist()) {
            clientApiRest.keyspace(WORKING_KEYSPACE)
                         .create(new DataCenter(cloudRegion.get(), 3));
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
            int wait = 0;
            while (wait++ < 5 && !clientApiRest.keyspace(WORKING_KEYSPACE).exist()) {
                Thread.sleep(2000);
                 System.out.print("+ ");
            }
        }
        Assertions.assertTrue(clientApiRest.keyspace(WORKING_KEYSPACE).exist());
    }
    
    @Test
    @Order(2)
    public void working_keyspace_should_exist() {
        Assertions.assertTrue(clientApiRest
                .keyspaceNames()
                .collect(Collectors.toSet())
                .contains(WORKING_KEYSPACE));
    }
    
    @Test
    @Order(3)
    public void should_list_tables() 
    throws InterruptedException {
        clientApiRest.keyspace("bootiful")
                    .tableNames()
                    .forEach(System.out::println);
    }

}
