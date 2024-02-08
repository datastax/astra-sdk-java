package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.data.domain.query.SelectQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    sq2.setFilter(new HashMap<>());
    Map<String, List<Map<String, Object>>> or1Criteria = new HashMap<>();
    or1Criteria.put("$or", new ArrayList<Map<String, Object>>());
    or1Criteria.get("$or").add(Map.of("product_price", 9.99));
    or1Criteria.get("$or").add(Map.of("product_name", "HealthyFresh - Beef raw dog food"));
    Map<String, List<Map<String, Object>>> or2Criteria = new HashMap<>();
    or2Criteria.put("$or", new ArrayList<Map<String, Object>>());
    or2Criteria.get("$or").add(Map.of("product_price", 12.99));
    or2Criteria.get("$or").add(Map.of("product_name", "HealthyFresh - Beef raw dog food"));
    List<Map<String, List<Map<String, Object>>>> andCriteria = new ArrayList<>();
    andCriteria.add(or1Criteria);
    andCriteria.add(or2Criteria);
    sq2.getFilter().put("$and", andCriteria);
    collection.findOne(sq2).ifPresent(System.out::println);

    // Perform a complex query with AND and OR as String
    collection.findOne(
        "{\"filter\":{" +
        "\"$and\":[" +
        "{\"$or\":[" +
        "  {\"product_price\":9.99}," +
        "  {\"product_name\":\"HealthyFresh - Beef raw dog food\"}" +
        "]" +
        "}," +
        "{\"$or\":[" +
        "  {\"product_price\":12.99}," +
        "  {\"product_name\":\"HealthyFresh - Beef raw dog food\"}" +
        "]" +
        "}" +
        "]" +
        "}}")
    .ifPresent(System.out::println);
  }
}
