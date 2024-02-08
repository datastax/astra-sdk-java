package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;

import java.util.UUID;

public class DeleteDatabase {
  public static void main(String[] args) {
    AstraDBAdmin client = new AstraDBAdmin("TOKEN");

    // Delete an existing database
    client.deleteDatabaseByName("<database_name>");

    // Delete an existing database by ID
    client.deleteDatabaseById(
            UUID.fromString("<replace_with_db_uuid>"));
  }
}
