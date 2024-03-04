package com.datastax.astradb.client;

import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.DbOpsClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.AstraRc;
import io.stargate.sdk.data.DataApiClient;
import io.stargate.sdk.http.RetryHttpClient;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.stargate.sdk.utils.AnsiUtils.green;
import static io.stargate.sdk.utils.Utils.readEnvVariable;

/**
 * Client for AstraDB at organization level (crud for databases).
 */
@Slf4j
public class AstraDBAdmin implements Closeable  {

    public static final String USER_AGENT = "astra-db-java";

    /** Default timeout for initiating connection. */
    public static final int CONNECT_TIMEOUT_SECONDS = 20;

    /** Default cloud provider if not provided by user. (free-tier) */
    public static final CloudProviderType FREE_TIER_CLOUD = CloudProviderType.GCP;

    /** Default region if not provided by user. (free-tier) */
    public static final String FREE_TIER_CLOUD_REGION = "us-east1";

    /** Header name used to hold the Astra Token. */
    public static final String TOKEN_HEADER_PARAM = "X-Token";

    /** Default keyspace name if not provided by user. */
    public static final String DEFAULT_KEYSPACE = "default_keyspace";

    /** Client for the Astra Devops Api. (crud on databases) */
    final AstraDBOpsClient devopsDbClient;

    /** Target Astra Environment, default is PROD. */
    final AstraEnvironment env;

    /** Astra Token used as credentials. */
    @Getter
    final String token;

    /** JDK11 HttpClient to interact with apis. */
    final HttpClient httpClient;

    /** Token value read for environment variable. */
    static String astraConfigToken;

    /*
     * Load token values from environment variables and ~/.astrarc.
     */
    static {
        new AstraRc().getSectionKey(
                AstraRc.ASTRARC_DEFAULT,
                AstraRc.ASTRA_DB_APPLICATION_TOKEN).ifPresent(s -> astraConfigToken = s);
        // lookup env variable
        readEnvVariable(AstraRc.ASTRA_DB_APPLICATION_TOKEN).ifPresent(s -> astraConfigToken = s);
    }

    /**
     * Default initialization, the token is retrieved from environment variable <code>ASTRA_DB_APPLICATION_TOKEN</code> or from
     * file <code>~/.astrarc</code>, section <code>default</code>,  key <code>ASTRA_DB_APPLICATION_TOKEN</code>.
     */
    public AstraDBAdmin() {
        this(astraConfigToken);
    }

