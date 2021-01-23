package com.dstx.astra.sdk.rest;


import static com.dstx.astra.sdk.rest.ApiRestClient.PATH_SCHEMA_KEYSPACES;
import static com.dstx.astra.sdk.utils.ApiSupport.CONTENT_TYPE_JSON;
import static com.dstx.astra.sdk.utils.ApiSupport.HEADER_CASSANDRA;
import static com.dstx.astra.sdk.utils.ApiSupport.HEADER_CONTENT_TYPE;
import static com.dstx.astra.sdk.utils.ApiSupport.PATH_SCHEMA;
import static com.dstx.astra.sdk.utils.ApiSupport.REQUEST_TIMOUT;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.dstx.astra.sdk.utils.ApiResponse;
import com.dstx.astra.sdk.utils.Assert;
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
    
    
    /**
     * Full constructor.
     */
    public KeyspaceClient(ApiRestClient restclient, String keyspace) {
        this.restclient    = restclient;
        this.keyspace = keyspace;
    }
    
    public Optional<Keyspace> find() {
        Assert.hasLength(keyspace, "keyspaceId");
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, restclient.getToken())
                    .uri(URI.create(restclient.getBaseUrl() + PATH_SCHEMA 
                            + PATH_SCHEMA_KEYSPACES + "/" + keyspace ))
                    .GET().build();
            HttpResponse<String> response = ApiRestClient.getHttpClient()
                    .send(request, BodyHandlers.ofString());
            
            if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
                return Optional.empty();
            }
            return Optional.ofNullable(
                    ApiRestClient.getObjectMapper().readValue(
                            ApiRestClient.getHttpClient().send(request, BodyHandlers.ofString()).body(), 
                              new TypeReference<ApiResponse<Keyspace>>(){}).getData());
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Check it the namespace exist.
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Create a namespace.
     */
    public void create(List<DataCenter> datacenters) {
        Assert.notNull(keyspace, "namespace");
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(!datacenters.isEmpty(), "DataCenters are required");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, restclient.getToken())
                    .uri(URI.create(restclient.getBaseUrl() + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES))
                    .POST(BodyPublishers.ofString(
                            ApiRestClient.getObjectMapper().writeValueAsString(
                                    new Keyspace(keyspace, datacenters))))
                    .build();
            restclient.handleError(ApiRestClient.getHttpClient()
                    .send(request, BodyHandlers.ofString()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public void delete() {
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, restclient.getToken())
                    .uri(URI.create(restclient.getBaseUrl() + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES + "/" + keyspace ))
                    .DELETE().build();
            restclient.handleError(ApiRestClient.getHttpClient()
                    .send(request, BodyHandlers.ofString()));
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        
    }

    // List tables
    // https://docs.astra.datastax.com/reference#get_api-rest-v2-schemas-keyspaces-keyspace-id-tables-1
    
    public Stream<TableDefinition> tables() {
        // TODO
        return null;
    }
    
    // TOOD
    public TableClient table(String tableName) {
        return new TableClient();
    }
    
    
}
