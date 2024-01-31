package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.db.domain.Database;
import java.util.stream.Stream;

public class FindAllDatabases {
  public static void main(String[] args) {
    AstraDBAdmin client = new AstraDBAdmin("TOKEN");
    boolean exists = client.isDatabaseExists("<database_name>");

    // List all available databases
    Stream<Database> dbStream = client.findAllDatabases();
  }
}
