/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.doc;

import static com.datastax.stargate.sdk.core.ApiSupport.PATH_SCHEMA;
import static com.datastax.stargate.sdk.core.ApiSupport.getHttpClient;
import static com.datastax.stargate.sdk.core.ApiSupport.getObjectMapper;
import static com.datastax.stargate.sdk.core.ApiSupport.handleError;
import static com.datastax.stargate.sdk.core.ApiSupport.startRequest;
import static com.datastax.stargate.sdk.doc.ApiDocumentClient.PATH_SCHEMA_NAMESPACES;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.doc.domain.CollectionDefinition;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;

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
     * 
     * @param docClient ApiDocumentClient
     * @param namespace String
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
     * 
     * @return Namespace
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
     * 
     * @return boolean
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Create a namespace.
     * 
     * @param datacenters DataCenter
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
     * 
     * @param replicas int
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
     * GET /v2/namespaces/{namespace-id}/collections
     * 
     * @return CollectionDefinition
     */
    public Stream<CollectionDefinition> collections() {
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
                    .collect(Collectors.toSet()).stream();
        } catch (Exception e) {
            throw new RuntimeException("Cannot marshall collection list", e);
        }
    }
    
    /**
     * List collections in namespace.
     * GET /v2/namespaces/{namespace-id}/collections
     * 
     * @return String
     */
    public Stream<String> collectionNames() {
        return collections().map(CollectionDefinition::getName);
    }
    
    /**
     * marshallApiResponseNamespace
     * 
     * @param body String
     * @return ApiResponse
     * @throws Exception Exception
     */
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
     * 
     * @param collectionName String
     * @return CollectionClient
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
