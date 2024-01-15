package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.DocumentMutationResult;
import io.stargate.sdk.data.DocumentMutationStatus;
import io.stargate.sdk.data.JsonDocumentMutationResult;
import io.stargate.sdk.data.domain.CollectionDefinition;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.JsonDocumentResult;
import io.stargate.sdk.data.domain.SimilarityMetric;
import io.stargate.sdk.data.domain.odm.Document;
import io.stargate.sdk.data.domain.odm.DocumentResult;
import io.stargate.sdk.data.domain.query.SelectQuery;
import io.stargate.sdk.data.exception.DataApiDocumentAlreadyExistException;
import io.stargate.sdk.data.exception.DataApiInvalidArgumentException;
import io.stargate.sdk.data.exception.DataApiNamespaceNotFoundException;
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

/**
 * Once upon a time in the vector database wonderland.
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AstraDBTestSuiteIT {

    /**
     * Test Constants
     */
    public static final String TEST_DBNAME = "test_java_astra_db_client";
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
        astraToken = "AstraCS:ZiWfNzYJtUGszRuGyyTjFIXU:2c5a21a4623c6ee688d4bca4b8e55a269aa3ee864fcd16b26b7f9a82ca57b999";
        targetCloud = CloudProviderType.GCP;
        targetRegion = "europe-west4";
        log.info("Environment Setup for Development");
    }

    private void setupTestSuiteForProduction() {
        log.info("Environment Setup for PRODUCTION");
        targetEnvironment = AstraEnvironment.PROD;
        astraToken = "AstraCS:iLPiNPxSSIdefoRdkTWCfWXt:2b360d096e0e6cb732371925ffcc6485541ff78067759a2a1130390e231c2c7a";
        targetCloud = CloudProviderType.GCP;
        targetRegion = "us-east1";
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
        if (databaseId == null) shouldCreateDatabase();

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
        Assertions.assertThrows(DataApiNamespaceNotFoundException.class, () -> new AstraDB(astraDbAdmin.getToken(), astraDb.getApiEndpoint(), "invalid_keyspace"));
    }

    @Test
    @Order(3)
    @DisplayName("03. Find a single database")
    public void shouldFindSingleDatabase() {
        if (databaseId == null) shouldCreateDatabase();

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
        Database db = astraDbAdmin
                .findDatabaseById(dbId)
                .orElseThrow(() -> new IllegalStateException("Should have found a database"));
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
                .vector(14, SimilarityMetric.cosine)
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
        collectionSimple.insertOne(new JsonDocument().id("1"));
        Assertions.assertEquals(1, collectionSimple.countDocuments());
        // When
        collectionSimple.deleteAll();
        // Then
        Assertions.assertEquals(0, collectionSimple.countDocuments());
    }

    @Test
    @Order(12)
    @DisplayName("12. InsertOne with json String")
    public void shouldInsertOneJson() {
        if (collectionVector == null) shouldCreateCollectionVector();
        collectionVector.deleteAll();

        String json = "{" +
                "\"$vector\": [0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3,0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3], " +
                "\"product_name\": \"HealthyFresh - Chicken raw dog food\", " +
                "\"product_price\": 9.99" +
                "}";
        JsonDocumentMutationResult dmr = collectionVector.insertOne(json);
        Assertions.assertNotNull(dmr.getDocument().getId());
        Assertions.assertEquals(9.99d, dmr.getDocument().getDouble("product_price"));
        Assertions.assertEquals(DocumentMutationStatus.CREATED, dmr.getStatus());

        // Insert again Asynchronously
        collectionVector.insertOneAsync(json).thenAccept(dmr2 -> {
            Assertions.assertNotNull(dmr2.getDocument().getId());
            Assertions.assertEquals(9.99d, dmr2.getDocument().getDouble("product_price"));
            Assertions.assertEquals(DocumentMutationStatus.CREATED, dmr2.getStatus());
        });
    }

    @Test
    @Order(13)
    @DisplayName("13. InsertOne with a JsonDocument")
    public void shouldInsertOneJsonDocument() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        // Working document
        JsonDocument doc = new JsonDocument().id("1").put("a", "a").put("b", true);

        // Insert ONE
        JsonDocumentMutationResult res = collectionSimple.insertOne(doc);
        Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
        Assertions.assertEquals("1", res.getDocument().getId());
        Assertions.assertEquals("a", res.getDocument().getString("a"));
        Assertions.assertEquals(true, res.getDocument().getBoolean("b"));

        // Insert ONE => ERROR
        Assertions.assertThrows(DataApiDocumentAlreadyExistException.class, () -> collectionSimple.insertOne(doc));
    }

    @Test
    @Order(14)
    @DisplayName("14. InsertOne with a Java Bean")
    public void shouldInsertOneDocument() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        // Working document
        Document<Product> doc = new Document<Product>().id("p1").data(new Product("p1", 10.1));

        // Insert ONE
        DocumentMutationResult<Product> res = collectionSimple.insertOne(doc);
        Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
        Assertions.assertEquals("p1", res.getDocument().getId());
        Assertions.assertEquals("p1", res.getDocument().getData().getName());

        // Insert ONE => ERROR
        Assertions.assertThrows(DataApiDocumentAlreadyExistException.class, () -> collectionSimple.insertOne(doc));
    }

    @Test
    @Order(15)
    @DisplayName("15. Insert Doc and find By Id")
    public void shouldInsertAndRetrieve() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        // Working document
        Document<Product> doc = new Document<Product>().id("p1").data(new Product("p1", 10.1));

        // Insert ONE
        DocumentMutationResult<Product> res = collectionSimple.insertOne(doc);
        Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
        Assertions.assertEquals("p1", res.getDocument().getId());
        Assertions.assertEquals("p1", res.getDocument().getData().getName());

        // Insert ONE => ERROR
        Optional<DocumentResult<Product>> optRes = collectionSimple.findById("p1", Product.class);
        if (optRes.isPresent()) {
            DocumentResult<Product> res2 = optRes.get();
            Assertions.assertEquals("p1", res2.getId());
            Assertions.assertEquals("p1", res2.getData().getName());
        } else {
            Assertions.fail("Should have found a document");
        }
    }

    @Test
    @Order(16)
    @DisplayName("16. UpsertOne with a jsonDocument")
    public void testUpsert() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        JsonDocument doc = new JsonDocument().id("1").put("a", "a").put("b", "c");
        JsonDocumentMutationResult res = collectionSimple.upsertOne(doc);
        Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
        Assertions.assertEquals("1", res.getDocument().getId());

        // Upsert with no CHANGE
        res = collectionSimple.upsertOne(doc);
        Assertions.assertEquals(DocumentMutationStatus.UNCHANGED, res.getStatus());
        Assertions.assertEquals("1", res.getDocument().getId());


        // Upsert with a CHANGE
        doc.put("b", "updated");
        res = collectionSimple.upsertOne(doc);
        Assertions.assertEquals(DocumentMutationStatus.UPDATED, res.getStatus());
        Assertions.assertEquals("1", res.getDocument().getId());
    }

    // Insert Many........................................................

    JsonDocument player1 = new JsonDocument().id("1").put("firstName", "Lucas").put("lastName", "Hernandez");
    JsonDocument player2 = new JsonDocument().id("2").put("firstName", "Antoine").put("lastName", "Griezmann");
    JsonDocument player3 = new JsonDocument().id("3").put("firstName", "N'Golo").put("lastName", "Kanté");
    JsonDocument player4 = new JsonDocument().id("4").put("firstName", "Paul").put("lastName", "Pogba");
    JsonDocument player5 = new JsonDocument().id("5").put("firstName", "Raphaël").put("lastName", "Varane");
    JsonDocument player6 = new JsonDocument().id("6").put("firstName", "Hugo").put("lastName", "Lloris");
    JsonDocument player7 = new JsonDocument().id("7").put("firstName", "Olivier").put("lastName", "Giroud");
    JsonDocument player8 = new JsonDocument().id("8").put("firstName", "Benjamin").put("lastName", "Pavard");
    JsonDocument player9 = new JsonDocument().id("9").put("firstName", "Kylian").put("lastName", "Mbappé");
    JsonDocument player10 = new JsonDocument().id("10").put("firstName", "Blaise").put("lastName", "Matuidi");
    JsonDocument player11 = new JsonDocument().id("11").put("firstName", "Samuel").put("lastName", "Umtiti");
    JsonDocument player12 = new JsonDocument().id("12").put("firstName", "Thomas").put("lastName", "Lemar");
    JsonDocument player13 = new JsonDocument().id("13").put("firstName", "Ousmane").put("lastName", "Dembélé");
    JsonDocument player14 = new JsonDocument().id("14").put("firstName", "Karim").put("lastName", "Benzema");
    JsonDocument player15 = new JsonDocument().id("15").put("firstName", "Adrien").put("lastName", "Rabiot");
    JsonDocument player16 = new JsonDocument().id("16").put("firstName", "Kingsley").put("lastName", "Coman");
    JsonDocument player17 = new JsonDocument().id("17").put("firstName", "Moussa").put("lastName", "Sissoko");
    JsonDocument player18 = new JsonDocument().id("18").put("firstName", "Lucas").put("lastName", "Digne");
    JsonDocument player19 = new JsonDocument().id("19").put("firstName", "Steve").put("lastName", "Mandanda");
    JsonDocument player20 = new JsonDocument().id("20").put("firstName", "Presnel").put("lastName", "Kimpembe");
    JsonDocument player21 = new JsonDocument().id("21").put("firstName", "Clement").put("lastName", "Lenglet");
    JsonDocument player22 = new JsonDocument().id("22").put("firstName", "Leo").put("lastName", "Dubois");
    JsonDocument player23 = new JsonDocument().id("23").put("firstName", "Kurt").put("lastName", "Zouma");
    JsonDocument player24 = new JsonDocument().id("24").put("firstName", "Tanguy").put("lastName", "Ndombele");

    @Test
    @Order(17)
    @DisplayName("17. InsertMany Java Bean")
    public void testInsertManyJavaBean() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        Document<Product> p1 = new Document<Product>().id("doc1").data(new Product("test1", 12.99));
        Document<Product> p2 = new Document<Product>().id("doc2").data(new Product("test2", 2.99));
        List<DocumentMutationResult<Product>> results = collectionSimple.insertMany(List.of(p1, p2));
        if (results !=null) {
            results.stream().forEach(r -> {
                Assertions.assertEquals(DocumentMutationStatus.CREATED, r.getStatus());
                Assertions.assertNotNull(r.getDocument().getId());
                Product p = r.getDocument().getData();
                Assertions.assertNotNull(p.getName());
            });
        }
    }

    @Test
    @Order(18)
    @DisplayName("18. InsertMany JsonDocuments")
    public void insertManyJson() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        collectionSimple.insertManyJsonDocuments(List.of(player1, player2)).stream().forEach(r -> {
                Assertions.assertEquals(DocumentMutationStatus.CREATED, r.getStatus());
                Assertions.assertNotNull(r.getDocument().getId());
                Assertions.assertNotNull(r.getDocument().getString("firstName"));
            });
        }

    @Test
    @Order(19)
    @DisplayName("19. InsertMany Json")
    public void testInsertManyJson() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();
        String jsonMany = "[" +
                "{\"product_name\":\"test1\",\"product_price\":12.99,\"_id\":\"doc1\"}," +
                "{\"product_name\":\"test2\",\"product_price\":2.99,\"_id\":\"doc2\"}" +
                "]";
        collectionSimple.insertMany(jsonMany);
    }

    @Test
    @Order(20)
    @DisplayName("20. InsertMany too many items")
    public void testInsertTooMany() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        Assertions.assertThrows(DataApiInvalidArgumentException.class,
                () -> collectionSimple.insertMany( List.of(
                        player1, player2, player3, player4, player5, player6,
                        player7, player8, player9, player10,player11, player12,
                        player13, player14, player15, player16, player17, player18,
                        player19, player20, player21, player22, player23, player24)));
    }

    @Test
    @Order(21)
    @DisplayName("21. InsertMany order true, no replace")
    public void testInsertManyOK() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        List<Document<Map<String, Object>>> othersPlayers = new ArrayList<>(List.of(player1, player2, player3, player4, player5, player6,
                player7, player8, player9));
        List<DocumentMutationStatus> statuses1 = new ArrayList<>();
        collectionSimple.insertMany(othersPlayers).stream().forEach(res -> {
            Assertions.assertNotNull(res.getDocument().getId());
            Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
            statuses1.add(res.getStatus());
        });
        log.info("Statuses => " + statuses1.stream().map(s -> s.name()).collect(Collectors.joining(", ")));

        // Insert again
        boolean orderInsertion         = true;
        boolean replaceIfAlreadyExists = false;
        List<DocumentMutationStatus> statuses = new ArrayList<>();
        collectionSimple.insertMany(othersPlayers, orderInsertion, replaceIfAlreadyExists).stream().forEach(res -> {
            Assertions.assertNotNull(res.getDocument().getId());
            if (player1.getId().equals(res.getDocument().getId())) {
                Assertions.assertEquals(DocumentMutationStatus.ALREADY_EXISTS, res.getStatus());
            } else {
                Assertions.assertEquals(DocumentMutationStatus.NOT_PROCESSED, res.getStatus());
            }
            statuses.add(res.getStatus());
        });
        log.info("Statuses => " + statuses.stream().map(s -> s.name()).collect(Collectors.joining(", ")));
    }

    @Test
    @Order(22)
    @DisplayName("22. InsertMany with replacements")
    public void testInsertManyWithDuplicatesOrder() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        List<Document<Map<String, Object>>> othersPlayers = new ArrayList<>(List.of(
                player1, player2, player3, player4, player5, player6,
                player7, player8));
        othersPlayers.add(new JsonDocument().id("9").put("firstName", "Kylian2").put("lastName", "Mbappé"));
        othersPlayers.addAll(List.of(player9, player10,player11));
        log.info("Players order, 9 is duplicate :" + othersPlayers
                .stream().map(Document::getId)
                .collect(Collectors.joining(", "))
                .toString());

        boolean orderingInserts        = true;
        boolean replaceIfAlreadyExists = false;

        // Status CREATED up to the duplicate
        List<DocumentMutationStatus> statuses = new ArrayList<>();
        collectionSimple.insertMany(othersPlayers, orderingInserts, replaceIfAlreadyExists).stream().forEach(res -> {
            Assertions.assertNotNull(res.getDocument().getId());
            int id = Integer.parseInt(res.getDocument().getId());
            if (id<9) {
                Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
            } else if (id==9) {
                Assertions.assertEquals(DocumentMutationStatus.ALREADY_EXISTS, res.getStatus());
            } else {
                Assertions.assertEquals(DocumentMutationStatus.NOT_PROCESSED, res.getStatus());
            }
            statuses.add(res.getStatus());
        });
        log.info("Statuses1 => " + statuses.stream().map(s -> s.name()).collect(Collectors.joining(", ")));

        // Status ALREADY EXIST for first and else NOT PROCESS
        List<DocumentMutationStatus> statuses2 = new ArrayList<>();
        collectionSimple.insertMany(othersPlayers, orderingInserts, replaceIfAlreadyExists).stream().forEach(res -> {
            Assertions.assertNotNull(res.getDocument().getId());
            int id = Integer.parseInt(res.getDocument().getId());
            if (id==1) {
                Assertions.assertEquals(DocumentMutationStatus.ALREADY_EXISTS, res.getStatus());
            } else {
                Assertions.assertEquals(DocumentMutationStatus.NOT_PROCESSED, res.getStatus());
            }
            statuses2.add(res.getStatus());
        });
        log.info("Statuses2 => " + statuses2.stream().map(s -> s.name()).collect(Collectors.joining(", ")));

        // 1 to 9 is ALREADY_EXIST, 10 and 11 are created
        orderingInserts = false;
        List<DocumentMutationStatus> statuses3 = new ArrayList<>();
        collectionSimple.insertMany(othersPlayers, orderingInserts, replaceIfAlreadyExists).stream().forEach(res -> {
            Assertions.assertNotNull(res.getDocument().getId());
            int id = Integer.parseInt(res.getDocument().getId());
            if (id<10) {
                Assertions.assertEquals(DocumentMutationStatus.ALREADY_EXISTS, res.getStatus());
            } else {
                Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
            }
            statuses3.add(res.getStatus());
        });
        log.info("Statuses3 => " + statuses3.stream().map(s -> s.name()).collect(Collectors.joining(", ")));

        // Try to replace
        replaceIfAlreadyExists = true;
        List<DocumentMutationStatus> statuses4 = new ArrayList<>();
        collectionSimple.insertMany(othersPlayers, orderingInserts, replaceIfAlreadyExists).stream().forEach(res -> {
            Assertions.assertNotNull(res.getDocument().getId());
            int id = Integer.parseInt(res.getDocument().getId());
            if (id==9) {
                Assertions.assertEquals(DocumentMutationStatus.UPDATED, res.getStatus());
            } else {
                Assertions.assertEquals(DocumentMutationStatus.UNCHANGED, res.getStatus());
            }
            statuses4.add(res.getStatus());
        });
        log.info("Statuses4 => " + statuses4.stream().map(s -> s.name()).collect(Collectors.joining(", ")));
    }

    // Insert Many Chunked........................................................

    @Test
    @Order(23)
    @DisplayName("23. Insert a Document with multiple Manner")
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
        collectionVector.insertOne(new JsonDocument().put("demo", 1));
        Assertions.assertEquals(5, collectionVector.countDocuments());
    }

    @Test
    @Order(24)
    @DisplayName("24. Insert document with a lot of properties and retrieve them")
    public void shouldInsertOneComplexDocument() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        // Given
        collectionSimple.deleteAll();
        Assertions.assertEquals(0, collectionSimple.countDocuments());

        // Adding Many with a Json document with a lof properties
        collectionSimple.insertOne(
                new JsonDocument().id("1")
                        .put("metadata_instant", Instant.now())
                        .put("metadata_date", new Date())
                        .put("metadata_calendar", Calendar.getInstance())
                        .put("metadata_int", 1)
                        .put("metadata_long", 12321323L)
                        .put("metadata_double", 1213.343243d)
                        .put("metadata_float", 1.1232434543f)
                        .put("metadata_string", "hello")
                        .put("metadata_short", Short.valueOf("1"))
                        .put("metadata_string_array", new String[]{"a", "b", "c"})
                        .put("metadata_int_array", new Integer[]{1, 2, 3})
                        .put("metadata_long_array", new Long[]{1L, 2L, 3L})
                        .put("metadata_double_array", new Double[]{1d, 2d, 3d})
                        .put("metadata_float_array", new Float[]{1f, 2f, 3f})
                        .put("metadata_short_array", new Short[]{1, 2, 3})
                        .put("metadata_boolean", true)
                        .put("metadata_boolean_array", new Boolean[]{true, false, true})
                        .put("metadata_uuid", UUID.randomUUID())
                        .put("metadata_uuid_array", new UUID[]{UUID.randomUUID(), UUID.randomUUID()})
                        .put("metadata_map", Map.of("key1", "value1", "key2", "value2"))
                        .put("metadata_list", List.of("value1", "value2"))
                        .put("metadata_byte", Byte.valueOf("1"))
                        .put("metadata_character", 'c')
                        .put("metadata_enum", AstraDBAdmin.FREE_TIER_CLOUD)
                        .put("metadata_enum_array", new CloudProviderType[]{AstraDBAdmin.FREE_TIER_CLOUD, CloudProviderType.AWS})
                        .put("metadata_object", new Product("name", 1d)));

        // Search By id
        JsonDocumentResult res = collectionSimple.findById("1")
                .orElseThrow(() -> new IllegalStateException("Should have found a document" ));

        // Accessing result
        Instant i = res.getInstant("metadata_instant");
        Assertions.assertNotNull(i);
        Date d = res.getDate("metadata_date");
        Assertions.assertNotNull(d);
        Calendar c = res.getCalendar("metadata_calendar");
        Assertions.assertNotNull(c);
        Integer integer = res.getInteger("metadata_int");
        Assertions.assertNotNull(integer);
        Long l = res.getLong("metadata_long");
        Assertions.assertNotNull(l);
        Double db = res.getDouble("metadata_double");
        Assertions.assertNotNull(db);
        Float f = res.getFloat("metadata_float");
        Assertions.assertNotNull(f);
        String s = res.getString("metadata_string");
        Assertions.assertNotNull(s);
        Short sh = res.getShort("metadata_short");
        Assertions.assertNotNull(sh);
        Boolean b = res.getBoolean("metadata_boolean");
        Assertions.assertNotNull(b);
        UUID u = res.getUUID("metadata_uuid");
        Assertions.assertNotNull(u);
        Byte by = res.getByte("metadata_byte");
        Assertions.assertNotNull(by);
        Character ch = res.getCharacter("metadata_character");
        Assertions.assertNotNull(ch);
        Product p = res.getObject("metadata_object", Product.class);
        Assertions.assertNotNull(p);
        List<String> l2 = res.getList("metadata_list", String.class);
        Assertions.assertNotNull(l2);
        Boolean[] ba = res.getArray("metadata_boolean_array", Boolean.class);
        Assertions.assertNotNull(ba);
    }

    @Test
    @Order(25)
    @DisplayName("25. Find with $gte")
    public void shouldFindWithGreaterThan() {
        shouldInsertADocument();
        Assertions.assertEquals(1, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isGreaterOrEqualsThan(12.99)
                .build()).count());
    }

    @Test
    @Order(26)
    @DisplayName("26. Find with $gt")
    // Greater than
    public void shouldFindGreaterThan() {
        shouldInsertADocument();
        Assertions.assertEquals(1, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isGreaterThan(10)
                .build()).count());
    }

    @Test
    @Order(27)
    @DisplayName("27. Find with $lt (less than)")
    // Greater than
    public void shouldFindLessThen() {
        shouldInsertADocument();
        Assertions.assertEquals(2, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isLessThan(10)
                .build()).count());
    }

    @Test
    @Order(28)
    @DisplayName("28. Find with $lte (less than or equals)")
    // Greater than
    public void shouldFindLessOrEqualsThen() {
        shouldInsertADocument();
        Assertions.assertEquals(2, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isLessOrEqualsThan(9.99)
                .build()).count());
    }

    @Test
    @Order(29)
    @DisplayName("29. Find with $eq")
    // Greater than
    public void shouldEqualsThen() {
        shouldInsertADocument();
        Assertions.assertEquals(1, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isEqualsTo(9.99)
                .build()).count());
    }

    @Test
    @Order(30)
    @DisplayName("30. Find Nwith $ne (not equals)")
    // Greater than
    public void shouldNotEqualsThen() {
        shouldInsertADocument();
        Assertions.assertEquals(4, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .isNotEqualsTo(9.99)
                .build()).count());
    }

    @Test
    @Order(31)
    @DisplayName("31. Find with $exists")
    // Greater than
    public void shouldFindExists() {
        shouldInsertADocument();
        Assertions.assertEquals(3, collectionVector.find(SelectQuery.builder()
                .where("product_price")
                .exists()
                .build()).count());
    }

    @Test
    @Order(32)
    @DisplayName("32. AND with Exists and Not Equals")
    // Greater than
    public void shouldFindAndExistsAndNotEquals() {
        shouldInsertADocument();
        // Exists AND not equals
        // {"find":{"filter":{"$and":[{"product_price":{"$exists":true}},{"product_price":{"$ne":9.99}}]}}}
        SelectQuery existAndNotEquals = new SelectQuery();
        List<Map<String, Map<String, Object>>> andCriteriaList = new ArrayList<>();
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

    @Test
    @Order(33)
    @DisplayName("33. Find $in")
    public void shouldFindWithIn() {
        shouldInsertOneComplexDocument();

        // $in
        log.info("Search with $in...");
        Assertions.assertTrue(collectionSimple.find(SelectQuery.builder()
                        .where("metadata_string")
                        .isInArray(new String[]{"hello", "world"}).build())
                .findFirst().isPresent());
    }

    @Test
    @Order(34)
    @DisplayName("34. Find $nin")
    public void shouldFindWithNIn() {
        shouldInsertOneComplexDocument();
        Assertions.assertTrue(collectionSimple.find(SelectQuery.builder()
                        .where("metadata_string")
                        .isNotInArray(new String[]{"Hallo", "Welt"}).build())
                .findFirst().isPresent());
    }

    @Test
    @Order(35)
    @DisplayName("35. Should find with $size")
    public void shouldFindWithSize() {
        shouldInsertOneComplexDocument();
        Assertions.assertTrue(collectionSimple.find(SelectQuery.builder()
                .where("metadata_boolean_array")
                .hasSize(3).build()).findFirst().isPresent());
    }

    @Test
    @Order(36)
    @DisplayName("36. Should find with $lt")
    public void shouldFindWithLT() {
        shouldInsertOneComplexDocument();
        Assertions.assertTrue(collectionSimple.find(SelectQuery.builder()
                .where("metadata_int")
                .isLessThan(2).build()).findFirst().isPresent());
    }

    @Test
    @Order(37)
    @DisplayName("37. Should find with $lte")
    public void shouldFindWithLTE() {
        Assertions.assertTrue(collectionSimple.find(SelectQuery.builder()
                .where("metadata_int")
                .isLessOrEqualsThan(1).build()).findFirst().isPresent());
    }

    @Test
    @Order(38)
    @DisplayName("38. Should find with $gt")
    public void shouldFindWithGTE() {
        Assertions.assertTrue(collectionSimple.find(SelectQuery.builder()
                .where("metadata_int")
                .isGreaterThan(0).build()).findFirst().isPresent());
    }

    @Test
    @Order(39)
    @DisplayName("39. Should find with $gte and Instant")
    public void shouldFindWithGTEInstant() {
        log.info("Search with $gte and an Instant...");
        Assertions.assertTrue(collectionSimple.find(SelectQuery.builder()
                .where("metadata_instant")
                .isLessThan(Instant.now()).build()).findFirst().isPresent());
    }

    @Test
    @Order(40)
    @DisplayName("40. UpsertOne with a jsonString ")
    public void testUpsertWithJson() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();

        String json = "{" +
                "\"_id\": \"1\", " +
                "\"$vector\": [0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3,0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3], " +
                "\"product_name\": \"HealthyFresh - Chicken raw dog food\", " +
                "\"product_price\": 9.99" +
                "}";

        JsonDocumentMutationResult res = collectionSimple.upsertOne(json);
        Assertions.assertEquals(DocumentMutationStatus.CREATED, res.getStatus());
        Assertions.assertEquals("1", res.getDocument().getId());

        // Upsert with no CHANGE
        res = collectionSimple.upsertOne(json);
        Assertions.assertEquals(DocumentMutationStatus.UNCHANGED, res.getStatus());
        Assertions.assertEquals("1", res.getDocument().getId());


        // Upsert with a CHANGE
        String json2 = "{" +
                "\"_id\": \"1\", " +
                "\"$vector\": [0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3,0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3], " +
                "\"product_name\": \"new Name\", " +
                "\"product_price\": 9.99" +
                "}";
        res = collectionSimple.upsertOne(json2);
        Assertions.assertEquals(DocumentMutationStatus.UPDATED, res.getStatus());
        Assertions.assertEquals("1", res.getDocument().getId());
    }

    @Test
    @Order(42)
    @DisplayName("42. InsertVeryMany Documents")
    public void insertVeryMany() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();
        List<Document<Product>> documents = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 251; i++) {
            documents.add(new Document<Product>().id(String.valueOf(i)).data(new Product("Desc " + i, i * 1.0d)));
        }
        collectionSimple.insertManyChunked(documents);
        long end = System.currentTimeMillis();
        log.info("Inserting 251 documents took {} ms", end - start);
        Assertions.assertEquals(251, collectionSimple.countDocuments());
    }

    @Test
    @Order(43)
    @DisplayName("43. InsertVeryMany concurrently")
    public void insertVeryManyConcurrent() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();
        List<Document<Product>> documents = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 2510; i++) {
            documents.add(new Document<Product>().id(String.valueOf(i)).data(new Product("Desc " + i, i * 1.0d)));
        }
        collectionSimple.insertManyChunked(documents, 20, 20);
        long end = System.currentTimeMillis();
        log.info("Inserting 2510 documents took {} ms", end - start);
        Assertions.assertEquals(2510, collectionSimple.countDocuments());
    }

    @Test
    @Order(44)
    @DisplayName("44. InsertMany with duplicates")
    public void insertVeryWithDuplicatesLeadToErrors() {
        if (collectionSimple == null) shouldCreateCollectionSimple();
        collectionSimple.deleteAll();
        List<JsonDocumentMutationResult> status = collectionSimple.insertManyJsonDocuments(List.of(
                new JsonDocument().id("1").put("firstName", "Kylian").put("lastName", "Mbappé"),
                new JsonDocument().id("1").put("firstName", "Antoine").put("lastName", "Griezmann")), true,false);
        Assertions.assertEquals(DocumentMutationStatus.ALREADY_EXISTS, status.get(0).getStatus());
    }

    // ----------------------------------------
    // ------------ Examples ------------------
    // ----------------------------------------



    // ----------------------------------------
    // --------- Object Mapping ---------------
    // ----------------------------------------

    /*
    @Test
    @Order(50)
    @DisplayName("50. Insert with CollectionRepository and vector")
    public void shouldInsertRecords() {
        astraDb = astraDbAdmin.database(TEST_DBNAME);
        productRepositoryVector = astraDb.collectionRepository(TEST_COLLECTION_VECTOR, Product.class);
        productRepositoryVector.insert(new Document<>(
                "product1",
                new Product("something Good", 9.99),
                new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        // Add vector without an id
        productRepositoryVector.insert(new Document<>(
                new Product("id will be generated for you", 10.99),
                new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        // Insert a full-fledged object
        productRepositoryVector.insert(new Document<Product>()
                .id("pf2000")
                .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));
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
    }*/

}
