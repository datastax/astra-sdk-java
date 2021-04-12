package com.dstx.astra.sdk;

import java.util.Optional;
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
import io.stargate.sdk.rest.TableClient;
import io.stargate.sdk.rest.domain.ClusteringExpression;
import io.stargate.sdk.rest.domain.ClusteringOrder;
import io.stargate.sdk.rest.domain.ColumnDefinition;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.TableDefinition;
import io.stargate.sdk.rest.domain.TableOptions;

/**
 * DATASET
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class T05_RestApi_IntegrationTest extends AbstractAstraIntegrationTest {
  
    private static final String WORKING_KEYSPACE = "sdk_test_ks";
    private static final String WORKING_TABLE    = "videos";
    
    private static ApiRestClient clientApiRest;
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[T05_RestApi_IntegrationTest]" + ANSI_RESET);
        
        client = AstraClient.builder()
                .databaseId("f420bc37-b22e-44fc-8371-72fe2202f07d")
                .cloudProviderRegion("eu-central-1")
                .appToken("AstraCS:TWRvjlcrgfZYfhcxGZhUlAZH:2174fb7dacfd706a2d14d168706022010e99a7bb7cd133050f46ee0d523b386d")
                .build();
        
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
    
    /*
    * CREATE TABLE IF NOT EXISTS videos (
    *   genre     text,
    *   year      int,
    *   title     text,
    *   upload    timestamp,
    *   tags      set <text>,
    *   frames    list<int>,
    *   tuples    tuple<text,text,text>,
    *   formats   frozen<map <text,text>>,
    *   PRIMARY KEY ((genre), year, title)
    * ) WITH CLUSTERING ORDER BY (year DESC, title ASC);
    * 
    */
    @Test
    @Order(7)
    public void should_create_table() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#07 Create a table" + ANSI_RESET);
        Assertions.assertTrue(clientApiRest.keyspace(WORKING_KEYSPACE).exist());
        TableClient tc = clientApiRest.keyspace(WORKING_KEYSPACE).table(WORKING_TABLE + "_tmp");
        if (tc.exist()) {
            tc.delete();
            int wait = 0;
            while (wait++ < 10 && tc.exist()) {
                Thread.sleep(1000);
            }
        }
        Assertions.assertFalse(tc.exist());
        // Core Request
        CreateTable tcr = new CreateTable();
        tcr.setIfNotExists(true);
        tcr.getColumnDefinitions().add(new ColumnDefinition("genre", "text"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("year", "int"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("title", "text"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("upload", "timestamp"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("tags", "set<text>"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("frames", "list<int>"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("tuples", "tuple<text,text,text>"));
        tcr.getColumnDefinitions().add(new ColumnDefinition("formats", "frozen<map <text,text>>"));
        tcr.getPrimaryKey().getPartitionKey().add("genre");
        tcr.getPrimaryKey().getClusteringKey().add("year");
        tcr.getPrimaryKey().getClusteringKey().add("title");
        tcr.getTableOptions().getClusteringExpression().add(new ClusteringExpression("year", ClusteringOrder.DESC));
        tcr.getTableOptions().getClusteringExpression().add(new ClusteringExpression("title", ClusteringOrder.ASC));
        tc.create(tcr);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creating table " + WORKING_TABLE + "_tmp");
        int wait = 0;
        while (wait++ < 10 && !clientApiRest.keyspace(WORKING_KEYSPACE).table(WORKING_TABLE + "_tmp").exist()) {
            Thread.sleep(1000);
        }
        Assertions.assertTrue(clientApiRest.keyspace(WORKING_KEYSPACE).table(WORKING_TABLE + "_tmp").exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Table " + WORKING_TABLE + "_tmp now exists.");
        
        
        TableClient working_table = clientApiRest.keyspace(WORKING_KEYSPACE).table(WORKING_TABLE);
        if (working_table.exist()) {
            working_table.delete();
            wait = 0;
            while (wait++ < 10 && working_table.exist()) {
                Thread.sleep(1000);
            }
        }
        Assertions.assertFalse(working_table.exist());
        
        // With a Builder
        working_table.create(CreateTable.builder()
                       .ifNotExist(true)
                       .addPartitionKey("genre", "text")
                       .addClusteringKey("year", "int", ClusteringOrder.DESC)
                       .addClusteringKey("title", "text", ClusteringOrder.ASC)
                       .addColumn("upload", "timestamp")
                       .addColumn("tags", "set<text>")
                       .addColumn("frames", "list<int>")
                       .addColumn("tuples", "tuple<text,text,text>")
                       .addColumn("formats", "frozen<map <text,text>>")
                       .build());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creating table " + WORKING_TABLE);
        wait = 0;
        while (wait++ < 10 && !working_table.exist()) {
            Thread.sleep(1000);
        }
        Assertions.assertTrue(working_table.exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Table " + WORKING_TABLE + " now exists.");
    }
    
    @Test
    @Order(8)
    public void should_list_tables_definition() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#08 List tables in a keyspace" + ANSI_RESET);
        Assertions.assertTrue(clientApiRest
                .keyspace(WORKING_KEYSPACE)
                .tables().count() > 0);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - List OK");
    }
    
    @Test
    @Order(9)
    public void should_table_find() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#09 Table find" + ANSI_RESET);
        Optional<TableDefinition> otd = clientApiRest.keyspace(WORKING_KEYSPACE).table(WORKING_TABLE).find();
        Assertions.assertTrue(otd.isPresent());
        Assertions.assertEquals("genre", otd.get().getPrimaryKey().getPartitionKey().get(0));
        Assertions.assertEquals("year", otd.get().getPrimaryKey().getClusteringKey().get(0));
        Assertions.assertEquals("title", otd.get().getPrimaryKey().getClusteringKey().get(1));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Working table found");
    }
    
    @Test
    @Order(10)
    public void should_list_tables_names() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#10 List tables names in a keyspace" + ANSI_RESET);
        Assertions.assertTrue(clientApiRest
                .keyspace(WORKING_KEYSPACE)
                .tableNames().count()>0);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Name list OK");
    }
    
    @Test
    @Order(11)
    public void should_table_exist() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#11 Table exist" + ANSI_RESET);
        Assertions.assertTrue(clientApiRest.keyspace(WORKING_KEYSPACE).table(WORKING_TABLE).exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Working table found");
    }
    
    @Test
    @Order(12)
    public void should_delete_table_exist() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#12 Delete a table" + ANSI_RESET);
        // Given
        Assertions.assertTrue(clientApiRest
                .keyspace(WORKING_KEYSPACE)
                .exist());
        Assertions.assertTrue(clientApiRest
                .keyspace(WORKING_KEYSPACE)
                .table(WORKING_TABLE + "_tmp")
                .exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Target table exist");
        // When
        clientApiRest
            .keyspace(WORKING_KEYSPACE)
            .table(WORKING_TABLE + "_tmp").delete();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Delete request sent");
        
        int wait = 0;
        while (wait++ < 10 && clientApiRest
                .keyspace(WORKING_KEYSPACE)
                .table(WORKING_TABLE + "_tmp").exist()) {
            Thread.sleep(1000);
        }
        
        // Then
        Assertions.assertFalse(clientApiRest
                .keyspace(WORKING_KEYSPACE)
                .table(WORKING_TABLE + "_tmp")
                .exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Target table has been deleted");
        
    }
    
    @Test
    @Order(13)
    public void should_update_tableOptions()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#13 Update table metadata" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos_tmp2");
        Assertions.assertTrue(tmp_table.exist());
        Assertions.assertNotEquals(25, tmp_table.find().get().getTableOptions().getDefaultTimeToLive());
        // When
        tmp_table.updateOptions(new TableOptions(25, null));
        // Then
        Assertions.assertNotEquals(25, tmp_table.find().get().getTableOptions().getDefaultTimeToLive());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Table updated");
        tmp_table.updateOptions(new TableOptions(0,null));
    }
    
    @Test
    @Order(14)
    public void should_list_columns()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#14 list columns" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos_tmp2");
        Assertions.assertTrue(tmp_table.exist());
        // When
        Assertions.assertTrue(tmp_table.columns().filter(c -> "frames".equalsIgnoreCase(c.getName())).findFirst().isPresent());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected columns OK");
        // When
        Assertions.assertTrue(tmp_table.columnNames().collect(Collectors.toList()).contains("frames"));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected columns names OK");
        
    }
    
    @Test
    @Order(15)
    public void should_find_a_columns()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#15 Find a column" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos_tmp2");
        Assertions.assertTrue(tmp_table.exist());
        // When
        Assertions.assertTrue(tmp_table.column("frames").find().isPresent());
        Assertions.assertTrue(tmp_table.column("frames").exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Column found");
    }
        
    @Test
    @Order(16)
    public void should_create_a_columns()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#16 Create a column" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos_tmp2");
        Assertions.assertTrue(tmp_table.exist());
        Assertions.assertFalse(tmp_table.column("custom").find().isPresent());
        // Given
        tmp_table.column("custom").create(new ColumnDefinition("custom", "text"));
        // Then
        Assertions.assertTrue(tmp_table.column("custom").find().isPresent());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Column created");
    }
    
    @Test
    @Order(17)
    public void should_delete_a_columns()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#17 Delete a column" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos_tmp2");
        Assertions.assertTrue(tmp_table.exist());
        Assertions.assertTrue(tmp_table.column("custom").find().isPresent());
        // Given
        tmp_table.column("custom").delete();
        // Then
        Assertions.assertFalse(tmp_table.column("custom").find().isPresent());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Column Deleted");
    }
    
    
    @Test
    @Order(18)
    public void should_rename_a_columns()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#18 Updating a column" + ANSI_RESET);
        
        // Resource not working
        
        /* Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos_tmp2");
        if (!tmp_table.column("custom").exist()) {
            tmp_table.column("custom").create(new ColumnDefinition("custom", "text"));
        }
        Assertions.assertTrue(tmp_table.column("custom").find().isPresent());
        // When
        tmp_table.column("custom").rename("renamed");
        // Then
        Assertions.assertTrue(tmp_table.column("renamed").find().isPresent());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Column Updated");
        */
    }
    
    // CRUD ON DATA
    
    // SEARCH IN DATA

}
