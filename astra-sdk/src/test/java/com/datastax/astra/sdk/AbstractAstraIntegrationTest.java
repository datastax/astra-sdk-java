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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.datastax.astra.sdk.databases.DatabaseClient;
import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.databases.domain.DatabaseCreationRequest;
import com.datastax.astra.sdk.databases.domain.DatabaseStatusType;
import com.datastax.astra.sdk.databases.domain.DatabaseTierType;

/**
 * Mutualize logic to initialize the client.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class AbstractAstraIntegrationTest {
    
    public static final String ANSI_RESET           = "\u001B[0m";
    public static final String ANSI_GREEN           = "\u001B[32m";
    public static final String ANSI_YELLOW          = "\u001B[33m";
    
    public static final CloudProviderType TEST_PROVIDER = CloudProviderType.AWS;
    public static final DatabaseTierType  TEST_TIER     = DatabaseTierType.serverless;
    public static final String            TEST_REGION   = "us-east-1";
    public static final String            TEST_KEYSPACE = "ks_test";
    
    // Client initialized based on environment variables
    public static AstraClient client = AstraClient.builder().build();
    
    /**
     * Terminate a db.
     *
     * @param dbName
     *      database name
     */
    protected void terminateDb(String dbName) {
        printYellow("Terminate DB " + dbName);
        DatabaseClient dbc = client
                .apiDevopsDatabases()
                .databaseByName(dbName);
        Optional<Database> existingDb = dbc.find();
        
        if(existingDb.isPresent()) {
            String dbid = existingDb.get().getId();
            dbc.delete();
            printOK("Terminating [" + dbName + "] id=" + dbid);
            printOK("Terminating ");
            while(DatabaseStatusType.TERMINATED != dbc.find().get().getStatus() ) {
                System.out.print(ANSI_GREEN + "\u25a0" +ANSI_RESET); 
                waitForSeconds(5);
            }
            System.out.println("\n");
            printOK("DB [TERMINATED]");
        } else {
            printOK("Nothing to do.");
        }
    }
    
    /**
     * Display a message in yellow in the console.
     * 
     * @param msg
     *          message
     */
    protected static void printYellow(String msg) {
        System.out.println(ANSI_YELLOW + msg + " " + ANSI_RESET);
    }
    
    /**
     * Display a message in green in the console.
     * 
     * @param msg
     *          message
     */
    protected static void printOK(String msg) {
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - " + msg);
    }
    
    /**
     * Hold execution for X seconds waiting for async APIS.
     * 
     * @param seconds
     *          time to wait
     */
    protected static void waitForSeconds(int seconds) {
        try {Thread.sleep(seconds * 1000);} catch (InterruptedException e) {}
    }
    
    protected static String createDbAndKeyspaceIfNotExist(String dbName, String keyspace) {
        List<Database> dbs = client
                .apiDevopsDatabases()
                .databasesNonTerminatedByName(dbName)
                .collect(Collectors.toList());
        
        if (dbs.size() > 0) {
            // db exists
            printOK("DB exist");
            Database db = dbs.get(0);
            DatabaseClient dbc = client.apiDevopsDatabases().database(db.getId());
            // Should we create keyspace
            if (!db.getInfo().getKeyspaces().contains(keyspace)) {
                printOK("Creating keyspace " + keyspace);
                dbc.createKeyspace(keyspace);
                while(DatabaseStatusType.ACTIVE != dbc.find().get().getStatus() ) {
                    waitForSeconds(1);
                }
                // When
                Assert.assertEquals(DatabaseStatusType.ACTIVE, dbc.find().get().getStatus());
                printOK("DB in [ACTIVE] mode");
            }
            return db.getId();
        } else {
            // db does not exist, creating
            String serverlessDbId = client.apiDevopsDatabases().createDatabase(DatabaseCreationRequest
                    .builder()
                    .name(dbName)
                    .tier(DatabaseTierType.serverless)
                    .cloudProvider(CloudProviderType.AWS)
                    .cloudRegion("us-east-1")
                    .keyspace(keyspace)
                    .build());
            printOK("Database id=" + serverlessDbId);
            printOK("Initializing ");
            int atempt = 0;
            DatabaseClient dbc = client.apiDevopsDatabases().database(serverlessDbId);
            while(!DatabaseStatusType.ACTIVE.equals(dbc.find().get().getStatus()) && atempt < 50) {
                System.out.print(ANSI_GREEN + "\u25a0" +ANSI_RESET); 
                waitForSeconds(5);
                atempt++;
            }
            Assert.assertEquals(DatabaseStatusType.ACTIVE, dbc.find().get().getStatus());
            printOK("DB is active");
            return serverlessDbId;
        }
    }

}