package com.datastax.astra.sdk;

import io.stargate.sdk.StargateClient;
import io.stargate.sdk.utils.AnsiUtils;
import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.AstraDbClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper for tetst.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraTestUtils {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraTestUtils.class);
    
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
    private AstraTestUtils() {}
    
    /**
     * Initiliaze the {@link StargateClient} for Astra.
     *
     * @return
     *      stargate client
     */
    public static StargateClient initStargateClient() {
        //   Loading ASTRA_DB_APPLICATION_TOKEN from AstraRC or environment variable
        String dbId = createTestDbIfNotExist(AstraClient.builder().build());
        
        // Initialization client with test database
        return AstraClient.builder()
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .withDatabaseId(dbId)
                .build().getStargateClient();
    }
    
    /**
     * Allows to TODO
     * @param dbc
     * @param status
     */
    public static void waitForDbStatus(DatabaseClient dbc, DatabaseStatusType status, int timeoutSeconds) {
        long top = System.currentTimeMillis();
        LOGGER.info("Waiting for DB {} to be in status {} with timeout '{}' seconds.", 
                dbc.getDatabaseId(), status, timeoutSeconds);
        while(status != dbc.find().get().getStatus() && ((System.currentTimeMillis()-top) < 1000*timeoutSeconds)) {
            System.out.print(AnsiUtils.green("\u25a0")); 
            waitForSeconds(5);
        }
        System.out.println("\n");
        if (dbc.find().get().getStatus() != status) {
            throw new IllegalStateException("Database is not in expected state after timeouts");
        }
        LOGGER.info("Status {}", status);
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
     * Create the jave DB test is not present.
     *
     * @param astraClient
     *      current cli
     * @return
     *      db id.
     *      
     */
    public static String createTestDbIfNotExist(AstraClient astraClient) {
        return createDbAndKeyspaceIfNotExist(
                astraClient.apiDevopsDatabases(), TEST_DBNAME, TEST_NAMESPACE);
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
            LOGGER.info("A database with the expected name [" + AnsiUtils.cyan("{}") + "] already exists, checking keyspace.", dbName);
            Database db = dbs.get(0);
            DatabaseClient dbc =devopsDbCli.dbClientById(db.getId());
            if (!db.getInfo().getKeyspaces().contains(keyspace)) {
                LOGGER.info("Creating keyspace {}", keyspace);
                dbc.createKeyspace(keyspace);
                waitForDbStatus(dbc, DatabaseStatusType.ACTIVE, 60);
            } else {
                LOGGER.info("A keyspace with the expected name [" + AnsiUtils.cyan("{}") + "] already exists.", keyspace);
            }
            return db.getId();
        } else {
            // db does not exist, creating
            LOGGER.info("Creating db '{}'", dbName);
            String serverlessDbId = devopsDbCli.create(DatabaseCreationRequest
                    .builder()
                    .name(dbName)
                    .tier(TEST_TIER)
                    .cloudProvider(TEST_PROVIDER)
                    .cloudRegion(TEST_REGION)
                    .keyspace(keyspace)
                    .build());
            LOGGER.info("db id = '{}'", serverlessDbId);
            DatabaseClient dbc = devopsDbCli.dbClientById(serverlessDbId);
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
        LOGGER.info("Terminating DB {}", dbName);
        DatabaseClient dbc = devopsDbCli.dbClientByName(dbName);
        if(dbc.exist()) {
            dbc.delete();
            waitForDbStatus(dbc, DatabaseStatusType.TERMINATED, 60);
        } else {
            LOGGER.info("No database with name '{}'", dbName);
        }
    }

}
