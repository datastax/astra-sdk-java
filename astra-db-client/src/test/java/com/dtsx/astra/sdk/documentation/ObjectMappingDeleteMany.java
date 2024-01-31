package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.query.DeleteQuery;

public class ObjectMappingDeleteMany {
  static class Product {
    @JsonProperty("product_name") private String name;
    @JsonProperty("product_price") private Double price;
  }

  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Create a vector collection
    AstraDBRepository<Product> collection1 =
        db.createCollection("collection_simple", Product.class);

    // Delete rows based on a query
    int deletedCount = collection1.deleteAll(
        DeleteQuery.builder()
            .where("product_price")
            .isEqualsTo(9.99)
            .build());
  }
}
