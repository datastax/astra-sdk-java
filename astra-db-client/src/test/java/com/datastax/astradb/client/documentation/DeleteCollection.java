package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDB;

public class DeleteCollection {
  public static void main(String[] args) {
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Delete an existing collection
    db.deleteCollection("collection_vector2");
  }
}
