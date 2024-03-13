package com.datastax.astra.documentation.db;

import com.datastax.astra.db.AstraDBDatabase;

public class CreateNamespace {
  public static void main(String[] args) {
    // Default initialization
    AstraDBDatabase db = new AstraDBDatabase("API_ENDPOINT", "TOKEN");

    // Create a new namespace
    db.createNamespace("<namespace_name>");
  }
}
