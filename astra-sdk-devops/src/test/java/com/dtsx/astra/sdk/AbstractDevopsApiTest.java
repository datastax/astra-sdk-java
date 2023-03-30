package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.AstraDevopsApiClient;
import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.AstraDbClient;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.utils.AstraRc;
import com.dtsx.astra.sdk.utils.Utils;
import org.junit.Assert;

/**
 * Superclass for test.
 */
public abstract class AbstractDevopsApiTest {

    /** Test Constants. */
    public static final String SDK_TEST_DB_NAME = "sdk_java_test";

    /** Test Constants. */
    public static final String SDK_TEST_DB_REGION = "us-east1";

    /** Test Constants. */
    public static final String SDK_TEST_KEYSPACE = "sdk_java";

    /** Test Constants. */
    public static final String SDK_TEST_KEYSPACE2 = "sdk_java2";

    /**
     * Hold reference to token
     */
    private static String token;

    /**
     * Reference to Databases Client.
     */
    private static AstraDbClient databasesClient;

    /**
     * Reference to organization client.
     */
    private static AstraDevopsApiClient apiDevopsClient;

    /**
     * Working db.
     */
    private static DatabaseClient dbClient;

    /**
     * Reference to Databases Client.
     */
    private static AstraStreamingClient streamingClient;

    /**
     * Access DB client.
     *
     * @return
     *      client fot databases
     */
    protected AstraDevopsApiClient getApiDevopsClient() {
        if (apiDevopsClient == null) {
            apiDevopsClient = new AstraDevopsApiClient(getToken());
        }
        return apiDevopsClient;
    }

    /**
     * Access DB client.
     *
     * @return
     *      client fot databases
     */
    protected AstraDbClient getDatabasesClient() {
        if (databasesClient == null) {
            databasesClient = new AstraDbClient(getToken());
        }
        return databasesClient;
    }

    /**
     * Access Streaming client.
     *
     * @return
     *      client fot streaming
     */
    protected AstraStreamingClient getStreamingClient() {
        if (streamingClient == null) {
            streamingClient = new AstraStreamingClient(getToken());
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
    protected DatabaseClient getSdkTestDatabaseClient() {
        if (dbClient == null) {
            if (!getDatabasesClient().findByName(SDK_TEST_DB_NAME).findAny().isPresent()) {
                getDatabasesClient().create(DatabaseCreationRequest
                        .builder()
                        .name(SDK_TEST_DB_NAME)
                        .keyspace(SDK_TEST_KEYSPACE)
                        .cloudRegion(SDK_TEST_DB_REGION)
                        .build());
            }
            dbClient = getApiDevopsClient().db().databaseByName(SDK_TEST_DB_NAME);
            Assert.assertTrue(dbClient.exist());
        }
        return dbClient;
    }
}
