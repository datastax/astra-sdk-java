package com.datastax.astra.sdk.databases;


import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.databases.domain.DatabaseCreationRequest;
import com.datastax.astra.sdk.databases.domain.DatabaseFilter;
import com.datastax.astra.sdk.databases.domain.DatabaseFilter.Include;
import com.datastax.astra.sdk.utils.ApiLocator;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for the Astra Devops API.
 * 
 * The JDK11 client http is used and as such jdk11+ is required
 * 
 * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabasesClient {
    
    /** Pth in the */
    public static final String PATH_DATABASES = "/databases";
    
    /** Load Database responses. */
    private static final TypeReference<List<Database>> RESPONSE_DATABASES =  
            new TypeReference<List<Database>>(){};
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http = HttpApisClient.getInstance();
            
    /** hold a reference to the bearer token. */
    protected final String bearerAuthToken;
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param bearerAuthToken
     *      authenticated token
     */
    public DatabasesClient(String bearerAuthToken) {
       this.bearerAuthToken = bearerAuthToken;
       Assert.hasLength(bearerAuthToken, "bearerAuthToken");
    } 
    
    // ---------------------------------
    // ----        CRUD             ----
    // ---------------------------------
    
    /**
     * Returns list of databases with default filter.
     * (include=nonterminated, provider=ALL,limit=25)
     *
     * @return
     *      matching db
     */
    public Stream<Database> databases() {
        return searchDatabases(DatabaseFilter.builder()
                .include(Include.ALL)
                .provider(CloudProviderType.ALL)
                .limit(1000)
                .build());
    }
    
    /**
     * Default Filter to find databases.
     *
     * @return
     *      list of non terminated db
     */
    public Stream<Database> databasesNonTerminated() {
        return searchDatabases(DatabaseFilter.builder().build());
    }
    
    /**
     * Retrieve list of all Databases of the account and filter on name
     * 
     * @param name
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Stream<Database> databasesNonTerminatedByName(String name) {
        Assert.hasLength(name, "Database name");
        return databasesNonTerminated().filter(db->name.equals(db.getInfo().getName()));
    }
    
    /**
     * Find Databases matching the provided filter.
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/listDatabases
     * 
     * @param filter
     *      filter to search for db
     * @return
     *      list of db
     */
    public Stream<Database> searchDatabases(DatabaseFilter filter) {
        Assert.notNull(filter, "filter");
        ApiResponseHttp res = http.GET(getApiDevopsEndpointDatabases() + filter.urlParams(), bearerAuthToken);
        return unmarshallType(res.getBody(), RESPONSE_DATABASES).stream();
    }
    
    /**
     * Create a database base on some parameters.
     * 
     * @param dbCreationRequest
     *      creation request with tier and capacity unit
     * @return
     *      the new instance id.
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/createDatabase
     */
    public String createDatabase(DatabaseCreationRequest dbCreationRequest) {
        Assert.notNull(dbCreationRequest, "Database creation request");
        ApiResponseHttp res = http.POST(getApiDevopsEndpointDatabases(), bearerAuthToken, marshall(dbCreationRequest));
        
        if (HttpURLConnection.HTTP_CREATED != res.getCode()) {
            throw new IllegalStateException("Expected code 201 to create db but got " 
                        + res.getCode() + "body=" + res.getBody());
        }
        return res.getHeaders().get("location");
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Use the database part of the API.
     * 
     * @param dbId
     *          unique identifieer id
     * @return
     *          client specialized for this db
     */
    public DatabaseClient database(String dbId) {
        Assert.hasLength(dbId, "Database Id should not be null nor empty");
        return new DatabaseClient(this, dbId);
    }
    
    /**
     * Find a database from its name and not its id.
     * 
     * @param dbName
     *          name for a database
     * @return DatabaseClient
     */
    public DatabaseClient databaseByName(String dbName) {
        Assert.hasLength(dbName, "Database Id should not be null nor empty");
        List<Database> dbs = databasesNonTerminatedByName(dbName).collect(Collectors.toList());
        if (1 == dbs.size()) {
            return new DatabaseClient(this, dbs.get(0).getId());
        }
        throw new IllegalArgumentException("Cannot retrieve database from its name "
                + "(matching count=" + dbs.size() + ")");
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointDatabases() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_DATABASES;
    }
    
    /**
     * Access to the current authentication token.
     *
     * @return
     *      authentication token
     */
    public String getToken() {
        return bearerAuthToken;
    }

}
