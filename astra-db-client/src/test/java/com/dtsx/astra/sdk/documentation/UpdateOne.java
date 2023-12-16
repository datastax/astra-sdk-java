package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.JsonDocument;
import io.stargate.sdk.json.domain.UpdateQuery;

public class UpdateOne {
 public static void main(String[] args) {
   AstraDB db = new AstraDB("<token>", "<api_endpoint>");
   AstraDBCollection collection = db.collection("collection_vector1");

   // You must delete any existing rows with the same IDs as the
    // rows you want to insert
    collection.deleteAll();

    // Insert rows defined by key/value
    collection.updateOne(UpdateQuery.builder()
      .updateSet("product_name", 12.99)
      .where("product_name")
      .isEqualsTo("HealthyFresh - Beef raw dog food")
      .build()
    );

    // Upsert
    collection.upsert(new JsonDocument()
            .id("id1")
            .put("product_name", 12.99));
 }
}
