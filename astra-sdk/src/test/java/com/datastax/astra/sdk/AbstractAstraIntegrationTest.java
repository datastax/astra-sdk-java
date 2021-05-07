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

package com.datastax.astra.sdk;

import java.util.Optional;

import org.junit.Assert;

import com.datastax.astra.sdk.AstraClient.AstraClientBuilder;
import com.datastax.astra.sdk.devops.ApiDevopsClient;
import com.datastax.astra.sdk.devops.CloudProviderType;
import com.datastax.astra.sdk.devops.DatabaseStatusType;
import com.datastax.astra.sdk.devops.DatabaseTierType;
import com.datastax.astra.sdk.devops.req.DatabaseCreationRequest;
import com.datastax.astra.sdk.devops.res.Database;
import com.datastax.oss.driver.shaded.guava.common.base.Strings;

/**
 * Mutualize logic to initialize the client.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class AbstractAstraIntegrationTest {
    
    public static final String ANSI_RESET           = "\u001B[0m";
    public static final String ANSI_GREEN           = "\u001B[32m";
    public static final String ANSI_YELLOW          = "\u001B[33m";
    
    public static final String            TEST_REGION   = "us-east-1";
    public static final CloudProviderType TEST_PROVIDER = CloudProviderType.AWS;
    public static final DatabaseTierType  TEST_TIER     = DatabaseTierType.serverless;
    public static final String            TEST_KEYSPACE = "ks_test";
    
    // Client initialized based on environment variables
    public static AstraClient client;
    
    // Read Configuration from properties
    public static Optional<String> cloudRegion  = getPropertyFromEnv(AstraClient.ASTRA_DB_REGION, TEST_REGION);
    public static Optional<String> dbId         = getPropertyFromEnv(AstraClient.ASTRA_DB_ID, null);
    public static Optional<String> clientId     = getPropertyFromEnv(AstraClient.ASTRA_DB_CLIENT_ID, null);
    public static Optional<String> clientSecret = getPropertyFromEnv(AstraClient.ASTRA_DB_CLIENT_SECRET, null);
    public static Optional<String> appToken     = getPropertyFromEnv(AstraClient.ASTRA_DB_APPLICATION_TOKEN, null);
    
    protected static Optional<String> getPropertyFromEnv(String varname, String defaultValue) {
        String value = defaultValue;
        if (!Strings.isNullOrEmpty(System.getenv(varname))) {
            value = System.getenv(varname);
        }
        if (!Strings.isNullOrEmpty(System.getProperty(varname))) {
            value = System.getProperty(varname);
        }
        return Optional.ofNullable(value);
    }
    
    protected static void initDb(String dbName) {
        AstraClientBuilder clientBuilder = AstraClient.builder();
        if(dbId.isEmpty()) {
            dbId = Optional.ofNullable(createTestDatabaseIfNotExist(dbName));
        }
        clientBuilder.databaseId(dbId.get());
        if (cloudRegion.isPresent()) {
            clientBuilder.cloudProviderRegion(cloudRegion.get());
        }
        if (clientId.isPresent()) {
            clientBuilder.clientId(clientId.get());
        }
        if (clientSecret.isPresent()) {
            clientBuilder.clientSecret(clientSecret.get());
        }
        if (appToken.isPresent()) {
            clientBuilder.appToken(appToken.get());
        }
        client = clientBuilder.build();
    }
    
    protected static void terminateDb(String dbName) {
        System.out.println(ANSI_YELLOW + "Terminate DB " + dbName + ANSI_RESET);
        Optional<Database> existingDb = client
                .apiDevops()
                .findDatabasesNonTerminatedByName(dbName)
                .filter(db -> dbName.equals(db.getInfo().getName()))
                .findFirst();
        if(existingDb.isPresent()) {
            client
            .apiDevops().terminateDatabase(existingDb.get().getId());
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Terminating [" + dbName + "] id=" + existingDb.get().getId());
            System.out.print(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Terminating ");
            while(DatabaseStatusType.TERMINATED != client.apiDevops()
                    .findDatabaseById(existingDb.get().getId()).get().getStatus() ) {
                System.out.print(ANSI_GREEN + "\u25a0" +ANSI_RESET); 
                waitForSeconds(5);
            }
            System.out.println(ANSI_GREEN + "\n[OK]" + ANSI_RESET + " - DB [TERMINATED]");
        } else {
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Nothing to do.");
        }
    }
    
    /**
     * Multiple operations are async and 
     * we need to wait until a condition is reached.
     */
    protected static void waitForSeconds(int s) {
        try {Thread.sleep(s * 1000);} catch (InterruptedException e) {}
    }
    
    protected static String createTestDatabaseIfNotExist(String dbName) {
        System.out.println(ANSI_YELLOW + "Create DB " + dbName + ANSI_RESET);
        Assert.assertTrue(appToken.isPresent());
        Assert.assertNotNull(dbName);
        ApiDevopsClient cli = new ApiDevopsClient(appToken.get());
        
        Optional<Database> existingDb = cli
                    .findDatabasesNonTerminatedByName(dbName)
                    .filter(db -> dbName.equals(db.getInfo().getName()))
                    .findFirst();
        if (existingDb.isPresent()) {
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Using existing db with id " + existingDb.get().getId());
            return existingDb.get().getId();
        }
        
        String id = cli.createDatabase(DatabaseCreationRequest
                .builder()
                .name(dbName)
                .tier(TEST_TIER)
                .cloudProvider(TEST_PROVIDER)
                .cloudRegion(TEST_REGION)
                .keyspace(TEST_KEYSPACE)
                .build());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Database [" + dbName + "] id=" + id);
        System.out.print(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Initializing ");
        while(!DatabaseStatusType.ACTIVE.equals(cli.findDatabaseById(id).get().getStatus())) {
            System.out.print(ANSI_GREEN + "\u25a0" +ANSI_RESET); 
            waitForSeconds(5);
        }
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli.findDatabaseById(id).get().getStatus());
        System.out.println(ANSI_GREEN + "\n[OK]" + ANSI_RESET + " - DB is active");
        return id;
    }

}
