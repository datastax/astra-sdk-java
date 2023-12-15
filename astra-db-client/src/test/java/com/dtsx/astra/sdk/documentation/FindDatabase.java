package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.Database;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class FindDatabase {
    public static void main(String[] args) {

AstraDBAdmin client = new AstraDBAdmin("<replace_with_token>");

// Check is a database exists
boolean exists = client.isDatabaseExists("<replace_with_db_name>");

// Find a database from its name (name does not ensure unicity)
Stream<Database> dbStream = client.findDatabaseByName("<replace_with_db_name>");
Optional<Database> dbByName = dbStream
 .findFirst();

// Find a database from its id
Optional<Database> dbById =  client
 .findDatabaseById(UUID.fromString("<replace_with_db_uuid>"));
}
}
