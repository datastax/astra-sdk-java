package com.datastax.astra.documentation.collection;

import com.datastax.astra.db.AstraDBCollection;
import com.datastax.astra.db.AstraDBDatabase;
import io.stargate.sdk.data.client.model.Document;

public class ClearCollection {
  public static void main(String[] args) {
    // Connect to running dn
    AstraDBDatabase db = new AstraDBDatabase("API_ENDPOINT", "TOKEN");

    // Accessing the collection
    AstraDBCollection<Document> collection = db.getCollection("collection_simple");

    // Delete all rows from an existing collection
    collection.deleteAll();
  }
}
