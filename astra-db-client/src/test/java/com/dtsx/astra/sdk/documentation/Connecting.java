package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;
import java.util.UUID;

public class Connecting {
  public static void main(String[] args) {
    // Default initialization
    AstraDB db = new AstraDB("TOKEN", "API_ENDPOINT");

    // Initialize with a non-default keyspace
    AstraDB db1 = new AstraDB("TOKEN", "API_ENDPOINT", "<keyspace>");

    // Initialize with an identifier instead of an endpoint
    UUID databaseUuid = UUID.fromString("<database_id>");
    AstraDB db2 = new AstraDB("TOKEN", databaseUuid);
  }
}
