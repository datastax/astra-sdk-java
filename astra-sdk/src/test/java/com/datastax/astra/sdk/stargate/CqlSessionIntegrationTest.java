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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.datastax.astra.sdk.AbstractAstraIntegrationTest;
import com.datastax.astra.sdk.AstraClient;

/**
 * Multiple Connectivity mode for eacj parameters.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CqlSessionIntegrationTest extends AbstractAstraIntegrationTest {
    
    @Test
    @DisplayName("Connect Cassandra with CqlSession using clientId/ClientSecret")
    public void should_enable_cqlSession_with_clientId_clientSecret() {
        // Given
        System.out.println(ANSI_YELLOW + "- Connect Cassandra with CqlSession using clientId/ClientSecret" + ANSI_RESET);
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Connect Cassandra with CqlSession using token/appToken")
    public void should_enable_cqlSession_with_token() {
        System.out.println(ANSI_YELLOW + "- Connect Cassandra with CqlSession using token/appToken" + ANSI_RESET);
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Invoke Document Api providing dbId,cloudRegion,appToken")
    public void should_enable_documentApi_withToken() {
        System.out.println(ANSI_YELLOW + "- Invoke Document Api providing dbId,cloudRegion,appToken" + ANSI_RESET);
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Invoke REST Api providing dbId,cloudRegion,appToken")
    public void should_enable_restApi_withToken() {
        System.out.println(ANSI_YELLOW + "- Invoke REST Api providing dbId,cloudRegion,appToken" + ANSI_RESET);
        
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }
    
    @Test
    @DisplayName("Invoke DEVOPS Api providing dbId,cloudRegion,appToken")
    public void should_enable_devops_withToken() {
        System.out.println(ANSI_YELLOW + "- Contact Devops API" + ANSI_RESET);
        
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
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET);
    }

}
