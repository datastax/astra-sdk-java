package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.Database;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FindDatabase {
  public static void main(String[] args) {
    AstraDBAdmin client = new AstraDBAdmin("TOKEN");

    // Check if a database exists
    boolean exists = client.isDatabaseExists("<database_name>");

    // Find a database by name (names may not be unique)
    Stream<Database> dbStream = client.findDatabaseByName("<database_name>");
    Optional<Database> dbByName = dbStream.findFirst();

    // Find a database by ID
    Optional<Database> dbById = client
        .findDatabaseById(UUID.fromString("<replace_with_db_uuid>"));
  }
}
