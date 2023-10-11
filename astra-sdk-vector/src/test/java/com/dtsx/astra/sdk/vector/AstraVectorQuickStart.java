package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.core.domain.ObjectMap;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.vector.JsonVectorStore;
import io.stargate.sdk.json.vector.VectorStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

public class AstraVectorQuickStart {

    public void quickStartTest() {
        String databaseName = "vector_client_test";
        String astraToken = System.getenv("ASTRA_DB_APPLICATION_TOKEN");

        AstraVectorClient vectorClient = new AstraVectorClient(astraToken);
        if (!vectorClient.isDatabaseExists(databaseName)) {
            vectorClient.createDatabase(databaseName);
        }

        // Without ODM Accessing the Vector DB with JSON-ISH
        AstraVectorDatabaseClient vectorDb = vectorClient.database(databaseName);
        JsonVectorStore jsonVectorStore =
                vectorDb.createVectorStore("demo_product", 14);

        // ======== INSERTIONS =========

        jsonVectorStore.insert(new JsonDocument("doc1")
                .put("product_name", "HealthyFresh - Beef raw dog food")
                .put("product_price", 12.99)
                .vector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        jsonVectorStore.insert(new JsonDocument("doc2")
                .data(Map.of("product_name", "HealthyFresh - Chicken raw dog food"))
                .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        jsonVectorStore.insert(new JsonDocument("doc3")
                .data("{"
                        +"   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
                        + "   \"product_price\": 9.99, "
                        + "}")
                .vector(new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}));

        //jsonVectorStore.insert("{"
        //           + "   \"_id\":\"doc4\","
        //           + "   \"$vector\":[1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f],"
        //           + "   \"product_name\": \"HealthyFresh - Chicken raw dog food\", "
        //           + "   \"product_price\": 9.99, "
        //           + "}");

        // With ODM
        VectorStore<Product> productVectorStore =
                vectorDb.createVectorStore("demo_product", 14, Product.class);

        // 3 fields: id, payload, vector
        productVectorStore.insert("doc5",
                new Product("HealthyFresh - Beef raw dog food", 12.99),
                new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});

        // build the "document" and insert the document
        Document<Product> doc6 = new Document<>("doc6",
                new Product("HealthyFresh - Beef raw dog food", 12.99),
                new float[]{1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f});
        productVectorStore.insert(doc6);

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
