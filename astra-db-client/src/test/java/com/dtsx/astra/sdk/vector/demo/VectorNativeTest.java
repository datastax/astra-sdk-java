package com.dtsx.astra.sdk.vector.demo;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBClient;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.CollectionClient;
import io.stargate.sdk.json.CollectionRepository;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.odm.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

public class VectorNativeTest {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class Product {
        @JsonProperty("product_name")
        private String name;
        @JsonProperty("product_price")
        private Double price;
    }

    final String databaseName   = "vector_client_test";
    final String collectionName =  "demo_vector_store";
    final String apiKey = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
    final String apiEndpoint = "https://735f0dcd-cd2d-41bb-b20c-beb4f0226192-us-east1.apps.astra.datastax.com/api/json";

    @Test
    public void shouldDoMagic() {

        // Create Db if not exist, resume if needed
        UUID dbTest = new AstraDBClient(apiKey).createDatabase("vector_client_test");

        // Database already exists
        AstraDB db1 = new AstraDB(apiKey, apiEndpoint);
        AstraDB db2 = new AstraDBClient(apiKey).database("vector_client_test");
        AstraDB db3 = new AstraDBClient(apiKey).database(dbTest);

        // Access the db in dev
        //AstraDB dbDev = new AstraDBClient(apiKey, AstraEnvironment.DEV).database(dbTest);

        ApiLocator.getApiJsonEndpoint("735f0dcd-cd2d-41bb-b20c-beb4f0226192", "us-east1");

        // init
        AstraDB db = new AstraDB(apiKey, apiEndpoint);
        // create collection
        CollectionClient col = db.createCollection(collectionName, 14);
        // Insertions
        col.insertOne(new JsonDocument()
                .put("metadata1", "value1")
                .vector(new float[]{0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f}));
        // Search
        Page<JsonResult> resultSet = col
                .similaritySearch(new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},
                        null,2, null);

        AstraDB database = new AstraDB(apiKey, apiEndpoint);
        // Create Vector Store (if exist)
        database.deleteCollection(collectionName);
        // Create vector Store (if not exists)
        database.createCollection(collectionName, 14);

        // Given a bean 'Product', work with collection
        CollectionRepository<Product> collection = database
                .collectionRepository(collectionName, Product.class);

        // Insert Products
        collection.save(new Document<>("pf7044",
                new Product("Pupper Sausage Beef dog Treats", 9.99),
                new float[]{0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f}));
        collection.saveAll(List.of(
                new Document<>("pt0041",
                        new Product("Dog Ring Chew Toy", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f}),
                new Document<>("pf7043",
                        new Product("Pepper Sausage Bacon dog Treats", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f})
        ));

        // Similarity Search
        float[] embeddings =  new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        collection.similaritySearch(embeddings,null, 2).forEach(r->  {
            System.out.println(r.getSimilarity() + " - " + r.getId() + " - " + r.getData().getName());
        });

    }

    public void useInApplication() {

        CollectionRepository<Product> vectorCollection = new AstraDBClient(apiKey)
                .database(databaseName)
                .collectionRepository(collectionName, Product.class);

        float[] embeddings =  new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        vectorCollection.similaritySearch(embeddings,null, 2).forEach(r->  {
            System.out.println(r.getSimilarity() + " - " + r.getId() + " - " + r.getData().getName());
        });

    }
}
