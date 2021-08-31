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
import java.util.stream.Stream;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.rest.domain.TableDefinition;
import com.datastax.stargate.sdk.rest.domain.TypeDefinition;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
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
            
    /** Astra Client. */
    private final ApiDataClient restclient;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Namespace. */
    private final String keyspace;
    
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
    public KeyspaceClient(ApiDataClient restclient, String keyspace) {
        this.restclient    = restclient;
        this.keyspace      = keyspace;
        this.http          = HttpApisClient.getInstance();
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
        ApiResponseHttp res = http.GET(getEndPointSchemaKeyspace());
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
        http.POST(restclient.getEndpointSchemaKeyspaces(), 
             marshall(new Keyspace(keyspace, Arrays.asList(datacenters))));
    }
    
    /**
     * Create a namespace.
     * 
     * @param replicas int
     */
    public void createSimple(int replicas) {
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        http.POST(restclient.getEndpointSchemaKeyspaces(),
             marshall(new Keyspace(keyspace, replicas)));
    }
    
    /**
     * Delete a keyspace.
     * https://stargate.io/docs/stargate/1.0/developers-guide/api_ref/openapi_rest_ref.html#_deletekeyspace
     */
    public void delete() {
        http.DELETE(getEndPointSchemaKeyspace());
    }
    
    /**
     * List tablenames in keyspace.
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getTables
     * 
     * @return TableDefinition
     */
    public Stream<TableDefinition> tables() {
        // Access API
        ApiResponseHttp res = http.GET(getEndPointSchemaTables());
        // Masharl returned objects
        return unmarshallType(res.getBody(), RESPONSE_TABLE_DEFINITIONS).getData().stream();
    }
    
    /**
     * List types in the keyspace.
     * 
     * @return
     *      list of types.
     */
    public Stream<TypeDefinition> types() {
        // Access API
        ApiResponseHttp res = http.GET(getEndPointSchemaTypes());
        // Marshall returned objects
        return unmarshallType(res.getBody(), RESPONSE_TYPE_DEFINITIONS).getData().stream();
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
            tablesClient.put(tableName, new TableClient(this, tableName));
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
            typesClient.put(typeName, new TypeClient(this, typeName));
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
    // ----       Utilities         ----
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
     * Access to schema namespace.
     * 
     * @return
     *      schema namespace
     */
    public String getEndPointSchemaKeyspace() {
        return restclient.getEndpointSchemaKeyspaces() + "/" + keyspace;
    }
    
    /**
     * Access to schema namespace.
     * 
     * @return
     *      schema namespace
     */
    public String getEndPointSchemaTables() {
        return getEndPointSchemaKeyspace() + PATH_TABLES;
    }
    
    /**
     * Access Keyspace data.
     *
     * @return
     *      the endpoint for keyspace
     */
    public String getEndPointKeyspace() {
        return restclient.getEndPointKeyspaces() + "/" + keyspace;
    }
    
    /**
     * Access to schema namespace.
     * 
     * @return
     *      schema namespace
     */
    public String getEndPointTables() {
        return getEndPointKeyspace() + PATH_TABLES;
    }
    
    /**
     * Access to schema namespace.
     * 
     * @return
     *      schema namespace
     */
    public String getEndPointTypes() {
        return getEndPointKeyspace() + PATH_TYPES;
    }
    
    /**
     * Access to schema namespace.
     * 
     * @return
     *      schema namespace
     */
    public String getEndPointSchemaTypes() {
        return getEndPointSchemaKeyspace() + PATH_TYPES;
    }
}
