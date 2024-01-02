package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.odm.Document;
import java.util.List;

public class ObjectMappingInsertMany {
  static class Product {
    @JsonProperty("product_name") private String name;
    @JsonProperty("product_price") private Double price;
    Product(String name, Double price) {
      this.name = name;
      this.price = price;
    }
  }

  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBRepository<Product> productRepository =
        db.createCollection("collection_vector1", 14, Product.class);

    // Insert documents into the collection (IDs are generated automatically)
    List<String> identifiers = productRepository.saveAll(
        List.of(
            new Document<Product>()
                .vector(new float[]{1f, 0f, 1f, 1f, .5f, 1f, 0f, 0.3f, 0f, 0f, 0f, 0f, 0f, 0f})
                .data(new Product("product1", 9.99)),
            new Document<Product>()
                .vector(new float[]{1f, 0f, 1f, 1f, .5f, 1f, 0f, 0.3f, 0f, 0f, 0f, 0f, 0f, 0f})
                .data(new Product("product2", 12.99))));
  }
}
