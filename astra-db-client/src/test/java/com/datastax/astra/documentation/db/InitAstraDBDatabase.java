package com.datastax.astra.documentation.db;

import com.datastax.astra.db.AstraDBDatabase;
import com.datastax.astra.db.AstraDBOptions;

public class InitAstraDBDatabase {
  public static void main(String[] args) {
    // Default initialization
    AstraDBDatabase db = new AstraDBDatabase("TOKEN", "API_ENDPOINT");

    // 'Options' allows fined-grained configuration.
    AstraDBOptions options = AstraDBOptions.builder()
            .connectionRequestTimeoutInSeconds(10)
            .connectionRequestTimeoutInSeconds(10)
            .build();
    AstraDBDatabase db2 = new AstraDBDatabase("TOKEN", "API_ENDPOINT", options);

    // Initialize with a non-default namespace.
    AstraDBDatabase db3 =
            new AstraDBDatabase("TOKEN", "API_ENDPOINT", "NAMESPACE");

    // non-default namespace + options
    AstraDBDatabase db4 =
            new AstraDBDatabase("TOKEN", "API_ENDPOINT", "NAMESPACE", options);
  }
}
