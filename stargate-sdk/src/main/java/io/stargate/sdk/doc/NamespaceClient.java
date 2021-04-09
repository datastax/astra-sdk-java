package io.stargate.sdk.doc;

import static io.stargate.sdk.doc.ApiDocumentClient.PATH_SCHEMA_NAMESPACES;
import static io.stargate.sdk.utils.ApiSupport.PATH_SCHEMA;
import static io.stargate.sdk.utils.ApiSupport.getHttpClient;
import static io.stargate.sdk.utils.ApiSupport.getObjectMapper;
import static io.stargate.sdk.utils.ApiSupport.handleError;
import static io.stargate.sdk.utils.ApiSupport.startRequest;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.stargate.sdk.rest.DataCenter;
import io.stargate.sdk.utils.ApiResponse;
import io.stargate.sdk.utils.Assert;

/**
 * Client for Document API 'Namespace' resource /v2/namespaces
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
    
    private String getEndPointSchemaNamespace() {
        return docClient.getEndPointApiDocument() 
                + PATH_SCHEMA 
                + PATH_SCHEMA_NAMESPACES 
                + "/" + namespace;
    }
    /**
     * Find a namespace and its metadata based on its id
     */
    public Optional<Namespace> find() {
        Assert.hasLength(namespace, "namespaceId");
        // Invoke Http Endpoint
        HttpResponse<String> response;
        try {
             response = getHttpClient().send(
                     startRequest(getEndPointSchemaNamespace(), docClient.getToken()).GET().build(), 
                     BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot find namespace " + namespace, e);
        }
        
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            return Optional.empty();
        }
        
        handleError(response);
        
        try {
            return Optional.of(marshallApiResponseNamespace(response.body()).getData());
        } catch (Exception e) {
            throw new RuntimeException("Cannot Marshall output in 'find namespace()' body=" + response.body(), e);
        }
    }
    
    /**
     * Check if namespace exists.
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Create a namespace.
     */
    public void create(DataCenter... datacenters) {
        Assert.notNull(namespace, "namespace");
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(datacenters.length>0, "DataCenters are required");
        String endpoint = docClient.getEndPointApiDocument() + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES;
        HttpResponse<String> response;
        try {
            
            String reqBody = getObjectMapper().writeValueAsString(
                    new Namespace(namespace, Arrays.asList(datacenters)));
            
            response = getHttpClient().send(
                  startRequest(endpoint, docClient.getToken())
                  .POST(BodyPublishers.ofString(reqBody)).build(), BodyHandlers.ofString());
            
        } catch (Exception e) {
            throw new RuntimeException("Cannot find namespace " + namespace, e);
        }
        handleError(response);
    }
    
    /**
     * Create a namespace.
     */
    public void createSimple(int replicas) {
        Assert.notNull(namespace, "namespace");
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        String endpoint = docClient.getEndPointApiDocument() + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES;
        HttpResponse<String> response;
        try {
            String reqBody = getObjectMapper().writeValueAsString(
                    new Namespace(namespace, replicas));
            response = getHttpClient().send(
                  startRequest(endpoint, docClient.getToken())
                  .POST(BodyPublishers.ofString(reqBody)).build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot find namespace " + namespace, e);
        }
        handleError(response);
    }
    
    
    /**
     * Delete a namespace.
     */
    public void delete() {
        String delEndPoint = docClient.getEndPointApiDocument() 
                + PATH_SCHEMA 
                + PATH_SCHEMA_NAMESPACES + "/" + namespace;
        HttpResponse<String> response;
        try {
            response = getHttpClient().send(
                    startRequest(delEndPoint, docClient.getToken())
                    .DELETE().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot delete namespace", e);
        }
        handleError(response);
    }

    /**
     * List collections in namespace.
     * 
     * GET /v2/namespaces/{namespace-id}/collections
     */
    public Stream<String> collectionNames() {
        String listcolEndpoint = docClient.getEndPointApiDocument() 
                + PATH_NAMESPACES 
                + "/" + namespace 
                + PATH_COLLECTIONS;
        HttpResponse<String> response;
        try {
            // Invoke
            response = getHttpClient().send(
                    startRequest(listcolEndpoint, docClient.getToken())
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve collection list", e);
        }
        handleError(response);
        try {
            return marshallApiResponseCollections(response.body())
                    .getData().stream()
                    .map(CollectionDefinition::getName)
                    .collect(Collectors.toSet()).stream();
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall collection list", e);
        }
    }
    
    private ApiResponse<Namespace> marshallApiResponseNamespace(String body)
    throws Exception {
       return getObjectMapper()
                 .readValue(body, 
                         new TypeReference<ApiResponse<Namespace>>(){});
    }
    
    private ApiResponse<List<CollectionDefinition>> marshallApiResponseCollections(String body)
    throws JsonMappingException, JsonProcessingException {
        return getObjectMapper()
                .readValue(body, 
                        new TypeReference<ApiResponse<List<CollectionDefinition>>>(){});
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
