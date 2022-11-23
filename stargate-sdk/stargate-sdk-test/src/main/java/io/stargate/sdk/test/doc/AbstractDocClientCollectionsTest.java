package io.stargate.sdk.test.doc;


import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.doc.domain.CollectionDefinition;
import io.stargate.sdk.test.doc.domain.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class test the document api for namespaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractDocClientCollectionsTest implements TestDocClientConstants {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDocClientCollectionsTest.class);
    
    // -----------------------------------------
    //         Operation on Collections
    // -----------------------------------------

    /** Tested Store. */
    protected static StargateDocumentApiClient stargateDocumentApiClient;

    /** Tested Store. */
    protected static NamespaceClient nsClient;
    
    // -----------------------------------------
    //         Operation on Collection
    // -----------------------------------------
    
    /**
     * Test.
     * 
     * @throws InterruptedException
     *      error
     */
    @Test
    @Order(1)
    @DisplayName("01-Create and delete empty collection")
    public void a_should_create_empty_collection() throws InterruptedException {
        // Operations on collections
        LOGGER.info("should_create_empty_collection");
        CollectionClient cc = nsClient.collection(TEST_COLLECTION_PERSON);
        if (cc.exist()) {
            LOGGER.info("Collection already exists");
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
    public void b_should_list_collections() {
        Map<String, CollectionDefinition> collections = nsClient
                .collections()
                .collect(Collectors.toMap(CollectionDefinition::getName, Function.identity()));
        Assertions.assertTrue(collections.containsKey(TEST_COLLECTION_PERSON));
                
        Assertions.assertTrue(nsClient
                .collectionNames()
                .anyMatch(TEST_COLLECTION_PERSON::equals));
        LOGGER.info("Collection is available in list");
    }
    
    /**
     * Test.
     */
    @Test
    @Order(3)
    @DisplayName("03-Find Collection by its name")
    public void c_should_find_collection() {
        Assertions.assertTrue(nsClient
                .collection(TEST_COLLECTION_PERSON)
                .exist());
        LOGGER.info("Collection is available find find()");
    }
    
    /**
     * Test.
     * 
     * @throws InterruptedException
     *      error
     */
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

    /**
     * Test.
     */
    @Test
    @Order(5)
    @DisplayName("06-Insert as a BATCH")
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

    
    /**
     * Test.
     */
    @Test
    @Order(6)
    @DisplayName("05-Assign a Json Schema")
    public void e_should_set_schema() {
        LOGGER.info("should_set_schema");
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        CollectionClient cc = nsClient.collection(randomCollection);
        cc.create();
        Assertions.assertTrue(cc.exist());
        // Then I can add a person with negative age
        cc.document("doc1").upsert(new Person("first", "last", 20, null));
        // When Assign Schema
        Assertions.assertFalse(cc.getSchema().isPresent());
        cc.setSchema(TEST_JSON_SCHEMA);
        // Then a schema is present
        Assertions.assertTrue(cc.getSchema().isPresent());
        // And validation should be enabled
        LOGGER.info("negative_should_rise_error");
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                cc.document("doc1")
                  .upsert(new Person("first", "last", -20, null)));
        // Clean up
        cc.delete();
        Assertions.assertFalse(cc.exist());
    }
    


}
