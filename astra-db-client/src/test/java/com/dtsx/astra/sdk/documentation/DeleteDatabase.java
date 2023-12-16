package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;

public class DeleteDatabase {
  public static void main(String[] args) {
    AstraDBAdmin client = new AstraDBAdmin("<token>");

    // Delete an existing database
    client.deleteDatabase("<database_name>");
  }
}
