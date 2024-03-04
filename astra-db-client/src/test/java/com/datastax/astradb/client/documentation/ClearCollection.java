package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDB;
import com.datastax.astradb.client.AstraCollection;

public class ClearCollection {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");
    AstraCollection collection = db.createCollection("collection_vector1", 14);

    // Delete all rows from an existing collection
    collection.deleteAll();
  }
}
