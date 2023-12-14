package com.dtsx.astra.sdk.documentation;

import com.dtsx.astra.sdk.AstraDB;

import java.util.UUID;

public class Connecting {
    public static void main(String[] args) {
        // Default Initialization
        AstraDB db = new AstraDB("<token>", "<api_endpoint>");

        // --- Other Initializations ---

        // (1) using non-default keyspace
        AstraDB db1 = new AstraDB("<token>", "<api_endpoint>", "<keyspac_name>e");

        // (2) using identifier instead of endpoint (regions LB
        UUID databaseUuid = UUID.fromString("<database_id>");
        AstraDB db2 = new AstraDB("<token>", databaseUuid);
    }
}
