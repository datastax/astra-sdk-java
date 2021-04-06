package com.dstx.astra.sdk;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;

import io.stargate.sdk.rest.ApiRestClient;
import io.stargate.sdk.rest.DataCenter;

public class ApiRestWithAstraTest {
    
    private static final String ANSI_RESET           = "\u001B[0m";
    private static final String ANSI_GREEN           = "\u001B[32m";
    private static final String ANSI_YELLOW          = "\u001B[33m";
    
    // Update those values to ease unit tests wuhen debug
    private static String cliendID     = "ZykcSMWLUDktHMZsYCKWtNQa";
    private static String clientSecret = "uk4ZjvBMyZA075P1jQozwLtNwtb5mELx.bi5n1PDhxZ+U2p+SJAQkg,G9Nf6d_C0cGjt-lub9a2an.agFWnliRc70Kk,+H8wZc_FT4f3K.cUHQH30A1ZMJagC7sunzd.";
    private static String appToken     = "AstraCS:ZykcSMWLUDktHMZsYCKWtNQa:7f95412e3c5014d952febbf5bd4223c74afa95f1f9b205327804c8cac1597e2b";
    private static String dbId         = "9ea35b33-aa48-49fb-88a5-20525aad07fd";
    private static String cloudRegion  = "eu-central-1";
    
    private static final String WORKING_KEYSPACE   = "astra_sdk_keyspacec_test";
    private static final String TABLE_PERSON       = "person";
    
    public static AstraClient client;
    public static ApiRestClient clientApiRest;

    @BeforeAll
    public static void initClient() {
        // =====================================================
        // LOAD PARAMETER VALUES FROM COMMANDS (IF AVAILABLE)
        // =====================================================
        if (null != System.getenv(AstraClient.ASTRA_DB_REGION) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_REGION))) {
            cloudRegion = System.getenv(AstraClient.ASTRA_DB_REGION);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_REGION) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_REGION))) {
            cloudRegion = System.getProperty(AstraClient.ASTRA_DB_REGION);
        } 
        if (null != System.getenv(AstraClient.ASTRA_DB_ID) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_ID))) {
            dbId = System.getenv(AstraClient.ASTRA_DB_ID);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_ID) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_ID))) {
            dbId = System.getProperty(AstraClient.ASTRA_DB_ID);
        }
        if (null != System.getenv(AstraClient.ASTRA_DB_CLIENT_ID) && 
           !"".equals(System.getenv(AstraClient.ASTRA_DB_CLIENT_ID))) {
          cliendID = System.getenv(AstraClient.ASTRA_DB_CLIENT_ID);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_CLIENT_ID) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_CLIENT_ID))) {
          cliendID = System.getProperty(AstraClient.ASTRA_DB_CLIENT_ID);
        }
        if (null != System.getenv(AstraClient.ASTRA_DB_CLIENT_SECRET) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_CLIENT_SECRET))) {
          clientSecret = System.getenv(AstraClient.ASTRA_DB_CLIENT_SECRET);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_CLIENT_SECRET) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_CLIENT_SECRET))) {
          clientSecret = System.getProperty(AstraClient.ASTRA_DB_CLIENT_SECRET);
        }
        if (null != System.getenv(AstraClient.ASTRA_DB_APPLICATION_TOKEN) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_APPLICATION_TOKEN))) {
            appToken = System.getenv(AstraClient.ASTRA_DB_APPLICATION_TOKEN);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_APPLICATION_TOKEN) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_APPLICATION_TOKEN))) {
            appToken = System.getProperty(AstraClient.ASTRA_DB_APPLICATION_TOKEN);
        }
        // =====================================================
        
        
        client = AstraClient.builder()
                .databaseId(dbId)
                .cloudProviderRegion(cloudRegion)
                .clientId(cliendID)
                .clientSecret(clientSecret)
                .appToken(appToken).build();
        clientApiRest = client.apiRest();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Connection with Rest API");
    }
    
    @Test
    public void testSuite() throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n[POST] Create keyspace if needed" + ANSI_RESET);
        should_create_keyspace();
        working_keyspace_should_exist();
    }
    
    public void should_create_keyspace() 
    throws InterruptedException {
        if (!clientApiRest.keyspace(WORKING_KEYSPACE).exist()) {
            clientApiRest.keyspace(WORKING_KEYSPACE)
                         .create(new DataCenter(cloudRegion, 3));
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
            int wait = 0;
            while (wait++ < 5 && !clientApiRest.keyspace(WORKING_KEYSPACE).exist()) {
                Thread.sleep(2000);
                 System.out.print("+ ");
            }
        }
        Assertions.assertTrue(clientApiRest.keyspace(WORKING_KEYSPACE).exist());
    }
    
  
    public void working_keyspace_should_exist() {
        Assertions.assertTrue(clientApiRest
                .keyspaceNames()
                .collect(Collectors.toSet())
                .contains(WORKING_KEYSPACE));
    }
    
    @Test
    public void should_list_tables() 
    throws InterruptedException {
        clientApiRest.keyspace("bootiful")
                    .tableNames()
                    .forEach(System.out::println);
    }

}
