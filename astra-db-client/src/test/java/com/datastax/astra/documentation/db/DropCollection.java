package com.datastax.astra.documentation.db;

import com.datastax.astra.db.AstraDBDatabase;

public class DropCollection {
  public static void main(String[] args) {
    AstraDBDatabase db = new AstraDBDatabase("API_ENDPOINT", "TOKEN");

    // Delete an existing collection
    db.dropCollection("collection_vector2");
  }
}
