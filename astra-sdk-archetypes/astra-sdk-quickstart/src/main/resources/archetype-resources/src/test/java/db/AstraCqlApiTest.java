#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.db;

import com.datastax.astra.sdk.AstraClient;
import ${package}.AbstractSdkTest;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Test Class to work with SDK and CQL.
 * Variables are defined and loading in Super class @see {@link AbstractSdkTest}
 */
public class AstraCqlApiTest extends AbstractSdkTest {

    @BeforeAll
    public static void init() {
        loadRequiredEnvironmentVariables();
    }

    @Test
    public void shouldConnectWithCql() {

        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_APPLICATION_TOKEN)   // credentials are mandatory
                .withDatabaseId(ASTRA_DB_ID)             // identifier of the database
                .withDatabaseRegion(ASTRA_DB_REGION)     // connection is different for each dc
                .enableCql()                             // as stateful, connection is not always establish
                .enableDownloadSecureConnectBundle()     // secure connect bundles can be downloaded
                .withCqlKeyspace(ASTRA_DB_KEYSPACE)      // target keyspace
                .build()) {

            // Session is already opened for you
            CqlSession cqlSession = astraClient.cqlSession();

            // Sample Usage
            Row row = cqlSession.execute("SELECT cql_version from system.local").one();
            Assertions.assertNotNull(row);
            Assertions.assertNotNull(row.getString("cql_version"));
            System.out.println("Your db is running on " + row.getString("cql_version"));
        }
    }

}
