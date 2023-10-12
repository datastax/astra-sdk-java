package com.dtsx.astra.sdk.vector;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.vector.JsonVectorStore;
import io.stargate.sdk.json.vector.VectorStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class AstraVectorQuickStart {




    @Test
    public void quickStartTest() {
        String databaseName    = "vector_client_test";
        String vectorStoreName = "demo_store";
        String astraToken      = System.getenv("ASTRA_DB_APPLICATION_TOKEN");

        // 1a. Initialization with a client
        AstraVectorClient vectorClient = new AstraVectorClient(astraToken);

        // 1b. Create DB (Skip if you already have a database running)
        if (!vectorClient.isDatabaseExists(databaseName)) {
            vectorClient.createDatabase(databaseName);
        }

        // 2. Create a  store (delete if exist)
        AstraVectorDatabaseClient vectorDb = vectorClient.database(databaseName);
        vectorDb.deleteStore(vectorStoreName);
        vectorDb.createVectorStore(vectorStoreName, 14);

        // 3. Insert data in the store
        JsonVectorStore vectorStore = vectorDb.vectorStore(vectorStoreName);
           // 3a. Insert One (attributes as key/value)
           vectorStore.insert(new JsonDocument()
                .id("doc1") // generated if not set
                .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                .put("product_name", "HealthyFresh - Beef raw dog food")
                .put("product_price", 12.99));
           // 3b. Insert One (attributes as JSON)
           vectorStore.insert(new JsonDocument()
                 .id("doc2")
                 .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                 .data("{"
                   +"   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
                   + "  \"product_price\": 9.99"
                   + "}")
                 );
           // 3c. Insert One (attributes as a MAP)
           vectorStore.insert(new JsonDocument()
                .id("doc3")
                .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
                .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food"))
           );
           // 3d. Insert as a single Big JSON
           vectorStore.insert("{"
                   + "   \"_id\":\"doc4\","
                   + "   \"$vector\":[1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0],"
                   + "   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
                   + "   \"product_price\": 9.99"
                   + "}");

        // With ODM
        VectorStore<Product> productVectorStore = vectorDb.vectorStore(vectorStoreName, Product.class);

        // 3 fields: id, payload, vector
        productVectorStore.insert("doc5",
                new Product("HealthyFresh - Beef raw dog food", 12.99),
                new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});

        // build the "document" and insert the document
        Document<Product> doc6 = new Document<>("doc6",
                new Product("HealthyFresh - Beef raw dog food", 12.99),
                new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});
        productVectorStore.insert(doc6);

        Assertions.assertEquals(6, productVectorStore.count());
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
