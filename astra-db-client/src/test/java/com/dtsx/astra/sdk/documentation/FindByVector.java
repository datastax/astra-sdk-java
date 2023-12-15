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
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBCollection collection = db.collection("collection_vector1");

    // Fetch a row by vector and return JSON
    collection
        .findOneByVector(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
        .ifPresent(jsonResult -> System.out.println(jsonResult.getSimilarity()));

    // Fetch a row by ID and map it to an object with ResultMapper
    Optional<Result<MyBean>> res2 = collection
        .findOneByVector(
            new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f},
            new ResultMapper<MyBean>() {
                @Override
                public Result<MyBean> map(JsonResult record) {
                    MyBean bean = new MyBean(
                        (String)record.getData().get("product_name"),
                        (Double)record.getData().get("product_price"));
                    return new Result<>(record, bean);
                }
            }
        );

    // Fetch a row by ID and map the result to a class
    Optional<Result<MyBean>> res3 = collection.findOneByVector(
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
