package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.DeleteQuery;

public class DeleteMany {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBCollection collection = db
        .createCollection("collection_vector1",14);

    // Delete items from an existing collection with a query
    int deletedCount = collection
        .deleteMany(DeleteQuery.builder()
        .where("product_price").isEqualsTo(9.99)
        .build());
  }
}
