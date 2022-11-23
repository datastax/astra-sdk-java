package io.stargate.sdk.test.rest;

import io.stargate.sdk.core.Ordering;
import io.stargate.sdk.core.Sort;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.rest.KeyClient;
import io.stargate.sdk.rest.KeyspaceClient;
import io.stargate.sdk.rest.TableClient;
import io.stargate.sdk.rest.domain.QueryWithKey;
import io.stargate.sdk.rest.domain.RowResultPage;
import io.stargate.sdk.rest.domain.SearchTableQuery;
import io.stargate.sdk.test.rest.domain.Video;
import io.stargate.sdk.test.rest.domain.VideoRowMapper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test resources related to DATA.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractRestClientRecordTest implements TestRestClientConstants {
    
    /** Tested Store. */
    protected static StargateRestApiClient stargateRestApiClient;
    
    /** Tested Store. */
    protected static KeyspaceClient workingKeyspace;
    
    /** Tested Store. */
    protected static TableClient videoTable;
    
    /**
     * Test.
     */
    @Test
    @Order(1)
    @DisplayName("01-should-add-a-row")
    public void addRowTest() {
        Assertions.assertTrue(videoTable.exist());
        // Insert a record
        Map<String, Object> data = new HashMap<>();
        data.put("genre", "Sci-Fi");
        data.put("year", 1990);
        data.put("title", "Test Line");
        data.put("frames", "[ 1, 2, 3 ]");
        data.put("formats", "{ '2020':'good', '2019':'okay' }");
        data.put("tags", "{ 'Emma', 'The Color Purple' }");
        data.put("tuples", "( 'France', '2016-01-01', '2020-02-02' )");
        data.put("upload", Instant.now());
        videoTable.upsert(data);
        RowResultPage rrp = videoTable
                .key("Sci-Fi",1990)
                .findPage(QueryWithKey.builder().build());
        Assertions.assertEquals(1, rrp.getResults().size());
        
        // Update a record
        data.put("title", "title2");
        videoTable.upsert(data);
    }
    
    /**
     * Test.
     */
    @Test
    @Order(2)
    @DisplayName("02-should-delete-a-row")
    public void deleteRowTest()  {
        // Given
        Assertions.assertTrue(videoTable.exist());
        KeyClient record = videoTable.key("Sci-Fi", 1990);
        RowResultPage rrp = record.findPage(QueryWithKey.builder().build());
        Assertions.assertTrue(rrp.getResults().size() > 0);
        
        record.delete();
        rrp = record.findPage(QueryWithKey.builder().build());
        Assertions.assertEquals(0, rrp.getResults().size());
    }
    
    /**
     * Test.
     */
    @Test
    @Order(3)
    @DisplayName("03-should-update-a-row")
    public void updateRowTest(){
        // Given
        Assertions.assertTrue(videoTable.exist());
        Map<String, Object> data = new HashMap<>();
        data.put("genre", "Sci-Fi");
        data.put("year", 1990);
        data.put("title", "line_update");
        data.put("upload", Instant.now());
        videoTable.upsert(data);
        
        // When updating just a value
        Map<String, Object> update = new HashMap<>();
        update.put("upload", Instant.now());
        videoTable.key("Sci-Fi", 1990, "line_update").update(update);
        
        /* Then
        RowResultPage rrp = videoTable
                .key("Sci-Fi",1990, "line_update")
                .findPage(QueryWithKey.builder().build());
        Assertions.assertTrue(rrp.getResults().size() == 1);
        // When you read a long you get back a String
        Map<String, Object > map = (Map<String, Object>) rrp.getResults().get(0);
        Assertions.assertEquals("line_update", map.get("title"));
        Assertions.assertEquals(1990, map.get("year"));*/
    }
    
   
    /**
     * Test.
     */
    @Test
    @Order(4)
    @DisplayName("04-should-replace-a-row")
    public void replaceRowTest() {
        // Given
        Assertions.assertTrue(videoTable.exist());
        Map<String, Object> data = new HashMap<>();
        data.put("genre", "Sci-Fi");
        data.put("year", 1990);
        data.put("title", "line_replace");
        data.put("upload", Instant.now());
        videoTable.upsert(data);
        // When updating just a value
        Map<String, Object> replace = new HashMap<>();
        replace.put("upload", Instant.now());
        videoTable.key("Sci-Fi", 1990, "line_update").replace(replace);
        // Then
        // Disabled as BUG for Tuples
        // RowResultPage rrp = videoTable
        //        .key("Sci-Fi",1990, "line_update")
        //        .findPage(QueryWithKey.builder().build());
        //Assertions.assertTrue(rrp.getResults().size() == 1);
        //Map<String, Object > map = (Map<String, Object>) rrp.getResults().get(0);
        //Assertions.assertEquals("line_update", map.get("title"));
        //Assertions.assertEquals(1990, map.get("year"));
    }
    
    /**
     * Test.
     * Disabled as BUG for Tuples
    */
    public void getTowsPk()  {
        // Given
        Assertions.assertTrue(videoTable.exist());
        RowResultPage rrp = videoTable.key("Sci-Fi",1990)
                .findPage(QueryWithKey.builder()
                        .addSortedField("year", Ordering.ASC)
                        .build());
        Assertions.assertEquals(2, rrp.getResults().size());
    }

    /**
     * Test.
     * Disabled as BUG for Tuples
    */
    public void getRowsPkMapperTest() {
        // Given
        Assertions.assertTrue(videoTable.exist());
        // When
        List<Video> result = videoTable.key("Sci-Fi",1990)
            .findPage(QueryWithKey.builder()
                .addSortedField("year", Ordering.ASC)
                .build(), new VideoRowMapper())
                .getResults();
        // Then
        Assertions.assertEquals(2, result.size());
    }
    
    /**
     * Test.
     */
    @Test
    @Order(7)
    @DisplayName("07-should-search-a-table")
    public void searchTableTest() {
        Assertions.assertTrue(videoTable.exist());
        
        // Empty table, delete per partition
        videoTable.key("Sci-Fi").delete();
        videoTable.key("genre1").delete();
        videoTable.key("genre2").delete();
        
        // 3 rows with genre1 1990
        Map<String, Object> data = new HashMap<>();
        data.put("genre", "genre1");
        data.put("year", 1990);
        data.put("title", "line1");videoTable.upsert(data);
        data.put("title", "line2");videoTable.upsert(data);
        data.put("title", "line3");videoTable.upsert(data);
        // 3 rows with Search 1991
        data.put("year", 1991);
        data.put("title", "line4");videoTable.upsert(data);
        data.put("title", "line5");videoTable.upsert(data);
        data.put("title", "line6");videoTable.upsert(data);
        // 3 rows with genre2 1990
        data.put("genre", "genre2");
        data.put("title", "line7");videoTable.upsert(data);
        data.put("title", "line8");videoTable.upsert(data);
        data.put("title", "line9");videoTable.upsert(data);
        
        // Search 
        RowResultPage res1 = videoTable.search(
                SearchTableQuery.builder()
                          .where("genre").isEqualsTo("genre1")
                          .withReturnedFields("title", "year")
                          .build());
        Assertions.assertEquals(6, res1.getResults().size());
       
        RowResultPage res2 = videoTable.search(
                SearchTableQuery.builder()
                          .where("genre").isEqualsTo("genre2")
                          .withReturnedFields("title", "year")
                          .build());
        Assertions.assertEquals(3, res2.getResults().size());
        
        // This req would need allow filtering
        RowResultPage res3 = videoTable.search(SearchTableQuery.builder()
               .select("title", "year")
               .where("genre").isEqualsTo("genre1")
               .where("year").isGreaterThan(1989)
               .sortBy(new Sort("year", Ordering.ASC))
               .build());
        
        Assertions.assertEquals(6, res3.getResults().size());
        
    }
    
    
}
