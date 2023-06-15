package com.datastax.astra.sdk.quickstart.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.quickstart.AbstractSdkTest;
import io.stargate.sdk.core.Ordering;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.SearchTableQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AstraGraphQLApiTest extends AbstractSdkTest {

    @BeforeAll
    public static void init() {
        loadRequiredEnvironmentVariables();
    }

    @Test
    public void shouldConnectWithGraphQL() {

        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_APPLICATION_TOKEN)  // credentials are mandatory
                .withDatabaseId(ASTRA_DB_ID)            // identifier of the database
                .withDatabaseRegion(ASTRA_DB_REGION)    // endpoint contains region
                .build()) {

            // List Keyspaces
            System.out.println("Keyspaces:" + astraClient
                    .apiStargateGraphQL()
                    .keyspaceDDL()
                    .keyspaces());

            // List Tables
            String getTables = "query GetTables {\n"
                    + "  keyspace(name: \"" + ASTRA_DB_KEYSPACE + "\") {\n"
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

