package com.dtsx.astra.sdk.documentation;

import com.datastax.oss.driver.api.core.CqlSession;
import com.dtsx.astra.sdk.AstraDBAdmin;
import com.dtsx.astra.sdk.cassio.CassIO;
import com.dtsx.astra.sdk.utils.TestUtils;

import java.util.UUID;

public class CassIOConnection {

    public static void main(String[] args) {

        // Create db if not exists
        UUID databaseId = new AstraDBAdmin("TOKEN")
                .createDatabase("database");

        // Initializing CqlSession
        try (CqlSession cqlSession = CassIO.init("TOKEN",
                databaseId, TestUtils.TEST_REGION,
                AstraDBAdmin.DEFAULT_KEYSPACE)) {
            cqlSession
                    .execute("SELECT datacenter FROM system.local;")
                    .one()
                    .get("datacenter", String.class);
        }
    }
}
