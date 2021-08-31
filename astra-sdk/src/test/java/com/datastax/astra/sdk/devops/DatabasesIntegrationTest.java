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
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.AbstractAstraIntegrationTest;
import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.databases.domain.DatabaseCreationRequest;
import com.datastax.astra.sdk.databases.domain.DatabaseRegion;
import com.datastax.astra.sdk.databases.domain.DatabaseStatusType;
import com.datastax.astra.sdk.databases.domain.DatabaseTierType;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.stargate.sdk.utils.HttpApisClient;

@TestMethodOrder(OrderAnnotation.class)
public class DatabasesIntegrationTest extends AbstractAstraIntegrationTest {
    
    // Test Keys
    private static final String TEST_DBNAME      = "sdk_test_api_devops_db";
    private static final String TEST_KEYSPACE_1  = "sdk_ks1";
    private static final String TEST_KEYSPACE_2  = "sdk_ks2";
    private static final String TEST_NAMESPACE_1 = "sdk_ns1";
    
    // To be updated after db creation
    public static String serverlessDbId;
    
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        printYellow("Parameter validation");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DatabasesClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DatabasesClient((String) null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DatabasesClient((HttpApisClient) null));
    }
    
    @Test
    @Order(2)
    public void should_connect_with_AstraClient() throws InterruptedException {
        printYellow("Connection with AstraClient");
        Assertions.assertTrue(client.getToken().isPresent());
        HttpApisClient.getInstance().setToken(client.getToken().get());
        Assertions.assertNotNull(client
                    .apiDevopsOrganizations()
                    .regions().collect(Collectors.toList()).size() > 1);
        printOK("Can connect to ASTRA with AstraClient");
    }
   
    @Test
    @Order(3)
    public void should_connect_with_OrganizationsClient() {
        printYellow("Connection with OrganizationsClient");
        // Given
        Assertions.assertTrue(client.getToken().isPresent());
        // When
        OrganizationsClient cli = new OrganizationsClient(client.getToken().get());
        // Then
        Assert.assertTrue(cli.regions().collect(Collectors.toList()).size() > 1);
        printOK("Can connect to ASTRA with OrganizationsClient");
    }
    
    @Test
    @Order(4)
    public void should_aws_us_east_be_available() {
        printYellow("AWS Region available");
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
        printOK("Tier `serverless` for region 'aws/us-east-1' is available");
    }
    
    @Test
    @Order(5)
    public void should_createServerlessDb() {
        printYellow(" DB Creation");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // When
        Assert.assertFalse(cli.databasesNonTerminatedByName(TEST_DBNAME).collect(Collectors.toSet()).size()>0);
        printOK("Instance with name'" + TEST_DBNAME + "' does not exist.");
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
        printOK("DB Creation request started");
        
        // Then
        Assert.assertTrue(cli.database(serverlessDbId).exist());
        printOK("Database id=" + serverlessDbId);
        printOK("Initializing ");
        int atempt = 0;
        while(!DatabaseStatusType.ACTIVE.equals(cli
                .database(serverlessDbId)
                .find().get().getStatus()) && atempt < 50) {
            System.out.print(ANSI_GREEN + "\u25a0" +ANSI_RESET); 
            waitForSeconds(5);
            atempt++;
        }
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli
                .database(serverlessDbId).find()
                .get().getStatus());
        System.out.println();
        printOK("DB is active");
    }
    
    @Test
    @Order(6)
    public void should_find_database_byName() {
        printYellow("[GET] Returns a list of databases");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // When
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(null));
        printOK("Validated parameters are required");
        Assert.assertFalse(cli.database("i-like-cheese").exist());
        
        printYellow("[GET] Finds database by NAME");
        Assert.assertTrue(cli.databasesNonTerminated().anyMatch(
                db -> TEST_DBNAME.equals(db.getInfo().getName())));
        printOK("List retrieved and the new created DB is present");
    }
        
    @Test
    @Order(7)
    public void should_find_databases_by_id() {
        printYellow("[GET] Finds database by ID");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        Optional<Database> odb = cli.database(serverlessDbId).find();
        Assert.assertTrue(odb.isPresent());
        Assert.assertEquals(DatabaseStatusType.ACTIVE, odb.get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB can be loaded from its id");
        Assert.assertNotNull(odb.get().getInfo());
        Assert.assertEquals(DatabaseTierType.serverless, odb.get().getInfo().getTier());
        Assert.assertNotNull(odb.get().getMetrics());
        Assert.assertNotNull(odb.get().getStorage());
        Assert.assertNotNull(TEST_KEYSPACE_1, odb.get().getInfo().getKeyspace());
        printOK("Fields metrics,storage,info are populated");
    }
    
    /*
    @Test
    @Order(8)
    public void should_find_databases_by_id_Async() throws InterruptedException {
        printYellow("[GET] Returns a list of databases");
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
        client.apiDevopsDatabases().database("your_id").createKeyspace("ks2");
        printYellow("[POST] Adds keyspace into database");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // Check Parameters
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createNamespace(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createNamespace(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createKeyspace(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createKeyspace(null));
        
        printOK("Parameters validation is working");
        
        // Given
        Assert.assertFalse(cli.database(serverlessDbId).find().get().getInfo().getKeyspaces().contains(TEST_KEYSPACE_2));
        // When
        cli.database(serverlessDbId).createKeyspace(TEST_KEYSPACE_2);
        printOK("Keyspace creation request successful for '" + TEST_KEYSPACE_2 + "'");
        Assert.assertEquals(DatabaseStatusType.MAINTENANCE, cli.database(serverlessDbId).find().get().getStatus());
        printOK("DB in [MAINTENANCE] mode");
        while(DatabaseStatusType.ACTIVE != cli.database(serverlessDbId).find().get().getStatus() ) {
            waitForSeconds(1);
        }
        // When
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli.database(serverlessDbId).find().get().getStatus());
        printOK("DB in [ACTIVE] mode");
        
        cli.database(serverlessDbId).createNamespace(TEST_NAMESPACE_1);
        printOK("Namespace creation request successful for '" + TEST_NAMESPACE_1 + "'");
        Assert.assertEquals(DatabaseStatusType.MAINTENANCE, cli.database(serverlessDbId).find().get().getStatus());
        printOK("DB in [MAINTENANCE] mode");
        while(DatabaseStatusType.ACTIVE != cli.database(serverlessDbId).find().get().getStatus() ) {
            waitForSeconds(1);
        }
        printOK("DB in [ACTIVE] mode");
        // Then
        Database db = cli.database(serverlessDbId).find().get();
        Assert.assertTrue(db.getInfo().getKeyspaces().contains(TEST_KEYSPACE_2));
        Assert.assertTrue(db.getInfo().getKeyspaces().contains(TEST_NAMESPACE_1));
        printOK("Expected keyspaces and namespaces are now present");
        
        // Cann create keyspace that already exist
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createNamespace(TEST_NAMESPACE_1));
        printOK("You cannot create an existing keyspace");
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).createKeyspace(TEST_KEYSPACE_2));
        printOK("You cannot create an existing namespace");
    }
    
    @Test
    @Order(9)
    public void should_download_secureBundle() {
        printYellow("[POST] Obtain zip for connecting to the database");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        String randomFile = "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
        Assert.assertFalse(new File(randomFile).exists());
        // When
        cli.database(serverlessDbId).downloadSecureConnectBundle(randomFile);
        printOK("Downloading file call");
        // Then
        Assert.assertTrue(new File(randomFile).exists());
        printOK("File as been download in " + randomFile);
    }
    
    @Test
    @Order(10)
    public void should_not_parkserverless() {
        printYellow("Cannot park serverless");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // (6) - Check that we cannot park a serverlessDB
        printOK("POST] Parks a database serverless is not possible" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).park());
        printOK("Expected exception retrieved");
    }
    
    @Test
    @Order(11)
    public void should_not_unpark_serverless() {
        printYellow("Cannot unpark serverless");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // (7)
        printOK("[POST] Unparks a database serverless is not possible)" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).unpark());
        printOK("Expected exception retrieved");
    }
    
    @Test
    @Order(12)
    public void should_not_resize_serverless() {
        printYellow("Cannot resize serverless");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        printOK("[POST] Resize a database serverless is not possible)" + ANSI_RESET);
        // When-Then
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).resize(2));
        printOK("Expected exception retrieved");
    }
    
    @Test
    @Order(13)
    public void should_not_resetpassword() {
        printYellow("Cannot reset password");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        // (9)
        printOK("[POST] Reset a Password in a database serverless is not possible)" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.database(serverlessDbId).resetPassword("token", "cedrick1"));
        printOK("Expected exception retrieved");
    }
    
    @Test
    @Order(14)
    public void should_terminate_db() {
        printYellow("[POST] Terminating an instance");
        // Given
        DatabasesClient cli = new DatabasesClient(client.getToken().get());
        cli.database(serverlessDbId).delete();
        while(DatabaseStatusType.TERMINATING != cli.database(serverlessDbId).find().get().getStatus() ) {
            waitForSeconds(1);
        }
        printOK("Status changed to TERMINATING");
        while(DatabaseStatusType.TERMINATED != cli.database(serverlessDbId).find().get().getStatus() ) {
            waitForSeconds(1);
        }
        printOK("Status changed to TERMINATED");
    }
   
    
}
