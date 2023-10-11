package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.utils.AstraRc;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.domain.odm.Result;
import io.stargate.sdk.json.vector.VectorStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Test the Vector Store Client with no third party embeddings.
 */
@Slf4j
class VectorClientProductTest {

    public static final String DBNAME_VECTOR_CLIENT = "vector_client_test";

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

    static VectorStore<Product> vectorStore;

    @BeforeAll
    public static void setup() {
        if (System.getenv(AstraRc.ASTRA_DB_APPLICATION_TOKEN) == null) {
            throw new IllegalStateException("Please setup 'ASTRA_DB_APPLICATION_TOKEN' env variable");
        }
        AstraVectorClient vectorClient = new AstraVectorClient();
        UUID databaseId = vectorClient.createDatabase(DBNAME_VECTOR_CLIENT);
        log.info("{} is created and active", databaseId);
    }

    @Test
    @Order(1)
    @DisplayName("01. Import Data")
    @EnabledIfEnvironmentVariable(named = "ASTRA_DB_APPLICATION_TOKEN", matches = "Astra.*")
    public void shouldInsertStaticDocument() {
        // Recreating the store
        AstraVectorDatabaseClient dbClient = new AstraVectorClient().database(DBNAME_VECTOR_CLIENT);
        dbClient.deleteStore(VECTOR_STORE_NAME);
        vectorStore = dbClient.createVectorStore(VECTOR_STORE_NAME, 14, Product.class);
        log.info("store {} is created ", VECTOR_STORE_NAME);
        assertTrue(dbClient.isStoreExist(VECTOR_STORE_NAME));

        // Easy insert one
        vectorStore.insert("pf7044",
                new Product("Pupper Sausage Beef dog Treats", 9.99),
                new float[]{0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f});
        // Easy insert many
        vectorStore.insertAll(List.of(
                new Document<>("pt0041",
                        new Product("Dog Ring Chew Toy", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f}),
                new Document<>("pf7043",
                        new Product("Pepper Sausage Bacon dog Treats", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f})
        ));
        // Insert Many with Json
        vectorStore.insertAllJsonDocuments(List.of(
                new JsonDocument("pf1844")
                        .put("product_name", "HealthyFresh - Beef raw dog food")
                        .put("product_price", 12.99)
                        .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}),
                new JsonDocument("pf1843")
                        .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                        .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food")),
                new JsonDocument()
                        .id("pt0021")
                        .data("{ \"product_name\": \"Dog Tennis Ball Toy\" }")
                        .vector(new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f}))
        );
        assertEquals(6, vectorStore.count());
        assertEquals(3, vectorStore.count(new Filter()
                .where("product_price").isEqualsTo(9.99)));
    }

    @Test
    @Order(2)
    @DisplayName("02. Similarity Search")
    public void shouldSimilaritySearch() {

        vectorStore = new AstraVectorClient()
                .database(DBNAME_VECTOR_CLIENT)
                .vectorStore(VECTOR_STORE_NAME, Product.class);

        for(Result<Product> result : vectorStore
                .similaritySearch(new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},2)
                .getResults()) {
            System.out.println(result.getId() + ") similarity=" + result.getSimilarity() + ", vector=" + Arrays.toString(result.getVector()));
        }
    }


    @Test
    @Order(3)
    @DisplayName("03. Search with Meta Data")
    public void shouldSimilaritySearchWithMetaData() {

        vectorStore = new AstraVectorClient()
                .database(DBNAME_VECTOR_CLIENT)
                .vectorStore(VECTOR_STORE_NAME, Product.class);

        for(Result<Product> result : vectorStore
                .similaritySearch(
                        new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},
                        new Filter().where("product_price").isEqualsTo(9.99),
                        2)
                .getResults()) {
            System.out.println(result.getId() + ") similarity=" + result.getSimilarity() + ", vector=" + Arrays.toString(result.getVector()));
        }
    }

}
