package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraCollection;
import com.datastax.astradb.client.AstraDB;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.query.UpdateQuery;

import static io.stargate.sdk.http.domain.FilterOperator.EQUALS_TO;

public class UpdateOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraCollection collection = db.getCollection("collection_vector1");

    // You must delete any existing rows with the same IDs as the
    // rows you want to insert
    collection.deleteAll();

    // Upsert a document based on a query
    collection.updateOne(UpdateQuery.builder()
      .updateSet("product_name", 12.99)
      .where("product_name", EQUALS_TO, "HealthyFresh - Beef raw dog food")
      .build());

    // Upsert a document by ID
    collection.upsertOne(new JsonDocument()
        .id("id1")
        .put("product_name", 12.99));
  }
}
