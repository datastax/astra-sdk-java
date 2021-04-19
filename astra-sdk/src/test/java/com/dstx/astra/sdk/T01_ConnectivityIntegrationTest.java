package com.dstx.astra.sdk;

import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Multiple Connectivity mode for eacj parameters.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class T01_ConnectivityIntegrationTest extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void setup() {
        initDb("sdk_test_connect");
    }
    
    @AfterAll
    public static void shutdown() {
        //terminateDb("sdk_test_connect");
    }
    
    @Test
    @DisplayName("Connect Cassandra with CqlSession using clientId/ClientSecret")
    public void should_enable_cqlSession_with_clientId_clientSecret() {
        // Given
        System.out.println(ANSI_YELLOW + "- Connect Cassandra with CqlSession using clientId/ClientSecret" + ANSI_RESET);
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Connect Cassandra with CqlSession using token/appToken")
    public void should_enable_cqlSession_with_token() {
        System.out.println(ANSI_YELLOW + "- Connect Cassandra with CqlSession using token/appToken" + ANSI_RESET);
        
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Invoke Document Api providing dbId,cloudRegion,appToken")
    public void should_enable_documentApi_withToken() {
        System.out.println(ANSI_YELLOW + "- Invoke Document Api providing dbId,cloudRegion,appToken" + ANSI_RESET);
        
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
                Assertions.assertTrue(cli.apiDocument().namespaceNames().count() > 0);
         }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Invoke REST Api providing dbId,cloudRegion,appToken")
    public void should_enable_restApi_withToken() {
        System.out.println(ANSI_YELLOW + "- Invoke REST Api providing dbId,cloudRegion,appToken" + ANSI_RESET);
        
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
            Assertions.assertTrue(cli.apiRest().keyspaceNames().count() > 0);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Invoke DEVOPS Api providing dbId,cloudRegion,appToken")
    public void should_enable_devops_withToken() {
        System.out.println(ANSI_YELLOW + "- Contact Devops API" + ANSI_RESET);
        
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }

}
