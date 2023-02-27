package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.DatabasesClient;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.org.OrganizationsClient;
import com.dtsx.astra.sdk.streaming.StreamingClient;
import com.dtsx.astra.sdk.utils.AstraRc;
import com.dtsx.astra.sdk.utils.Utils;
import org.junit.Assert;

/**
 * Superclass for test.
 */
public abstract class AbstractDevopsApiTest {

    /** Test Constants. */
    static final String SDK_TEST_DB_NAME = "sdk_java_test";

    /** Test Constants. */
    static final String SDK_TEST_DB_REGION = "us-east1";

    /** Test Constants. */
    static final String SDK_TEST_KEYSPACE = "ks1";

    /** Test Constants. */
    static final String SDK_TEST_KEYSPACE2 = "ks2";

    /**
     * Hold reference to token
     */
    private static String token;

    /**
     * Reference to Databases Client.
     */
    private static DatabasesClient databasesClient;

    /**
     * Reference to organization client.
     */
    private static OrganizationsClient organizationClient;

    /**
     * Working db.
     */
    private static DatabaseClient dbClient;

    /**
     * Reference to Databases Client.
     */
    private static StreamingClient streamingClient;


    /**
     * Access Org client.
     *
     * @return
     *      client fot organization
     */
    protected OrganizationsClient getOrganizationClient() {
        if (organizationClient == null) {
            organizationClient = new OrganizationsClient(getToken());
        }
        return organizationClient;
    }

    /**
     * Access DB client.
     *
     * @return
     *      client fot databases
     */
    protected DatabasesClient getDatabasesClient() {
        if (databasesClient == null) {
            databasesClient = new DatabasesClient(getToken());
        }
        return databasesClient;
    }

    /**
     * Access Streaming client.
     *
     * @return
     *      client fot streaming
     */
    protected StreamingClient getStreamingClient() {
        if (streamingClient == null) {
            streamingClient = new StreamingClient(getToken());
        }
        return streamingClient;
    }

    /**
     * Read Token for tests.
     *
     * @return
     *      token for test or error
     */
    protected String getToken() {
        if (token == null) {
            if (AstraRc.isDefaultConfigFileExists()) {
                token = new AstraRc()
                        .getSectionKey(AstraRc.ASTRARC_DEFAULT, AstraRc.ASTRA_DB_APPLICATION_TOKEN)
                        .orElse(null);
            }
            token = Utils.readEnvVariable(AstraRc.ASTRA_DB_APPLICATION_TOKEN).orElse(token);
        }
        return token;
    }

    /**
     * Create DB if not exist
     *
     * @return
     *      database client
     */
    protected DatabaseClient getSdkTestDbClient() {
        if (dbClient == null) {
            if (getDatabasesClient().findByName(SDK_TEST_DB_NAME).count() == 0) {
                getDatabasesClient().create(DatabaseCreationRequest
                        .builder()
                        .name(SDK_TEST_DB_NAME)
                        .keyspace(SDK_TEST_KEYSPACE)
                        .cloudRegion(SDK_TEST_DB_REGION)
                        .build());
            }
            dbClient = getDatabasesClient().name(SDK_TEST_DB_NAME);
            Assert.assertTrue(dbClient.exist());
        }
        return dbClient;
    }
}
