package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import com.dtsx.astra.sdk.db.domain.Datacenter;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.db.exception.RegionNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
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

/**
 * Devops API Client working with a Database.
 */
public class DatabaseClient extends AbstractApiClient {

    /**
     * Logger for our Client.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseClient.class);

    /**
     * unique db identifier.
     */
    private final String databaseId;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DatabaseClient(String token, String databaseId) {
        this(token, ApiLocator.AstraEnvironment.PROD, databaseId);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DatabaseClient(String token, ApiLocator.AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.databaseId = databaseId;
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
        ApiResponseHttp res = GET(getEndpointDatabase());
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

    // ---------------------------------
    // ----    SECURE BUNDLE        ----
    // ---------------------------------

    /**
     * Download SecureBundle for a specific data center.
     * @return
     *      binary content.
     */
    public byte[] downloadDefaultSecureConnectBundle() {
        return Utils.downloadFile(getDefaultSecureConnectBundleUrl());
    }

    /**
     * Download SecureBundle for a specific data center
     *
     * @param destination
     *         file to save the secure bundle
     */
    public void downloadDefaultSecureConnectBundle(String destination) {
        // Parameters Validation
        Assert.hasLength(destination, "destination");
        // Download binary in target folder
        Utils.downloadFile(getDefaultSecureConnectBundleUrl(), destination);
    }

    /**
     * This utility method retrieve the binary content for the bundle.
     *
     * @return
     *      secure connect bundle binary content.
     */
    private String getDefaultSecureConnectBundleUrl() {
        if (!isActive())
            throw new IllegalStateException("Database '" + databaseId + "' is not available.");
        // Get list of urls
        ApiResponseHttp res = POST(getEndpointDatabase() + "/secureBundleURL");
        // Mapping
        return (String) JsonUtils.unmarshallBean(res.getBody(), Map.class).get("downloadURL");
    }

    /**
     * Download SecureBundle for a specific data center.
     *
     * @param region
     *      region to download the SCB
     * @return
     *      binary content.
     */
    public byte[] downloadSecureConnectBundle(String region) {
        Assert.hasLength(region, "region");
        Database db = get();
        return downloadSecureConnectBundle(db.getInfo()
                .getDatacenters()
                .stream()
                .filter(d -> region.equalsIgnoreCase(d.getRegion()))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException(region, databaseId)));
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
     */
    private byte[] downloadSecureConnectBundle(Datacenter dc) {
        return Utils.downloadFile(dc.getSecureBundleUrl());
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
            LOGGER.debug("+ SCB already available ({}) ", destination);
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
        ApiResponseHttp res = POST(getEndpointDatabase() + "/park");
        // Check response code
        assertHttpCodeAccepted(res, "park", databaseId);
    }

    /**
     * unpark a database.
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/unparkDatabase
     */
    public void unpark() {
        // Invoke Http endpoint
        ApiResponseHttp res = POST(getEndpointDatabase() + "/unpark");
        // Check response code
        assertHttpCodeAccepted(res, "unpark", databaseId);
    }

    /**
     * Terminates a database.
     * <p>
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/terminateDatabase
     */
    public void delete() {
        // Invoke Http endpoint
        ApiResponseHttp res = POST(getEndpointDatabase() + "/terminate");
        // Check response code
        assertHttpCodeAccepted(res, "terminate", databaseId);
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
        ApiResponseHttp res = POST(getEndpointDatabase() + "/resize", body);
        // Check response code
        assertHttpCodeAccepted(res, "resize", databaseId);
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
        ApiResponseHttp res = POST(getEndpointDatabase() + "/resetPassword", body);
        // Check response code
        assertHttpCodeAccepted(res, "resetPassword", databaseId);
    }

    // ---------------------------------
    // ----       Keyspaces         ----
    // ---------------------------------

    /**
     * Work with keyspaces.

     * @return
     *      keyspaces client
     */
    public DbKeyspacesClient keyspaces() {
        return new DbKeyspacesClient(token, environment, databaseId);
    }

    // ---------------------------------
    // ----     Datacenters         ----
    // ---------------------------------    

    /**
     * Delegate datacenters operation in a dedicated class
     *
     * @return cdc client
     */
    public DbDatacentersClient datacenters() {
        return new DbDatacentersClient(token, environment, databaseId);
    }

    // ---------------------------------
    // ----       Access List       ----
    // ---------------------------------

    /**
     * Delegate access lists operation in a dedicated class
     *
     * @return access list client
     */
    public DbAccessListsClient accessLists() {
        return new DbAccessListsClient(token, environment, databaseId);
    }

    // ---------------------------------
    // ----          CDC            ----
    // ---------------------------------

    /**
     * Delegate cdc operation in a dedicated class
     *
     * @return cdc client
     */
    public DbCdcsClient cdc() {
        return new DbCdcsClient(token, environment, databaseId);
    }

    // ---------------------------------
    // ----       Telemetry         ----
    // ---------------------------------

    /**
     * Delegate Telemetry operation in a dedicated class
     *
     * @return telemetry client
     */
    public DbTelemetryClient telemetry() {
        return new DbTelemetryClient(token, environment, databaseId);
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
        return new DbPrivateLinksClient(token, environment, databaseId);
    }

    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------

    /**
     * Gets databaseId
     *
     * @return value of databaseId
     */
    public String getDatabaseId() {
        return databaseId;
    }

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
    public String getEndpointDatabase(String dbId) {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/databases/" + dbId;
    }

}
