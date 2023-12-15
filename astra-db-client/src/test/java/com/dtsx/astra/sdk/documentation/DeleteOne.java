package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.json.domain.DeleteQuery;

public class DeleteOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    // Delete items from an existing collection with a query
    int deletedCount = collection.deleteOne(DeleteQuery.deleteById("id1"));
  }
}
