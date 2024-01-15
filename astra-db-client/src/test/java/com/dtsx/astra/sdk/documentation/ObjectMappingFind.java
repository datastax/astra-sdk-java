package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.query.SelectQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectMappingFind {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    // Retrieve the first document with a product_price
    collection.find(
        SelectQuery.builder()
            .where("product_price")
            .exists()
            .build())
        .forEach(System.out::println);

    // Retrieve the first document where product_price is 12.99
    collection.find(
        SelectQuery.builder()
            .where("product_price")
            .isEqualsTo(12.99)
            .build())
        .forEach(System.out::println);

    // Only retrieve the product_name and product_price fields
    collection.find(
        SelectQuery.builder()
            .select("product_name", "product_price")
            .where("product_price")
            .isEqualsTo(9.99)
            .build())
        .forEach(System.out::println);

    // Order the results by similarity
    collection.find(
        SelectQuery.builder()
            .where("product_price")
            .isEqualsTo(9.99)
            .orderByAnn(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
            .build())
        .forEach(System.out::println);

    // Order the results by a specific field
    collection.find(
        SelectQuery.builder()
            .where("product_name")
            .isEqualsTo("HealthyFresh - Chicken raw dog food")
            .orderBy("product_price", 1)
            .build())
        .forEach(System.out::println);

    // Complex query with AND and OR:
    //     (product_price == 9.99 OR product_name == "HealthyFresh - Beef raw dog food")
    // AND (product_price == 12.99 OR product_name == "HealthyFresh - Beef raw dog food")
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
    collection.find(sq2).forEach(System.out::println);
  }
}
