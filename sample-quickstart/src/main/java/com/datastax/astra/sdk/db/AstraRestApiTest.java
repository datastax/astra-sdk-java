package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import io.stargate.sdk.core.Ordering;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.rest.TableClient;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.SearchTableQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AstraRestApiTest {

    // Authentication
    static String ASTRA_DB_TOKEN = "<change me>";

    // Target db (will be created if not exist)
    static String DB_ID       = "<change me>";
    static String DB_REGION   = "<change me>";
    static String DB_KEYSPACE = "<change me>";

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

            StargateRestApiClient restApiClient = astraClient.apiStargateData();

            // List keyspaces
            System.out.println("+ Keyspaces (rest) : "+ restApiClient.keyspaceNames().collect(Collectors.toList()));

            // Create a table (if not exists)
            restApiClient.keyspace(DB_KEYSPACE).table("movies").create(
                    CreateTable.builder().ifNotExist(true)
                            .addPartitionKey("genre", "text")
                            .addClusteringKey("year", "int", Ordering.DESC)
                            .addClusteringKey("title", "text", Ordering.ASC)
                            .addColumn("producer", "text")
                            .build());

            // Insert data
            Map<String, Object> newRow = new HashMap<>();
            newRow.put("genre", "Sci-Fi");
            newRow.put("year", 1990);
            newRow.put("title", "Avatar");
            newRow.put("producer", "James Cameron");
            restApiClient.keyspace(DB_KEYSPACE)
                    .table("movies")
                    .upsert(newRow);

            // Search
            restApiClient
                    .keyspace(DB_KEYSPACE)
                    .table("movies")
                    .search(SearchTableQuery.builder()
                            .where("genre").isEqualsTo("Sci-Fi")
                            .withReturnedFields("title", "year")
                            .build())
                    .getResults()
                    .forEach(row -> System.out.println(row.get("title") + " (" + row.get("year") + ")"));

            restApiClient.keyspace(DB_KEYSPACE)
                    .table("movies")
                    .key("Sci-Fi", 1990, "Avatar")
                    .delete();
        }
    }
}
