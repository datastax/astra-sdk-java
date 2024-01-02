package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.UpdateQuery;

public class UpdateMany {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBCollection collection = db.collection("collection_vector1");

    // Update multiple documents based on a query
    collection.updateMany(UpdateQuery.builder()
        .updateSet("product_name", 12.99)
        .where("product_name")
        .isEqualsTo("HealthyFresh - Beef raw dog food")
        .build());
  }
}
