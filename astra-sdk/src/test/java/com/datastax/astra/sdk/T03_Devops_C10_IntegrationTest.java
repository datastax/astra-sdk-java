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

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(OrderAnnotation.class)
public class T03_Devops_C10_IntegrationTest extends AbstractAstraIntegrationTest {
    
    /*
    private static final String C10_DB_NAME      = "sdk_c10";
    private static final String KEYSPACE         = "sdk_ks1";
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[Astra DEVOPS Api Test Suite c10]" + ANSI_RESET);
    }
    
    public static String c10DbId;
    
    @Test
    @Order(1)
    @DisplayName("Create a C10 Database and wait for the status to be ACTIVE")
    public void should_createC10Database() {
        System.out.println(ANSI_YELLOW + "\n#1 c10 DB Creation" + ANSI_RESET);
        // Given
        ApiDevopsClient cli = new ApiDevopsClient(appToken.get());
        // Given
        Stream<DatabaseAvailableRegion> streamDb = cli.findAllAvailableRegions();
        Map <DatabaseTierType, Map<CloudProviderType,List<DatabaseAvailableRegion>>> available = cli.mapAvailableRegions(streamDb);
        Assert.assertTrue(available.containsKey(DatabaseTierType.serverless));
        Assert.assertTrue(available
                .get(DatabaseTierType.C10)
                .containsKey(CloudProviderType.AWS));
        Assert.assertTrue(available
                .get(DatabaseTierType.C10)
                .get(CloudProviderType.AWS).stream()
                .anyMatch(db -> "us-east-1".equalsIgnoreCase(db.getRegion())));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Tier `c10` for region 'aws/us-east-1' is available");
        Assert.assertFalse(cli.findDatabasesNonTerminatedByName(C10_DB_NAME).collect(Collectors.toSet()).size()>0);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Instance with name'" + C10_DB_NAME + "' does not exist.");
        // When
        
        DatabaseCreationRequest dcr = DatabaseCreationRequest
                .builder()
                .name(C10_DB_NAME)
                .tier(DatabaseTierType.C10)
                .cloudProvider(CloudProviderType.AWS)
                .cloudRegion("us-east-1")
                .keyspace(KEYSPACE)
                .username("cedrick")
                .password("cedrick1")
                .build();
       
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createDatabase(null));
        
        c10DbId = cli.createDatabase(dcr);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Creation request successful");
        
        // Then
        Assert.assertTrue(cli.databaseExist(c10DbId));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB now exists id='" + dbId + "'");
        
        Assert.assertEquals(DatabaseStatusType.PENDING, cli.findDatabaseById(c10DbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Status is [PENDING]");
        while(DatabaseStatusType.PENDING.equals(cli.findDatabaseById(c10DbId).get().getStatus())) {
            waitForSeconds(1);   
        }
        Assert.assertEquals(DatabaseStatusType.INITIALIZING, cli.findDatabaseById(c10DbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Status is [INITIALIZING] (should take about 30 min)");
        //  When
        System.out.print("Waiting for db");
        while(DatabaseStatusType.INITIALIZING.equals(cli.findDatabaseById(c10DbId).get().getStatus())) {
            System.out.print("#");
            waitForSeconds(5);
        }
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli.findDatabaseById(c10DbId).get().getStatus());
        System.out.println(ANSI_GREEN + "\n[OK]" + ANSI_RESET + " - DB Status is [ACTIVE] your are set.");
    }
    
    @Test
    @Order(2)
    public void should_park_c10() {
        System.out.println(ANSI_YELLOW + "\n#1 c10 DB Parking" + ANSI_RESET);
        // Given
        ApiDevopsClient cli = new ApiDevopsClient(appToken.get());
        cli.parkDatabase(c10DbId);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Parking command sent");
        while(DatabaseStatusType.PARKING != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Status changed to PARKING");
        while(DatabaseStatusType.PARKED != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Status changed to PARKED");
    }
    
    @Test
    @Order(3)
    public void should_unpark_cc10() {
        System.out.println(ANSI_YELLOW + "\n#1 c10 DB Unparkiing" + ANSI_RESET);
        // Given
        ApiDevopsClient cli = new ApiDevopsClient(appToken.get());
        cli.unparkDatabase(c10DbId);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - UnParking command sent");
        while(DatabaseStatusType.UNPARKING != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - c10DbId changed to UNPARKING");
        while(DatabaseStatusType.ACTIVE != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Status changed to ACTIVE");
    }
    
    @Test
    @Order(4)
    public void should_resize_c10() {
        System.out.println(ANSI_YELLOW + "\n#1 c10 DB Resizing" + ANSI_RESET);
        // Given
        ApiDevopsClient cli = new ApiDevopsClient(appToken.get());
        cli.resizeDatase(c10DbId, 2);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Resizing command sent");
        while(DatabaseStatusType.MAINTENANCE != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Status changed to MAINTENANCE");
        while(DatabaseStatusType.ACTIVE != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Status changed to ACTIVE");
    }
    
    @Test
    @Order(5)
    public void should_resetpassword_c10() {
        // Given
        ApiDevopsClient cli = new ApiDevopsClient(appToken.get());
        // (9)
        System.out.println(ANSI_YELLOW + "\n#13 - [POST] Reset a Password in a c10 is possible)" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.resetPassword(c10DbId, "token", "cedrick1"));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected exception retrieved");
        
        cli.resetPassword(c10DbId, "token", "cedrick1");
        while(DatabaseStatusType.ACTIVE != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB is back to ACTIVE");
    }
    
    @Test
    @Order(6)
    public void should_terminate_db() {
        System.out.println(ANSI_YELLOW + "\n#14 - [POST] Terminating an instance" + ANSI_RESET);
        // Given
        ApiDevopsClient cli = new ApiDevopsClient(appToken.get());
        cli.terminateDatabase(c10DbId);
        while(DatabaseStatusType.TERMINATING != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Status changed to TERMINATING");
        while(DatabaseStatusType.TERMINATED != cli.findDatabaseById(c10DbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Status changed to TERMINATED");
    }
    
    */
   
    
}
