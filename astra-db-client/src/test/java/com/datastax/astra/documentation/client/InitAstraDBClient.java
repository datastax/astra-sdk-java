package com.datastax.astra.documentation.client;

import com.datastax.astra.db.AstraDBClient;
import com.datastax.astra.db.AstraDBOptions;

public class InitAstraDBClient {
  public static void main(String[] args) {

    // Default Initialization
    AstraDBClient client = new AstraDBClient("TOKEN");

    // Specialize with some extra options
    AstraDBClient client2 = new AstraDBClient("TOKEN", AstraDBOptions.builder()
            .connectionRequestTimeoutInSeconds(10)
            .responseTimeoutInSeconds(10)
            // more options
            .build());

    // You can omit the token if you defined the `ASTRA_DB_APPLICATION_TOKEN`
    // environment variable or if you are using the Astra CLI.
    AstraDBClient defaultClient = new AstraDBClient();
  }
}
