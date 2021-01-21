package org.datastax.astra.rest;

import static org.datastax.astra.AstraClient.DEFAULT_CONTENT_TYPE;
import static org.datastax.astra.AstraClient.DEFAULT_TIMEOUT;
import static org.datastax.astra.AstraClient.HEADER_CONTENT_TYPE;
import static org.datastax.astra.AstraClient.PATH_SCHEMA;
import static org.datastax.astra.AstraClient.PATH_SCHEMA_KEYSPACES;

import static org.datastax.astra.AstraClient.handleError;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;

import org.datastax.astra.AstraClient;
import org.datastax.astra.api.ApiResponse;
import org.datastax.astra.schemas.DataCenter;
import org.datastax.astra.schemas.Keyspace;
import org.datastax.astra.utils.Assert;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for API resource /v2/namespaces.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyspaceClient {
    
    /** Astra Client. */
    private final AstraClient client;
    
    /** Namespace. */
    private final String keyspace;
    
    

    
    /**
     * Full constructor.
     */
    public KeyspaceClient(AstraClient astraClient, String keyspace) {
        this.client    = astraClient;
        this.keyspace = keyspace;
    }
    
    public Optional<Keyspace> find() {
        Assert.hasLength(keyspace, "keyspaceId");
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() + PATH_SCHEMA 
                            + PATH_SCHEMA_KEYSPACES + "/" + keyspace ))
                    .GET().build();
            HttpResponse<String> response = client.getHttpClient()
                    .send(request, BodyHandlers.ofString());
            
            if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
                return Optional.empty();
            }
            return Optional.ofNullable(
                    client.getObjectMapper().readValue(
                            client.getHttpClient().send(request, BodyHandlers.ofString()).body(), 
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
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES))
                    .POST(BodyPublishers.ofString(
                            client.getObjectMapper().writeValueAsString(
                                    new Keyspace(keyspace, datacenters))))
                    .build();
            handleError(client.getHttpClient()
                    .send(request, BodyHandlers.ofString()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    public void delete() {
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES + "/" + keyspace ))
                    .DELETE().build();
            handleError(client.getHttpClient()
                    .send(request, BodyHandlers.ofString()));
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        
    }

    
    //TODO list Tables etc...
    
}
