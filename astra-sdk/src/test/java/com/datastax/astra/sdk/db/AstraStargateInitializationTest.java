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

package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraSdkTest;
import com.datastax.oss.driver.api.core.cql.Row;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import static com.datastax.astra.devops.utils.TestUtils.TEST_REGION;
import static com.datastax.astra.devops.utils.TestUtils.setupDatabase;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Multiple Connectivity mode for each parameter.
 */
public class AstraStargateInitializationTest implements AstraSdkTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraStargateInitializationTest.class);
    
    private static AstraClient client;

    @BeforeAll
    public static void config() {
        client = AstraClient.builder()
                .withDatabaseRegion(TEST_REGION)
                .withDatabaseId(setupDatabase(TEST_DATABASE_NAME, TestDocClientConstants.TEST_NAMESPACE))
                .build();
        LOGGER.info("Connected");
    }

    @Test
    @DisplayName("Invoke REST Api providing dbId,cloudRegion,appToken")
    public void restApiTest() {
        assertTrue(client.apiStargateData().keyspaceNames().findAny().isPresent());
    }

    /*
    @Test
    @DisplayName("Connect Cassandra with CqlSession using clientId/ClientSecret")
    public void should_enable_cqlSession_with_clientId_clientSecret() {
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
                .enableCql()
                .build()) {
            // Then
            Assertions.assertNotNull(astraClient
                    .cqlSession().execute("SELECT release_version FROM system.local")
                    .one()
                    .getString("release_version"));
        }
        LOGGER.info("[OK]");
    }*/
    
    @Test
    @DisplayName("Connect Cassandra with CqlSession using token/appToken")
    public void should_enable_cqlSession_with_token() {
        LOGGER.info( "- Connect Cassandra with CqlSession using token/appToken");
        // Given
        assertNotNull(client.getConfig().getDatabaseId());
        assertNotNull(client.getConfig().getDatabaseRegion());
        assertNotNull(client.getConfig().getToken());
        // When (autocloseable)
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(client.getConfig().getDatabaseId())
                .withDatabaseRegion(client.getConfig().getDatabaseRegion())
                .withToken(client.getConfig().getToken())
                .enableCql()
                .build()) {
            // Then
            Row row = astraClient.cqlSession().execute("SELECT release_version FROM system.local").one();
            assertNotNull(row);
            assertNotNull(row.getString("release_version"));
        }
        LOGGER.info("[OK]");
    }
    
    @Test
    @DisplayName("Invoke Document Api providing dbId,cloudRegion,appToken")
    public void should_enable_documentApi_withToken() {
        LOGGER.info( "- Invoke Document Api providing dbId,cloudRegion,appToken");
        // Given
        assertNotNull(client.getConfig().getDatabaseId());
        assertNotNull(client.getConfig().getDatabaseRegion());
        assertNotNull(client.getConfig().getToken());
        // When
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(client.getConfig().getDatabaseId())
                .withDatabaseRegion(client.getConfig().getDatabaseRegion())
                .withToken(client.getConfig().getToken())
                .disableCrossRegionFailOver()
                .build()) {
                // Then
                assertTrue(astraClient
                        .apiStargateDocument().namespaceNames().findAny().isPresent());
         }
        LOGGER.info("[OK]");
    }
    
    @Test
    @DisplayName("Invoke REST Api providing dbId,cloudRegion,appToken")
    public void should_enable_restApi_withToken() {
        LOGGER.info( "- Invoke REST Api providing dbId,cloudRegion,appToken");
        
        // Given
        assertNotNull(client.getConfig().getDatabaseId());
        assertNotNull(client.getConfig().getDatabaseRegion());
        assertNotNull(client.getConfig().getToken());
        // When
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(client.getConfig().getDatabaseId())
                .withDatabaseRegion(client.getConfig().getDatabaseRegion())
                .withToken(client.getConfig().getToken())
                .build()) {
                // Then
            assertTrue(astraClient
                    .apiStargateData().keyspaceNames().findAny().isPresent());
        }
        LOGGER.info("[OK]");
    }
    
    @Test
    @DisplayName("Invoke DEVOPS Api providing dbId,cloudRegion,appToken")
    public void should_enable_devops_withToken() {
        LOGGER.info( "- Contact Devops API");
        
        // Given
        assertTrue(client.getToken().isPresent());
        // When
        try(AstraClient cli = AstraClient.builder().withToken(client.getToken().get()).build()) {
          
            // Then
            assertNotNull(cli
                    .apiDevopsDatabases()
                    .findAll()
                    .collect(Collectors.toList()));
         }
        LOGGER.info("[OK]");
    }

}
