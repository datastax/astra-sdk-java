package com.dtsx.astra.sdk;

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
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

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
 * Astra Vector Client, Native experience for Vector Database.
 */
@Slf4j
public class AstraDBAdmin {

    /** Default timeout for connection. */
    public static final int CONNECT_TIMEOUT_SECONDS = 20;

    /**
     * Free tier.
     */
    public static final CloudProviderType FREE_TIER_CLOUD = CloudProviderType.GCP;

    /**
     * Free tier.
     */
    public static final String FREE_TIER_CLOUD_REGION = "us-east1";

    /**
     * Token header param
     */
    public static final String TOKEN_HEADER_PARAM = "X-Cassandra-Token";

    /**
     * Technical Keyspace name.
     */
    public static final String DEFAULT_KEYSPACE = "default_keyspace";

    /**
     * First Level of the API will
     */
    final AstraDBOpsClient devopsDbClient;

    /**
     * Environment
     */
    final AstraEnvironment env;

    /**
     * Token required to initialize the clients
     */
    @Getter
    final String token;

    /**
     * JDK HttpClient
     */
    final HttpClient httpClient;

    /**
     * Configuration token
     */
    static String astraConfigToken;

    /*
     * Load from environment.
     */
    static {
        new AstraRc().getSectionKey(
                AstraRc.ASTRARC_DEFAULT,
                AstraRc.ASTRA_DB_APPLICATION_TOKEN).ifPresent(s -> astraConfigToken = s);
        // lookup env variable
        readEnvVariable(AstraRc.ASTRA_DB_APPLICATION_TOKEN).ifPresent(s -> astraConfigToken = s);
    }

    /**
     * Load with token from environment
     */
    public AstraDBAdmin() {
        this(astraConfigToken);
    }

    /**
     * Default constructor.
     *
     * @param token
     *      a token is all you need
     */
    public AstraDBAdmin(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * Second constructor for non-production environments.
     *
     * @param token
     *      token
     * @param env
     *      target environments
     */
    public AstraDBAdmin(String token, AstraEnvironment env) {
        this.env = env;
        this.token = token;
        this.devopsDbClient = new AstraDBOpsClient(token, env);
        this.httpClient = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(CONNECT_TIMEOUT_SECONDS))
                .build();
    }

    /**
     * List active vector databases.
     *
     * @return
     *      active devops databases.
     */
    public Stream<Database> findAllDatabases() {
        return devopsDbClient
                .findAllNonTerminated()
                .filter(db -> db.getInfo().getDbType() != null);
    }

    /**
     * Create Db in free Tier.
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
     * Delete a Database if exists from its name
     *
     * @param name
     *    database name
     * @return
     *      if the db has been deleted
     */
    public boolean deleteDatabaseByName(@NonNull String name) {
        Optional<Database> opDb = findDatabaseByName(name).findFirst();
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
    public boolean deleteDatabaseById(@NonNull UUID databaseId) {
        if (findDatabaseById(databaseId).isPresent()) {
            devopsDbClient.database(databaseId.toString()).delete();
            return true;
        }
        return false;
    }

    /**
     * Create a database with the full definition.
     *
     * @param name
     *      database name
     * @param cloud
     *      cloud provider
     * @param cloudRegion
     *      cloud region
     * @return
     *      database uid
     */
    public UUID createDatabase(@NonNull String name, @NonNull CloudProviderType cloud, @NonNull String cloudRegion) {
        Optional<Database> optDb = findDatabaseByName(name).findFirst();
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
     * Retrieve list of all Databases of the account and filter on name
     *
     * @param name
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Stream<Database> findDatabaseByName(String name) {
        Assert.hasLength(name, "Database name");
        return findAllDatabases().filter(db->name.equals(db.getInfo().getName()));
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
        return findDatabaseByName(name).findFirst().isPresent();
    }

    /**
     * Find a database from its id.
     *
     * @param id
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Optional<Database> findDatabaseById(@NonNull UUID id) {
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
    public AstraDB database(@NonNull String databaseName) {
        List<Database> dbs = findDatabaseByName(databaseName).collect(Collectors.toList());
        if (dbs.isEmpty()) {
            throw new DatabaseNotFoundException(databaseName);
        }
        if (dbs.size() > 1) {
            throw new IllegalStateException("More than one database exists with the same name, use id.");
        }
        return database(UUID.fromString(dbs.get(0).getId()));
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
    public AstraDB database(UUID databaseId) {
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
            // Compute Endpoint for the Keyspaces
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
    public DataApiClient getInternalDataApiClient(@NonNull String databaseName) {
        return database(databaseName).getApiClient();
    }

    /**
     * Access database functions.
     *
     * @param databaseId
     *      database identifier
     * @return
     *      database client
     */
    public DataApiClient getInternalDataApiClient(@NonNull UUID databaseId) {
        return database(databaseId).getApiClient();
    }

    /**
     * Access the devops client.
     *
     * @return
     *      devops client.
     */
    public AstraDBOpsClient getInternalDevopsApiClient() {
        return this.devopsDbClient;
    }


}
