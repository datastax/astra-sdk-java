package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseInfo;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.AstraRc;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.odm.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.stargate.sdk.json.domain.SimilarityMetric.cosine;

/**
 * Once upon a time in the vector database wonderland.
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AstraDBIntegrationsTest extends AbstractAstraDBTest {

    /**
     * Test Constants
     */
    static final String TEST_DBNAME = "test_java_astra_db_client";
    static final String TEST_COLLECTION_NAME = "collection_simple";
    static final String TEST_COLLECTION_VECTOR = "collection_vector";
    static AstraEnvironment targetEnvironment = AstraEnvironment.PROD;
    static CloudProviderType targetCloud = AstraDBAdmin.FREE_TIER_CLOUD;
    static String targetRegion = AstraDBAdmin.FREE_TIER_CLOUD_REGION;
    /**
     * Shared working environment
     */
    static String astraToken;
    static AstraDBAdmin astraDbAdmin;
    static AstraDB astraDb;
    static UUID databaseId;
    static AstraDBRepository<Product> productRepositoryVector;

    static AstraDBRepository<Product> productRepositorySimple;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Product {
        @JsonProperty("product_name")
        private String name;
        @JsonProperty("product_price")
        private Double price;
    }

    @BeforeAll
    public static void setup() {
        // (1) Read arguments from env vars
        astraToken = System.getenv(AstraRc.ASTRA_DB_APPLICATION_TOKEN);
        Assertions.assertNotNull(astraToken);
        if (System.getenv(AstraRc.ASTRA_ENV) != null) {
            targetEnvironment = AstraEnvironment.valueOf(System.getenv(AstraRc.ASTRA_ENV).toUpperCase());
        }
        if (System.getenv("ASTRA_REGION") != null) {
            targetRegion = System.getenv("ASTRA_REGION");
        }
        if (System.getenv("ASTRA_CLOUD") != null) {
            targetCloud = CloudProviderType.valueOf(System.getenv("ASTRA_CLOUD").toUpperCase());
        }
        // When
        astraDbAdmin = new AstraDBAdmin(astraToken, targetEnvironment);
        // Then
        Assertions.assertNotNull(astraDbAdmin.getRawDevopsApiClient());
        Assertions.assertNotNull(astraDbAdmin.getToken());
        log.info("Initialization OK: environment '{}', cloud '{}', region '{}'",
                targetEnvironment, targetCloud.name(), targetRegion);
    }

    @Test
    @Order(1)
    @DisplayName("01. List Databases (devops)")
    public void shouldListDatabases() {
        log.info("Databases currently running in your organization:");
        List<Database> list = astraDbAdmin.findAllDatabases().collect(Collectors.toList());
        Assertions.assertNotNull(list);
        list.stream().map(Database::getInfo)
                .map(DatabaseInfo::getName)
                .forEach(log::info);
    }

    @Test
    @Order(2)
    @DisplayName("02. Create Database (devops)")
    public void shouldCreateDatabases() {
        databaseId = astraDbAdmin.createDatabase(TEST_DBNAME, targetCloud, targetRegion);
        Assertions.assertTrue(astraDbAdmin.isDatabaseExists(TEST_DBNAME));
    }

    @Test
    @Order(3)
    @DisplayName("03. Find Database (devops)")
    public void shouldLoadDatabase() {
        astraDb = astraDbAdmin.database(TEST_DBNAME);
        Assertions.assertNotNull(astraDb);
        Assertions.assertNotNull(astraDbAdmin.database(databaseId));
    }

    @Test
    @Order(4)
    @DisplayName("04. Create Collection Simple")
    public void shouldCreateCollectionSimple() {
        // Given
        astraDb.deleteCollection(TEST_COLLECTION_NAME);
        Assertions.assertFalse(astraDb.isCollectionExists(TEST_COLLECTION_NAME));
        // When
        astraDb.createCollection(TEST_COLLECTION_NAME);
        // Then
        Assertions.assertTrue(astraDb.isCollectionExists(TEST_COLLECTION_NAME));
    }

    @Test
    @Order(5)
    @DisplayName("05. Create Collections with Vector")
    public void shouldCreateCollectionVector() {
        // Given
        astraDb.deleteCollection(TEST_COLLECTION_VECTOR);
        Assertions.assertFalse(astraDb.isCollectionExists(TEST_COLLECTION_VECTOR));
        // When
        astraDb.createCollection(TEST_COLLECTION_VECTOR, 14);
        // Then
        Assertions.assertTrue(astraDb.isCollectionExists(TEST_COLLECTION_VECTOR));

        // Full fledged collection creation
        astraDb.createCollection(CollectionDefinition.builder()
                        .name("tmp_collection")
                        .vector(14, cosine)
                        .build());
    }

    @Test
    @Order(6)
    @DisplayName("06. Find Collections")
    public void shouldListCollection() {
        Assertions.assertNotNull(astraDb.findAllCollections());
        Optional<CollectionDefinition> def = astraDb.findCollection(TEST_COLLECTION_VECTOR);
        Assertions.assertTrue(def.isPresent());

        Assertions.assertEquals(14, def.get().getOptions().getVector().getDimension());
        Optional<CollectionDefinition> def2 = astraDb.findCollection(TEST_COLLECTION_NAME);
        Assertions.assertTrue(def2.isPresent());
        Assertions.assertFalse(astraDb.findCollection("invalid").isPresent());
    }

    @Test
    @Order(6)
    @DisplayName("06. Insert with CollectionRepository and vector")
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
    @Order(7)
    @DisplayName("07.  Insert with CollectionRepository")
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
    @Order(8)
    @DisplayName("08. Insert with CollectionClient")
    public void shouldInsertWithSimpleCollectionJsonMapping() {
        AstraDBCollection collection = astraDb.collection(TEST_COLLECTION_NAME);
        Assertions.assertNotNull(collection);
        collection.insertOne(new JsonDocument()
                .id("4")
                .put("name", "Coded Cleats Copy")
                .put("description", "ChatGPT integrated sneakers that talk to you"));
        // Insert Products
        collection.insertOne(new JsonDocument()
                .id("p6")
                .data(Map.of("product_name", "Pupper Sausage Beef dog Treats", "product_price", 9.99))
        );
    }

    @Test
    @Order(9)
    @DisplayName("09. Upsert with CollectionClient")
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
