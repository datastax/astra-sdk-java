package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.SelectQuery;
import io.stargate.sdk.json.domain.odm.Result;

import java.util.List;
import java.util.Optional;

public class ObjectMappingFindVector {
 static class Product {
   @JsonProperty("product_name") private String name;
   @JsonProperty("product_price") private Double price;
 }

 public static void main(String[] args) {
   AstraDB db = new AstraDB("<token>", "<api_endpoint>");
   AstraDBRepository<Product> productRepository = db
    .createCollection("collection_vector1", 14, Product.class);

   // Perform a semantic search
   float[] embeddings = new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
   Filter metadataFilter = new Filter().where("product_price").isEqualsTo(9.99);
   int maxRecord = 10;
   List<Result<Product>> res = productRepository.findVector(embeddings, metadataFilter, maxRecord);

   // If you do not have max record or metadata filter, you can use the following
   productRepository.findVector(embeddings, maxRecord);
   productRepository.findVector(embeddings, metadataFilter);
 }
}
