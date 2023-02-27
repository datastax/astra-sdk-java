package com.dtsx.astra.sdk.utils;

import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.DatabasesClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;

import java.util.List;
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
    public static String createDbAndKeyspaceIfNotExist(DatabasesClient devopsDbCli, String dbName, String keyspace) {
        List<Database> dbs = devopsDbCli.findByName(dbName).collect(Collectors.toList());
        if (dbs.size() > 0) {
            Database db = dbs.get(0);
            DatabaseClient dbc =devopsDbCli.id(db.getId());
            if (!db.getInfo().getKeyspaces().contains(keyspace)) {
                dbc.createKeyspace(keyspace);
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
            DatabaseClient dbc = devopsDbCli.id(serverlessDbId);
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
    public static void terminateDatabaseByName(DatabasesClient devopsDbCli, String dbName) {
        DatabaseClient dbc = devopsDbCli.name(dbName);
        if(dbc.exist()) {
            dbc.delete();
            waitForDbStatus(dbc, DatabaseStatusType.TERMINATED, 60);
        }
    }

}
