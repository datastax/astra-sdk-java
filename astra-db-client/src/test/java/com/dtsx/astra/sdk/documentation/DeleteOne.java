package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import com.dtsx.astra.sdk.AstraDBCollection;
import io.stargate.sdk.data.domain.query.DeleteQuery;
import io.stargate.sdk.data.domain.query.DeleteResult;

public class DeleteOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraDBCollection collection = db.createCollection("collection_vector1", 14);

    // Delete items from an existing collection with a query
    DeleteResult deletedCount = collection
            .deleteOne(DeleteQuery.deleteById("id1"));
  }
}
