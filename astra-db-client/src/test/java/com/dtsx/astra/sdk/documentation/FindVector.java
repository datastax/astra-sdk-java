package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.Filter;
import io.stargate.sdk.json.domain.JsonResult;
import io.stargate.sdk.json.domain.SelectQuery;
import java.util.stream.Stream;

public class FindVector {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    float[] embeddings = new float[]{1f, 0f, 1f, 1f, 1f, 1f, 0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f};
    Filter metadataFilter = new Filter().where("product_price").isEqualsTo(9.99);
    int maxRecord = 10;

    // Retrieve all document with product price based on the ann search
    collection.findVector(SelectQuery.builder()
       .withFilter(metadataFilter)
       .orderByAnn(embeddings)
       .withLimit(maxRecord)
       .build())
    .forEach(System.out::println);

    // Same using another signature
    Stream<JsonResult> result = collection.findVector(embeddings, metadataFilter, maxRecord);
  }
}
