package com.datastax.stargate.sdk.rest.test;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.rest.KeyspaceClient;
import com.datastax.stargate.sdk.rest.TableClient;
import com.datastax.stargate.sdk.rest.domain.ClusteringExpression;
import com.datastax.stargate.sdk.rest.domain.ColumnDefinition;
import com.datastax.stargate.sdk.rest.domain.CreateIndex;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.IndexDefinition;
import com.datastax.stargate.sdk.rest.domain.Ordering;
import com.datastax.stargate.sdk.rest.domain.TableDefinition;
import com.datastax.stargate.sdk.rest.domain.TableOptions;

/**
 * Test resources related to DATA.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiDataTableTest implements ApiDataTest {
    
    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    /** Tested Store. */
    protected static KeyspaceClient ksClient;
    
    /**
     * Helper to delete table before a test.
     *
     * @param tableName
     *      tableName
     * @throws InterruptedException
     *      error
     */
    protected void _deleteTableIfExist(String tableName) 
    throws InterruptedException {
        TableClient tc = ksClient.table(tableName);
        if (tc.exist()) tc.delete();
        int wait = 0;
        while (wait++ < 10 && tc.exist()) {
            Thread.sleep(1000);
        }
        Assertions.assertFalse(tc.exist());
    }
    
    /*
     * Create a table videos
     * 
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
    @Order(1)
    @DisplayName("01-should-create-a-table")
    public void a_should_create_a_table() 
    throws InterruptedException { 
        // Given
        _deleteTableIfExist(TEST_TABLE_TMP);
        
        TableClient tableTmp = ksClient.table(TEST_TABLE_TMP);
        CreateTable tcr = new CreateTable();
        tcr.setName(TEST_TABLE_TMP);
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
        Assertions.assertFalse(tableTmp.exist());
        
        // When
        tableTmp.create(tcr);
        
        // Then
        int wait = 0;
        while (wait++ < 10 && !tableTmp.exist()) Thread.sleep(1000);
        Assertions.assertTrue(tableTmp.exist());
        
        // When
        tableTmp.delete();
        wait = 0;
        while (wait++ < 10 && tableTmp.exist()) Thread.sleep(1000);
        Assertions.assertFalse(tableTmp.exist());
        
        // Same, using the BUILDER and target table
        TableClient tableVideos = ksClient.table(TEST_TABLE);
        tableVideos.create(CreateTable.builder()
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
        // Then
        wait = 0;
        while (wait++ < 10 && !tableVideos.exist()) Thread.sleep(1000);
        Assertions.assertTrue(tableVideos.exist());
    }
   
    
    /**
     * Should list tables
     */
    @Test
    @Order(2)
    @DisplayName("02-should list table names")
    public void b_should_list_tables_names() {
        Assertions.assertTrue(ksClient
                .tableNames()
                .collect(Collectors.toSet())
                .contains(TEST_TABLE));
    }
    
    
    /**
     * List table definitions
     */
    @Test
    @Order(3)
    @DisplayName("03-should list table definitions")
    public void c_should_list_tables_definitions() 
    throws InterruptedException {
        Map<String, TableDefinition > mapOfTables = ksClient.tablesAsMap();
        Assertions.assertTrue(mapOfTables.containsKey(TEST_TABLE));
        Assertions.assertFalse(mapOfTables.get(TEST_TABLE).getColumnDefinitions().isEmpty());
    }
    
    /**
     * Find table by its name
     */
    @Test
    @Order(4)
    @DisplayName("04-Find a table by its name")
    public void d_should_table_find() 
    throws InterruptedException {
        // If not present no errors but empty
        Assertions.assertFalse(ksClient.table("invalid").find().isPresent());
        // If present let us go
        Optional<TableDefinition> otd = ksClient.table(TEST_TABLE).find();
        Assertions.assertTrue(otd.isPresent());
        Assertions.assertEquals("genre", otd.get().getPrimaryKey().getPartitionKey().get(0));
        Assertions.assertEquals("year", otd.get().getPrimaryKey().getClusteringKey().get(0));
        Assertions.assertEquals("title", otd.get().getPrimaryKey().getClusteringKey().get(1));
    }
    
    /**
     * Find table by its name
     */
    @Test
    @Order(5)
    @DisplayName("05-Check if table exists")
    public void e_should_table_exist() 
    throws InterruptedException {
        Assertions.assertTrue(ksClient.table(TEST_TABLE).exist());
        Assertions.assertFalse(ksClient.table("invalid").exist());
    }
    
    @Test
    @Order(6)
    @DisplayName("06-Should delete table if exists")
    public void f_should_delete_table_exist() 
    throws InterruptedException {
        TableClient tc = ksClient.table(TEST_TABLE_TMP);
        CreateTable tcr = new CreateTable();
        tcr.setName(TEST_TABLE_TMP);
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
        int wait = 0;
        while (wait++ < 10 && !tc.exist()) Thread.sleep(1000);
        Assertions.assertTrue(tc.exist());
        // When
        tc.delete();
        wait = 0;
        while (wait++ < 10 && tc.exist()) Thread.sleep(1000);
        Assertions.assertFalse(tc.exist());
    }
        
    @Test
    @Order(7)
    @DisplayName("07-Should update table TTL")
    public void g_should_update_tableOptions()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        Assertions.assertNotEquals(25, videoTable.find().get().getTableOptions().getDefaultTimeToLive());
        // When
        videoTable.updateOptions(new TableOptions(25, null));
        // Then
        Assertions.assertNotEquals(25, videoTable.find().get().getTableOptions().getDefaultTimeToLive());
        videoTable.updateOptions(new TableOptions(0,null));
    }
    
    @Test
    @Order(8)
    @DisplayName("08-Should list columns")
    public void h_should_list_columns()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        // When
        Assertions.assertTrue(videoTable
                .columns()
                .filter(c -> "frames".equalsIgnoreCase(
                        c.getName())).findFirst().isPresent());
        // When
        Assertions.assertTrue(videoTable
                .columnNames().collect(Collectors.toList())
                .contains("frames"));
    }
    
    @Test
    @Order(9)
    @DisplayName("08-Should find a column")
    public void i_should_find_a_columns()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        // When
        Assertions.assertTrue(videoTable.column("frames").find().isPresent());
        Assertions.assertTrue(videoTable.column("frames").exist());
    }
        
    @Test
    @Order(10)
    @DisplayName("10-Should create a columns")
    public void j_should_create_a_columns()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        Assertions.assertFalse(videoTable.column("custom").find().isPresent());
        // Given
        videoTable.column("custom").create(new ColumnDefinition("custom", "text"));
        // Then
        Assertions.assertTrue(videoTable.column("custom").find().isPresent());
    }
    
    @Test
    @Order(11)
    @DisplayName("11-Should delete a columns")
    public void k_should_delete_a_columns()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        Assertions.assertTrue(videoTable.column("custom").find().isPresent());
        // Given
        videoTable.column("custom").delete();
        // Then
        Assertions.assertFalse(videoTable.column("custom").find().isPresent());
    }
    
    @Test
    @Order(12)
    @DisplayName("12-Should rename a clustering columns")
    public void l_should_rename_clustering_columns()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        Assertions.assertTrue(videoTable.column("title").exist());
        Assertions.assertTrue(videoTable.find().get().getPrimaryKey().getClusteringKey().contains("title"));
        // When
        videoTable.column("title").rename("new_title");
        Thread.sleep(500);
        // Then
        Assertions.assertTrue(videoTable.column("new_title").find().isPresent());
        // Put back original name
        videoTable.column("new_title").rename("title");
    }
    
    @Test
    @Order(13)
    @DisplayName("13-Should create 2nd index")
    public void m_should_create_secondaryIndex()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        Assertions.assertFalse(videoTable.index("idx_test").exist());
        // When
        videoTable.index("idx_test").create(
                CreateIndex.builder().column("title").build());
        Thread.sleep(500);
        // Then
        Assertions.assertTrue(videoTable.index("idx_test").exist());
        IndexDefinition idxDef = videoTable.index("idx_test").find().get();
        Assertions.assertNotNull(idxDef);
    }
    
    @Test
    @Order(14)
    @DisplayName("14-Should delete a 2nd index")
    public void n_should_delete_secondaryIndex()
    throws InterruptedException {
        // Given
        TableClient videoTable = ksClient.table(TEST_TABLE);
        Assertions.assertTrue(videoTable.exist());
        Assertions.assertTrue(videoTable.index("idx_test").exist());
        // When
        videoTable.index("idx_test").delete();
        // Then
        Assertions.assertFalse(videoTable.index("idx_test").exist());
    }
    
}
