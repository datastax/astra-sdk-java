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

package com.datastax.astra.sdk.devops;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.databases.domain.DatabaseCreationRequest;
import com.datastax.astra.sdk.databases.domain.DatabaseRegion;
import com.datastax.astra.sdk.databases.domain.DatabaseStatusType;
import com.datastax.astra.sdk.databases.domain.DatabaseTierType;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.stargate.sdk.utils.AnsiUtils;

@TestMethodOrder(OrderAnnotation.class)
public class DatabasesIntegrationTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabasesIntegrationTest.class);
    
    // Test Keys
    private static final String TEST_DBNAME      = "sdk_test_api_devops_db";
    private static final String TEST_KEYSPACE_1  = "sdk_ks1";
    private static final String TEST_KEYSPACE_2  = "sdk_ks2";
    private static final String TEST_NAMESPACE_1 = "sdk_ns1";
    
    // To be updated after db creation
    public static String serverlessDbId;
    
    private static AstraClient client;
    
    @BeforeAll
    public static void initAstraClient() {
        client= AstraClient.builder().build();
    }
    
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DatabasesClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DatabasesClient((String) null));
    }
    
    @Test
    @Order(2)
    public void should_connect_with_AstraClient() throws InterruptedException {
        Assertions.assertNotNull(client
                    .apiDevopsOrganizations()
                    .regions()
                    .collect(Collectors.toList()).size() > 1);
        LOGGER.info("Can connect to ASTRA with AstraClient");
    }
   
    @Test
    @Order(3)
    public void should_connect_with_OrganizationsClient() {
        LOGGER.info("Connection with OrganizationsClient");
        // Given
        Assertions.assertTrue(client.getToken().isPresent());
        // When
        OrganizationsClient cli = new OrganizationsClient(client.getToken().get());
        // Then
        Assert.assertTrue(cli.regions().collect(Collectors.toList()).size() > 1);
        LOGGER.info("Can connect to ASTRA with OrganizationsClient");
    }
    
    @Test
    @Order(4)
    public void should_aws_us_east_be_available() {
        LOGGER.info("AWS Region available");
        // Given
        OrganizationsClient cli = new OrganizationsClient(client.getToken().get());
        // When
        Map <DatabaseTierType, Map<CloudProviderType,List<DatabaseRegion>>> available = 
                cli.regionsMap();
        // Then
        Assert.assertTrue(available
                .containsKey(DatabaseTierType.serverless));
        Assert.assertTrue(available
                .get(DatabaseTierType.serverless)
                .containsKey(CloudProviderType.AWS));
        Assert.assertTrue(available
                .get(DatabaseTierType.serverless)
                .get(CloudProviderType.AWS).stream()
                .anyMatch(db -> "us-east-1".equalsIgnoreCase(db.getRegion())));
        LOGGER.info("Tier `serverless` for region 'aws/us-east-1' is available");
    }
    
    @Test
    @Order(5)
    public void should_createServerlessDb() {
        LOGGER.info(" DB Creation");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // When
        Assert.assertFalse(cli.databasesNonTerminatedByName(TEST_DBNAME).collect(Collectors.toSet()).size()>0);
        LOGGER.info("Instance with name'" + TEST_DBNAME + "' does not exist.");
        // When
        DatabaseCreationRequest dcr = DatabaseCreationRequest
                .builder()
                .name(TEST_DBNAME)
                .tier(DatabaseTierType.serverless)
                .cloudProvider(CloudProviderType.AWS)
                .cloudRegion("us-east-1")
                .keyspace(TEST_KEYSPACE_1)
                .build();
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createDatabase(null));
        
        serverlessDbId = cli.createDatabase(dcr);
        LOGGER.info("DB Creation request started");
        
        // Then
        Assert.assertTrue(cli.database(serverlessDbId).exist());
        LOGGER.info("Database id=" + serverlessDbId);
        LOGGER.info("Initializing ");
        int atempt = 0;
        while(!DatabaseStatusType.ACTIVE.equals(cli
                .database(serverlessDbId)
                .find().get().getStatus()) && atempt < 50) {
            System.out.print(AnsiUtils.green("\u25a0")); 
            AstraTestUtils.waitForSeconds(5);
            atempt++;
        }
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli
                .database(serverlessDbId).find()
                .get().getStatus());
        System.out.println();
        LOGGER.info("DB is active");
    }
    
    @Test
    @Order(6)
    public void should_find_database_byName() {
        LOGGER.info("[GET] Returns a list of databases");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // When
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(null));
        LOGGER.info("Validated parameters are required");
        Assert.assertFalse(cli.database("i-like-cheese").exist());
        
        LOGGER.info("[GET] Finds database by NAME");
        Assert.assertTrue(cli.databasesNonTerminated().anyMatch(
                db -> TEST_DBNAME.equals(db.getInfo().getName())));
        LOGGER.info("List retrieved and the new created DB is present");
    }
        
    @Test
    @Order(7)
    public void should_find_databases_by_id() {
        LOGGER.info("[GET] Finds database by ID");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        Optional<Database> odb = cli.database(serverlessDbId).find();
        Assert.assertTrue(odb.isPresent());
        Assert.assertEquals(DatabaseStatusType.ACTIVE, odb.get().getStatus());
        Assert.assertNotNull(odb.get().getInfo());
        Assert.assertEquals(DatabaseTierType.serverless, odb.get().getInfo().getTier());
        Assert.assertNotNull(odb.get().getMetrics());
        Assert.assertNotNull(odb.get().getStorage());
        Assert.assertNotNull(TEST_KEYSPACE_1, odb.get().getInfo().getKeyspace());
        LOGGER.info("Fields metrics,storage,info are populated");
    }
    
    /*
    @Test
    @Order(8)
    public void should_find_databases_by_id_Async() throws InterruptedException {
        LOGGER.info("[GET] Returns a list of databases");
        client.apiDevopsDatabases()
              .database(serverlessDbId).findAsync()
              .whenComplete((db,error) -> {
                  if (error != null) {
                      Assertions.fail();
                  }
               System.out.println(db.get().getId()); 
        });    
        Thread.sleep(1000);
    }*/
    
    @Test
    @Order(8)
    public void should_create_keyspaces() {
        LOGGER.info("[POST] Adds keyspace into database");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // Check Parameters
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createNamespace(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createNamespace(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createKeyspace(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createKeyspace(null));
        
        LOGGER.info("Parameters validation is working");
        
        // Given
        Assert.assertFalse(cli.database(serverlessDbId).find().get().getInfo().getKeyspaces().contains(TEST_KEYSPACE_2));
        // When
        cli.database(serverlessDbId).createKeyspace(TEST_KEYSPACE_2);
        LOGGER.info("Keyspace creation request successful for '" + TEST_KEYSPACE_2 + "'");
        Assert.assertEquals(DatabaseStatusType.MAINTENANCE, cli.database(serverlessDbId).find().get().getStatus());
        LOGGER.info("DB in [MAINTENANCE] mode");
        while(DatabaseStatusType.ACTIVE != cli.database(serverlessDbId).find().get().getStatus() ) {
            AstraTestUtils.waitForSeconds(1);
        }
        // When
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli.database(serverlessDbId).find().get().getStatus());
        LOGGER.info("DB in [ACTIVE] mode");
        
        cli.database(serverlessDbId).createNamespace(TEST_NAMESPACE_1);
        LOGGER.info("Namespace creation request successful for '" + TEST_NAMESPACE_1 + "'");
        Assert.assertEquals(DatabaseStatusType.MAINTENANCE, cli.database(serverlessDbId).find().get().getStatus());
        LOGGER.info("DB in [MAINTENANCE] mode");
        while(DatabaseStatusType.ACTIVE != cli.database(serverlessDbId).find().get().getStatus() ) {
            AstraTestUtils.waitForSeconds(1);
        }
        LOGGER.info("DB in [ACTIVE] mode");
        // Then
        Database db = cli.database(serverlessDbId).find().get();
        Assert.assertTrue(db.getInfo().getKeyspaces().contains(TEST_KEYSPACE_2));
        Assert.assertTrue(db.getInfo().getKeyspaces().contains(TEST_NAMESPACE_1));
        LOGGER.info("Expected keyspaces and namespaces are now present");
        
        // Cann create keyspace that already exist
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createNamespace(TEST_NAMESPACE_1));
        LOGGER.info("You cannot create an existing keyspace");
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createKeyspace(TEST_KEYSPACE_2));
        LOGGER.info("You cannot create an existing namespace");
    }
    
    @Test
    @Order(9)
    public void should_download_secureBundle() {
        LOGGER.info("[POST] Obtain zip for connecting to the database");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        String randomFile = "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
        Assert.assertFalse(new File(randomFile).exists());
        // When
        cli.database(serverlessDbId).downloadSecureConnectBundle(randomFile);
        LOGGER.info("Downloading file call");
        // Then
        Assert.assertTrue(new File(randomFile).exists());
        LOGGER.info("File as been download in " + randomFile);
    }
    
    @Test
    @Order(10)
    public void should_not_parkserverless() {
        LOGGER.info("Cannot park serverless");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // (6) - Check that we cannot park a serverlessDB
        LOGGER.info("POST] Parks a database serverless is not possible");
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).park());
        LOGGER.info("Expected exception retrieved");
    }
    
    @Test
    @Order(11)
    public void should_not_unpark_serverless() {
        LOGGER.info("Cannot unpark serverless");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // (7)
        LOGGER.info("[POST] Unparks a database serverless is not possible)" );
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).unpark());
        LOGGER.info("Expected exception retrieved");
    }
    
    @Test
    @Order(12)
    public void should_not_resize_serverless() {
        LOGGER.info("Cannot resize serverless");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        LOGGER.info("[POST] Resize a database serverless is not possible)");
        // When-Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).resize(2));
        LOGGER.info("Expected exception retrieved");
    }
    
    @Test
    @Order(13)
    public void should_not_resetpassword() {
        LOGGER.info("Cannot reset password");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // (9)
        LOGGER.info("[POST] Reset a Password in a database serverless is not possible)");
        Assertions.assertThrows(RuntimeException.class, () -> cli.database(serverlessDbId).resetPassword("token", "cedrick1"));
        LOGGER.info("Expected exception retrieved");
    }
    
    @Test
    @Order(14)
    public void should_terminate_db() {
        LOGGER.info("[POST] Terminating an instance");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        cli.database(serverlessDbId).delete();
        while(DatabaseStatusType.TERMINATING != cli.database(serverlessDbId).find().get().getStatus() ) {
            AstraTestUtils.waitForSeconds(1);
        }
        LOGGER.info("Status changed to TERMINATING");
        while(DatabaseStatusType.TERMINATED != cli.database(serverlessDbId).find().get().getStatus() ) {
            AstraTestUtils.waitForSeconds(1);
        }
        LOGGER.info("Status changed to TERMINATED");
    }
   
    
}
