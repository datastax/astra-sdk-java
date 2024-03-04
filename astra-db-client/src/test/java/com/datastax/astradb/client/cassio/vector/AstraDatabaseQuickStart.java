package com.datastax.astradb.client.cassio.vector;

import com.datastax.astradb.client.AstraDB;
import com.datastax.astradb.client.AstraDBAdmin;
import com.datastax.astradb.client.AstraCollection;
import com.datastax.astradb.client.AstraDBRepository;
import com.datastax.astradb.client.AstraDatabaseTestSuiteIT;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.odm.Document;
import io.stargate.sdk.data.domain.odm.DocumentResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class AstraDatabaseQuickStart {

    @Test
    public void quickStartTest() {
        String astraToken      = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
        String databaseName    = AstraDatabaseTestSuiteIT.TEST_DBNAME;
        String collectionName  = "collection_quickstart";

        // 1a. Initialization with a client
        AstraDBAdmin astraDBAdmin = new AstraDBAdmin(astraToken);
        // 1b. Create DB (Skip if you already have a database running)
        if (!astraDBAdmin.isDatabaseExists(databaseName)) {
            astraDBAdmin.createDatabase(databaseName);
        }

        // 2. Create a  store (delete if exist)
        AstraDB astraDBClient = astraDBAdmin.getDatabase(databaseName);
        // 3. Insert data in the store
        astraDBClient.deleteCollection(collectionName);
        AstraCollection collection = astraDBClient.createCollection(collectionName, 14);
           // 3a. Insert One (attributes as key/value)
           collection.insertOne(new JsonDocument()
                .id("doc1") // generated if not set
                .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                .put("product_name", "HealthyFresh - Beef raw dog food")
                .put("product_price", 12.99));
           // 3b. Insert One (attributes as JSON)
           collection.insertOne(new JsonDocument()
                 .id("doc2")
                 .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                 .data("{"
                   +"   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
                   + "  \"product_price\": 9.99"
                   + "}")
                 );
           // 3c. Insert One (attributes as a MAP)
           collection.insertOne(new JsonDocument()
                .id("doc3")
                .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food"))
           );
           // 3d. Insert as a single Big JSON
            collection.insertOne(new JsonDocument()
                    .id("doc4")
                    .vector(new float[]{1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f})
                    .put("product_name", "HealthyFresh - Chicken raw dog food")
                    .put("product_price", 9.99));
           collection.findVector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});

        // ------------------------------------------
        // ----- Crud Repository                 ----
        // ------------------------------------------

        // With ODM
        AstraDBRepository<Product> productRepository =
                astraDBClient.getCollection(collectionName, Product.class);

        // 3 fields: id, payload, vector
        productRepository.insert(new Document<>("doc5",
                new Product("HealthyFresh - Beef raw dog food", 12.99),
                new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        // build the "document" and insert the document
        Document<Product> doc6 = new Document<>("doc6",
                new Product("HealthyFresh - Beef raw dog food", 12.99),
                new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});
        productRepository.insert(doc6);

        Assertions.assertEquals(6, productRepository.count());

        List<DocumentResult<Product>> results = productRepository
                .findVector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}, null, 2);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Product {
        @JsonProperty("product_name")
        private String name;
        @JsonProperty("product_price")
        private Double price;
    }
}
