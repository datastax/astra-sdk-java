package com.dtsx.astra.sdk.vector.demo;

import com.dtsx.astra.sdk.vector.AstraVectorClient;
import com.dtsx.astra.sdk.vector.VectorDatabase;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.vector.VectorStore;
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

    @Test
    public void shouldDoMagic() {

        // Token retrieved from CLI config or env var
        AstraVectorClient vectorClient = new AstraVectorClient();

        // Create Database
        UUID uuid = vectorClient.createDatabase("vector_client_test");

        // Select Database
        VectorDatabase vectorDb = vectorClient.vectorDatabase("vector_client_test");

        // Create Vector Store
        vectorDb.deleteVectorStore("demo_vector_store");
        vectorDb.createVectorStore("demo_vector_store", 14);

        // Select Vector Store
        VectorStore<Product> vectorStore = vectorDb.vectorStore("demo_vector_store", Product.class);

        // Insert Products
        vectorStore.insert("pf7044",
                new Product("Pupper Sausage Beef dog Treats", 9.99),
                new float[]{0f, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 1f, 0f});
        vectorStore.insertAll(List.of(
                new Document<>("pt0041",
                        new Product("Dog Ring Chew Toy", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f}),
                new Document<>("pf7043",
                        new Product("Pepper Sausage Bacon dog Treats", 9.99),
                        new float[]{0f, 0f, 0f, 1f, 0f, 0f, 0f, 1f, 1f, 1f, 0f, 0f, 0f, 0f})
        ));

        // Similarity Search
        float[] embeddings =  new float[] {1f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
        vectorStore.similaritySearch(embeddings,2).forEach(r->  {
            System.out.println(r.getSimilarity() + " - " + r.getId() + " - " + r.getData().getName());
        });





    }
}
