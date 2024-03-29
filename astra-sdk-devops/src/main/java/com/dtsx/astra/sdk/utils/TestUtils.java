package com.dtsx.astra.sdk.utils;

import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.DbOpsClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationBuilder;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

/**
 * Helper for test.
 */
public class TestUtils {

    /** Test constant. */
    public static final String TEST_REGION = "us-east1";

    /** Test constant. */
    public static final String TEST_TIER = "serverless";

    /** Test constant. */
    public static final CloudProviderType TEST_PROVIDER = CloudProviderType.GCP;

    /**
     * Logger for the class.
     */
    static Logger logger = LoggerFactory.getLogger(TestUtils.class);

    /**
     * Hide default constructor
     */
    private TestUtils() {}

    /**
     * Read Token for tests.
     *
     * @return
     *      token for test or error
     */
    public static String getAstraToken() {
        String token = null;
        if (AstraRc.isDefaultConfigFileExists()) {
            token = new AstraRc()
                    .getSectionKey(AstraRc.ASTRARC_DEFAULT, AstraRc.ASTRA_DB_APPLICATION_TOKEN)
                    .orElse(null);
        }
        return Optional.ofNullable(Utils
                        .readEnvVariable(AstraRc.ASTRA_DB_APPLICATION_TOKEN).
                        orElse(token))
                .orElseThrow(() -> new IllegalStateException(
                        "ASTRA_DB_APPLICATION_TOKEN is not defined as env variable or present in file ~/.astrarc"));
    }

    /**
     * Initialize databases for tests.
     *
     * @param dbName
     *      database name
     * @param keyspace
     *      expected keyspace
     * @return
     *      the database id
     */
    public static String setupVectorDatabase(String dbName, String keyspace) {
        return setupDatabase(getAstraToken(), AstraEnvironment.PROD, dbName, keyspace, true);
    }

    /**
     * Initialize databases for tests.
     *
     * @param env
     *      astra environment
     * @param dbName
     *      database name
     * @param keyspace
     *      expected keyspace
     * @return
     *      the database id
     */
    public static String setupVectorDatabase(AstraEnvironment env, String dbName, String keyspace) {
        return setupDatabase(getAstraToken(), env, dbName, keyspace, true);
    }

    /**
     * Initialize databases for tests.
     *
     * @param env
     *      astra environment
     * @param dbName
     *      database name
     * @param keyspace
     *      expected keyspace
     * @return
     *      the database id
     */
    public static String setupDatabase(AstraEnvironment env, String dbName, String keyspace) {
        return setupDatabase(getAstraToken(), env, dbName, keyspace, false);
    }

    /**
     * Initialize databases for tests.
     *
     * @param dbName
     *      database name
     * @param keyspace
     *      expected keyspace
     * @return
     *      the database id
     */
    public static String setupDatabase(String dbName, String keyspace) {
        return setupDatabase(getAstraToken(), AstraEnvironment.PROD, dbName, keyspace, false);
    }

    /**
     * Initialize databases for tests.
     *
     * @param env
     *      astra environment
     * @param dbName
     *      database name
     * @param keyspace
     *      expected keyspace
     * @param vector
     *      include vector
     * @return
     *      the database id
     */
    public static String setupDatabase(AstraEnvironment env, String dbName, String keyspace, boolean vector) {
        return setupDatabase(getAstraToken(), env, dbName, keyspace, vector);
    }

    /**
     * Initialize databases for tests.
     *
     * @param env
     *      astra environment
     * @param token
     *     token for the organization
     * @param dbName
     *      database name
     * @param keyspace
     *      expected keyspace
     * @param vector
     *      include vector
     * @return
     *      the database id
     */
    public static String setupDatabase(String token, AstraEnvironment env, String dbName, String keyspace, boolean vector) {
        AstraDBOpsClient devopsDbCli = new AstraDBOpsClient(getAstraToken(), env);
        Optional<Database> optDb  = devopsDbCli.findByName(dbName).findAny();
        if (optDb.isPresent()) {
            // Db is present, should we resume it ?
            Database db = optDb.get();
            DbOpsClient dbClient = devopsDbCli.database(db.getId());
            if (db.getStatus().equals(DatabaseStatusType.HIBERNATED)) {
                logger.info("Resume DB {} as HIBERNATED ", dbName);
                resumeDb(optDb.get());
                waitForDbStatus(dbClient, DatabaseStatusType.ACTIVE, 500);
            }
            // Db is active, should I add a keyspace ?
            if (!dbClient.keyspaces().findAll().contains(keyspace)) {
                dbClient.keyspaces().create(keyspace);
                waitForDbStatus(dbClient, DatabaseStatusType.ACTIVE, 1000);
            }
            return db.getId();
        } else {
            // Db is not present...creation
            DatabaseCreationBuilder builder = DatabaseCreationRequest
                    .builder()
                    .name(dbName)
                    .tier(TEST_TIER)
                    .cloudProvider(TEST_PROVIDER)
                    .cloudRegion(TEST_REGION)
                    .keyspace(keyspace);
            if (vector) {
                builder = builder.withVector();
            }
            String serverlessDbId = devopsDbCli.create(builder.build());
            DbOpsClient dbc = new DbOpsClient(devopsDbCli.getToken(), serverlessDbId);
            waitForDbStatus(dbc, DatabaseStatusType.ACTIVE, 1800);
            return serverlessDbId;
        }
    }

    /**
     * Wait for db to have proper status.
     *
     * @param dbc
     *      database client
     * @param status
     *      database status
     * @param timeoutSeconds
     *      timeout
     */
    public static void waitForDbStatus(DbOpsClient dbc, DatabaseStatusType status, int timeoutSeconds) {
        long top = System.currentTimeMillis();
        while(status != dbc.find().get().getStatus() && ((System.currentTimeMillis()-top) < 1000*timeoutSeconds)) {
            System.out.print("\u25a0");
            waitForSeconds(5);
        }
        System.out.println("\n");
        if (dbc.find().get().getStatus() != status) {
            throw new IllegalStateException("Database is not in expected state after timeouts");
        }
    }
    
    /**
     * Hold execution for X seconds waiting for async APIS.
     * 
     * @param seconds
     *          time to wait
     */
    public static void waitForSeconds(int seconds) {
        try {Thread.sleep(seconds * 1000);} catch (InterruptedException e) {}
    }
   
    /**
     * Terminate database if needed.
     *
     * @param devopsDbCli
     *      devops cli
     * @param dbName
     *      database name
     */
    public static void terminateDatabaseByName(AstraDBOpsClient devopsDbCli, String dbName) {
        DbOpsClient dbc = new AstraDBOpsClient(devopsDbCli.getToken()).databaseByName(dbName);
        if(dbc.exist()) {
            dbc.delete();
            waitForDbStatus(dbc, DatabaseStatusType.TERMINATED, 60);
        }
    }

    /**
     * Database name.
     *
     * @param db
     *      database name
     */
    private static void resumeDb(Database db) {
        try(CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(ApiLocator
                    .getApiRestEndpoint(db.getId(), db.getInfo().getRegion()) +
                    "/v2/schemas/keyspace");
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            request.setHeader("X-Cassandra-Token", getAstraToken());
            request.setHeader("Content-Type", "application/json");
            httpClient.execute(request).close();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot resume DB", e);
        }
    }

}
