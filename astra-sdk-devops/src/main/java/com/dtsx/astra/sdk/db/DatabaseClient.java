package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.*;
import com.dtsx.astra.sdk.db.domain.exception.RegionNotFoundException;
import com.dtsx.astra.sdk.db.telemetry.TelemetryClient;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.dtsx.astra.sdk.utils.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static java.net.HttpURLConnection.HTTP_ACCEPTED;
import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Working with the Database part of the devop API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseClient.class);

    /** Returned type. */
    private static final TypeReference<List<Datacenter>> DATACENTER_LIST =
            new TypeReference<List<Datacenter>>(){};

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
     *          uniique database identifier
     */
    public DatabaseClient(DatabasesClient databasesClient, String databaseId) {
       Assert.notNull(databasesClient,"databasesClient");
       Assert.hasLength(databaseId, "databaseId");
       this.databaseId = databaseId;
       this.databasesClient = databasesClient;
    }
    
    // ---------------------------------
    // ----       CRUD              ----
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
        Optional<Database> db = find();
        return db.isPresent() && (DatabaseStatusType.ACTIVE == db.get().getStatus());
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
        http.POST(getEndpointKeyspace(keyspace), databasesClient.bearerAuthToken);
    }
    
    /**
     * Syntax sugar for doc API
     * 
     * @param namespace
     *          target namespace
     *         
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace          
     */
    public void createNamespace(String namespace) {
        createKeyspace(namespace);
    }
    
    /**
     * Download SecureBundle for a specific data center
     * 
     * @param destination
     *      file to save the securebundle
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/generateSecureBundleURL
     */
    public void downloadSecureConnectBundle(String destination) {
        if (isActive() ) {
            // Parameter validation
            Assert.hasLength(destination, "destination");
            // Invoke
            ApiResponseHttp res = http.POST(getEndpointDatabase() + "/secureBundleURL", databasesClient.bearerAuthToken);
            // Check response coode
            Assert.isTrue(HTTP_OK == res.getCode(), "Error in 'downloadSecureConnectBundle', with id=" +databaseId);
            // Read file url to download
            String url = (String) JsonUtils.unmarshallBean(res.getBody(), Map.class).get("downloadURL");
            // Download binary in target folder
            Utils.downloadFile(url, destination);
        } else {
            LOGGER.warn("DB "+ databaseId + " is not active, no download");
        }
    }
    
    /**
     * Download all SecureBundle.
     * 
     * @param destination
     *      file to save the securebundle
     */
    public void downloadAllSecureConnectBundles(String destination) {
        Optional<Database> odb = find(); 
        // Validation
        Assert.hasLength(destination, "destination");
        Assert.isTrue(new File(destination).exists(), "Destination folder");
        Assert.isTrue(odb.isPresent(), "Database '" + databaseId + "' has not been found");
        odb.get().getInfo().getDatacenters().stream().forEach(dc -> {
            String fileName = destination + File.separator + buildScbFileName(odb.get().getId(), dc.getRegion());
            if (!new File(fileName).exists()) {
                Utils.downloadFile(dc.getSecureBundleUrl(), fileName);
                LOGGER.info("+ Downloading file: {}", fileName);
            } else {
                LOGGER.info("+ {} available (no download)", buildScbFileName(odb.get().getId(), dc.getRegion()));
            }
        });
    }

    private String buildScbFileName(String dId, String dbRegion) {
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
        checkResponse(res, "park");
    }
    
    /**
     * Unparks a database.
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/unparkDatabase
     */
    public void unpark() {
        // Invoke Http endpoint
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/unpark", databasesClient.bearerAuthToken);
        // Check response code
        checkResponse(res, "unpark");
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
        checkResponse(res, "terminate");
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
        checkResponse(res, "resize");
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
        String body = new StringBuilder("{")
                .append("\"username\": \"").append(username).append("\", ")
                .append("\"password\": \"").append(password).append( "\"  }")
                .toString();
        // Invoke
        ApiResponseHttp res = http.POST(getEndpointDatabase() + "/resetPassword", databasesClient.bearerAuthToken, body);
        // Check response code
        checkResponse(res, "resetPassword");
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
        DatabaseRegionCreationRequest req = new DatabaseRegionCreationRequest(tier, cloudProvider.getCode(), regionName);
        String body = JsonUtils.marshall(Arrays.asList(req));
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
        checkResponse(res, "deleteRegion");
    }

    // ---------------------------------
    // ----       Telemetry         ----
    // ---------------------------------

    /**
     * Delegate Telemetry operation in a dedicated class
     *
      * @return
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
    public void updateccessLists() {
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
     *      database identifer
     * @return
     *      database endpoint
     */
    public static String getEndpointDatabase(String dbId) {
        return DatabasesClient.getApiDevopsEndpointDatabases() + "/" + dbId;
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
     *      database identifer
     * @param keyspace
     *      keyspace identifier
     * @return
     *      endpoint
     */
    public static String getEndpointKeyspace(String dbId, String keyspace) {
        return getEndpointDatabase(dbId) + "/keyspaces/" + keyspace;
    }
    
    /**
     * Mutualization of 202 code validation.
     * 
     * @param res
     *      current response
     * @param action
     *      action taken
     */
    private void checkResponse(ApiResponseHttp res, String action) {
        String errorMsg = new StringBuilder()
                .append(" Cannot " + action)
                .append(" db=" + databaseId )
                .append(" code=" + res.getCode() )
                .append(" msg=" + res.getBody()).toString();
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
