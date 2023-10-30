package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBClient;
import com.dtsx.astra.sdk.utils.AstraRc;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.CollectionRepository;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.domain.odm.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the Vector Store Client with no third party embeddings.
 */
@Slf4j
class VectorClientProductTest {

    public static final String DBNAME_VECTOR_CLIENT = "test_java_astra_db_client";

    public static final String VECTOR_STORE_NAME = "demo_product";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Product {

        @JsonProperty("product_name")
        private String name;

        @JsonProperty("product_price")
        private Double price;
    }

    static CollectionRepository<Product> productRepository;

    @BeforeAll
    public static void setup() {
        if (System.getenv(AstraRc.ASTRA_DB_APPLICATION_TOKEN) == null) {
            throw new IllegalStateException("Please setup 'ASTRA_DB_APPLICATION_TOKEN' env variable");
        }
        AstraDBClient vectorClient = new AstraDBClient();
        UUID databaseId = vectorClient.createDatabase(DBNAME_VECTOR_CLIENT);
        log.info("{} is created and active", databaseId);
    }

    @Test
    @Order(1)
    @DisplayName("01. Import Data")
    @EnabledIfEnvironmentVariable(named = "ASTRA_DB_APPLICATION_TOKEN", matches = "Astra.*")
    public void shouldInsertStaticDocument() {
        // Recreating the store
        AstraDB astraDB = new AstraDBClient().database(DBNAME_VECTOR_CLIENT);
        astraDB.deleteCollection(VECTOR_STORE_NAME);
        productRepository = astraDB.createCollection(VECTOR_STORE_NAME, 14, Product.class);
        log.info("store {} is created ", VECTOR_STORE_NAME);
        assertTrue(astraDB.isCollectionExists(VECTOR_STORE_NAME));

        // Easy insert one
        Document<Product> doc = new Document<>("pf7044",
                new Product("Pupper Sausage Beef dog Treats", 9.99),
                new float[]{0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f});
        productRepository.insert(doc);
        // Easy insert many
        productRepository.insertAll(List.of(
                new Document<>("pt0041",
                        new Product("Dog Ring Chew Toy", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f}),
                new Document<>("pf7043",
                        new Product("Pepper Sausage Bacon dog Treats", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f})
        ));
        // Insert Many with Json
        productRepository.saveAll(List.of(
                new Document<>("pf1844",
                        new Product("HealthyFresh - Beef raw dog food", 12.99),
                        new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}),
                new Document<>("pf1843",
                        new Product("HealthyFresh - Beef raw dog food", 12.99),
                        new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}),
                new Document<Product>()
                        .id("pt0021")
                        .data(new Product("Dog Tennis Ball Toy", 12.99))
                        .vector(new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f}))
        );
        assertEquals(6, productRepository.count());
        assertEquals(3, productRepository.count(new Filter()
                .where("product_price").isEqualsTo(9.99)));
    }

    @Test
    @Order(2)
    @DisplayName("02. Similarity Search")
    public void shouldSimilaritySearch() {

        productRepository = new AstraDBClient()
                .database(DBNAME_VECTOR_CLIENT)
                .collectionRepository(VECTOR_STORE_NAME, Product.class);

        float[] embeddings =  new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        for(Result<Product> result : productRepository.similaritySearch(embeddings,null, 2)) {
            System.out.println(result.getId()
                    + ") similarity=" + result.getSimilarity()
                    + ", vector=" + Arrays.toString(result.getVector()));
        }
    }

    @Test
    @Order(3)
    @DisplayName("03. Search with Meta Data")
    public void shouldSimilaritySearchWithMetaData() {
        productRepository = new AstraDBClient()
                .database(DBNAME_VECTOR_CLIENT)
                .collectionRepository(VECTOR_STORE_NAME, Product.class);

        float[] embeddings     = new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        Filter  metadataFilter = new Filter().where("product_price").isEqualsTo(9.99);

        for(Result<Product> result : productRepository
                .similaritySearch(embeddings, metadataFilter, 2)) {
            System.out.println(result.getId()
                    + ") similarity=" + result.getSimilarity()
                    + ", vector=" + Arrays.toString(result.getVector()));
        }

    }

}
