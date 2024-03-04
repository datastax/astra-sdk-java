package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraCollection;
import com.datastax.astradb.client.AstraDB;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.data.domain.query.SelectQuery;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Find {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraCollection collection = db.createCollection("collection_vector1", 14);

    // Retrieve the first document with a product_price
    Filter filter = new Filter()
            .where("product_price")
            .exists();
    collection.find(
        SelectQuery.builder().filter(filter).build()
    ).forEach(System.out::println);

    // Retrieve the first document where the product_price is 12.99
    Filter filter2 = new Filter()
            .where("product_price")
            .isEqualsTo(12.99);
    collection
            .find(SelectQuery.builder().filter(filter2).build())
            .forEach(System.out::println);

    // Only retrieve the product_name and product_price fields
    collection.find(
        SelectQuery.builder()
            .select("product_name", "product_price")
            .filter(filter2)
            .build())
        .forEach(System.out::println);

    // Order the results by similarity
    collection.find(
        SelectQuery.builder()
            .filter(filter2)
            .orderByAnn(new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f})
            .build())
        .forEach(System.out::println);

    // Order the results by a specific field
    Filter filter3 = new Filter()
            .where("product_name")
            .isEqualsTo("HealthyFresh - Chicken raw dog food");
    collection.find(
        SelectQuery.builder()
            .filter(filter3)
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
