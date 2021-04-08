package com.dstx.astra.sdk;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Multiple Connectivity mode for eacj parameters.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class T01_ConnectivityIntegrationTest extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[T01_Connectivity]" + ANSI_RESET);
     /*
       
      client = AstraClient.builder()
       .databaseId("58c6335b-766f-49e0-8e12-ed222c943e35")
       .cloudProviderRegion("europe-west1")
       .appToken("AstraCS:MGJEgIcLhuosUFiYJBtBzCdd:6c728ba45be91e43140f7390d12de5c06419cea602a5fc31a8acac232fcfbe7b")
       .build();
      
      */
    }
    
    @Test
    public void should_enable_cqlSession_with_clientId_clientSecret() {
        // Given
        Assertions.assertTrue(dbId.isPresent());
        Assertions.assertTrue(cloudRegion.isPresent());
        Assertions.assertTrue(clientId.isPresent());
        Assertions.assertTrue(clientSecret.isPresent());
        // When (autocloseable)
        try(AstraClient astraClient = AstraClient.builder()
                .databaseId(dbId.get())
                .cloudProviderRegion(cloudRegion.get())
                .clientId(clientId.get())
                .clientSecret(clientSecret.get())
                .build()) {
            // Then
            Assertions.assertNotNull(astraClient
                    .cqlSession().execute("SELECT release_version FROM system.local")
                    .one()
                    .getString("release_version"));
        }
    }
    
    @Test
    public void should_enable_cqlSession_with_token() {
        // Given
        Assertions.assertTrue(dbId.isPresent());
        Assertions.assertTrue(cloudRegion.isPresent());
        Assertions.assertTrue(appToken.isPresent());
        // When (autocloseable)
        try(AstraClient astraClient = AstraClient.builder()
                .databaseId(dbId.get())
                .cloudProviderRegion(cloudRegion.get())
                .appToken(appToken.get())
                .build()) {
            // Then
            Assertions.assertNotNull(astraClient
                    .cqlSession().execute("SELECT release_version FROM system.local")
                    .one()
                    .getString("release_version"));
        } 
    }
    
    @Test
    public void should_enable_documentApi_withToken() {
        // Given
        Assertions.assertTrue(dbId.isPresent());
        Assertions.assertTrue(cloudRegion.isPresent());
        Assertions.assertTrue(appToken.isPresent());
        // When
        try(AstraClient cli = AstraClient.builder()
                .databaseId(dbId.get())
                .cloudProviderRegion(cloudRegion.get())
                .appToken(appToken.get())
                .build()) {
                // Then
                Assertions.assertNotNull(cli.apiDocument().getToken());
         }
    }
    
    @Test
    public void should_enable_restApi_withToken() {
        // Given
        Assertions.assertTrue(dbId.isPresent());
        Assertions.assertTrue(cloudRegion.isPresent());
        Assertions.assertTrue(appToken.isPresent());
        // When
        try(AstraClient cli = AstraClient.builder()
                .databaseId(dbId.get())
                .cloudProviderRegion(cloudRegion.get())
                .appToken(appToken.get())
                .build()) {
                // Then
                Assertions.assertNotNull(cli.apiRest().getToken());
         }
    }
    
    @Test
    public void should_enable_devops_withToken() {
        // Given
        Assertions.assertTrue(appToken.isPresent());
        // When
        try(AstraClient cli = AstraClient.builder()
                .appToken(appToken.get())
                .build()) {
            // Then
            Assertions.assertNotNull(cli
                    .apiDevops()
                    .findAllDatabases()
                    .collect(Collectors.toList()));
         }
       
    }
    
    

}
