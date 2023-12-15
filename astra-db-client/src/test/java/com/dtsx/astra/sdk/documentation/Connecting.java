package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import java.util.UUID;

public class Connecting {
  public static void main(String[] args) {
    // Default initialization
    AstraDB db = new AstraDB("<token>", "<api_endpoint>");

    // Initialize with a non-default keyspace
    AstraDB db1 = new AstraDB("<token>", "<api_endpoint>", "<keyspace>");

    // Initialize with an identifier instead of an endpoint
    UUID databaseUuid = UUID.fromString("<database_id>");
    AstraDB db2 = new AstraDB("<token>", databaseUuid);
  }
}
