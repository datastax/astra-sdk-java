package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.odm.Result;

public class ObjectMappingPaging {
  static class Product {
    @JsonProperty("product_name") private String name;
    @JsonProperty("product_price") private Double price;
  }
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBRepository<Product> productRepository =
        db.createCollection("collection_vector1", 14, Product.class);

    // Retrieve page 1 of a search (up to 20 results)
    float[] embeddings = new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    Filter metadataFilter = new Filter().where("product_price").isEqualsTo(9.99);
    Page<Result<Product>> page1 = productRepository.findVector(embeddings, metadataFilter);
  }
}
