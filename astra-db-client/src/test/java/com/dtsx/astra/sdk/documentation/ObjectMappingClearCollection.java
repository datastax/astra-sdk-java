package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectMappingClearCollection {

static class Product {
 @JsonProperty("product_name")
 private String name;
 @JsonProperty("product_price")
 private Double price;
}
 public static void main(String[] args) {

// Accessing existing DB
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

// Access existing collection
AstraDBRepository<Product> collection1 = db
        .createCollection("collection_simple", Product.class);

// Clear collection
collection1.deleteAll();
}
}
