package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;

public class AstraGrpcApiTest {

    // Authentication
    static String ASTRA_DB_TOKEN = "AstraCS:uZclXTYecCAqPPjiNmkezapR:" +
            "e87d6edb702acd87516e4ef78e0c0e515c32ab2c3529f5a3242688034149a0e4";

    // You need a running database
    static String DB_ID = "dde308f5-a8b0-474d-afd6-81e5689e3e25";
    static String DB_REGION = "eu-central-1";
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
                .enableGrpc()
                .build()) {
            System.out.println("+ Cql Version (grpc)  : " + astraClient
                    .apiStargateGrpc()
                    .execute("SELECT cql_version from system.local")
                    .one().getString("cql_version"));
        }
    }
}
