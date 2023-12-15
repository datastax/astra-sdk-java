package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.SelectQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindOne {
public static void main(String[] args) {

// Accessing existing DB
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

// Access existing collection
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

// Retrieve first document where product_price exists
    collection.findOne(SelectQuery.builder()
                    .where("product_price")
                    .exists().build())
            .ifPresent(System.out::println);

// Retrieve first document where product_price is 12.99
    collection.findOne(SelectQuery.builder()
                    .where("product_price")
                    .isEqualsTo(12.99).build())
            .ifPresent(System.out::println);

// Retrieve first document where product_price is 12.99 and product_name is "HealthyFresh - Beef raw dog food"
    collection.findOne(SelectQuery.builder()
                    .where("product_name").isEqualsTo("HealthyFresh - Chicken raw dog food")
                    .andWhere("product_price").isEqualsTo(9.99).build())
            .ifPresent(System.out::println);

// Send the request as a JSON String
    collection.findOne("{" +
                    "\"filter\":{" +
                    "\"product_price\":9.99,\"product_name\":\"HealthyFresh - Chicken raw dog food\"}" +
                    "}")
            .ifPresent(System.out::println);

// Limit retrieved fields to product_name and product_price
    collection
            .findOne(SelectQuery.builder()
                    .select("product_name", "product_price")
                    .where("product_price")
                    .isEqualsTo(9.99)
                    .build())
            .ifPresent(System.out::println);

// Add an Ann Search
    collection.findOne(SelectQuery
            .builder()
            .where("product_price")
            .isEqualsTo(9.99)
            .orderByAnn(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
            .build());

// Complex query with AND and OR
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

// Complex query with AND and OR as String
collection.findOne("{\"filter\":{" +
        "\"$and\":[" +
        "{\"$or\":[" +
        "  {\"product_price\":9.99}," +
        "  {\"product_name\":\"HealthyFresh - Beef raw dog food\"}" +
        " ]" +
        "}," +
        "{\"$or\":[" +
        "  {\"product_price\":12.99}," +
        "  {\"product_name\":\"HealthyFresh - Beef raw dog food\"}" +
        " ]" +
        "}" +
        "]" +
        "}}").ifPresent(System.out::println);
}

}