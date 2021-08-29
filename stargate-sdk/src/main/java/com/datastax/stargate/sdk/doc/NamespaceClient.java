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

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.doc.domain.CollectionDefinition;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for Document API 'Namespace' resource /v2/namespaces
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class NamespaceClient {
    
    /** Constants. */
    public static final String PATH_NAMESPACES  = "/v2/namespaces";
    public static final String PATH_COLLECTIONS = "/collections";
    
    /** Marshalling {@link TypeReference}. */
    private static final TypeReference<ApiResponse<Namespace>> RESPONSE_NAMESPACE = 
            new TypeReference<ApiResponse<Namespace>>(){};
            
    private static final TypeReference<ApiResponse<List<CollectionDefinition>>> RESPONSE_COLLECTIONS = 
            new TypeReference<ApiResponse<List<CollectionDefinition>>>(){};
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
            
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
        this.http = HttpApisClient.getInstance();
        Assert.notNull(namespace, "namespace");
    }

    /**
     * Find a namespace and its metadata based on its id
     * 
     * @return Namespace
     */
    public Optional<Namespace> find() {
        ApiResponseHttp res = http.GET(getEndPointSchemaNamespace());
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(unmarshallType(res.getBody(), RESPONSE_NAMESPACE).getData());
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
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(datacenters.length > 0, "DataCenters are required");
        http.POST(docClient.getEndpointSchemaNamespaces(), 
             marshall(new Namespace(namespace, Arrays.asList(datacenters))));
    }
    
    /**
     * Create a namespace.
     * 
     * @param replicas int
     */
    public void createSimple(int replicas) {
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        http.POST(docClient.getEndpointSchemaNamespaces(),
             marshall(new Namespace(namespace, replicas)));
    }
    
    
    /**
     * Delete a namespace.
     */
    public void delete() {
        http.DELETE(getEndPointSchemaNamespace());
    }
   
    /**
     * List collections in namespace.
     * GET /v2/namespaces/{namespace-id}/collections
     * 
     * @return CollectionDefinition
     */
    public Stream<CollectionDefinition> collections() {
        // Access API
        ApiResponseHttp res = http.GET(getEndPointCollections());
        // Masharl returned objects
        return unmarshallType(res.getBody(), RESPONSE_COLLECTIONS).getData().stream();
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
    
    /**
     * Access to schema namespace.
     * 
     * @return
     *      schema namespace
     */
    public String getEndPointSchemaNamespace() {
        return docClient.getEndpointSchemaNamespaces() + "/" + namespace;
    }
    
    /**
     * Access to schema namespace.
     * 
     * @return
     *      schema namespace
     */
    public String getEndPointCollections() {
        return docClient.getEndPointApiDocument() + PATH_NAMESPACES + "/" + namespace + PATH_COLLECTIONS;
    }
    
    
}
