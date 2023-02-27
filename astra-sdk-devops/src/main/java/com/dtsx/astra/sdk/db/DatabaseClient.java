package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.*;
import com.dtsx.astra.sdk.db.exception.*;
import com.dtsx.astra.sdk.db.telemetry.TelemetryClient;
import com.dtsx.astra.sdk.streaming.StreamingClient;
import com.dtsx.astra.sdk.streaming.domain.CdcDefinition;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.dtsx.astra.sdk.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.*;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;

/**
 * Devops API Client working with a Database.
 */
public class DatabaseClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseClient.class);

    /** Returned type. */
    private static final TypeReference<List<Datacenter>> DATACENTER_LIST =
            new TypeReference<List<Datacenter>>(){};

    /** Load Cdc responses. */
    private static final TypeReference<List<CdcDefinition>> TYPE_LIST_CDC =
            new TypeReference<List<CdcDefinition>>(){};

    /** unique db identifier. */
    private final String databaseId;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();


    /** Reference to upper resource. */
    private final DatabasesClient databasesClient;
    
    /**
     * Default constructor.
     *
     * @param databasesClient
     *          database client
     * @param databaseId
     *          unique database identifier
     */
    public DatabaseClient(DatabasesClient databasesClient, String databaseId) {
       Assert.notNull(databasesClient,"databasesClient");
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
     * @return
     *      the database if present,
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/getDatabase
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
     * @return
     *      current db or error
     */
    public Database get() {
        return find().orElseThrow(() -> new DatabaseNotFoundException(databaseId));
    }
    
    /**
     * Evaluate if a database exists using the findById method.
     * 
     * @return
     *      database existence
     *      
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * If the app is active.
     *
     * @return
     *      tells if database is ACTIVE
     */
    public boolean isActive() {
        return DatabaseStatusType.ACTIVE == get().getStatus();
    }
    
    /**
     * Create a new keyspace in a DB.
     * 
     * @param keyspace
     *      keyspace name to create
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace
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
     *      file to save the secure bundle
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/generateSecureBundleURL
     */
    public void downloadDefaultSecureConnectBundle(String destination) {
        // Parameters Validation
        Assert.hasLength(destination, "destination");
        if (!isActive()) throw new IllegalStateException("Database '" + databaseId + "' is not available.");
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
     *      file to save the secure bundle
     * @param region
     *      download for a target region
     *
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/generateSecureBundleURL
     */
    public void downloadSecureConnectBundle(String region, String destination) {
        Assert.hasLength(region, "region");
        Assert.hasLength(destination, "destination");
        Database   db = get();
        Datacenter dc = db.getInfo().getDatacenters()
                .stream()
                .filter(d -> region.equalsIgnoreCase(d.getRegion()))
                .findFirst()
                .orElseThrow(() -> new RegionNotFoundException(region, databaseId));
        downloadSecureConnectBundle(db.getId(), dc, destination);
    }

    /**
     * Download SCB for a database and a datacenter in target location.
     * @param dbId
     *      current database identifier
     * @param dc
     *      current region
     * @param destination
     *      target destination
     */
    private void downloadSecureConnectBundle(String dbId, Datacenter dc, String destination) {
        Assert.hasLength(dbId, "database id");
        Assert.hasLength(destination, "destination");
        System.out.println(destination);
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
     *      file to save the secured bundle
     */
    public void downloadAllSecureConnectBundles(String destination) {
        Assert.hasLength(destination, "destination");
        Assert.isTrue(new File(destination).exists(), "Destination folder");
        Database db = get();
        db.getInfo()
          .getDatacenters()
          .forEach(dc -> downloadSecureConnectBundle(db.getId(), dc,
                  destination + File.separator + buildScbFileName(db.getId(), dc.getRegion())));
    }

    /**
     * Build filename for the secure connect bundle.
     *
     * @param dId
     *      databaseId
     * @param dbRegion
     *      databaseRegion
     * @return
     *      file name for the secure bundled
     */
    public String buildScbFileName(String dId, String dbRegion) {
        return "scb_" + dId + "_" + dbRegion + ".zip";
    }
    
    // ---------------------------------
    // ----       MAINTENANCE       ----
    // ---------------------------------
    
    /**
     * Parks a database
     */
    public void park() {
        // Invoke Http endpoint
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/park", databasesClient.bearerAuthToken);
        // Check response code
        assertHttpCodeAccepted(res, "park");
    }
    
    /**
     * unpark a database.
     * 
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
     * 
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
     *          sizing of a 'classic' db in Astra
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resizeDatabase
     */
    public void resize(int capacityUnits) {
        // Parameter validations
        Assert.isTrue(capacityUnits>0, "Capacity Unit");
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
     *      username
     * @param password
     *      password
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resetPassword
     */
    public void resetPassword(String username, String password) {
        // Parameter validations
        Assert.hasLength(username, "username");
        Assert.hasLength(password, "password");
        // Build body
        String body = "{" +
                "\"username\": \"" + username + "\", " +
                "\"password\": \"" + password + "\"  }";
        // Invoke
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/resetPassword", databasesClient.bearerAuthToken, body);
        // Check response code
        assertHttpCodeAccepted(res, "resetPassword");
    }
    
    // ---------------------------------
    // ----       Regions           ----
    // ---------------------------------    

    /**
     * Get Datacenters details for a region
     *
     * @return
     *      list of datacenters.
     */
    public Stream<Datacenter> regions() {
        get();
        ApiResponseHttp res = http.GET(getEndpointRegions(), databasesClient.bearerAuthToken);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Stream.of();
        } else {
            return JsonUtils.unmarshallType(res.getBody(), DATACENTER_LIST).stream();
        }
    }

    /**
     * Get a region from its name.
     *
     * @param regionName
     *      region name
     * @return
     *      datacenter if exists
    i     */
    public Optional<Datacenter> findRegion(String regionName) {
        Assert.hasLength(regionName, "regionName");
        return regions().filter(dc -> regionName.equals(dc.getRegion())).findFirst();
    }

    /**
     * Create a Region.
     *
     * @param tier
     *      tier for the db
     * @param cloudProvider
     *      Cloud provider to add a region
     * @param regionName
     *      name of the region
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/addDatacenters
     */
    public void addRegion(String tier, CloudProviderType cloudProvider, String regionName) {
        Assert.hasLength(tier, "tier");
        Assert.notNull(cloudProvider, "cloudProvider");
        Assert.hasLength(regionName, "regionName");
        get();
        if (findRegion(regionName).isPresent()) {
            throw new RegionAlreadyExistException(databaseId, regionName);
        }
        DatabaseRegionCreationRequest req = new DatabaseRegionCreationRequest(tier, cloudProvider.getCode(), regionName);
        String body = JsonUtils.marshall(Collections.singletonList(req));
        ApiResponseHttp res = http.POST(getEndpointRegions(), databasesClient.bearerAuthToken,body);
        if (res.getCode() != HttpURLConnection.HTTP_CREATED) {
            throw new IllegalStateException("Cannot Add Region: " + res.getBody());
        }
    }

    /**
     * Delete a region from its name.
     * 
     * @param regionName
     *      name of the region
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/terminateDatacenter
     */
    public void deleteRegion(String regionName) {
        Optional<Datacenter> optDc = findRegion(regionName);
        if (!optDc.isPresent()) {
            throw new RegionNotFoundException(databaseId, regionName);
        }
        // Invoke Http endpoint
        ApiResponseHttp res = http.POST(getEndpointRegions() + "/"
                + optDc.get().getId() + "/terminate", databasesClient.getToken());
        // Check response code
        assertHttpCodeAccepted(res, "deleteRegion");
    }

    // ---------------------------------
    // --- Change Data Capture (cdc) ---
    // ---------------------------------

    /**
     * Access Cdc component for a DB.
     *
     * @return
     *      list of cdc
     */
    public Stream<CdcDefinition> cdcs() {
        get();
        ApiResponseHttp res = http.GET(getEndpointDatabaseCdc(), databasesClient.bearerAuthToken);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Stream.of();
        } else {
            return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_CDC).stream();
        }
    }

    /**
     * Find a cdc by its id.
     *
     * @param cdcId
     *      identifier
     * @return
     *      cdc definition if exist
     */
    public Optional<CdcDefinition> findCdcById(String cdcId) {
        Assert.hasLength(cdcId, "cdc identifier");
        return cdcs().filter(cdc -> cdc.getConnectorName().equals(cdcId)).findFirst();
    }

    /**
     * Find the cdc based on its components.
     *
     * @param keyspace
     *      keyspace name
     * @param table
     *      table name
     * @param tenant
     *      tenant identifier
     * @return
     *      definition if present
     */
    public Optional<CdcDefinition> findCdcByDefinition(String keyspace, String table, String tenant) {
        Assert.hasLength(keyspace, "keyspace");
        Assert.hasLength(table, "table");
        Assert.hasLength(tenant, "tenant");
        return cdcs().filter(cdc ->
                        cdc.getKeyspace().equals(keyspace)
                        && cdc.getDatabaseTable().equals(table)
                        && cdc.getTenant().equals(tenant)).findFirst();
    }

    /**
     * Create cdcd from definition.
     *
     * @param keyspace
     *      keyspace name
     * @param table
     *      table name
     * @param tenant
     *      tenant identifier
     * @param topicPartition
     *      topic partition
     */
    public void createCdc(String keyspace, String table, String tenant, int topicPartition) {
        Database db = get();
        Assert.hasLength(keyspace, "keyspace");
        if (!db.getInfo().getKeyspaces().contains(keyspace)) {
            throw new KeyspaceNotFoundException(databaseId, keyspace);
        }
        new StreamingClient(databasesClient.bearerAuthToken)
                .tenant(tenant)
                .cdc()
                .create(db.getId(), keyspace, table, topicPartition);
    }

    /**
     * Delete cdc from its identifier.
     *
     * @param cdcId
     *      cdc identifier
     */
    public void deleteCdc(String cdcId) {
        deleteCdc(findCdcById(cdcId).orElseThrow(
                () -> new ChangeDataCaptureNotFoundException(cdcId, databaseId)));
    }

    /**
     * Delete cdc from its identifier.
     *
     * @param keyspace
     *      keyspace name
     * @param table
     *      table name
     * @param tenant
     *      tenant identifier
     */
    public void deleteCdc(String keyspace, String table, String tenant) {
        deleteCdc(findCdcByDefinition(keyspace, table, tenant).orElseThrow(
                () -> new ChangeDataCaptureNotFoundException(keyspace, table, tenant, databaseId)));
    }

    /**
     * Delete Cdc from its definition.
     *
     * @param cdc
     *      cdcd definition
     */
    private void deleteCdc(CdcDefinition cdc) {
        new StreamingClient(databasesClient.bearerAuthToken)
                .tenant(cdc.getTenant())
                .cdc()
                .delete(databaseId, cdc.getKeyspace(), cdc.getDatabaseTable());
    }

    // ---------------------------------
    // ----       Telemetry         ----
    // ---------------------------------

    /**
     * Delegate Telemetry operation in a dedicated class
     *
      * @return
     *      telemetry client
     */
    public TelemetryClient telemetry() {
        return new TelemetryClient(this, databaseId);
    }

    // ---------------------------------
    // ----       Access List       ----
    // ---------------------------------
    
    /**
     * TODO Get access list for a database
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetAccessListForDatabase
     */
    public void accessLists() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Replace access list for your database.
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase
     */
    public void replaceAccessLists() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Update existing fields in access list for database
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/UpsertAccessListForDatabase
     */
    public void updateAccessLists() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Add addresses to access list for a database
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AddAddressesToAccessListForDatabase
     */
    public void addAccessList() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Delete addresses or access list for database
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/DeleteAddressesOrAccessListForDatabase
     */
    public void deleteAccessList() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    // ---------------------------------
    // ----       Private Links     ----
    // ---------------------------------
    
    /**
     * TODO Get info about all private endpoint connections for a specific database
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/ListPrivateLinksForOrg
     */
    public void privateLinks() {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO  Get info about private endpoints in a region.
     *
     * @param region
     *      current region where add the private link
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetPrivateLinksForDatacenter
     */
    public void privateLinks(String region) {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Add an allowed principal to the service.
     * 
     * @param region
     *       region where add the principal
     * Configure a private endpoint connection by providing the allowed principal to connect with
     */
    public void addPrincipal(String region) {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Accept a private endpoint connection.
     * 
     * @param region
     *       region where add the private endpoint
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/AcceptEndpointToService
     */
    public void addPrivateEndpoint(String region) {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Get a specific endpoint.
     *
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/GetPrivateLinkEndpoint
     * 
     * @param region
     *      current region
     * @param endpointId
     *      endpoint id fo the region
     * @return
     *      the private endpoint of exist
     */
    public Optional<Object> findPrivateEndpoint(String region, String endpointId) {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Update private endpoint description.
     * 
     * @param region
     *      current region
     * @param endpointId
     *      endpoint id fo the region
     * @param endpoint
     *      new value for the endpoint
     *     
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/UpdateEndpoint
     */
    public void updatePrivateEndpoint(String region, String endpointId, Object endpoint) {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * TODO Delete private endpoint connection.
     *
     * @param region
     *      current region
     * @param endpointId
     *      endpoint id fo the region
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv2.html#operation/DeleteEndpoint
     */
    public void deletePrivateEndpoint(String region, String endpointId) {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointDatabase() {
        return getEndpointDatabase(databaseId);
    }

    /**
     * Endpoint to access datacenters of a db
     *
     * @return
     *      database endpoint
     */
    public String getEndpointRegions() {
        return getEndpointDatabase() + "/datacenters";
    }
    
    /**
     * Endpoint to access dbs (static)
     *
     * @param dbId
     *      database identifier
     * @return
     *      database endpoint
     */
    public static String getEndpointDatabase(String dbId) {
        return DatabasesClient.getApiDevopsEndpointDatabases() + "/" + dbId;
    }

    /**
     * Http Client for Cdc list.
     *
     * @return
     *      url to invoke CDC
     */
    public String getEndpointDatabaseCdc() {
        return StreamingClient.getApiDevopsEndpointStreaming() +  "/astra-cdc/databases/" + databaseId;
    }
    
    /**
     * Endpoint to access keyspace.
     *
     * @param keyspace
     *      keyspace identifier
     * @return
     *      endpoint
     */
    public String getEndpointKeyspace(String keyspace) {
        return getEndpointKeyspace(databaseId, keyspace);
    }
    
    /**
     * Endpoint to access keyspace. (static).
     * 
     * @param dbId
     *      database identifier
     * @param keyspace
     *      keyspace identifier
     * @return
     *      endpoint
     */
    public static String getEndpointKeyspace(String dbId, String keyspace) {
        return getEndpointDatabase(dbId) + "/keyspaces/" + keyspace;
    }
    
    /**
     * Response validation
     * 
     * @param res
     *      current response
     * @param action
     *      action taken
     */
    private void assertHttpCodeAccepted(ApiResponseHttp res, String action) {
        String errorMsg = " Cannot " + action
                + " db=" + databaseId + " code=" + res.getCode()
                + " msg=" + res.getBody();
        Assert.isTrue(HTTP_ACCEPTED == res.getCode(), errorMsg);
    }

    /**
     * Getter accessor for attribute 'databaseId'.
     *
     * @return
     *       current value of 'databaseId'
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Access current token.
     *
     * @return
     *      current token
     */
    public String getToken() {
        return databasesClient.getToken();
    }

}
