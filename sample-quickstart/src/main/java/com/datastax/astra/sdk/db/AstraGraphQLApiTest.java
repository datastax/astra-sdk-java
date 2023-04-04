package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;

public class AstraGraphQLApiTest {
    // Authentication
    static String ASTRA_DB_TOKEN = "AstraCS:uZclXTYecCAqPPjiNmkezapR:" +
            "e87d6edb702acd87516e4ef78e0c0e515c32ab2c3529f5a3242688034149a0e4";

    // You need a running database
    static String DB_ID = "dde308f5-a8b0-474d-afd6-81e5689e3e25";
    static String DB_REGION = "eu-central-1";
    static String DB_KEYSPACE = "ks_mtg";
    // <---

    /**
     * Sample Code to work with Document API.
     *
     * @param args
     *         no arguments
     */
    public static void main(String[] args) {

        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_TOKEN)        // credentials are mandatory
                .withDatabaseId(DB_ID)            // identifier of the database
                .withDatabaseRegion(DB_REGION)    // endpoint contains region
                .build()) {

            // List Keyspaces
            System.out.println("Keyspaces:" + astraClient
                    .apiStargateGraphQL()
                    .keyspaceDDL()
                    .keyspaces());

            // List Tables
            String getTables = "query GetTables {\n"
                    + "  keyspace(name: \"" + DB_KEYSPACE + "\") {\n"
                    + "      name\n"
                    + "      tables {\n"
                    + "          name\n"
                    + "          columns {\n"
                    + "              name\n"
                    + "              kind\n"
                    + "              type {\n"
                    + "                  basic\n"
                    + "                  info {\n"
                    + "                      name\n"
                    + "                  }\n"
                    + "              }\n"
                    + "          }\n"
                    + "      }\n"
                    + "  }\n"
                    + "}";

            System.out.println("Tables : " + astraClient
                    .apiStargateGraphQL()
                    .keyspaceDDL()
                    .execute(getTables));
        }
    }
}
