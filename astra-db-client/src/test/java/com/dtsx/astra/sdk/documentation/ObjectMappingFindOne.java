package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.odm.Result;

import java.util.Optional;

public class ObjectMappingFindOne {
 static class Product {
   @JsonProperty("product_name") private String name;
   @JsonProperty("product_price") private Double price;
 }

public static void main(String[] args) {
   AstraDB db = new AstraDB("<token>", "<api_endpoint>");
   AstraDBRepository<Product> productRepository = db
      .createCollection("collection_vector1", 14, Product.class);

   // Retrieve a products from its id
   Optional<Result<Product>> res1 = productRepository.findById("id1");

    // Retrieve a products from its vector (id any)
    float[] vector = new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    Optional<Result<Product>> res2 = productRepository.findByVector(vector);
 }
}
