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

package com.datastax.stargate.sdk.rest;


import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.rest.domain.TableDefinition;
import com.datastax.stargate.sdk.rest.domain.TypeDefinition;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for API resource /v2/namespaces.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyspaceClient {
    
    /** Constants. */
    public static final String PATH_KEYSPACES   = "/v2/keyspaces";
    public static final String PATH_TABLES      = "/tables";
    public static final String PATH_TYPES       = "/types";
    
    /** Marshalling {@link TypeReference}. */
    private static final TypeReference<ApiResponse<Keyspace>> RESPONSE_KEYSPACE = 
            new TypeReference<ApiResponse<Keyspace>>(){};
    
    /** Marshalling {@link TypeReference}. */
    private static final TypeReference<ApiResponse<List<TableDefinition>>> RESPONSE_TABLE_DEFINITIONS = 
            new TypeReference<ApiResponse<List<TableDefinition>>>(){};
            
    /** Marshalling {@link TypeReference}. */
    private static final TypeReference<ApiResponse<List<TypeDefinition>>> RESPONSE_TYPE_DEFINITIONS = 
            new TypeReference<ApiResponse<List<TypeDefinition>>>(){};
            
    
    /** Reference for the resources. */
    private ApiDataClient apiData = null;
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /** Namespace. */
    private String keyspace;
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, TableClient> tablesClient = new HashMap<>();
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, TypeClient> typesClient = new HashMap<>();
    
    /**
     * Full constructor.
     * 
     * @param restclient ApiRestClient
     * @param keyspace String
     */
    public KeyspaceClient(ApiDataClient apiData, String keyspace) {
        this.apiData            = apiData;
        this.keyspace           = keyspace;
        this.stargateHttpClient = apiData.stargateHttpClient;
        Assert.hasLength(keyspace, "keyspace");
    }
    
    // ---------------------------------
    // ----        CRUD             ----
    // ---------------------------------
     
    /**
     * Find a namespace and its metadata based on its id
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getKeyspace
     * 
     * @return Keyspace
     */
    public Optional<Keyspace> find() {
        ApiResponseHttp res = stargateHttpClient.GET(keyspaceSchemaResource);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(unmarshallType(res.getBody(), RESPONSE_KEYSPACE).getData());
        }
    }
    
    /**
     * Check it the keyspace exist.
     * 
     * @return boolean
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Create a keyspace providing the replications per Datacenter.
     * - IF NOT EXIST is always applied.
     * 
     * @param datacenters DataCenter
     */
    public void create(DataCenter... datacenters) {
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(datacenters.length > 0, "DataCenters are required");
        stargateHttpClient.POST(
                apiData.keyspacesSchemaResource, 
                marshall(new Keyspace(keyspace, Arrays.asList(datacenters))));
    }
    
    /**
     * Create a namespace.
     * 
     * @param replicas int
     */
    public void createSimple(int replicas) {
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        stargateHttpClient.POST(
                apiData.keyspacesSchemaResource, 
                marshall(new Keyspace(keyspace, replicas)));
    }
    
    /**
     * Delete a keyspace.
     * https://stargate.io/docs/stargate/1.0/developers-guide/api_ref/openapi_rest_ref.html#_deletekeyspace
     */
    public void delete() {
        stargateHttpClient.DELETE(keyspaceSchemaResource);
    }
    
    /**
     * List tablenames in keyspace.
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getTables
     * 
     * @return TableDefinition
     */
    public Stream<TableDefinition> tables() {
        return unmarshallType(
                stargateHttpClient.GET(tablesSchemaResource).getBody(), 
                RESPONSE_TABLE_DEFINITIONS)
                .getData().stream();
    }
    
    /**
     * List types in the keyspace.
     * 
     * @return
     *      list of types.
     */
    public Stream<TypeDefinition> types() {
        return unmarshallType(
                stargateHttpClient.GET(typesSchemaResource).getBody(), 
                RESPONSE_TYPE_DEFINITIONS)
                .getData().stream();
    }
        
    /**
     * Map to list only table names.
     * 
     * @return Stream
     */
    public Stream<String> tableNames() {
        return tables().map(TableDefinition::getName);
    }
    
    /**
     * Map to list only types names.
     * 
     * @return Stream
     */
    public Stream<String> typeNames() {
        return types().map(TypeDefinition::getName);
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move to the Table client
     * 
     * @param tableName String
     * @return TableClient
     */
    public TableClient table(String tableName) {
        Assert.hasLength(tableName, "tableName");
        if (!tablesClient.containsKey(tableName)) {
            tablesClient.put(tableName, new TableClient(stargateHttpClient, this, tableName));
        }
        return tablesClient.get(tableName);
    }
    
    /**
     * Move to the Type client
     * 
     * @param typeName String
     * @return TypeClient
     */
    public TypeClient type(String typeName) {
        Assert.hasLength(typeName, "typeName");
        if (!typesClient.containsKey(typeName)) {
            typesClient.put(typeName, new TypeClient(stargateHttpClient, this, typeName));
        }
        return typesClient.get(typeName);
    }
    
    /**
     * Syntax sugar more easier to understand in a fluent API.
     * 
     * @param tableName tableName
     * @param ct CreateTable
     */
    public void createTable(String tableName, CreateTable ct) {
        table(tableName).create(ct);
    }
    
    // ---------------------------------
    // ----       Resources         ----
    // ---------------------------------
    
    /**
     * Getter accessor for attribute 'keyspace'.
     *
     * @return current value of 'keyspace'
     */
    public String getKeyspace() {
        return keyspace;
    }

    /**
     * /v2/keyspaces/{keyspace}
     */
    public Function<StargateClientNode, String> keyspaceResource = 
             (node) -> apiData.keyspacesResource.apply(node) + "/" + keyspace;
    
    /**
     * /v2/schemas/keyspaces/{keyspace}
     */
    public Function<StargateClientNode, String> keyspaceSchemaResource = 
            (node) -> apiData.keyspacesSchemaResource.apply(node) + "/" + keyspace;
   
    /**
     * /v2/keyspaces/{keyspace}/tables
     */
    public Function<StargateClientNode, String> tablesResource = 
            (node) -> keyspaceResource.apply(node) + PATH_TABLES;

    /**
     * /v2/schemas/keyspaces/{keyspace}/tables
     */
    public Function<StargateClientNode, String> tablesSchemaResource = 
            (node) -> keyspaceSchemaResource.apply(node) + PATH_TABLES;
    
    /**
     * /v2/keyspaces/{keyspace}/types
     */
    public Function<StargateClientNode, String> typesResource = 
            (node) -> keyspaceResource.apply(node) + PATH_TYPES;
                       
    /** 
     * /v2/schemas/keyspaces/{keyspace}/types 
     */
    public Function<StargateClientNode, String> typesSchemaResource = 
            (node) -> keyspaceSchemaResource.apply(node) + PATH_TYPES;
  
}
