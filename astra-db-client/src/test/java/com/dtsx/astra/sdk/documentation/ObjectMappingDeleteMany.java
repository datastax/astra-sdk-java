package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.DeleteQuery;

public class ObjectMappingDeleteMany {

    static class Product {

        @JsonProperty("product_name")
        private String name;

        @JsonProperty("product_price")
        private Double price;

        // getters and setters
    }

    public static void main(String[] args) {

// Given an active db and a collection with a vector field (see CreateCollection.java)
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

/*
 * Create collection with no vector.
 */
AstraDBRepository<Product> collection1 = db
  .createCollection("collection_simple", Product.class);

// Delete item based on a query
int deletedCount = collection1.deleteAll(DeleteQuery.builder()
       .where("product_price").isEqualsTo(9.99)
       .build());
    }
}
