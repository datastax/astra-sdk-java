package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import org.junit.jupiter.api.Assertions;

/**
 * Test Class to work with SDK and CQL.
 */
public class AstraCqlApiTest {

    // Authentication
    static String ASTRA_DB_TOKEN = "<change me>";

    // Target db (will be created if not exist)
    static String DB_ID       = "<change me>";
    static String DB_REGION   = "<change me>";
    static String DB_KEYSPACE = "<change me>";

    public static void main(String[] args) {
        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_TOKEN)              // credentials are mandatory
                .withDatabaseId(DB_ID)                  // identifier of the database
                .withDatabaseRegion(DB_REGION)          // connection is different for each dc

                /** Specifics for cql */
                .enableCql()                            // as stateful, connection is not always establish
                .enableDownloadSecureConnectBundle()    // secure connect bundles can be downloaded
                .withCqlKeyspace(DB_KEYSPACE)           // target keyspace
                .build()) {

            // Session is already opened for you
            CqlSession cqlSession = astraClient.cqlSession();

            // Sample Usage
            ResultSet rs = cqlSession.execute("SELECT cql_version from system.local");
            Assertions.assertNotNull(rs);
            Row row = rs.one();
            Assertions.assertNotNull(row);
            Assertions.assertNotNull(row.getString("cql_version"));

            System.out.println("Your db is running on " + row.getString("cql_version"));
        }
    }

}
