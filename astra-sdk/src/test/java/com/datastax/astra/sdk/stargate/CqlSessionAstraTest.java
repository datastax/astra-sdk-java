/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.sdk.stargate;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import com.datastax.stargate.sdk.doc.test.ApiDocumentTest;

/**
 * Multiple Connectivity mode for eacj parameters.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CqlSessionAstraTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CqlSessionAstraTest.class);
    
    private static AstraClient client;
    
    @BeforeAll
    public static void config() {
        
        client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);
        
        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().get())
                .withKeyspace(ApiDocumentTest.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .build();
        System.out.println(dbId);
    }
    
    @Test
    @DisplayName("Connect Cassandra with CqlSession using clientId/ClientSecret")
    public void should_enable_cqlSession_with_clientId_clientSecret() {
        // Given
        LOGGER.info( "- Connect Cassandra with CqlSession using clientId/ClientSecret");
        Assertions.assertNotNull(client.getConfig().getDatabaseId());
        Assertions.assertNotNull(client.getConfig().getDatabaseRegion());
        Assertions.assertNotNull(client.getConfig().getClientId());
        Assertions.assertNotNull(client.getConfig().getClientSecret());
        // When (autocloseable)
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(client.getConfig().getDatabaseId())
                .withDatabaseRegion(client.getConfig().getDatabaseRegion())
                .withClientId(client.getConfig().getClientId())
                .withClientSecret(client.getConfig().getClientSecret())
                .build()) {
            // Then
            Assertions.assertNotNull(astraClient
                    .cqlSession().execute("SELECT release_version FROM system.local")
                    .one()
                    .getString("release_version"));
        }
        LOGGER.info("[OK]");
    }
    
    @Test
    @DisplayName("Connect Cassandra with CqlSession using token/appToken")
    public void should_enable_cqlSession_with_token() {
        LOGGER.info( "- Connect Cassandra with CqlSession using token/appToken");
        // Given
        Assertions.assertNotNull(client.getConfig().getDatabaseId());
        Assertions.assertNotNull(client.getConfig().getDatabaseRegion());
        Assertions.assertNotNull(client.getConfig().getToken());
        // When (autocloseable)
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(client.getConfig().getDatabaseId())
                .withDatabaseRegion(client.getConfig().getDatabaseRegion())
                .withToken(client.getConfig().getToken())
                .build()) {
            // Then
            Assertions.assertNotNull(astraClient
                    .cqlSession().execute("SELECT release_version FROM system.local")
                    .one()
                    .getString("release_version"));
        }
        LOGGER.info("[OK]");
    }
    
    @Test
    @DisplayName("Invoke Document Api providing dbId,cloudRegion,appToken")
    public void should_enable_documentApi_withToken() {
        LOGGER.info( "- Invoke Document Api providing dbId,cloudRegion,appToken");
        // Given
        Assertions.assertNotNull(client.getConfig().getDatabaseId());
        Assertions.assertNotNull(client.getConfig().getDatabaseRegion());
        Assertions.assertNotNull(client.getConfig().getToken());
        // When
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(client.getConfig().getDatabaseId())
                .withDatabaseRegion(client.getConfig().getDatabaseRegion())
                .withToken(client.getConfig().getToken())
                .build()) {
                // Then
                Assertions.assertTrue(astraClient
                        .apiStargateDocument().namespaceNames().count() > 0);
         }
        LOGGER.info("[OK]");
    }
    
    @Test
    @DisplayName("Invoke REST Api providing dbId,cloudRegion,appToken")
    public void should_enable_restApi_withToken() {
        LOGGER.info( "- Invoke REST Api providing dbId,cloudRegion,appToken");
        
        // Given
        Assertions.assertNotNull(client.getConfig().getDatabaseId());
        Assertions.assertNotNull(client.getConfig().getDatabaseRegion());
        Assertions.assertNotNull(client.getConfig().getToken());
        // When
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(client.getConfig().getDatabaseId())
                .withDatabaseRegion(client.getConfig().getDatabaseRegion())
                .withToken(client.getConfig().getToken())
                .build()) {
                // Then
            Assertions.assertTrue(astraClient
                    .apiStargateData().keyspaceNames().count() > 0);
        }
        LOGGER.info("[OK]");
    }
    
    @Test
    @DisplayName("Invoke DEVOPS Api providing dbId,cloudRegion,appToken")
    public void should_enable_devops_withToken() {
        LOGGER.info( "- Contact Devops API");
        
        // Given
        Assertions.assertTrue(client.getToken().isPresent());
        // When
        try(AstraClient cli = AstraClient.builder().withToken(client.getToken().get()).build()) {
          
            // Then
            Assertions.assertNotNull(cli
                    .apiDevopsDatabases()
                    .databases()
                    .collect(Collectors.toList()));
         }
        LOGGER.info("[OK]");
    }

}
