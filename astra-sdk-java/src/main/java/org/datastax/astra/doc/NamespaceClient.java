package org.datastax.astra.doc;

import static org.datastax.astra.api.AbstractApiClient.CONTENT_TYPE_JSON;
import static org.datastax.astra.api.AbstractApiClient.HEADER_CASSANDRA;
import static org.datastax.astra.api.AbstractApiClient.HEADER_CONTENT_TYPE;
import static org.datastax.astra.api.AbstractApiClient.PATH_SCHEMA;
import static org.datastax.astra.api.AbstractApiClient.REQUEST_TIMOUT;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datastax.astra.api.ApiResponse;
import org.datastax.astra.schemas.DataCenter;
import org.datastax.astra.schemas.Keyspace;
import org.datastax.astra.schemas.Namespace;
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
    public void create(DataCenter... datacenters) {
        Assert.notNull(namespace, "namespace");
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(datacenters.length>0, "DataCenters are required");
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() + PATH_SCHEMA + ApiDocumentClient.PATH_SCHEMA_NAMESPACES))
                    .POST(BodyPublishers.ofString(
                            ApiDocumentClient.getObjectMapper().writeValueAsString(
                                    new Namespace(namespace, Arrays.asList(datacenters)))))
                    .build();
            docClient.handleError(ApiDocumentClient.getHttpClient().send(request, BodyHandlers.ofString()));
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
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() + PATH_SCHEMA + ApiDocumentClient.PATH_SCHEMA_NAMESPACES + "/" + namespace ))
                    .DELETE().build();
            docClient.handleError(ApiDocumentClient.getHttpClient()
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
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, docClient.getToken())
                    .uri(URI.create(docClient.getBaseUrl() + PATH_NAMESPACES 
                            + "/" + namespace + PATH_COLLECTIONS))
                    .GET().build();
            
            // Invoke
            HttpResponse<String> response = 
                    ApiDocumentClient.getHttpClient().send(request, BodyHandlers.ofString());
            
            // Marshalling as Object
            ApiResponse<List<CollectionMetaData>> oResponse = 
                    ApiDocumentClient.getObjectMapper().readValue(response.body(), 
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
        return new CollectionClient(docClient, this, collectionName);
    }

    /**
     * Getter accessor for attribute 'namespace'.
     *
     * @return
     *       current value of 'namespace'
     */
    public String getNamespace() {
        return namespace;
    }
    
}
