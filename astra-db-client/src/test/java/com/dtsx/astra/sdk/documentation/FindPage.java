package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.data.domain.JsonDocumentResult;
import io.stargate.sdk.data.domain.odm.DocumentResult;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.data.domain.query.SelectQuery;

public class FindPage {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    // Retrieve page 1 of a search (up to 20 results)
    Filter filter = new Filter()
            .where("product_price")
            .exists();
    Page<JsonDocumentResult> page1 = collection.findPage(
        SelectQuery.builder()
            .filter(filter)
            .build());

    // Retrieve page 2 of the same search (if there are more than 20 results)
    Filter filter2 = new Filter()
            .where("product_price")
            .isEqualsTo(12.99);
    page1.getPageState().ifPresent(pageState -> {
        Page<JsonDocumentResult> page2 = collection.findPage(
            SelectQuery.builder()
                .filter(filter2)
                .withPagingState(pageState)
                .build());
    });

    // You can map the output as Result<T> using either a Java pojo or mapper
    Page<DocumentResult<MyBean>> page = collection.findPage(
        SelectQuery.builder().filter(filter2).build(),
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
