#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.db;

import com.datastax.astra.sdk.AstraClient;
import ${package}.AbstractSdkTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AstraGrpcApiTest extends AbstractSdkTest {

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
                .enableGrpc()                           // Import to start the grpc Channel
                .build()) {

            System.out.println("+ Cql Version (grpc)  : " + astraClient
                    .apiStargateGrpc()
                    .execute("SELECT cql_version from system.local")
                    .one().getString("cql_version"));
        }
    }
}

