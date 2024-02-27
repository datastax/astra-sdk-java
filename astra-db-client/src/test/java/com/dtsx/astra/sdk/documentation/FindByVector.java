package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.odm.DocumentResult;

import java.util.Optional;

public class FindByVector {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.getCollection("collection_vector1");

    // Fetch a row by vector and return JSON
    collection
        .findOneByVector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
        .ifPresent(jsonResult -> System.out.println(jsonResult.getSimilarity()));

    // Fetch a row by ID and map it to an object with ResultMapper
    Optional<DocumentResult<MyBean>> res2 = collection
        .findOneByVector(
            new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},
                record -> {
                    MyBean bean = new MyBean(
                        (String)record.getData().get("product_name"),
                        (Double)record.getData().get("product_price"));
                    return new DocumentResult<>(record, bean);
                }
        );

    // Fetch a row by ID and map the result to a class
    Optional<DocumentResult<MyBean>> res3 = collection.findOneByVector(
        new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},
        MyBean.class);
  }

  public static class MyBean {
    @JsonProperty("product_name") String name;
    @JsonProperty("product_price") Double price;
    public MyBean(String name, Double price) {
      this.name = name;
      this.price = price;
    }
  }
}
