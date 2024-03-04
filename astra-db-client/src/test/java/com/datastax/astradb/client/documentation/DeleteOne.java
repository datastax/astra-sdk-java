package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDB;
import com.datastax.astradb.client.AstraCollection;
import io.stargate.sdk.data.domain.query.DeleteQuery;
import io.stargate.sdk.data.domain.query.DeleteResult;

public class DeleteOne {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraCollection collection = db.createCollection("collection_vector1", 14);

    // Delete items from an existing collection with a query
    DeleteResult deletedCount = collection
            .deleteOne(DeleteQuery.deleteById("id1"));
  }
}
