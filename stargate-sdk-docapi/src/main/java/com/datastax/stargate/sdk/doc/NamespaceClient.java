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

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.ServiceClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.http.auth.domain.ApiResponseHttp;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.doc.domain.CollectionDefinition;
import com.datastax.stargate.sdk.doc.domain.FunctionDefinition;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

/**
 * Client for Document API 'Namespace' resource /v2/namespaces
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class NamespaceClient {
    
    /** Constants. */
    public static final String PATH_COLLECTIONS = "/collections";
    
    /** Constants. */
    public static final String PATH_FUNCTIONS = "/functions";
    
    /** Marshalling {@link TypeReference}. */
    private static final TypeReference<ApiResponse<Namespace>> RESPONSE_NAMESPACE = 
            new TypeReference<ApiResponse<Namespace>>(){};
            
    /** Column Definitions.*/
    private static final TypeReference<ApiResponse<List<CollectionDefinition>>> RESPONSE_COLLECTIONS = 
            new TypeReference<ApiResponse<List<CollectionDefinition>>>(){};
            
    /** Functions Definitions.*/        
    private static final TypeReference<Map<String,List<FunctionDefinition>>> RESPONSE_FUNCTIONS = 
           new TypeReference<Map<String,List<FunctionDefinition>>>(){};
    
    /** Get Topology of the nodes. */
    protected final ServiceClient stargateHttpClient;
    
    /** Astra Client. */
    private ApiDocumentClient apiDocument;
    
    /** Namespace. */
    private String namespace;
    
    /**
     * Full constructor.
     * 
     * @param stargateHttpClient stargateHttpClient
     * @param docClient ApiDocumentClient
     * @param namespace String
     */
    public NamespaceClient(ServiceClient stargateHttpClient, ApiDocumentClient docClient, String namespace) {
        this.apiDocument  = docClient;
        this.namespace    = namespace;
        this.stargateHttpClient = stargateHttpClient;
        Assert.notNull(namespace, "namespace");
    }

    /**
     * Find a namespace and its metadata based on its id
     * 
     * @return Namespace
     */
    public Optional<Namespace> find() {
        ApiResponseHttp res = stargateHttpClient.GET(namespaceSchemaResource);
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
        stargateHttpClient.POST(apiDocument.namespacesSchemaResource,
             marshall(new Namespace(namespace, Arrays.asList(datacenters))));
    }
    
    /**
     * Create a namespace.
     * 
     * @param replicas int
     */
    public void createSimple(int replicas) {
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        stargateHttpClient.POST(apiDocument.namespacesSchemaResource,
             marshall(new Namespace(namespace, replicas)));
    }
    
    
    /**
     * Delete a namespace.
     */
    public void delete() {
        stargateHttpClient.DELETE(namespaceSchemaResource);
    }
    

    /**
     * List function in namespace.
     * GET /v2/namespaces/{namespace-id}/functions
     * 
     * @return functionsResource
     */
    public Stream<FunctionDefinition> functions() {
        // Access API
        ApiResponseHttp res = stargateHttpClient.GET(functionsResource);
        return unmarshallType(res.getBody(), RESPONSE_FUNCTIONS).get("functions").stream();
    }
   
    /**
     * List collections in namespace.
     * GET /v2/namespaces/{namespace-id}/collections
     * 
     * @return CollectionDefinition
     */
    public Stream<CollectionDefinition> collections() {
        // Access API
        ApiResponseHttp res = stargateHttpClient.GET(collectionsResource);
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
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move to the collection client
     * 
     * @param collectionName String
     * @return CollectionClient
     */
    public CollectionClient collection(String collectionName) {
        return new CollectionClient(stargateHttpClient, this, collectionName);
    }
    
    // ---------------------------------
    // ----       Resources         ----
    // ---------------------------------
    
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
     * /v2/schemas/namespaces/{namespace}
     */
    public Function<StargateClientNode, String> namespaceSchemaResource = 
             (node) -> apiDocument.namespacesSchemaResource.apply(node) + "/" + namespace;
        
    /**
      * /v2/namespaces/{namespace}
      */
    public Function<StargateClientNode, String> namespaceResource = 
             (node) -> apiDocument.namespacesResource.apply(node) + "/" + namespace;
             
     /**
       * /v2/namespaces/{namespace}/collections
       */
     public Function<StargateClientNode, String> collectionsResource = 
                     (node) -> namespaceResource.apply(node) + PATH_COLLECTIONS; 
                     
      /**
       * /v2/namespaces/{namespace}/functions
       */
      public Function<StargateClientNode, String> functionsResource = 
                     (node) -> namespaceSchemaResource.apply(node) + PATH_FUNCTIONS;                      
    
}
