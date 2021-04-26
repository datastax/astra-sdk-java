package com.datastax.stargate.sdk.rest;


import static com.datastax.stargate.sdk.core.ApiSupport.PATH_SCHEMA;
import static com.datastax.stargate.sdk.core.ApiSupport.getHttpClient;
import static com.datastax.stargate.sdk.core.ApiSupport.getObjectMapper;
import static com.datastax.stargate.sdk.core.ApiSupport.handleError;
import static com.datastax.stargate.sdk.core.ApiSupport.startRequest;
import static com.datastax.stargate.sdk.rest.ApiRestClient.PATH_SCHEMA_KEYSPACES;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.rest.domain.TableDefinition;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for API resource /v2/namespaces.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyspaceClient {
    
    /** Astra Client. */
    private final ApiRestClient restclient;
    
    /** Namespace. */
    private final String keyspace;
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, TableClient> tablesClient = new HashMap<>();
    
    /**
     * Full constructor.
     */
    public KeyspaceClient(ApiRestClient restclient, String keyspace) {
        this.restclient    = restclient;
        this.keyspace = keyspace;
    }
    
    public String getEndPointSchemaKeyspace() {
        return restclient.getEndPointApiRest()
                + PATH_SCHEMA 
                + PATH_SCHEMA_KEYSPACES
                + "/" + keyspace;
    }
    
    /**
     * Find a namespace and its metadata based on its id
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getKeyspace
     */
    public Optional<Keyspace> find() {
        Assert.hasLength(keyspace, "keyspace id");
        // Invoke Http Endpoint
        HttpResponse<String> response;
        try {
             response = getHttpClient().send(
                     startRequest(getEndPointSchemaKeyspace(), 
                     restclient.getToken()).GET().build(), 
                     BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot find keyspace " + keyspace, e);
        }
        
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            return Optional.empty();
        }
        
        handleError(response);
        
        if (HttpURLConnection.HTTP_OK == response.statusCode()) {
            try {
                TypeReference<ApiResponse<Keyspace>> expectedType = new TypeReference<>(){};
                return Optional.ofNullable(
                        getObjectMapper().readValue(response.body(), expectedType)
                                         .getData());
            } catch (Exception e) {
                throw new RuntimeException("Cannot Marshall output in 'find keyspace()' body=" + response.body(), e);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Check it the keyspace exist.
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Create a keyspace providing the replications per Datacenter.
     *
     * - IF NOT EXIST is always applied.
     */
    public void create(DataCenter... datacenters) {
        Assert.notNull(keyspace, "keyspace");
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(datacenters.length>0, "DataCenters are required");
        String endpoint = restclient.getEndPointApiRest() + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES;
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(
                    new Keyspace(keyspace, Arrays.asList(datacenters)));
            response = getHttpClient().send(
                  startRequest(endpoint, restclient.getToken())
                  .POST(BodyPublishers.ofString(reqBody)).build(), BodyHandlers.ofString());
            
        } catch (Exception e) {
            throw new RuntimeException("Cannot create keyspace " + keyspace, e);
        }
        handleError(response);
    }
    
    /**
     * Create a namespace.
     */
    public void createSimple(int replicas) {
        Assert.notNull(keyspace, "namespace");
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        String endpoint = restclient.getEndPointApiRest() + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES;
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(
                    new Namespace(keyspace, replicas));
            response = getHttpClient().send(
                  startRequest(endpoint, restclient.getToken())
                  .POST(BodyPublishers.ofString(reqBody)).build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot find namespace " + keyspace, e);
        }
        handleError(response);
    }
    
    /**
     * Delete a keyspace.
     * 
     * @see https://stargate.io/docs/stargate/1.0/developers-guide/api_ref/openapi_rest_ref.html#_deletekeyspace
     */
    public void delete() {
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(getEndPointSchemaKeyspace(), restclient.getToken())
                    .DELETE().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete keyspace", e);
        }
        handleError(response);
    }
    
    /**
     * List tablenames in keyspace.
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getTables
     */
    public Stream<TableDefinition> tables() {
        HttpResponse<String> response;
        try {
            // Invoke
            response = getHttpClient().send(
                    startRequest(getEndPointSchemaKeyspace() + "/tables", restclient.getToken())
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve table list", e);
        }
        
        handleError(response);
        
        try {
            TypeReference<ApiResponse<List<TableDefinition>>> expectedType = new TypeReference<>(){};
            return getObjectMapper().readValue(response.body(), expectedType)
                                    .getData().stream()
                                    .collect(Collectors.toSet()).stream();
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall collection list", e);
        }
    }
    
    /**
     * Map to list only table names.
     */
    public Stream<String> tableNames() {
        return tables().map(TableDefinition::getName);
    }
    
    /**
     * Move to the Table client
     */
    public TableClient table(String tableName) {
        Assert.hasLength(tableName, "tableName");
        if (!tablesClient.containsKey(tableName)) {
            tablesClient.put(tableName, new TableClient(restclient, this, tableName));
        }
        return tablesClient.get(tableName);
    }
    
    /**
     * Syntax sugar more easier to understand in a fluent API.
     */
    public void createTable(String tableName, CreateTable ct) {
        table(tableName).create(ct);
    }
    
    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return
     *       current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }
    
    
}
