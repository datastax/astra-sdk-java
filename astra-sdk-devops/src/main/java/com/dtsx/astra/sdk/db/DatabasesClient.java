package com.dtsx.astra.sdk.db;


import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseFilter;
import com.dtsx.astra.sdk.db.domain.DatabaseFilter.Include;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Devops API Client working with Databases.
 */
public class DatabasesClient {
    
    /** Pth in the */
    public static final String PATH_DATABASES = "/databases";
    
    /** Load Database responses. */
    private static final TypeReference<List<Database>> RESPONSE_DATABASES =  
            new TypeReference<List<Database>>(){};

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /** hold a reference to the bearer token. */
    protected final String bearerAuthToken;
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param bearerAuthToken
     *      authenticated token
     */
    public DatabasesClient(String bearerAuthToken) {
        Assert.hasLength(bearerAuthToken, "bearerAuthToken");
        this.bearerAuthToken = bearerAuthToken;
    } 
    
    // ---------------------------------
    // ----        CRUD             ----
    // ---------------------------------
    
    /**
     * Returns list of databases with default filter.
     * (include=non terminated, provider=ALL,limit=25)
     *
     * @return
     *      matching db
     */
    public Stream<Database> findAll() {
        return search(DatabaseFilter.builder()
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
    public Stream<Database> findAllNonTerminated() {
        return search(DatabaseFilter.builder().build());
    }
    
    /**
     * Retrieve list of all Databases of the account and filter on name
     * 
     * @param name
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Stream<Database> findByName(String name) {
        Assert.hasLength(name, "Database name");
        return findAllNonTerminated().filter(db->name.equals(db.getInfo().getName()));
    }

    /**
     * Find a database from its id.
     *
     * @param id
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Optional<Database> findById(String id) {
        Assert.hasLength(id, "Database identifier");
        return id(id).find();
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
    public Stream<Database> search(DatabaseFilter filter) {
        Assert.notNull(filter, "filter");
        ApiResponseHttp res = http.GET(getApiDevopsEndpointDatabases() + filter.urlParams(), bearerAuthToken);
        return JsonUtils.unmarshallType(res.getBody(), RESPONSE_DATABASES).stream();
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
    public String create(DatabaseCreationRequest dbCreationRequest) {
        Assert.notNull(dbCreationRequest, "Database creation request");
        ApiResponseHttp res = http.POST(getApiDevopsEndpointDatabases(), bearerAuthToken, JsonUtils.marshall(dbCreationRequest));
        if (HttpURLConnection.HTTP_CREATED != res.getCode()) {
            throw new IllegalStateException("Expected code 201 to create db but got " 
                        + res.getCode() + "body=" + res.getBody());
        }
        return res.getHeaders().get("location");
    }
    
    // ---------------------------------
    // ----   Access a Database     ----
    // ---------------------------------
    
    /**
     * Use the database part of the API.
     * 
     * @param dbId
     *          unique identifier id
     * @return
     *          client specialized for this db
     */
    public DatabaseClient id(String dbId) {
        Assert.hasLength(dbId, "Database Id should not be null nor empty");
        return new DatabaseClient(this, dbId);
    }
    
    /**
     *  Use the database part of the API from its name.
     * 
     * @param dbName
     *          name for a database
     * @return DatabaseClient
     */
    public DatabaseClient name(String dbName) {
        Assert.hasLength(dbName, "Database Id should not be null nor empty");
        List<Database> dbs = findByName(dbName).collect(Collectors.toList());
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
