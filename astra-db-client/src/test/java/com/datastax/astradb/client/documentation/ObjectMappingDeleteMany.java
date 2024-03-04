package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDB;
import com.datastax.astradb.client.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.query.DeleteQuery;
import io.stargate.sdk.data.domain.query.DeleteResult;

import static io.stargate.sdk.http.domain.FilterOperator.EQUALS_TO;

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
    DeleteQuery q = DeleteQuery.builder()
            .where("product_price", EQUALS_TO, 9.99)
            .build();
    DeleteResult res = collection1.deleteAll(q);
  }
}
