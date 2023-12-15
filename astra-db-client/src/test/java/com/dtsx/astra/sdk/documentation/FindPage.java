package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.stargate.sdk.core.domain.Page;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.SelectQuery;
import io.stargate.sdk.json.domain.odm.Result;

public class FindPage {
    public static void main(String[] args) {

// Accessing existing DB
AstraDB db = new AstraDB("<token>", "<api_endpoint>");

// Access existing collection
AstraDBCollection collection = db
 .createCollection("collection_vector1", 14);

// Retrieve page1 of a search
Page<JsonResult> page1 = collection.findPage(SelectQuery.builder()
 .where("product_price").isEqualsTo(9.99)
 .build());

// Retrieving page 2 of the same search if more than 20
page1.getPageState().ifPresent(pageState -> {
    Page<JsonResult> page2 = collection.findPage(SelectQuery.builder()
     .where("product_price").isEqualsTo(9.99)
     .withPagingState(pageState)
     .build());
});

/*
 * As for any find* you can map the output as Result<T>
 * using either a java pojo or mapper.
 */
Page<Result<MyBean>> page = collection.findPage(SelectQuery.builder()
 .where("product_price").isEqualsTo(9.99)
 .build(), MyBean.class);

}

public static class MyBean {
    @JsonProperty("product_name")
    String name;
    @JsonProperty("product_price")
    Double price;

    public MyBean(String name, Double price) {
        this.name = name;
        this.price = price;
    }
// getters and setters
}
}
