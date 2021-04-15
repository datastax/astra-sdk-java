package com.dstx.astra.sdk;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.datastax.astra.dto.Video;
import org.datastax.astra.dto.VideoRowMapper;
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
import io.stargate.sdk.rest.domain.ColumnDefinition;
import io.stargate.sdk.rest.domain.CreateIndex;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.IndexDefinition;
import io.stargate.sdk.rest.domain.Ordering;
import io.stargate.sdk.rest.domain.QueryWithKey;
import io.stargate.sdk.rest.domain.Row;
import io.stargate.sdk.rest.domain.RowResultPage;
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
                .databaseId("8af224f7-1922-491f-a83b-2ebf294ca431")
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
        tcr.getTableOptions().getClusteringExpression().add(new ClusteringExpression("year", Ordering.DESC));
        tcr.getTableOptions().getClusteringExpression().add(new ClusteringExpression("title", Ordering.ASC));
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
                       .addClusteringKey("year", "int", Ordering.DESC)
                       .addClusteringKey("title", "text", Ordering.ASC)
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
    public void should_rename_clustering_columns()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#18 Updating a column" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos_tmp");
        Assertions.assertTrue(tmp_table.exist());
        Assertions.assertTrue(tmp_table.column("title").exist());
        Assertions.assertTrue(tmp_table.find().get().getPrimaryKey().getClusteringKey().contains("title"));
        // When
        tmp_table.column("title").rename("new_title");
        // Then
        Assertions.assertTrue(tmp_table.column("new_title").find().isPresent());
        // Put back original name
        tmp_table.column("new_title").rename("title");
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Column Updated");
    }
    
    @Test
    @Order(19)
    public void should_create_secondaryIndex()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#19 Create Secondary Index" + ANSI_RESET);
        // Given
        TableClient tableVideo = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos");
        Assertions.assertTrue(tableVideo.exist());
        Assertions.assertFalse(tableVideo.index("idx_test").exist());
        // When
        tableVideo.index("idx_test").create(
                CreateIndex.builder().column("title").build());
        // Then
        Assertions.assertTrue(tableVideo.index("idx_test").exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Now exist");
        IndexDefinition idxDef = tableVideo.index("idx_test").find().get();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Index type " + idxDef.getKind());
    }
    
    @Test
    @Order(20)
    public void should_delete_secondaryIndex()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#20 Delete Secondary Index" + ANSI_RESET);
        // Given
        TableClient tableVideo = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos");
        Assertions.assertTrue(tableVideo.exist());
        Assertions.assertTrue(tableVideo.index("idx_test").exist());
        // When
        tableVideo.index("idx_test").delete();
        // Then
        Assertions.assertFalse(tableVideo.index("idx_test").exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Index has been deleted");
    }
    
    // ==============================================================
    // ========================= DATA ===============================
    // ==============================================================
    
    // Still need to implement addData to automate this test but good results
    @Test
    @Order(21)
    public void should_add_row()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#21 Should add row" + ANSI_RESET);
        // Given
        TableClient tableVideo = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos");
        Assertions.assertTrue(tableVideo.exist());
        
        Map<String, Object> data = new HashMap<>();
        data.put("genre", "Sci-Fi");
        data.put("year", 1990);
        data.put("title", "Test Line");
        data.put("frames", "[ 1, 2, 3 ]");
        data.put("formats", "{ '2020':'good', '2019':'okay' }");
        data.put("tags", "{ 'Emma', 'The Color Purple' }");
        data.put("tuples", "( 'France', '2016-01-01', '2020-02-02' )");
        data.put("upload", 1618411879135L);
        tableVideo.upsert(data);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Line added");
        
        /*
        {
            "genre": "Sci-Fi",
            "year": 1990,
            "title":"Yes Cedrick Insert Rows",
            "formats": "{ '2020':'good', '2019':'okay' }",
            "frames": "[ '1', '2', '3' ]",
            "tags": "{ 'Emma', 'The Color Purple' }",
            "tuples": "( 'France', '2016-01-01', '2020-02-02' )",
            "upload": 1618411879135
          }
         */
        
        
    }
    
    
    @Test
    @Order(22)
    public void should_delete_row()
    throws InterruptedException {
        
    }
    
    @Test
    @Order(23)
    public void should_update_row()
    throws InterruptedException {
        
    }
    
    @Test
    @Order(24)
    public void should_replace_row()
    throws InterruptedException {
        
    }
        
    // Still need to implement addData to automate this test but good results
    @Test
    @Order(25)
    public void should_get_rows_pk()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#19 Retrieves row from primaryKey" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos");
        Assertions.assertTrue(tmp_table.exist());
        
        RowResultPage rrp = tmp_table.key("Action","2021")
                .find(QueryWithKey.builder()
                        .addSortedField("year", Ordering.ASC)
                        .build());
        for (Row row : rrp.getResults()) {
            System.out.println(row.get("title").toString() + " -- " + row.get("year").toString());
        }
    }
    
    @Test
    @Order(26)
    public void should_get_rows_pk_mapper()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#20 Retrieve row from primaryKey with RowMapper" + ANSI_RESET);
        // Given
        TableClient tmp_table = clientApiRest.keyspace(WORKING_KEYSPACE).table("videos");
        Assertions.assertTrue(tmp_table.exist());
        
        tmp_table.key("Action", "2021")
            .find(QueryWithKey.builder()
                .addSortedField("year", Ordering.ASC)
                .build(), new VideoRowMapper())
       
                .getResults()
                .stream()
                .map(Video::getGenre)
                .forEach(System.out::println);
    }
    
    @Test
    @Order(24)
    public void should_rsearch_table()
    throws InterruptedException {
        
    }
    
   
}
