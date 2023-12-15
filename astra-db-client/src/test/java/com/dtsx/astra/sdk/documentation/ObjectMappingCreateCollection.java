package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.SimilarityMetric;

public class ObjectMappingCreateCollection {

  static class Product {

    @JsonProperty("product_name")
    private String name;

    @JsonProperty("product_price")
    private Double price;

    // getters and setters
  }

  public static void main(String[] args) {
    // Given an active db
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    /*
     * Create collection with no vector.
     */
    AstraDBRepository<Product> collection1 = db
            .createCollection("collection_simple", Product.class);

    // Create collection with vector (builder)
    AstraDBRepository<Product> collection2 = db.createCollection(CollectionDefinition
            .builder()
            .name("collection_vector2")
            .vector(1536, SimilarityMetric.euclidean)
            .build(), Product.class);
  }
}
