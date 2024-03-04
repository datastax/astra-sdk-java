package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDB;
import com.datastax.astradb.client.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.CollectionDefinition;
import io.stargate.sdk.data.domain.SimilarityMetric;

public class ObjectMappingCreateCollection {

  static class Product {
    @JsonProperty("product_name") private String name;
    @JsonProperty("product_price") private Double price;
  }

  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Create a non-vector collection
    AstraDBRepository<Product> collection1 =
        db.createCollection("collection_simple", Product.class);

    // Create a vector collection with a builder
    AstraDBRepository<Product> collection2 =
        db.createCollection(
            CollectionDefinition.builder()
                .name("collection_vector2")
                .vector(1536, SimilarityMetric.euclidean)
                .build(),
            Product.class);
  }
}
