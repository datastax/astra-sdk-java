package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.JsonDocument;
import io.stargate.sdk.data.domain.query.UpdateQuery;

public class UpdateOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.collection("collection_vector1");

    // You must delete any existing rows with the same IDs as the
    // rows you want to insert
    collection.deleteAll();

    // Upsert a document based on a query
    collection.updateOne(UpdateQuery.builder()
      .updateSet("product_name", 12.99)
      .where("product_name")
      .isEqualsTo("HealthyFresh - Beef raw dog food")
      .build());

    // Upsert a document by ID
    collection.upsertOne(new JsonDocument()
        .id("id1")
        .put("product_name", 12.99));
  }
}
