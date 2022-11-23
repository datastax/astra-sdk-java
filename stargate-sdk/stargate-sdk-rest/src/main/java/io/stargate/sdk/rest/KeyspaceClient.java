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

package io.stargate.sdk.rest;


import io.stargate.sdk.api.ApiResponse;
import io.stargate.sdk.core.DataCenter;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.http.domain.ApiResponseHttp;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.rest.domain.Keyspace;
import io.stargate.sdk.rest.domain.TableDefinition;
import io.stargate.sdk.rest.domain.TypeDefinition;
import io.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.stargate.sdk.utils.JsonUtils.marshall;
import static io.stargate.sdk.utils.JsonUtils.unmarshallType;

/**
 * Client for API resource /v2/namespaces.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class KeyspaceClient {
    
    /** URL parts. */
    public static final String PATH_KEYSPACES   = "/v2/keyspaces";
    
    /** URL parts. */
    public static final String PATH_TABLES      = "/tables";
    
    /** URL parts. */
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
    private StargateRestApiClient apiData = null;

    /** Namespace. */
    private String keyspace;
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, TableClient> tablesClient = new HashMap<>();
    
    /** Hold a reference to client to keep singletons.*/
    private Map <String, TypeClient> typesClient = new HashMap<>();
    
    /**
     * Full constructor.
     * 
     * @param apiData apiData
     * @param keyspace String
     */
    public KeyspaceClient(StargateRestApiClient apiData, String keyspace) {
        this.apiData            = apiData;
        this.keyspace           = keyspace;
        Assert.hasLength(keyspace, "keyspace");
    }
    
    // ---------------------------------
    // ----        CRUD             ----
    // ---------------------------------
     
    /**
     * Find a namespace and its metadata based on its id.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getKeyspace">Reference Documentation</a>
     * 
     * @return Keyspace
     */
    public Optional<Keyspace> find() {
        ApiResponseHttp res = apiData.stargateHttpClient.GET(keyspaceSchemaResource);
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
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createKeyspace">Reference Documentation</a>
     * 
     * @param datacenters DataCenter
     */
    public void create(DataCenter... datacenters) {
        Assert.notNull(datacenters, "datacenters");
        Assert.isTrue(datacenters.length > 0, "DataCenters are required");
        System.out.println( marshall(new Keyspace(keyspace, Arrays.asList(datacenters))));
        apiData.stargateHttpClient.POST(
                apiData.keyspacesSchemaResource, 
                marshall(new Keyspace(keyspace, Arrays.asList(datacenters))));
    }
    
    /**
     * Create a keyspace.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createKeyspace">Reference Documentation</a>
     * 
     * @param replicas int
     */
    public void createSimple(int replicas) {
        Assert.isTrue(replicas>0, "Replica number should be bigger than 0");
        apiData.stargateHttpClient.POST(
                apiData.keyspacesSchemaResource, 
                marshall(new Keyspace(keyspace, replicas)));
    }
    
    /**
     * Delete a keyspace.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/deleteKeyspace">Reference Documentation</a>
     */
    public void delete() {
        apiData.stargateHttpClient.DELETE(keyspaceSchemaResource);
    }
    
    /**
     * List tablenames in keyspace.
     *
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getTables">Reference Documentation</a>
     * 
     * @return TableDefinition
     */
    public Stream<TableDefinition> tables() {
        return unmarshallType(
                apiData.stargateHttpClient.GET(tablesSchemaResource).getBody(),
                RESPONSE_TABLE_DEFINITIONS)
                .getData().stream();
    }
    
    /**
     * Sample collector as MAP of stream.
     * 
     * @return
     *      map
     */
    public Map<String, TableDefinition> tablesAsMap() {
        return tables().collect(
                Collectors.toMap(TableDefinition::getName, Function.identity()));
    }
    
    /**
     * List types in the keyspace.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getTypes">Reference Documentation</a>
     *
     * @return
     *      list of types.
     */
    public Stream<TypeDefinition> types() {
        return unmarshallType(
                apiData.stargateHttpClient.GET(typesSchemaResource).getBody(),
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
            tablesClient.put(tableName, new TableClient(apiData.stargateHttpClient, this, tableName));
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
            typesClient.put(typeName, new TypeClient(apiData.stargateHttpClient, this, typeName));
        }
        return typesClient.get(typeName);
    }
    
    /**
     * Syntax sugar more easier to understand in a fluent API.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createTable">Reference Documentation</a>
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
    public Function<ServiceHttp, String> keyspaceResource =
             (node) -> apiData.keyspacesResource.apply(node) + "/" + keyspace;
    
    /**
     * /v2/schemas/keyspaces/{keyspace}
     */
    public Function<ServiceHttp, String> keyspaceSchemaResource =
            (node) -> apiData.keyspacesSchemaResource.apply(node) + "/" + keyspace;
   
    /**
     * /v2/keyspaces/{keyspace}/tables
     */
    public Function<ServiceHttp, String> tablesResource =
            (node) -> keyspaceResource.apply(node) + PATH_TABLES;

    /**
     * /v2/schemas/keyspaces/{keyspace}/tables
     */
    public Function<ServiceHttp, String> tablesSchemaResource =
            (node) -> keyspaceSchemaResource.apply(node) + PATH_TABLES;
    
    /**
     * /v2/keyspaces/{keyspace}/types
     */
    public Function<ServiceHttp, String> typesResource =
            (node) -> keyspaceResource.apply(node) + PATH_TYPES;
                       
    /** 
     * /v2/schemas/keyspaces/{keyspace}/types 
     */
    public Function<ServiceHttp, String> typesSchemaResource =
            (node) -> keyspaceSchemaResource.apply(node) + PATH_TYPES;
  
}
