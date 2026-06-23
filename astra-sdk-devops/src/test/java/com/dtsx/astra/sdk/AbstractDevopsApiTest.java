package com.dtsx.astra.sdk;

import com.dtsx.astra.sdk.db.DbOpsClient;
import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.AstraRc;
import com.dtsx.astra.sdk.utils.Utils;
import org.junit.jupiter.api.Assertions;

/**
 * Superclass for test.
 */
public abstract class AbstractDevopsApiTest {

    /** Test Constants. */
    public static final String SDK_TEST_DB_NAME = "sdk_java_test_serverless";

    /** Test Constants. */
    public static final String SDK_TEST_DB_VECTOR_NAME = "sdk_java_test_vector";

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
     * Hold reference to environment
     */
    private static AstraEnvironment env;

    /**
     * Reference to Databases Client.
     */
    protected static AstraDBOpsClient databasesClient;

    /**
     * Reference to organization client.
     */
    protected static AstraOpsClient apiDevopsClient;

    /**
     * Working db.
     */
    protected static DbOpsClient dbClient;

    /**
     * Reference to Databases Client.
     */
    protected static AstraStreamingClient streamingClient;

    /**
     * Access DB client.
     *
     * @return
     *      client fot databases
     */
    protected AstraOpsClient getApiDevopsClient() {
        if (apiDevopsClient == null) {
            apiDevopsClient = new AstraOpsClient(getToken(), getEnvironment());
        }
        return apiDevopsClient;
    }

    /**
     * Access DB client.
     *
     * @return
     *      client for databases
     */
    protected AstraDBOpsClient getDatabasesClient() {
        if (databasesClient == null) {
            databasesClient = new AstraDBOpsClient(getToken(), getEnvironment());
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
            streamingClient = new AstraStreamingClient(getToken(), getEnvironment());
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
     * Get the target Astra environment from ASTRA_ENV environment variable.
     * Defaults to PROD if not set or invalid.
     *
     * @return
     *      target environment
     */
    protected AstraEnvironment getEnvironment() {
        String envStr = null;
        if (env == null) {
            if (AstraRc.isDefaultConfigFileExists()) {
                envStr = new AstraRc()
                    .getSectionKey(AstraRc.ASTRARC_DEFAULT, AstraRc.ASTRA_ENV)
                    .orElse(null);
            }
            envStr = Utils.readEnvVariable(AstraRc.ASTRA_ENV).orElse(envStr);
            if (envStr == null) {
                env = AstraEnvironment.PROD;
            } else {
                env = AstraEnvironment.valueOf(envStr);
            }
        }
        return env;
    }

    /**
     * Create DB if not exist
     *
     * @return
     *      database client
     */
    protected DbOpsClient getSdkTestDatabaseClient() {
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
            Assertions.assertTrue(dbClient.exist());
        }
        return dbClient;
    }
}
