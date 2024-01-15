package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.data.domain.JsonDocumentResult;
import io.stargate.sdk.data.domain.odm.DocumentResult;
import java.util.Optional;

public class FindById {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBCollection collection = db.collection("collection_vector1");

    // Fetch a document by ID and return it as JSON
    Optional<JsonDocumentResult> res = collection.findById("doc1");
    res.ifPresent(jsonResult -> System.out.println(jsonResult.getSimilarity()));

    // Fetch a document by ID and map it to an object with ResultMapper
    Optional<DocumentResult<MyBean>> res2 = collection.findById("doc1", record -> {
      MyBean bean = new MyBean(
          (String) record.getData().get("product_name"),
          (Double) record.getData().get("product_price"));
      return new DocumentResult<>(record, bean);
    });

    // Fetch a document by ID and map it to a class
    Optional<DocumentResult<MyBean>> res3 = collection.findById("doc1", MyBean.class);

    // Check if a document exists
    boolean exists = collection.isDocumentExists("doc1");
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
