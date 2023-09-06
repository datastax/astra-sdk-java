package com.dtsx.astra.sdk.utils;

import com.dtsx.astra.sdk.AstraDevopsApiClient;
import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.AstraDbClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Helper for tetst.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class TestUtils {

    /** Test constant. */
    public static final String TEST_DBNAME = "sdk_tests";

    /** Test constant. */
    public static final String  TEST_NAMESPACE = "java";

    /** Test constant. */
    public static final String TEST_REGION = "us-east1";

    /** Test constant. */
    public static final CloudProviderType TEST_PROVIDER = CloudProviderType.GCP;

    /** Test constant. */
    public static final String TEST_TIER = "serverless";

    /**
     * Hide default constructor
     */
    private TestUtils() {}

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
    public static void waitForDbStatus(DatabaseClient dbc, DatabaseStatusType status, int timeoutSeconds) {
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
     * Initialize databases for tests.
     * 
     * @param devopsDbCli
     *      devops database API.
     * @param dbName
     *      database name
     * @param keyspace
     *      expected keyspace
     * @return
     *      the database id
     */
    public static String createDbAndKeyspaceIfNotExist(AstraDbClient devopsDbCli, String dbName, String keyspace) {
        List<Database> dbs = devopsDbCli.findByName(dbName).collect(Collectors.toList());
        if (dbs.size() > 0) {
            Database db = dbs.get(0);
            DatabaseClient dbc = new DatabaseClient(devopsDbCli.getToken(), db.getId());
            if (!db.getInfo().getKeyspaces().contains(keyspace)) {
                dbc.keyspaces().create(keyspace);
                waitForDbStatus(dbc, DatabaseStatusType.ACTIVE, 60);
            }            return db.getId();
        } else {
            String serverlessDbId = devopsDbCli.create(DatabaseCreationRequest
                    .builder()
                    .name(dbName)
                    .tier(TEST_TIER)
                    .cloudProvider(TEST_PROVIDER)
                    .cloudRegion(TEST_REGION)
                    .keyspace(keyspace)
                    .build());
            DatabaseClient dbc = new DatabaseClient(devopsDbCli.getToken(), serverlessDbId);
            waitForDbStatus(dbc, DatabaseStatusType.ACTIVE, 120);
            return serverlessDbId;
        }
    }
   
    /**
     * Terminate database if needed.
     *
     * @param devopsDbCli
     *      devops cli
     * @param dbName
     *      database name
     */
    public static void terminateDatabaseByName(AstraDbClient devopsDbCli, String dbName) {
        DatabaseClient dbc = new AstraDbClient(devopsDbCli.getToken()).databaseByName(dbName);
        if(dbc.exist()) {
            dbc.delete();
            waitForDbStatus(dbc, DatabaseStatusType.TERMINATED, 60);
        }
    }

    /**
     * Read Token for tests.
     *
     * @return
     *      token for test or error
     */
    public static String readToken() {
        String token = null;
        if (AstraRc.isDefaultConfigFileExists()) {
            token = new AstraRc()
                    .getSectionKey(AstraRc.ASTRARC_DEFAULT, AstraRc.ASTRA_DB_APPLICATION_TOKEN)
                    .orElse(null);
        }
        return Utils.readEnvVariable(AstraRc.ASTRA_DB_APPLICATION_TOKEN).orElse(token);
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
            request.setHeader("X-Cassandra-Token", readToken());
            request.setHeader("Content-Type", "application/json");
            httpClient.execute(request).close();
        } catch (IOException e) {
            throw new IllegalStateException("Cannot resume DB", e);
        }
    }

    /**
     * Logger for the class.
     */
    static Logger logger = LoggerFactory.getLogger(TestUtils.class);

    /**
     * Create DB if not exist
     *
     * @param dbName
     *      database name
     * @param keyspace
     *      keyspace name
     * @return
     *      database client
     */
    public static String setupDatabase(String dbName, String keyspace)
    throws InterruptedException {
        AstraDbClient astraDb = new AstraDbClient(readToken());
        Optional<Database> optDb = astraDb.findByName(dbName).findAny();
        String dbId = null;
        if (!optDb.isPresent()) {
            logger.info("Creating database '{}' as it does not exist, this operation takes about 90s, please wait... ", dbName);
            dbId = astraDb.create(DatabaseCreationRequest
                    .builder().name(dbName)
                    .keyspace(keyspace)
                    .cloudRegion(TEST_REGION)
                    .withVector()
                    .build());
        } else {
            dbId = optDb.get().getId();
            DatabaseClient dbClient = astraDb.database(dbId);
            if (optDb.get().getStatus().equals(DatabaseStatusType.HIBERNATED)) {
                logger.info("Resume DB {} as HIBERNATED ", dbName);
                resumeDb(optDb.get());
                waitForDbStatus(dbClient, DatabaseStatusType.ACTIVE, 500);
            }
            if (!dbClient.keyspaces().findAll().contains(keyspace)) {
                dbClient.keyspaces().create(keyspace);
            }
        }
        TestUtils.waitForDbStatus(astraDb.database(dbId), DatabaseStatusType.ACTIVE, 500);
        return dbId;
    }


}
