package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.odm.Result;
import io.stargate.sdk.json.domain.odm.ResultMapper;

import java.util.Optional;

public class FindByVector {
    public static void main(String[] args) {

// Accessing existing DB
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

// Access existing collection
AstraDBCollection collection = db.collection("collection_vector1");

// (1) Find a Json Result from its vector
collection
  .findOneByVector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
  .ifPresent(jsonResult -> System.out.println(jsonResult.getSimilarity()));

// (2) find by id with Result Mapper
Optional<Result<MyBean>> res2 = collection
  .findOneByVector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f}, new ResultMapper<MyBean>() {
  @Override
  public Result<MyBean> map(JsonResult record) {
    MyBean bean = new MyBean(
      (String) record.getData().get("product_name"),
      (Double) record.getData().get("product_price"));
      return new Result<>(record, bean);
    }
  });

// (3) find by id with class Mapping
Optional<Result<MyBean>> res3 = collection
  .findOneByVector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},
    MyBean.class);

}

public static class MyBean {
 @JsonProperty("product_name") String name;
 @JsonProperty("product_price") Double price;
 public MyBean(String name, Double price) {
  this.name = name;
  this.price = price;
 }
 // getters and setters
}

}