    /**
     * Initialization with an authentification token, defaulting to production environment.
     *
     * @param token
     *      authentication token
     */
    public AstraDBAdmin(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * Initialization with an authentification token and target environment, Use this constructor for testing purpose.
     *
     * @param token
     *      authentication token
     * @param env
     *      target Astra environment
     */
    public AstraDBAdmin(String token, AstraEnvironment env) {
        this.env = env;
        this.token = token;
        this.devopsDbClient = new AstraDBOpsClient(token, env);
        this.httpClient = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build();
        String version = AstraDBAdmin.class.getPackage().getImplementationVersion();
        AstraDBAdmin.setCallerName(AstraDBAdmin.USER_AGENT, (null != version) ? version :  "dev");
    }

    // --------------------------
    // ---  User Agent      ----
    // --------------------------

    /**
     * Allow user to set the client name
     *
     * @param callerName
     *      client name
     * @param callerVersion
     *      client version
     */
    public static void setCallerName(String callerName, String callerVersion) {
        RetryHttpClient.getInstance().pushUserAgent(callerName, callerVersion);
    }

    // --------------------
    // --   Watch       ---
    // --------------------

    public void watch() {
        throw new UnsupportedOperationException("As we connect to a HTTP apis without hooks, no watch is possible.");
    }

    // --------------------
    // -- Keyspace      ---
    // --------------------

    /**
     * Create a keyspace.
     *
     * @param databaseName
     *      database name
     * @param keyspaceName
     *      keyspace name
     */
    public void createKeyspace(String databaseName, String keyspaceName) {
        devopsDbClient.databaseByName(databaseName).keyspaces().create(keyspaceName);
    }

    /**
     * Create a keyspace.
     *
     * @param databaseId
     *      database unique identifier
     * @param keyspaceName
     *      keyspace name
     */
    public void createKeyspace(UUID databaseId, String keyspaceName) {
        devopsDbClient.database(databaseId.toString()).keyspaces().create(keyspaceName);
    }

    /**
     * Delete a keyspace.
     *
     * @param databaseName
     *      database name
     * @param keyspaceName
     *      keyspace name
     */
    public void deleteKeyspace(String databaseName, String keyspaceName) {
        devopsDbClient.databaseByName(databaseName).keyspaces().delete(keyspaceName);
    }

    /**
     * Delete a keyspace.
     *
     * @param databaseId
     *      database unique identifier
     * @param keyspaceName
     *      keyspace name
     */
    public void deleteKeyspace(UUID databaseId, String keyspaceName) {
        devopsDbClient.database(databaseId.toString()).keyspaces().delete(keyspaceName);
    }

    // --------------------
    // -- Databases     ---
    // --------------------

    /**
     * List available database names.
     *
     * @return
     *      list of database names
     */
    public List<String> listDatabaseNames() {
        return listDatabases().map(db -> db.getInfo().getName()).collect(Collectors.toList());
    }

    /**
     * List active databases with vector enabled in current organization.
     *
     * @return
     *      active databases list
     */
    public Stream<Database> listDatabases() {
        return devopsDbClient
                .findAllNonTerminated()
                .filter(db -> db.getInfo().getDbType() != null);
    }

    /**
     * Create new database with a name on free tier. The database name should not exist in the tenant.
     *
     * @param name
     *    database name
     * @return
     *    database identifier
     */
    public UUID createDatabase(@NonNull String name) {
        return createDatabase(name, FREE_TIER_CLOUD, FREE_TIER_CLOUD_REGION);
    }

    /**
     * Create new database with a name on the specified cloud provider and region.
     * If the database with same name already exists it will be resumed if not active.
     * The method will wait for the database to be active.
     *
     * @param name
     *      database name
     * @param cloud
     *      cloud provider
     * @param cloudRegion
     *      cloud region
     * @return
     *      database identifier
     */
    public UUID createDatabase(@NonNull String name, @NonNull CloudProviderType cloud, @NonNull String cloudRegion) {
        Optional<Database> optDb = getDatabaseInformations(name).findFirst();
        // Handling all cases for the user
        if (optDb.isPresent()) {
            Database db = optDb.get();
            switch(db.getStatus()) {
                case ACTIVE:
                    log.info("Database " + green("{}") + " already exists and is ACTIVE.", name);
                    return UUID.fromString(db.getId());
                case MAINTENANCE:
                case INITIALIZING:
                case PENDING:
                case RESUMING:
                    log.info("Database {} already exists and is in {} state, waiting for it to be ACTIVE", name, db.getStatus());
                    waitForDatabase(devopsDbClient.database(db.getId()));
                    return UUID.fromString(db.getId());
                case HIBERNATED:
                    log.info("Database {} is in {} state, resuming...", name, db.getStatus());
                    resumeDb(db);
                    waitForDatabase(devopsDbClient.database(db.getId()));
                    return UUID.fromString(db.getId());
                default:
                    throw new IllegalStateException("Database already exist but cannot be activate");
            }
        }
        // Database is not present, creating and waiting for it to be active.
        UUID newDbId = UUID.fromString(devopsDbClient.create(DatabaseCreationRequest.builder()
                .name(name)
                .cloudProvider(cloud)
                .cloudRegion(cloudRegion)
                .keyspace(DEFAULT_KEYSPACE)
                .withVector().build()));
        log.info("Database {} is starting (id={}): it will take about a minute please wait...", name, newDbId);
        waitForDatabase(devopsDbClient.database(newDbId.toString()));
        return newDbId;
    }

    /**
     * Delete a Database if exists from its name
     *
     * @param name
     *    database name
     * @return
     *      if the db has been deleted
     */
    public boolean dropDatabase(@NonNull String name) {
        Optional<Database> opDb = getDatabaseInformations(name).findFirst();
        opDb.ifPresent(db -> devopsDbClient.database(db.getId()).delete());
        return opDb.isPresent();
    }

    /**
     * Delete a Database if exists from its name
     *
     * @param databaseId
     *    database identifier
     * @return
     *      if the db has been deleted
     */
    public boolean dropDatabase(@NonNull UUID databaseId) {
        if (getDatabaseInformations(databaseId).isPresent()) {
            devopsDbClient.database(databaseId.toString()).delete();
            return true;
        }
        return false;
    }

    /**
     * Retrieve list of all Databases of the account and filter on name
     *
     * @param name
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Stream<Database> getDatabaseInformations(String name) {
        Assert.hasLength(name, "Database name");
        return listDatabases().filter(db->name.equals(db.getInfo().getName()));
    }

    /**
     * Check if a database exists.
     *
     * @param name
     *     a database name
     * @return
     *     if the database exists
     */
    public boolean isDatabaseExists(String name) {
        return getDatabaseInformations(name).findFirst().isPresent();
    }

    /**
     * Find a database from its id.
     *
     * @param id
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Optional<Database> getDatabaseInformations(@NonNull UUID id) {
        Assert.notNull(id, "Database identifier should not be null");
        return devopsDbClient.findById(id.toString());
    }

    /**
     * Access the database functions.
     *
     * @param databaseName
     *      database name
     * @return
     *      database client
     */
    public AstraDB getDatabase(@NonNull String databaseName) {
        List<Database> dbs = getDatabaseInformations(databaseName).collect(Collectors.toList());
        if (dbs.isEmpty()) {
            throw new DatabaseNotFoundException(databaseName);
        }
        if (dbs.size() > 1) {
            throw new IllegalStateException("More than one database exists with the same name, use id.");
        }
        return getDatabase(UUID.fromString(dbs.get(0).getId()));
    }

    // --------------------
    // == Sub resources  ==
    // --------------------

    /**
     * Access the database functions.
     *
     * @param databaseId
     *      database identifier
     * @return
     *      database client
     */
    public AstraDB getDatabase(UUID databaseId) {
        return new AstraDB(token, databaseId, null, env, AstraDBAdmin.DEFAULT_KEYSPACE);
    }

    /**
     * Wait for db to have proper status.
     *
     * @param dbc
     *      database client
     */
    @SuppressWarnings("java:S2925")
    private void waitForDatabase(DbOpsClient dbc) {
        long top = System.currentTimeMillis();
        while(DatabaseStatusType.ACTIVE != getStatus(dbc) && ((System.currentTimeMillis()-top) < 1000L*180)) {
            try {
                Thread.sleep( 5000);
                System.out.print("■");
                System.out.flush();
            } catch (InterruptedException e) {
                log.warn("Interrupted {}",e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        if (getStatus(dbc) != DatabaseStatusType.ACTIVE) {
            throw new IllegalStateException("Database is not in expected state after timeouts");
        }
    }

    /**
     * Retrieve the status of a database.
     * @param dbc
     *      database client
     * @return
     *      database status
     */
    private DatabaseStatusType getStatus(DbOpsClient dbc) {
        return dbc.find().orElseThrow(() -> new DatabaseNotFoundException(dbc.getDatabaseId())).getStatus();
    }

    /**
     * Database name.
     *
     * @param db
     *      database name
     */
    private void resumeDb(Database db) {
        try {
            // Compute Endpoint for the Keyspace
            String endpoint = ApiLocator.getApiRestEndpoint(db.getId(), db.getInfo().getRegion()) + "/v2/schemas/keyspace";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(endpoint))
                    .timeout(Duration.ofSeconds(20))
                    .header("Content-Type", "application/json")
                    .header(TOKEN_HEADER_PARAM, token)
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            if (response.statusCode() == 500) {
                throw new IllegalStateException("Cannot resume database, please check your account");
            }
        } catch (InterruptedException e) {
            log.warn("Interrupted {}",e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.warn("Resuming request might have failed, please check {}}",e.getMessage());
        }
    }

    /**
     * Access database functions.
     *
     * @param databaseName
     *      database name
     * @return
     *      database client
     */
    public DataApiClient getDataApiClient(@NonNull String databaseName) {
        return getDatabase(databaseName).getApiClient();
    }

    /**
     * Access database functions.
     *
     * @param databaseId
     *      database identifier
     * @return
     *      database client
     */
    public DataApiClient getDataApiClient(@NonNull UUID databaseId) {
        return getDatabase(databaseId).getApiClient();
    }

    /**
     * Access the devops client.
     *
     * @return
     *      devops client.
     */
    public AstraDBOpsClient getDevopsApiClient() {
        return this.devopsDbClient;
    }


    /**
     * Close the client.
     */
    @Override
    public void close() throws IOException {
    }
}
