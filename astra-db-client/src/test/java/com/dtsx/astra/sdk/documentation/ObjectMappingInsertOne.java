package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.odm.Document;

public class ObjectMappingInsertOne {
  static class Product {
    @JsonProperty("product_name") private String name;
    @JsonProperty("product_price") private Double price;
    Product(String name, Double price) {
      this.name = name;
      this.price = price;
    }
  }

  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBRepository<Product> productRepository =
        db.createCollection("collection_vector1", 14, Product.class);

    // Upsert document
    productRepository.save(new Document<Product>()
        .id("product1")
        .vector(new float[]{1f, 0f, 1f, 1f, .5f, 1f, 0f, 0.3f, 0f, 0f, 0f, 0f, 0f, 0f})
        .data(new Product("product1", 9.99)));
  }
}
