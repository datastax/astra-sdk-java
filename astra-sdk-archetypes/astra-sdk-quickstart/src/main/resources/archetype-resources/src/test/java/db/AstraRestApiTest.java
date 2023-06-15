#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.db;

import com.datastax.astra.sdk.AstraClient;
import ${package}.AbstractSdkTest;
import io.stargate.sdk.core.Ordering;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.SearchTableQuery;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AstraRestApiTest extends AbstractSdkTest {

    @BeforeAll
    public static void init() {
        loadRequiredEnvironmentVariables();
    }

    @Test
    public void shouldConnectWithRest() {

        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_APPLICATION_TOKEN)  // credentials are mandatory
                .withDatabaseId(ASTRA_DB_ID)            // identifier of the database
                .withDatabaseRegion(ASTRA_DB_REGION)    // endpoint contains region
                .build()) {

            StargateRestApiClient restApiClient = astraClient.apiStargateData();

            // List keyspaces
            System.out.println("+ Keyspaces (rest) : "+ restApiClient.keyspaceNames().collect(Collectors.toList()));

            // Create a table (if not exists)
            restApiClient.keyspace(ASTRA_DB_KEYSPACE).table("movies").create(
                    CreateTable.builder().ifNotExist(true)
                            .addPartitionKey("genre", "text")
                            .addClusteringKey("year", "int", Ordering.DESC)
                            .addClusteringKey("title", "text", Ordering.ASC)
                            .addColumn("producer", "text")
                            .build());

            // Create (Insert data)
            Map<String, Object> newRow = new HashMap<>();
            newRow.put("genre", "Sci-Fi");
            newRow.put("year", 1990);
            newRow.put("title", "Avatar");
            newRow.put("producer", "James Cameron");
            restApiClient.keyspace(ASTRA_DB_KEYSPACE)
                    .table("movies")
                    .upsert(newRow);

            // Read (Search)
            restApiClient
                    .keyspace(ASTRA_DB_KEYSPACE)
                    .table("movies")
                    .search(SearchTableQuery.builder()
                            .where("genre").isEqualsTo("Sci-Fi")
                            .withReturnedFields("title", "year")
                            .build())
                    .getResults()
                    .forEach(row -> System.out.println(row.get("title") + " (" + row.get("year") + ")"));

            // Delete
            restApiClient.keyspace(ASTRA_DB_KEYSPACE)
                    .table("movies")
                    .key("Sci-Fi", 1990, "Avatar")
                    .delete();
        }
    }
}

