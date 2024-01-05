package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.SelectQuery;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.exception.NamespaceNotFoundException;
import jnr.ffi.annotations.In;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.stargate.sdk.json.domain.SimilarityMetric.cosine;

/**
 * Once upon a time in the vector database wonderland.
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AstraDBTestSuiteIT {

    /**
     * Test Constants
     */
    static final String TEST_DBNAME = "test_java_astra_db_client";
    static final String TEST_COLLECTION_NAME = "collection_simple";
    static final String TEST_COLLECTION_VECTOR = "collection_vector";

    /**
     * Test Environment
     */
    static AstraEnvironment targetEnvironment = AstraEnvironment.PROD;
    static CloudProviderType targetCloud = AstraDBAdmin.FREE_TIER_CLOUD;
    static String targetRegion = AstraDBAdmin.FREE_TIER_CLOUD_REGION;
    static String astraToken;

    /**
     * Shared working objects
     */
    static AstraDBAdmin astraDbAdmin;
    static AstraDB astraDb;
    static UUID databaseId;
    static AstraDBCollection collectionSimple;
    static AstraDBCollection collectionVector;
    static AstraDBRepository<Product> productRepositoryVector;
    static AstraDBRepository<Product> productRepositorySimple;

    /**
     * Bean to be used for the test suite
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Product {
        @JsonProperty("product_name")
        private String name;
        @JsonProperty("product_price")
        private Double price;
    }

    private static void setupTestSuiteForDevelopment() {
        targetEnvironment = AstraEnvironment.DEV;
        astraToken        = "AstraCS:ZiWfNzYJtUGszRuGyyTjFIXU:2c5a21a4623c6ee688d4bca4b8e55a269aa3ee864fcd16b26b7f9a82ca57b999";
        targetCloud       = CloudProviderType.GCP;
        targetRegion      = "europe-west4";
        log.info("Environment Setup for Development");
    }

    private void setupTestSuiteForProduction() {
        log.info("Environment Setup for PRODUCTION");
        targetEnvironment = AstraEnvironment.PROD;
        astraToken        = "AstraCS:iLPiNPxSSIdefoRdkTWCfWXt:2b360d096e0e6cb732371925ffcc6485541ff78067759a2a1130390e231c2c7a";
        targetCloud       = CloudProviderType.GCP;
        targetRegion      = "us-east1";
    }

    @BeforeAll
    @DisplayName("00. Connect to Astra")
    public static void setupAndConnectToAstra() {
        // Given
        setupTestSuiteForDevelopment();
        //setupTestSuiteForProduction();
        Assertions.assertNotNull(astraToken);
        Assertions.assertNotNull(targetEnvironment);
        // When
        astraDbAdmin = new AstraDBAdmin(astraToken, targetEnvironment);
        // When
        Assertions.assertNotNull(astraDbAdmin.getToken());
    }

    // ------------------------------------
    // ----------- Databases --------------
    // ------------------------------------

    @Test
    @Order(1)
    @DisplayName("01. Create a database")
    public void shouldCreateDatabase() {
        // Given
        Assertions.assertNotNull(targetCloud);
        Assertions.assertNotNull(targetRegion);
        // When
        databaseId = astraDbAdmin.createDatabase(TEST_DBNAME, targetCloud, targetRegion);
        // Then
        Assertions.assertNotNull(databaseId);
        Assertions.assertTrue(astraDbAdmin.isDatabaseExists(TEST_DBNAME));
    }

    @Test
    @Order(2)
    @DisplayName("02. Connect to a database")
    public void shouldConnectToDatabase() {
        if (databaseId== null) shouldCreateDatabase();

        // ---- Connect From Admin ----

        // Given
        Assertions.assertNotNull(databaseId);
        Assertions.assertTrue(astraDbAdmin.isDatabaseExists(TEST_DBNAME));
        // When
        astraDb = astraDbAdmin.database(databaseId);
        Assertions.assertNotNull(astraDb);
        // When
        astraDb = astraDbAdmin.database(TEST_DBNAME);
        Assertions.assertNotNull(astraDb);

        // ---- Connect with constructor ----

        // Given
        Assertions.assertNotNull(astraDb.getApiEndpoint());
        Assertions.assertNotNull(astraDbAdmin.getToken());
        // When
        AstraDB astraDb2 = new AstraDB(astraDbAdmin.getToken(), astraDb.getApiEndpoint());
        // Then
        Assertions.assertNotNull(astraDb2);
        Assertions.assertNotNull(astraDb2.findAllCollections());
        // When initializing with a keyspace
        AstraDB astraDb3 = new AstraDB(astraDbAdmin.getToken(), astraDb.getApiEndpoint(), AstraDBAdmin.DEFAULT_KEYSPACE);
        // Then
        Assertions.assertNotNull(astraDb3);
        Assertions.assertNotNull(astraDb3.findAllCollections());
        // When initializing with an INVALID keyspace
        Assertions.assertThrows(NamespaceNotFoundException.class, () -> {
            new AstraDB(astraDbAdmin.getToken(), astraDb.getApiEndpoint(), "invalid_keyspace");
        });
    }

    @Test
    @Order(3)
    @DisplayName("03. Find a single database")
    public void shouldFindSingleDatabase() {
        if (databaseId== null) shouldCreateDatabase();

        // When
        Optional<Database> opt = astraDbAdmin.findDatabaseById(databaseId);
        // Then
        Assertions.assertNotNull(opt);
        Assertions.assertTrue(opt.isPresent());
        Assertions.assertEquals(TEST_DBNAME, opt.get().getInfo().getName());

        // Given
        Assertions.assertTrue(astraDbAdmin.isDatabaseExists(TEST_DBNAME));
        // When
        Stream<Database> dbs = astraDbAdmin.findDatabaseByName(TEST_DBNAME);
        // Then
        Assertions.assertNotNull(dbs);
        dbs.findFirst().ifPresent(db -> {
            Assertions.assertEquals(TEST_DBNAME, db.getInfo().getName());
            Assertions.assertEquals(databaseId.toString(), db.getId());
        });
    }

    @Test
    @Order(4)
    @DisplayName("04. Find all databases")
    public void shouldFindAllDatabases() {
        // Given
        Assertions.assertTrue(astraDbAdmin.isDatabaseExists(TEST_DBNAME));
        // When
        Assertions.assertTrue(astraDbAdmin
                .findAllDatabases()
                .anyMatch(db -> db.getInfo().getName().equals(TEST_DBNAME)));
    }

    @Test
    @Order(5)
    @DisplayName("05. Delete a Database")
    @Disabled("This test is disabled because it is pretty lone")
    public void shouldDeleteDatabase() throws InterruptedException {
        String dbName = "test_delete_db";
        // Given
        Assertions.assertFalse(astraDbAdmin.isDatabaseExists(dbName));
        // When
        UUID dbId = astraDbAdmin.createDatabase(dbName, targetCloud, targetRegion);
        Assertions.assertTrue(astraDbAdmin.isDatabaseExists(dbName));
        // When
        astraDbAdmin.deleteDatabaseByName(dbName);
        // Then
        Thread.sleep(5000);
        Database db = astraDbAdmin.findDatabaseById(dbId).get();
        Assertions.assertEquals("TERMINATING", db.getStatus().name());
    }

    // ------------------------------------
    // --------- Collections --------------
    // ------------------------------------

    @Test
    @Order(6)
    @DisplayName("06. Create a Collection (no vector)")
    public void shouldCreateCollectionSimple() {
        if (astraDb == null) shouldConnectToDatabase();
        // Given
        astraDb.deleteCollection(TEST_COLLECTION_NAME);
        Assertions.assertFalse(astraDb.isCollectionExists(TEST_COLLECTION_NAME));
        // When
        collectionSimple = astraDb.createCollection(TEST_COLLECTION_NAME);
        // Then
        Assertions.assertTrue(astraDb.isCollectionExists(TEST_COLLECTION_NAME));
    }

    @Test
    @Order(7)
    @DisplayName("07. Create Collections (with vector)")
    public void shouldCreateCollectionVector() {
        if (astraDb == null) shouldConnectToDatabase();

        // Given
        astraDb.deleteCollection(TEST_COLLECTION_VECTOR);
        Assertions.assertFalse(astraDb.isCollectionExists(TEST_COLLECTION_VECTOR));
        // When
        astraDb.createCollection(TEST_COLLECTION_VECTOR, 14);
        // Then
        Assertions.assertTrue(astraDb.isCollectionExists(TEST_COLLECTION_VECTOR));

        // Given
        astraDb.deleteCollection(TEST_COLLECTION_VECTOR);
        Assertions.assertFalse(astraDb.isCollectionExists(TEST_COLLECTION_VECTOR));
        collectionVector = astraDb.createCollection(CollectionDefinition.builder()
                        .name(TEST_COLLECTION_VECTOR)
                        .vector(14, cosine)
                        .build());
    }

    @Test
    @Order(8)
    @DisplayName("08. Find a single collection")
    public void shouldFindCollection() {
        if (astraDb == null) shouldConnectToDatabase();

        // Find a collection (1)
        Assertions.assertTrue(astraDb.isCollectionExists(TEST_COLLECTION_VECTOR));
        Optional<CollectionDefinition> def = astraDb.findCollection(TEST_COLLECTION_VECTOR);
        Assertions.assertTrue(def.isPresent());
        Assertions.assertEquals(14, def.get().getOptions().getVector().getDimension());

        // Find a collection (2)
        Assertions.assertTrue(astraDb.isCollectionExists(TEST_COLLECTION_NAME));
        Assertions.assertTrue(astraDb.findCollection(TEST_COLLECTION_NAME).isPresent());

        // Find a collection (3)
        Assertions.assertFalse(astraDb.isCollectionExists("invalid"));
        Assertions.assertFalse(astraDb.findCollection("invalid").isPresent());
    }

    @Test
    @Order(9)
    @DisplayName("09. Find all collections")
    public void shouldListCollection() {
        if (astraDb == null) shouldConnectToDatabase();

        List<String> collections = astraDb
                .findAllCollections()
                .map(CollectionDefinition::getName)
                .collect(Collectors.toList());
        Assertions.assertTrue(collections.contains(TEST_COLLECTION_NAME));
        Assertions.assertTrue(collections.contains(TEST_COLLECTION_VECTOR));
    }

    @Test
    @Order(10)
    @DisplayName("10. Delete all documents in a collection")
    public void shouldDeleteACollection() {
        if (astraDb == null) shouldConnectToDatabase();

        String deleted_collection = "deleted_collection";
        astraDb.createCollection(deleted_collection);
        Assertions.assertTrue(astraDb.isCollectionExists(deleted_collection));

        astraDb.deleteCollection(deleted_collection);
        Assertions.assertFalse(astraDb.isCollectionExists(deleted_collection));
    }

    @Test
    @Order(11)
    @DisplayName("11. Delete a collection")
    public void shouldClearACollection() {
        // Given
        if (collectionSimple == null) shouldCreateCollectionSimple();
        Assertions.assertTrue(astraDb.isCollectionExists(TEST_COLLECTION_NAME));
        collectionSimple.insertOne(new JsonDocument("1"));
        Assertions.assertEquals(1, collectionSimple.countDocuments());
        // When
        collectionSimple.deleteAll();
        // Then
        Assertions.assertEquals(0, collectionSimple.countDocuments());
    }

    // ------------------------------------
    // --------- Documents   --------------
    // ------------------------------------

    @Test
    @Order(12)
    @DisplayName("12. Insert a Document")
    public void shouldInsertADocument() {
        if (collectionVector == null) shouldCreateCollectionVector();

        // You must delete any existing rows with the same IDs as the
        // rows you want to insert
        collectionVector.deleteAll();

        // Insert rows defined by key/value
        collectionVector.insertOne(
                new JsonDocument()
                        .id("doc1") // uuid is generated if not explicitely set
                        .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                        .put("product_name", "HealthyFresh - Beef raw dog food")
                        .put("product_price", 12.99));

        // Insert rows defined as a JSON String
        collectionVector.insertOne(
                new JsonDocument()
                        .data(
                                "{" +
                                        "\"_id\": \"doc2\", " +
                                        "\"$vector\": [1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0], " +
                                        "\"product_name\": \"HealthyFresh - Chicken raw dog food\", " +
                                        "\"product_price\": 9.99" +
                                        "}"));

        // Insert rows defined as a Map
        collectionVector.insertOne(
                new JsonDocument()
                        .id("doc3")
                        .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                        .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food")));

        // Insert rows defined as a combination of key/value, JSON, and Map
        collectionVector.insertOne(
                new JsonDocument()
                        .id("doc4")
                        .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                        .data("{" +
                                "\"product_name\": \"HealthyFresh - Chicken raw dog food\", " +
                                "\"product_price\": 8.99" +
                                "}"));

        // If you do not provide an ID, they are generated automatically
        String generatedId = collectionVector.insertOne(new JsonDocument().put("demo", 1));
        Assertions.assertEquals(5, collectionVector.countDocuments());
    }

    @Test
    @Order(13)
    @DisplayName("13. Insert many Documents")
    public void shouldInsertManyDocument() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        // Given
        collectionSimple.deleteAll();
        Assertions.assertEquals(0, collectionSimple.countDocuments());

        // Adding Many with a Json document with a lof properties
        collectionSimple.insertMany(List.of(
                new JsonDocument("1")
                        .put("metadata_instant", Instant.now())
                        .put("metadata_date", new Date())
                        .put("metadata_calendar", Calendar.getInstance())
                        .put("metadata_int", 1)
                        .put("metadata_long", 1L)
                        .put("metadata_double", 1d)
                        .put("metadata_float", 1f)
                        .put("metadata_string", "hello")
                        .put("metadata_short", Short.valueOf("1"))
                        .put("metadata_string_array", new String[] {"a", "b", "c"})
                        .put("metadata_int_array", new Integer[] {1,2,3})
                        .put("metadata_long_array", new Long[] {1L,2L,3L})
                        .put("metadata_double_array", new Double[] {1d,2d,3d})
                        .put("metadata_float_array", new Float[] {1f,2f,3f})
                        .put("metadata_short_array", new Short[] {1,2,3})
                        .put("metadata_boolean", true)
                        .put("metadata_boolean_array", new Boolean[] {true, false, true})
                        .put("metadata_uuid", UUID.randomUUID())
                        .put("metadata_uuid_array", new UUID[] {UUID.randomUUID(), UUID.randomUUID()})
                        .put("metadata_map", Map.of("key1", "value1", "key2", "value2"))
                        .put("metadata_list", List.of("value1", "value2"))
                        .put("metadata_enum", AstraDBAdmin.FREE_TIER_CLOUD)
                        .put("metadata_enum_array", new CloudProviderType[] {AstraDBAdmin.FREE_TIER_CLOUD, CloudProviderType.AWS})
                        .put("metadata_object", new Product("name", 1d))
        ));
        collectionSimple.findAll().forEach(System.out::println);
    }

    @Test
    @Order(14)
    @DisplayName("14. Find GTE")
    public void shouldFindWithGreaterThan() {
        shouldInsertADocument();
        Assertions.assertEquals(1, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isGreaterOrEqualsThan(12.99)
                .build()).count());
    }

    @Test
    @Order(15)
    @DisplayName("15. Find GT")
    // Greater than
    public void shouldFindGreaterThan() {
        shouldInsertADocument();
        Assertions.assertEquals(1, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isGreaterThan(10)
                .build()).count());
    }

    @Test
    @Order(16)
    @DisplayName("16. Find Less Than")
    // Greater than
    public void shouldFindLessThen() {
        shouldInsertADocument();
        Assertions.assertEquals(2, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isLessThan(10)
                .build()).count());
    }

    @Test
    @Order(17)
    @DisplayName("17. Find Less Than or Equals")
    // Greater than
    public void shouldFindLessOrEqualsThen() {
        shouldInsertADocument();
        Assertions.assertEquals(2, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isLessOrEqualsThan(9.99)
                .build()).count());
    }

    @Test
    @Order(18)
    @DisplayName("18. Find Equals")
    // Greater than
    public void shouldEqualsThen() {
        shouldInsertADocument();
        Assertions.assertEquals(1, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isEqualsTo(9.99)
                .build()).count());
    }

    @Test
    @Order(19)
    @DisplayName("19. Find Not Equals")
    // Greater than
    public void shouldNotEqualsThen() {
        shouldInsertADocument();
        Assertions.assertEquals(4, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isNotEqualsTo(9.99)
                .build()).count());
    }

    @Test
    @Order(20)
    @DisplayName("20. Find exists")
    // Greater than
    public void shouldFindExists() {
        shouldInsertADocument();
        Assertions.assertEquals(3, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .exists()
                .build()).count());
    }

    @Test
    @Order(21)
    @DisplayName("21. AND with Exists and Not Equals")
    // Greater than
    public void shouldFindAndExistsAndNotEquals() {
        shouldInsertADocument();
        // Exists AND not equals
        // {"find":{"filter":{"$and":[{"product_price":{"$exists":true}},{"product_price":{"$ne":9.99}}]}}}
        SelectQuery existAndNotEquals = new SelectQuery();
        List<Map<String, Map<String, Object>>> andCriteriaList = new ArrayList<Map<String, Map<String, Object>>>();
        Map<String, Map<String, Object>> criteria1 = new HashMap<>();
        criteria1.put("product_price", Map.of("$exists", true));
        Map<String, Map<String, Object>> criteria2 = new HashMap<>();
        criteria2.put("product_price", Map.of("$ne", 9.99));
        andCriteriaList.add(criteria1);
        andCriteriaList.add(criteria2);
        existAndNotEquals.setFilter(Map.of("$and", andCriteriaList));
        Assertions.assertEquals(2, collectionVector.find(existAndNotEquals).count());

        SelectQuery query2 = SelectQuery.builder().withJsonFilter("{" +
                "\"$and\":[" +
                "   {" +
                        "\"product_price\": {\"$exists\":true}" +
                "}," +
                "{" +
                        "\"product_price\":{\"$ne\":9.99}}]" +
                "}")
                .build();
        Assertions.assertEquals(2, collectionVector.find(query2).count());

    }

    // ----------------------------------------
    // --------- Object Mapping ---------------
    // ----------------------------------------

    @Test
    @Order(50)
    @DisplayName("50. Insert with CollectionRepository and vector")
    public void shouldInsertRecords() {
        astraDb = astraDbAdmin.database(TEST_DBNAME);
        productRepositoryVector = astraDb.collectionRepository(TEST_COLLECTION_VECTOR, Product.class);

        productRepositoryVector.insert(new Document<>(
                "product1",
                new Product("something Good", 9.99),
                new float[] {1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        // Add vector without an id
        productRepositoryVector.insert(new Document<>(
                new Product("id will be generated for you", 10.99),
                new float[] {1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        // Insert a full-fledged object
        productRepositoryVector.insert(new Document<Product>()
                .id("pf2000")
                .vector(new float[] {1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));
    }

    @Test
    @Order(51)
    @DisplayName("51. Insert with CollectionRepository")
    public void shouldInsertWithSimpleCollectionObjectMapping() {
        productRepositorySimple = astraDb.collectionRepository(TEST_COLLECTION_NAME, Product.class);
        Assertions.assertNotNull(productRepositorySimple);
        productRepositorySimple.save(new Document<>("p1", new Product("Pupper Sausage Beef dog Treats", 9.99)));
        productRepositorySimple.save(new Document<>("p2", new Product("Dog Ring Chew Toy", 10.99)));
        productRepositorySimple.saveAll(List.of(
          new Document<>("p3", new Product("Dog Ring Chew Toy", 9.99)),
          new Document<>("p4", new Product("Pepper Sausage Bacon dog Treats", 9.99))
        ));
    }

    @Test
    @Order(52)
    @DisplayName("52. Upsert with CollectionClient")
    public void shouldUpsertDocument() {
        if (astraDb == null) {
            astraDb = astraDbAdmin.database(TEST_DBNAME);
        }
        AstraDBCollection collection = astraDb.collection(TEST_COLLECTION_NAME);
        Assertions.assertNotNull(collection);
        JsonDocument doc = new JsonDocument()
                .id("4")
                .put("name", "Coded Cleats Copy")
                .put("description", "ChatGPT integrated sneakers that talk to you");
        collection.upsert(doc);
        collection.upsert(doc);
    }

}
