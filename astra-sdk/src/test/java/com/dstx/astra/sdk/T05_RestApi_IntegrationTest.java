package com.dstx.astra.sdk;

import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.stargate.sdk.core.DataCenter;
import io.stargate.sdk.rest.ApiRestClient;

/**
 * DATASET
 * 
 * CREATE TABLE IF NOT EXISTS videos (
 *   genre     text,
 *   year      int,
 *   title     text,
 *   upload    timestamp,
 *   email     text,
 *   url       text,
 *   tags      set <text>,
 *   frames    list<int>,
 *   formats   frozen<map <text,text>>,
 *   PRIMARY KEY ((genre), year, title)
 * ) WITH CLUSTERING ORDER BY (year DESC, title ASC);
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class T05_RestApi_IntegrationTest extends AbstractAstraIntegrationTest {
  
    private static final String WORKING_KEYSPACE = "astra_sdk_keyspacec_test";
    private static final String WORKING_TABLE    = "videos";
    
    private static ApiRestClient clientApiRest;
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[T05_RestApi_IntegrationTest]" + ANSI_RESET);
        clientApiRest = client.apiRest();
    }
    
    @Test
    @Order(1)
    @DisplayName("Parameter validations should through IllegalArgumentException(s)")
    public void builderParams_should_not_be_empty() {
        System.out.println(ANSI_YELLOW + "\n#01 Checking required parameters " + ANSI_RESET);
        Assertions.assertAll("Required parameters",
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().databaseId(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().databaseId(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().cloudProviderRegion(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().cloudProviderRegion(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().appToken(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().appToken(null); })
        );
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Validation OK");
    }
    
    @Test
    @Order(2)
    @DisplayName("Create and delete keyspace with replicas")
    public void should_create_tmp_namespace()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#02 Working with Keyspace" + ANSI_RESET);
        if (clientApiRest.keyspace("tmp_keyspace").exist()) {
            clientApiRest.keyspace("tmp_keyspace").delete();
            int wait = 0;
            while (wait++ < 5 && clientApiRest.keyspace("tmp_keyspace").exist()) {
                Thread.sleep(1000);
                System.out.println("+ ");
            }
        }
        clientApiRest.keyspace("tmp_keyspace").createSimple(1);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
        int wait = 0;
        while (wait++ < 5 && !clientApiRest.keyspace("tmp_keyspace").exist()) {
            Thread.sleep(1000);
        }
        clientApiRest.keyspace("tmp_keyspace").delete();
        wait = 0;
        while (wait++ < 5 && clientApiRest.keyspace("tmp_keyspace").exist()) {
            Thread.sleep(1000);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Keyspace craeted");
    }
    
    @Test
    @Order(3)
    @DisplayName("Create and delete keyspace with datacenter")
    public void should_create_tmp_namespace2() throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#03 Working with Keyspaces 2" + ANSI_RESET);
        // TMP KEYSPACE
        if (clientApiRest.keyspace("tmp_keyspace2").exist()) {
            clientApiRest.keyspace("tmp_keyspace2").delete();
            int wait = 0;
            while (wait++ < 5 && clientApiRest.keyspace("tmp_keyspace2").exist()) {
                Thread.sleep(1000);
            }
        }
        clientApiRest.keyspace("tmp_keyspace2").create(new DataCenter(cloudRegion.get(), 1));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
        int wait = 0;
        while (wait++ < 5 && !clientApiRest.keyspace("tmp_keyspace2").exist()) {
            Thread.sleep(1000);
            System.out.println("+ ");
        }

        clientApiRest.keyspace("tmp_keyspace2").delete();
        wait = 0;
        while (wait++ < 5 && clientApiRest.keyspace("tmp_keyspace2").exist()) {
            Thread.sleep(1000);
            System.out.println("+ ");
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Keyspace created");
    }
    
    @Test
    @Order(4)
    @DisplayName("Create working namespace and check list")
    public void should_create_keyspace() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#04 Working with Keyspace" + ANSI_RESET);
        if (!clientApiRest.keyspace(WORKING_KEYSPACE).exist()) {
            clientApiRest.keyspace(WORKING_KEYSPACE)
                         .create(new DataCenter(cloudRegion.get(), 3));
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
            int wait = 0;
            while (wait++ < 5 && !clientApiRest.keyspace(WORKING_KEYSPACE).exist()) {
                Thread.sleep(2000);
                 System.out.print("+ ");
            }
        }
        Assertions.assertTrue(clientApiRest.keyspace(WORKING_KEYSPACE).exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Keyspace " + WORKING_KEYSPACE + "created");
    }
    
    @Test
    @Order(5)
    public void working_keyspace_should_exist() {
        System.out.println(ANSI_YELLOW + "\n#05 Keyspace should exist" + ANSI_RESET);
        Assertions.assertTrue(clientApiRest
                .keyspaceNames()
                .collect(Collectors.toSet())
                .contains(WORKING_KEYSPACE));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Keyspace found");
    }
    
    
    @Test
    @Order(6)
    public void shoud_delete_keyspace() throws InterruptedException {
        // Given
        System.out.println(ANSI_YELLOW + "\n#06 Delete a keyspace" + ANSI_RESET);
        clientApiRest.keyspace("tmp_keyspace3").createSimple(3);
        int wait = 0;
        while (wait++ < 5 && !clientApiRest.keyspace("tmp_keyspace3").exist()) {
            Thread.sleep(1000);
        }
        Assertions.assertTrue(clientApiRest.keyspace("tmp_keyspace3").exist());
        
        // When
        clientApiRest.keyspace("tmp_keyspace3").delete();
        wait = 0;
        while (wait++ < 5 && !clientApiRest.keyspace("tmp_keyspace2").exist()) {
            Thread.sleep(1000);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Keyspace deleted");
    }
    
    // CRUD ON TABLES
    @Test
    @Order(7)
    public void should_create_new_table() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#07 Create a table" + ANSI_RESET);
        Assertions.assertFalse(clientApiRest.keyspace(WORKING_KEYSPACE).table(WORKING_TABLE).exist());
        clientApiRest.keyspace(WORKING_KEYSPACE).table("videos");
    }
    
    @Test
    @Order(8)
    public void should_list_tables_names() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#08 List tables names in a keyspace" + ANSI_RESET);
        clientApiRest.keyspace(WORKING_KEYSPACE).tableNames();
    }
    
    @Test
    @Order(9)
    public void should_list_tables_definition() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#09 List tables definition in a keyspace" + ANSI_RESET);
       
    }
    
    // CRUD ON COLUMNS
    
    public void should_create_new_columns() {
    }
    
    
    // CRUD ON DATA
    
    // SEARCH IN DATA

}
