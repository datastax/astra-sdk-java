package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * This class will contain executable documentation from Astra DB Client documentation.
 */
public class ReferenceGuideSamples {

    // ==>  TO USE THIS CLASS PLEASE REPLACE THOSE CONSTANTS
    public static String token = "AstraCS:iLPiNPxSSIdefoRdkTWCfWXt:2b360d096e0e6cb732371925ffcc6485541ff78067759a2a1130390e231c2c7a";
    public static String apiEndpoint = "astra_db_client_test_suite_java";
    // <===



    public void listDatabases() {

        AstraDBAdmin client = new AstraDBAdmin("<replace_with_token>");
        // list all databases in your organization
        Stream<Database> list = client.findAllDatabases();

        // find a database from its unique identifier
        UUID databaseID = UUID.fromString("<replace_with_dbid>");
        Optional<Database> db = client.findDatabaseById(databaseID);

        // Find databases from a name (unicity is not garantees)
        Stream<Database> dn = client.findDatabaseByName("<replace_with_db_name>");
    }

    public void createDatabase() {
        AstraDBAdmin client = new AstraDBAdmin("<replace_with_token>");

        String databaseName = "<replace_with_db_name>";
        // GCP, AZURE or AWS
        CloudProviderType cloudProvider = CloudProviderType.GCP;
        // To get the list of available regions see below
        String cloudRegion = "us-east1";
        UUID newDbId = client.createDatabase(databaseName, cloudProvider, cloudRegion);
    }

    public void connection() {

        /*
         * Given a valid token for your astra organization
         */
        AstraDBAdmin client = new AstraDBAdmin("AstraCS:...");

        /*
         * You can omit the token if you defined the environment variable
         * `ASTRA_DB_APPLICATION_TOKEN` or you if are using the Astra CLI.
         */
        AstraDBAdmin defaultClient = new AstraDBAdmin();

        // Initialization coming from  `AstraDBClient`
        String token = "<replace_with_token>";
        UUID databaseID = UUID.fromString("<database_id>");
        AstraDB db3 = new AstraDBAdmin(token).database(databaseID);

        String databaseName = "<replace_with_db_name>";
        AstraDB db4 = new AstraDBAdmin(token).database(databaseName);
    }


    public static UUID createDatabase(String token, String dbName) {
        // Given a valid token for your astra organization
        return new AstraDBAdmin(token).createDatabase(dbName);
    }

}
