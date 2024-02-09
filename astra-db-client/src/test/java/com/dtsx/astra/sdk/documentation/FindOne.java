package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.data.domain.query.SelectQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.stargate.sdk.http.domain.FilterOperator.EQUALS_TO;
import static io.stargate.sdk.http.domain.FilterOperator.EXISTS;
import static io.stargate.sdk.http.domain.FilterOperator.GREATER_THAN;
import static io.stargate.sdk.http.domain.FilterOperator.GREATER_THAN_OR_EQUALS_TO;
import static io.stargate.sdk.http.domain.FilterOperator.LESS_THAN;

public class FindOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    // Retrieve the first document where product_price exists
    Filter filter = new Filter()
            .where("product_price")
            .exists();
    collection.findOne(SelectQuery.builder()
            .filter(filter).build())
            .ifPresent(System.out::println);

    // Retrieve the first document where product_price is 12.99
    Filter filter2 = new Filter()
            .where("product_price")
            .isEqualsTo(12.99);
    collection.findOne(SelectQuery.builder()
        .filter(filter2).build())
    .ifPresent(System.out::println);

    // Send the request as a JSON String
    collection.findOne(
        "{" +
        "\"filter\":{" +
        "\"product_price\":9.99," +
        "\"product_name\":\"HealthyFresh - Chicken raw dog food\"}" +
        "}")
    .ifPresent(System.out::println);

    // Only retrieve the product_name and product_price fields
    collection.findOne(SelectQuery.builder()
        .select("product_name", "product_price")
        .filter(filter2)
        .build())
    .ifPresent(System.out::println);

    // Perform a similarity search
    collection.findOne(SelectQuery.builder()
        .filter(filter2)
        .orderByAnn(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
        .build());

    // Perform a complex query with AND and OR
    SelectQuery sq2 = new SelectQuery();
    Filter yaFilter = new Filter()
            .and()
              .or()
                .where("product_price", EQUALS_TO, 9.99)
                .where("product_name", EQUALS_TO, "HealthyFresh - Beef raw dog food")
              .end()
              .or()
                .where("product_price", EQUALS_TO, 9.99)
                .where("product_name", EQUALS_TO, "HealthyFresh - Beef raw dog food")
              .end();
    collection.findOne(sq2).ifPresent(System.out::println);
  }
}
