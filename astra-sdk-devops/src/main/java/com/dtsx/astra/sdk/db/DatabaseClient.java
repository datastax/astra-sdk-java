package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import com.dtsx.astra.sdk.db.domain.Datacenter;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.db.exception.KeyspaceAlreadyExistException;
import com.dtsx.astra.sdk.db.exception.RegionNotFoundException;
import com.dtsx.astra.sdk.db.telemetry.TelemetryClient;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.dtsx.astra.sdk.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.Optional;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;

/**
 * Devops API Client working with a Database.
 */
public class DatabaseClient {

    /**
     * Logger for our Client.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseClient.class);

    /**
     * Wrapper handling header and error management as a singleton.
     */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /**
     * unique db identifier.
     */
    private final String databaseId;

    /**
     * Reference to upper resource.
     */
    private final DatabasesClient databasesClient;

    /**
     * Default constructor.
     *
     * @param databasesClient
     *         database client
     * @param databaseId
     *         unique database identifier
     */
    public DatabaseClient(DatabasesClient databasesClient, String databaseId) {
        Assert.notNull(databasesClient, "databasesClient");
        Assert.hasLength(databaseId, "databaseId");
        this.databaseId = databaseId;
        this.databasesClient = databasesClient;
    }

    // ---------------------------------
    // ----       READ              ----
    // ---------------------------------

