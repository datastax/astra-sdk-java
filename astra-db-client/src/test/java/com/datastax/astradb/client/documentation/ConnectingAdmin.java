package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDBAdmin;

public class ConnectingAdmin {
  public static void main(String[] args) {
    // Default Initialization
    AstraDBAdmin client = new AstraDBAdmin("TOKEN");

    // You can omit the token if you defined the `ASTRA_DB_APPLICATION_TOKEN`
    // environment variable or if you are using the Astra CLI.
    AstraDBAdmin defaultClient=new AstraDBAdmin();
  }
}
