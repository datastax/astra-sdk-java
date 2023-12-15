package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.Database;

import java.util.stream.Stream;

public class FindAllDatabases {
    public static void main(String[] args) {

AstraDBAdmin client = new AstraDBAdmin("<replace_with_token>");

// Check is a database exists
boolean exists = client.isDatabaseExists("<replace_with_db_name>");

// List all available databases
Stream<Database> dbStream = client.findAllDatabases();
}
}
