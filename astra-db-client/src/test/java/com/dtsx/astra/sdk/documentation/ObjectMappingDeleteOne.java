package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.odm.Document;

public class ObjectMappingDeleteOne {
  static class Product {
    @JsonProperty("product_name") private String name;
    @JsonProperty("product_price") private Double price;
  }

  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBRepository<Product> collection1 =
        db.createCollection("collection_simple", Product.class);

    // Delete a document by ID
    collection1.deleteById("id1");

    // Delete a specific document
    collection1.delete(new Document<Product>().id("id2"));
  }
}
