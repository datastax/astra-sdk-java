package org.datastax.astra;

import java.nio.file.Paths;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

public class ContactTest {
    
    public static void main(String[] args) {
        // Create the CqlSession object:
        try (CqlSession session = CqlSession.builder()
            .withCloudSecureConnectBundle(Paths.get("/Users/cedricklunven/Downloads/secure-connect-freetier.zip"))
            .withAuthCredentials("astraUser","astraPassword1")
            .build()) {
            session.execute("use system");
            // Select the release_version from the system.local table:
            ResultSet rs = session.execute("select release_version from local");
            Row row = rs.one();
            //Print the results of the CQL query to the console:
            if (row != null) {
                System.out.println(row.getString("release_version"));
            } else {
                System.out.println("An error occurred.");
            }
        }
        System.exit(0);
    }
}