    /**
     * Retrieve a DB by its id.
     *
     * @return the database if present,
     */
    public Optional<Database> find() {
        ApiResponseHttp res = http.GET(getEndpointDatabase(), databasesClient.bearerAuthToken);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(JsonUtils.unmarshallBean(res.getBody(), Database.class));
        }
    }

    /**
     * Retrieve database or throw error.
     *
     * @return current db or error
     */
    public Database get() {
        return find().orElseThrow(() -> new DatabaseNotFoundException(databaseId));
    }

    /**
     * Evaluate if a database exists using the findById method.
     *
     * @return database existence
     */
    public boolean exist() {
        return find().isPresent();
    }

    /**
     * If the app is active.
     *
     * @return tells if database is ACTIVE
     */
    public boolean isActive() {
        return DatabaseStatusType.ACTIVE == get().getStatus();
    }

    /**
     * Create a new keyspace in a DB.
     *
     * @param keyspace
     *         keyspace name to create
     */
    public void createKeyspace(String keyspace) {
        Assert.hasLength(keyspace, "keyspace");
        Database db = get();
        if (db.getInfo().getKeyspaces().contains(keyspace)) {
            throw new KeyspaceAlreadyExistException(keyspace, db.getInfo().getName());
        }
        http.POST(getEndpointKeyspace(keyspace), databasesClient.bearerAuthToken);
    }

    // ---------------------------------
    // ----    SECURE BUNDLE        ----
    // ---------------------------------

    /**
     * Download SecureBundle for a specific data center
     *
     * @param destination
     *         file to save the secure bundle
     */
    public void downloadDefaultSecureConnectBundle(String destination) {
        // Parameters Validation
        Assert.hasLength(destination, "destination");
        if (!isActive())
            throw new IllegalStateException("Database '" + databaseId + "' is not available.");
        // Get list of urls
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/secureBundleURL", databasesClient.bearerAuthToken);
        // Mapping
        String url = (String) JsonUtils.unmarshallBean(res.getBody(), Map.class).get("downloadURL");
        // Download binary in target folder
        Utils.downloadFile(url, destination);
    }

    /**
     * Download SecureBundle for a specific data center
     *
     * @param destination
     *         file to save the secure bundle
     * @param region
     *         download for a target region
     */
    public void downloadSecureConnectBundle(String region, String destination) {
        Assert.hasLength(region, "region");
        Assert.hasLength(destination, "destination");
        Database db = get();
        downloadSecureConnectBundle(db.getInfo()
                .getDatacenters()
                .stream()
                .filter(d -> region.equalsIgnoreCase(d.getRegion()))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException(region, databaseId)), destination);
    }

    /**
     * Download SCB for a database and a datacenter in target location.
     *
     * @param dc
     *         current region
     * @param destination
     *         target destination
     */
    private void downloadSecureConnectBundle(Datacenter dc, String destination) {
        Assert.hasLength(destination, "destination");
        if (!new File(destination).exists()) {
            Utils.downloadFile(dc.getSecureBundleUrl(), destination);
            LOGGER.info("+ Downloading SCB to : {}", destination);
        } else {
            LOGGER.info("+ SCB {} already available.", destination);
        }
    }

    /**
     * Download all SecureBundle.
     *
     * @param destination
     *         file to save the secured bundle
     */
    public void downloadAllSecureConnectBundles(String destination) {
        Assert.hasLength(destination, "destination");
        Assert.isTrue(new File(destination).exists(), "Destination folder");
        Database db = get();
        db.getInfo()
                .getDatacenters()
                .forEach(dc -> downloadSecureConnectBundle(dc, destination + File.separator + buildScbFileName(db.getId(), dc.getRegion())));
    }

    /**
     * Build filename for the secure connect bundle.
     *
     * @param dId
     *         databaseId
     * @param dbRegion
     *         databaseRegion
     * @return file name for the secure bundled
     */
    public String buildScbFileName(String dId, String dbRegion) {
        return "scb_" + dId + "_" + dbRegion + ".zip";
    }

    // ---------------------------------
    // ----       MAINTENANCE       ----
    // ---------------------------------

    /**
     * Parks a database (classic)
     */
    public void park() {
        // Invoke Http endpoint
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/park", databasesClient.bearerAuthToken);
        // Check response code
        assertHttpCodeAccepted(res, "park");
    }

    /**
     * unpark a database.
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/unparkDatabase
     */
    public void unpark() {
        // Invoke Http endpoint
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/unpark", databasesClient.bearerAuthToken);
        // Check response code
        assertHttpCodeAccepted(res, "unpark");
    }

    /**
     * Terminates a database.
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/terminateDatabase
     */
    public void delete() {
        // Invoke Http endpoint
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/terminate", databasesClient.bearerAuthToken);
        // Check response code
        assertHttpCodeAccepted(res, "terminate");
    }

    /**
     * Resizes a database.
     *
     * @param capacityUnits
     *         sizing of a 'classic' db in Astra
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resizeDatabase
     */
    public void resize(int capacityUnits) {
        // Parameter validations
        Assert.isTrue(capacityUnits > 0, "Capacity Unit");
        // Build request
        String body = "{ \"capacityUnits\":" + capacityUnits + "}";
        // Invoke Http endpoint
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/resize", databasesClient.bearerAuthToken, body);
        // Check response code
        assertHttpCodeAccepted(res, "resize");
    }

    /**
     * Resets Password.
     *
     * @param username
     *         username
     * @param password
     *         password
     *         <p>
     *         https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resetPassword
     */
    public void resetPassword(String username, String password) {
        // Parameter validations
        Assert.hasLength(username, "username");
        Assert.hasLength(password, "password");
        // Build body
        String body = "{" + "\"username\": \"" + username + "\", " + "\"password\": \"" + password + "\"  }";
        // Invoke
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/resetPassword", databasesClient.bearerAuthToken, body);
        // Check response code
        assertHttpCodeAccepted(res, "resetPassword");
    }

    // ---------------------------------
    // ----     Datacenters         ----
    // ---------------------------------    

    /**
     * Delegate datacenters operation in a dedicated class
     *
     * @return cdc client
     */
    public DbDatacenterClient datacenter() {
        return new DbDatacenterClient(this, databasesClient.bearerAuthToken);
    }

    // ---------------------------------
    // ----          CDC            ----
    // ---------------------------------

    /**
     * Delegate cdc operation in a dedicated class
     *
     * @return cdc client
     */
    public DbCdcClient cdc() {
        return new DbCdcClient(databasesClient.bearerAuthToken, get());
    }

    // ---------------------------------
    // ----       Telemetry         ----
    // ---------------------------------

    /**
     * Delegate Telemetry operation in a dedicated class
     *
     * @return telemetry client
     */
    public TelemetryClient telemetry() {
        return new TelemetryClient(this, databaseId);
    }

    // ---------------------------------
    // ----       Access List       ----
    // ---------------------------------

    /**
     * Delegate access lists operation in a dedicated class
     *
     * @return accesslist client
     */
    public DbAccessListClient accessList() {
        return new DbAccessListClient(databasesClient.bearerAuthToken, get());
    }

    // ---------------------------------
    // ----       Private Links     ----
    // ---------------------------------

    /**
     * Delegate privateLink operation in a dedicated class
     *
     * @return privateLink client
     */
    public DbPrivateLinksClient privateLink() {
        return new DbPrivateLinksClient(databasesClient.bearerAuthToken, get());
    }

    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------

    /**
     * Endpoint to access dbs.
     *
     * @return database endpoint
     */
    public String getEndpointDatabase() {
        return getEndpointDatabase(databaseId);
    }

    /**
     * Endpoint to access dbs (static)
     *
     * @param dbId
     *         database identifier
     * @return database endpoint
     */
    public static String getEndpointDatabase(String dbId) {
        return DatabasesClient.getApiDevopsEndpointDatabases() + "/" + dbId;
    }

    /**
     * Endpoint to access keyspace.
     *
     * @param keyspace
     *         keyspace identifier
     * @return endpoint
     */
    public String getEndpointKeyspace(String keyspace) {
        return getEndpointKeyspace(databaseId, keyspace);
    }

    /**
     * Endpoint to access keyspace. (static).
     *
     * @param dbId
     *         database identifier
     * @param keyspace
     *         keyspace identifier
     * @return endpoint
     */
    public static String getEndpointKeyspace(String dbId, String keyspace) {
        return getEndpointDatabase(dbId) + "/keyspaces/" + keyspace;
    }

    /**
     * Response validation
     *
     * @param res
     *         current response
     * @param action
     *         action taken
     */
    public void assertHttpCodeAccepted(ApiResponseHttp res, String action) {
        String errorMsg = " Cannot " + action + " db=" + databaseId + " code=" + res.getCode() + " msg=" + res.getBody();
        Assert.isTrue(HTTP_ACCEPTED == res.getCode(), errorMsg);
    }

    /**
     * Getter accessor for attribute 'databaseId'.
     *
     * @return current value of 'databaseId'
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Access current token.
     *
     * @return current token
     */
    public String getToken() {
        return databasesClient.getToken();
    }

}
