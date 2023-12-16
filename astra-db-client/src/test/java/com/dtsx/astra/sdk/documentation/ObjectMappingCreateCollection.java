package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.SimilarityMetric;

public class ObjectMappingCreateCollection {

  static class Product {
    @JsonProperty("product_name") private String name;
    @JsonProperty("product_price") private Double price;
  }

  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

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
