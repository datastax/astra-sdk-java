package com.datastax.stargate.sdk.rest.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.rest.KeyClient;
import com.datastax.stargate.sdk.rest.KeyspaceClient;
import com.datastax.stargate.sdk.rest.TableClient;
import com.datastax.stargate.sdk.rest.domain.Ordering;
import com.datastax.stargate.sdk.rest.domain.QueryWithKey;
import com.datastax.stargate.sdk.rest.domain.RowResultPage;
import com.datastax.stargate.sdk.rest.domain.SearchTableQuery;
import com.datastax.stargate.sdk.rest.domain.SortField;
import com.datastax.stargate.sdk.rest.test.domain.Video;
import com.datastax.stargate.sdk.rest.test.domain.VideoRowMapper;

/**
 * Test resources related to DATA.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiDataRecordTest implements ApiDataTest {
    
    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    /** Tested Store. */
    protected static KeyspaceClient workingKeyspace;
    
    /** Tested Store. */
    protected static TableClient videoTable;
    
    @Test
    @Order(1)
    @DisplayName("01-should-add-a-row")
    public void a_should_add_row()
    throws InterruptedException {
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
        data.put("upload", 1618411879135L);
        videoTable.upsert(data);
        RowResultPage rrp = videoTable
                .key("Sci-Fi",1990)
                .findPage(QueryWithKey.builder().build());
        Assertions.assertEquals(1, rrp.getResults().size());
        
        // Update a record
        data.put("title", "title2");
        videoTable.upsert(data);
    }
    
    @Test
    @Order(2)
    @DisplayName("02-should-delete-a-row")
    public void b_should_delete_row()
    throws InterruptedException {
        // Given
        Assertions.assertTrue(videoTable.exist());
        KeyClient record = videoTable.key("Sci-Fi", 1990);
        RowResultPage rrp = record.findPage(QueryWithKey.builder().build());
        Assertions.assertTrue(rrp.getResults().size() > 0);
        
        record.delete();
        rrp = record.findPage(QueryWithKey.builder().build());
        Assertions.assertTrue(rrp.getResults().size() == 0);
    }
    
    @Test
    @Order(3)
    @DisplayName("03-should-update-a-row")
    @SuppressWarnings("unchecked")
    public void c_should_update_row()
    throws InterruptedException {
        // Given
        Assertions.assertTrue(videoTable.exist());
        Map<String, Object> data = new HashMap<>();
        data.put("genre", "Sci-Fi");
        data.put("year", 1990);
        data.put("title", "line_update");
        data.put("upload", 1618411879135L);
        videoTable.upsert(data);
        
        // When updating just a value
        Map<String, Object> update = new HashMap<>();
        update.put("upload", 1618411879130L);
        videoTable.key("Sci-Fi", 1990, "line_update").update(update);
        
        // Then
        RowResultPage rrp = videoTable
                .key("Sci-Fi",1990, "line_update")
                .findPage(QueryWithKey.builder().build());
        Assertions.assertTrue(rrp.getResults().size() == 1);
        Map<String, Object > map = (Map<String, Object>) rrp.getResults().get(0).get("upload");
        Assertions.assertEquals(130000000, map.get("nano"));
    }
    
   
    @Test
    @Order(4)
    @DisplayName("04-should-replace-a-row")
    @SuppressWarnings("unchecked")
    public void d_should_replace_row()
    throws InterruptedException {
        // Given
        Assertions.assertTrue(videoTable.exist());
        Map<String, Object> data = new HashMap<>();
        data.put("genre", "Sci-Fi");
        data.put("year", 1990);
        data.put("title", "line_replace");
        data.put("upload", 1618411879135L);
        videoTable.upsert(data);
        // When updating just a value
        Map<String, Object> replace = new HashMap<>();
        replace.put("upload", 1618411879130L);
        videoTable.key("Sci-Fi", 1990, "line_update").replace(replace);
        // Then
        RowResultPage rrp = videoTable
                .key("Sci-Fi",1990, "line_update")
                .findPage(QueryWithKey.builder().build());
        Assertions.assertTrue(rrp.getResults().size() == 1);
        Map<String, Object > map = (Map<String, Object>) rrp.getResults().get(0).get("upload");
        Assertions.assertEquals(130000000, map.get("nano"));
    }
    
    @Test
    @Order(5)
    @DisplayName("05-should-get-row-withpk")
    public void e_should_get_rows_pk()
    throws InterruptedException {
        // Given
        Assertions.assertTrue(videoTable.exist());
        RowResultPage rrp = videoTable.key("Sci-Fi",1990)
                .findPage(QueryWithKey.builder()
                        .addSortedField("year", Ordering.ASC)
                        .build());
        Assertions.assertEquals(2, rrp.getResults().size());
    }
    
    @Test
    @Order(6)
    @DisplayName("06-should-get-row-mapper")
    public void f_should_get_rows_pk_mapper()
    throws InterruptedException {
        // Given
        Assertions.assertTrue(videoTable.exist());
        List<Video> result = videoTable.key("Sci-Fi",1990)
            .findPage(QueryWithKey.builder()
                .addSortedField("year", Ordering.ASC)
                .build(), new VideoRowMapper())
                .getResults();
        Assertions.assertEquals(2, result.size());
    }
    
    @Test
    @Order(7)
    @DisplayName("07-should-search-a-table")
    public void g_should_rsearch_table()
    throws InterruptedException {
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
               .sortBy(new SortField("year", Ordering.ASC))
               .build());
        
        Assertions.assertEquals(6, res3.getResults().size());
        
    }
    
    
}
