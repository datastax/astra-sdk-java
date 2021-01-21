package org.datastax.astra.doc;

import static org.datastax.astra.AstraClient.DEFAULT_CONTENT_TYPE;
import static org.datastax.astra.AstraClient.DEFAULT_TIMEOUT;
import static org.datastax.astra.AstraClient.HEADER_CONTENT_TYPE;
import static org.datastax.astra.AstraClient.PATH_SCHEMA;
import static org.datastax.astra.AstraClient.PATH_SCHEMA_NAMESPACES;
import static org.datastax.astra.AstraClient.handleError;
import static org.datastax.astra.api.AbstractApiClient.*;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datastax.astra.AstraClient;
import org.datastax.astra.api.AbstractApiClient;
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
public class NamespaceClient {
    
    /** Constants. */
    public static final String PATH_NAMESPACES  = "/v2/namespaces";
    public static final String PATH_COLLECTIONS = "/collections";
    
    /** Astra Client. */
    private final ApiDocumentClient docClient;
    
    /** Namespace. */
    private final String namespace;
    
    /**
     * Full constructor.
     */
    public NamespaceClient(ApiDocumentClient docClient, String namespace) {
        this.docClient    = docClient;
        this.namespace    = namespace;
    }
    
    /**
     * Find a namespace and its metadata based on its id
     */
    public Optional<Keyspace> find() {
        Assert.hasLength(namespace, "namespaceId");
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() + PATH_SCHEMA + ApiDocumentClient.PATH_SCHEMA_NAMESPACES + "/" + namespace ))
                    .GET().build();
            HttpResponse<String> response = ApiDocumentClient.getHttpClient()
                    .send(request, BodyHandlers.ofString());
            
            if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
                return Optional.empty();
            }
            return Optional.ofNullable(
                    ApiDocumentClient.getObjectMapper().readValue(
                            ApiDocumentClient.getHttpClient().send(request, BodyHandlers.ofString()).body(), 
                              new TypeReference<ApiResponse<Keyspace>>(){}).getData());
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Check if namespace exists.
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Create a namespace. (not allowed on ASTRA yet)
     */
    public void create(List<DataCenter> datacenters) {
        Assert.notNull(namespace, "namespace");
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(!datacenters.isEmpty(), "DataCenters are required");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() + PATH_SCHEMA + ApiDocumentClient.PATH_SCHEMA_NAMESPACES))
                    .POST(BodyPublishers.ofString(
                            ApiDocumentClient.getObjectMapper().writeValueAsString(
                                    new Keyspace(namespace, datacenters))))
                    .build();
            handleError(ApiDocumentClient.getHttpClient()
                    .send(request, BodyHandlers.ofString()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Delete a namespace. (not allowed on ASTRA yet)
     */
    public void delete() {
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES + "/" + namespace ))
                    .DELETE().build();
            handleError(client.getHttpClient()
                    .send(request, BodyHandlers.ofString()));
            
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
        
    }

    /**
     * List collections in namespace.
     * 
     * GET /v2/namespaces/{namespace-id}/collections
     */
    public Stream<String> collectionNames() {
        try {
            // Create a GET REQUEST
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(DEFAULT_TIMEOUT)
                    .header(HEADER_CONTENT_TYPE, DEFAULT_CONTENT_TYPE)
                    .header(AstraClient.HEADER_CASSANDRA, client.getAuthenticationToken())
                    .uri(URI.create(client.getBaseUrl() + PATH_NAMESPACES 
                            + "/" + namespace + PATH_COLLECTIONS))
                    .GET().build();
            
            // Invoke
            HttpResponse<String> response = 
                    client.getHttpClient().send(request, BodyHandlers.ofString());
            
            // Marshalling as Object
            ApiResponse<List<CollectionMetaData>> oResponse = 
                    client.getObjectMapper().readValue(response.body(), 
                            new TypeReference<ApiResponse<List<CollectionMetaData>>>(){});
            
            // Mapping to set
            return oResponse.getData().stream()
                         .map(CollectionMetaData::getName)
                         .collect(Collectors.toSet()).stream();
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create a new collection", e);
        }
    }
    
    /**
     * Move to the collection client
     */
    public CollectionClient collection(String collectionName) {
        return new CollectionClient(client, this, namespace, collectionName);
    }
    
}
