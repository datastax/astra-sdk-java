package com.dtsx.astra.sdk.db;


import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.domain.*;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponse;
import com.dtsx.astra.sdk.utils.ApiResponseError;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.dtsx.astra.sdk.utils.observability.ApiRequestObserver;
import com.fasterxml.jackson.core.type.TypeReference;
import com.dtsx.astra.sdk.db.domain.DatabaseFilter.Include;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import java.net.HttpURLConnection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Devops API Client working with Databases.
 */
public class AstraDBOpsClient extends AbstractApiClient {

    /** Load Database responses. */
    private static final TypeReference<List<Database>> RESPONSE_DATABASES =  
            new TypeReference<List<Database>>(){};

    /** Load Database responses. */
    private static final TypeReference<List<AccessList>> RESPONSE_ACCESS_LIST =
            new TypeReference<List<AccessList>>(){};

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public AstraDBOpsClient(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public AstraDBOpsClient(String token, AstraEnvironment env) {
        super(token, env);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     * @param observers
     *     list of observers
     */
    public AstraDBOpsClient(String token, AstraEnvironment env, Map<String, ApiRequestObserver> observers) {
        super(token, env, observers);
        HttpClientWrapper.registerObservers(observers);
    }

    /** {@inheritDoc} */
    @Override
    public String getServiceName() {
        return "db";
    }

    // ---------------------------------
    // ----        REGIONS          ----
    // ---------------------------------

    /**
     * Access Astra Db region topology.
     *
     * @return
     *      work with regions
     */
    public DbRegionsClient regions() {
        return new DbRegionsClient(token, getEnvironment());
    }


    // ---------------------------------
    // ----  GLOBAL ACCESS LIST     ----
    // ---------------------------------

    /**
     * Find All Access List.
     * @return
     *      access list
     */
    public Stream<AccessList> findAllAccessLists() {
        return JsonUtils.unmarshallType(GET(getEndpointAccessLists(), getOperationName("findAllAccessLists"))
                .getBody(), RESPONSE_ACCESS_LIST).stream();
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
     * Retrieve frist DB from its name.
     * @param name
     *      name
     * @return
     *      if the db exists or not
     */
    public Optional<Database> findFirstByName(String name) {
        return findByName(name).findFirst();
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
        return database(id).find();
    }
    
    /**
     * Find Databases matching the provided filter.
     * <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/listDatabases">Reference Documentation</a>
     *
     * @param filter
     *      filter to search for db
     * @return
     *      list of db
     */
    public Stream<Database> search(DatabaseFilter filter) {
        Assert.notNull(filter, "filter");
        ApiResponseHttp res = GET(getEndpointDatabases() + filter.urlParams(), getOperationName("search"));
        try {
            return JsonUtils.unmarshallType(res.getBody(), RESPONSE_DATABASES).stream();
        } catch(Exception e) {
            // Specialization of the exception
            ApiResponseError responseError = null;
            try {
                responseError = JsonUtils.unmarshallBean(res.getBody(), ApiResponseError.class);
            } catch (Exception ef) {}
            if (responseError!= null && responseError.getErrors() != null && !responseError.getErrors().isEmpty()) {
                if (responseError.getErrors().get(0).getId() == 340018) {
                    throw new IllegalArgumentException("You have provided an invalid token, please check", e);
                }
            }
            throw e;
        }
    }
    
    /**
     * Create a database base on some parameters.
     *
     * @param dbCreationRequest
     *      creation request with tier and capacity unit
     * @return
     *      the new instance id.
     *
     * <a href="https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/createDatabase">Reference Documentation</a>
     */
    public String create(DatabaseCreationRequest dbCreationRequest) {
        Assert.notNull(dbCreationRequest, "Database creation request");
        ApiResponseHttp res = POST(getEndpointDatabases(), JsonUtils.marshall(dbCreationRequest), getOperationName("create"));
        if (HttpURLConnection.HTTP_CREATED != res.getCode()) {
            throw new IllegalStateException("Expected code 201 to create db but got " 
                        + res.getCode() + "body=" + res.getBody());
        }
        return res.getHeaders().get("location");
    }

    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------

    /**
     * Use the database part of the API.
     *
     * @param dbId
     *          unique identifier id
     * @return
     *          client specialized for this db
     */
    public DbOpsClient database(String dbId) {
        Assert.hasLength(dbId, "Database Id should not be null nor empty");
        return new DbOpsClient(token, environment, dbId);
    }

    /**
     * Use the database part of the API from its name.
     *
     * @param dbName
     *          name for a database
     * @return DatabaseClient
     */
    public DbOpsClient databaseByName(String dbName) {
        Assert.hasLength(dbName, "Database Id should not be null nor empty");
        List<Database> dbs = findByName(dbName).collect(Collectors.toList());
        if (1 == dbs.size()) {
            return new DbOpsClient(token, environment, dbs.get(0).getId());
        }
        throw new IllegalArgumentException("Cannot retrieve database from its name (matching count=" + dbs.size() + ")");
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getEndpointDatabases() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/databases";
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getEndpointAccessLists() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/access-lists";
    }

}
