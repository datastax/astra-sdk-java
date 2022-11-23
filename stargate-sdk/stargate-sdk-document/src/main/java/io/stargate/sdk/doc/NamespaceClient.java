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

package io.stargate.sdk.doc;

import com.fasterxml.jackson.core.type.TypeReference;
import io.stargate.sdk.api.ApiResponse;
import io.stargate.sdk.core.DataCenter;
import io.stargate.sdk.doc.domain.CollectionDefinition;
import io.stargate.sdk.doc.domain.Namespace;
import io.stargate.sdk.http.LoadBalancedHttpClient;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.http.domain.ApiResponseHttp;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.JsonUtils;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

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

    /** Get Topology of the nodes. */
    protected final LoadBalancedHttpClient stargateHttpClient;
    
    /** Astra Client. */
    private StargateDocumentApiClient apiDocument;
    
    /** Namespace. */
    private String namespace;
    
    /**
     * Full constructor.
     * 
     * @param stargateHttpClient stargateHttpClient
     * @param docClient ApiDocumentClient
     * @param namespace String
     */
    public NamespaceClient(LoadBalancedHttpClient stargateHttpClient, StargateDocumentApiClient docClient, String namespace) {
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
            return Optional.of(JsonUtils.unmarshallType(res.getBody(), RESPONSE_NAMESPACE).getData());
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
             JsonUtils.marshall(new Namespace(namespace, Arrays.asList(datacenters))));
    }
    
    /**
     * Create a namespace.
     * 
     * @param replicas int
     */
    public void createSimple(int replicas) {
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        stargateHttpClient.POST(apiDocument.namespacesSchemaResource,
             JsonUtils.marshall(new Namespace(namespace, replicas)));
    }
    
    
    /**
     * Delete a namespace.
     */
    public void delete() {
        stargateHttpClient.DELETE(namespaceSchemaResource);
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
        return JsonUtils.unmarshallType(res.getBody(), RESPONSE_COLLECTIONS).getData().stream();
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
    public Function<ServiceHttp, String> namespaceSchemaResource =
             (node) -> apiDocument.namespacesSchemaResource.apply(node) + "/" + namespace;
        
    /**
      * /v2/namespaces/{namespace}
      */
    public Function<ServiceHttp, String> namespaceResource =
             (node) -> apiDocument.namespacesResource.apply(node) + "/" + namespace;
             
     /**
       * /v2/namespaces/{namespace}/collections
       */
     public Function<ServiceHttp, String> collectionsResource =
                     (node) -> namespaceResource.apply(node) + PATH_COLLECTIONS; 
                     
      /**
       * /v2/namespaces/{namespace}/functions
       */
      public Function<ServiceHttp, String> functionsResource =
                     (node) -> namespaceSchemaResource.apply(node) + PATH_FUNCTIONS;                      
    
}
