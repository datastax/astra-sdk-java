package com.datastax.stargate.sdk.doc.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.NamespaceClient;
import com.datastax.stargate.sdk.doc.domain.CollectionDefinition;
import com.datastax.stargate.sdk.doc.test.ApiDocumentDocumentTest.Person;

/**
 * This class test the document api for namespaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiDocumentCollectionsTest implements ApiDocumentTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocumentCollectionsTest.class);
    
    // -----------------------------------------
    //         Operation on Collections
    // -----------------------------------------
    
    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    /** Tested Store. */
    protected static NamespaceClient nsClient;
    
    // -----------------------------------------
    //         Operation on Collection
    // -----------------------------------------
    
    @Test
    @Order(1)
    @DisplayName("01-Create and delete empty collection")
    public void a_should_create_empty_collection() throws InterruptedException {
        // Operations on collections
        LOGGER.info("should_create_empty_collection");
        CollectionClient cc = nsClient.collection(TEST_COLLECTION_PERSON);
        if (cc.exist()) {
            LOGGER.info("Collection aleady exists");
            cc.delete();
            LOGGER.info("Delete collection request sent");
            Thread.sleep(2000);
        }
        // Given
        Assertions.assertFalse(cc.exist());
        LOGGER.info("Collection does not exist");
        // When
        cc.create();
        LOGGER.info("Creation request sent");
        Thread.sleep(2000);
        // Then
        Assertions.assertTrue(cc.exist());
        LOGGER.info("Collection now exist");
    }

    @Test
    @Order(2)
    @DisplayName("02-Should list collections")
    public void b_should_list_collections() throws InterruptedException {
        Map<String, CollectionDefinition> collections = nsClient
                .collections()
                .collect(Collectors.toMap(CollectionDefinition::getName, Function.identity()));
        Assertions.assertTrue(collections.containsKey(TEST_COLLECTION_PERSON));
                
        Assertions.assertTrue(nsClient
                .collectionNames()
                .anyMatch(s -> TEST_COLLECTION_PERSON.equals(s)));
        LOGGER.info("Collection is available in list");
    }
    
    @Test
    @Order(3)
    @DisplayName("03-Find Collection by its name")
    public void c_should_find_collection() throws InterruptedException {
        Assertions.assertTrue(nsClient
                .collection(TEST_COLLECTION_PERSON)
                .exist());
        LOGGER.info("Collection is available find find()");
    }
    
    @Test
    @Order(4)
    @DisplayName("04-Delete a collection from its name")
    public void d_should_delete_collection() throws InterruptedException {
        LOGGER.info("should_delete_collection");
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        CollectionClient rcc = nsClient.collection(randomCollection);
        Assertions.assertFalse(rcc.exist());
        // When
        rcc.create();
        Thread.sleep(2000);
        // Then
        Assertions.assertTrue(rcc.exist());
        // When
        rcc.delete();
        Thread.sleep(2000);
        // Then
        Assertions.assertFalse(rcc.exist());
        LOGGER.info("Collection deleted");
    }
    
    @Test
    @Order(5)
    @DisplayName("05-Assign a Json Schema")
    public void e_should_set_schema() {
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        CollectionClient cc = nsClient.collection(randomCollection);
        cc.create();
        Assertions.assertTrue(cc.exist());
        // Then I can add a person with negative age
        cc.document("doc1").upsert(new Person("first", "last", -20, null));
        // When Assign Schema
        Assertions.assertFalse(cc.getSchema().isPresent());
        cc.setSchema(TEST_JSON_SCHEMA);
        // Then a schema is present
        Assertions.assertTrue(cc.getSchema().isPresent());
        // And validation should be enabled
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            cc.document("doc1").upsert(new Person("first", "last", -20, null));
        });
        // Clean up
        cc.delete();
        Assertions.assertFalse(cc.exist());
    }
    
    @Test
    @Order(6)
    @DisplayName("06-Insert as a BACTH")
    public void f_should_insert_batch() 
    throws InterruptedException {
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        CollectionClient cc = nsClient.collection(randomCollection);
        cc.create();
        Assertions.assertTrue(cc.exist());
        List <Person> persons = new ArrayList<>();
        for(int i=0;i<99;i++) {
            persons.add(new Person("" +i,"" +i,i,null));
        }
        List<String> ids = cc.batchInsert(persons);
        Thread.sleep(2000);
        Assertions.assertEquals(99, ids.size());
        
        List<String> ids2 = cc.batchInsert(persons, "lastname");
        Thread.sleep(2000);
        Assertions.assertEquals(99, ids2.size());
        Assertions.assertTrue(ids2.contains("16"));
    }

}
