package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraCollection;
import com.datastax.astradb.client.AstraDB;
import io.stargate.sdk.data.domain.query.Filter;
import io.stargate.sdk.data.domain.query.UpdateQuery;
import io.stargate.sdk.http.domain.FilterOperator;

public class UpdateMany {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraCollection collection = db.getCollection("collection_vector1");

    // Update multiple documents based on a query
    collection.updateMany(UpdateQuery.builder()
        .updateSet("product_name", 12.99)
        .filter(new Filter("product_name", FilterOperator.EQUALS_TO, "HealthyFresh - Beef raw dog food"))
        .build());
  }
}
