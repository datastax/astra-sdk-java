package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseInfo;
import com.dtsx.astra.sdk.utils.ApiLocator;
import io.stargate.sdk.json.domain.CollectionDefinition;
import io.stargate.sdk.json.domain.SimilarityMetric;
import org.junit.jupiter.api.Test;

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

    public static void main(String[] args) {

        connectionToDatabase();


    }

    public static void connectionToDatabase() {

        // Default Initialization
        AstraDB db = new AstraDB(token, apiEndpoint);

        // --- Other Initialization options ---

        // (1) if keyspace is different from default_keyspace
        String keyspace =  "default_keyspace";
        AstraDB db1 = new AstraDB(token, apiEndpoint, keyspace);

        // (2) With a database identifier
        UUID databaseID = UUID.fromString("<database_id>");
        AstraDB db2 = new AstraDB(token, databaseID);
    }

    public void listCollections() {
        // Given an active db
        String token = "<token>";
        String apiEndpoint = "<api_endpoint>";
        AstraDB db = new AstraDB(token, apiEndpoint);

        // List all collections
        db.findAllCollections().forEach(col -> {
            System.out.println("name=" + col.getName());
            if (col.getOptions() != null && col.getOptions().getVector() != null) {
                CollectionDefinition.Options.Vector vector = col.getOptions().getVector();
                System.out.println("dim=" + vector.getDimension());
                System.out.println("metric=" + vector.getMetric());
            }
        });
    }

    public void createCollection() {
        AstraDB db = new AstraDB("<token>", "<api_endpoint>");

        AstraDBCollection collection1 = db.createCollection("collection1", 1536, SimilarityMetric.cosine);

    }


    public void listDatabases() {

        AstraDBClient client = new AstraDBClient("<replace_with_token>");
        // list all databases in your organization
        Stream<Database> list = client.findAllDatabases();

        // find a database from its unique identifier
        UUID databaseID = UUID.fromString("<replace_with_dbid>");
        Optional<Database> db = client.findDatabaseById(databaseID);

        // Find databases from a name (unicity is not garantees)
        Stream<Database> dn = client.findDatabaseByName("<replace_with_db_name>");
    }

    public void createDatabase() {
        AstraDBClient client = new AstraDBClient("<replace_with_token>");

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
        AstraDBClient client = new AstraDBClient("AstraCS:...");

        /*
         * You can omit the token if you defined the environment variable
         * `ASTRA_DB_APPLICATION_TOKEN` or you if are using the Astra CLI.
         */
        AstraDBClient defaultClient = new AstraDBClient();

        // Initialization coming from  `AstraDBClient`
        String token = "<replace_with_token>";
        UUID databaseID = UUID.fromString("<database_id>");
        AstraDB db3 = new AstraDBClient(token).database(databaseID);

        String databaseName = "<replace_with_db_name>";
        AstraDB db4 = new AstraDBClient(token).database(databaseName);
    }


    public static UUID createDatabase(String token, String dbName) {
        // Given a valid token for your astra organization
        return new AstraDBClient(token).createDatabase(dbName);
    }

}
