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
import java.util.Optional;
import java.util.function.Function;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.rest.domain.ColumnDefinition;
import com.datastax.stargate.sdk.rest.exception.ColumnsNotFoundException;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Working with a Column.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ColumnsClient {
    
    /** Reference to http client. */
    private final StargateHttpClient stargateClient;
    
    /** Namespace. */
    private TableClient tableClient;
    
    /** Unique document identifer. */
    private String columnId;
    
    /** Marshall Column Definition. */
    private final TypeReference<ApiResponse<ColumnDefinition>> TYPE_COLUMN_DEF = 
            new TypeReference<ApiResponse<ColumnDefinition>>(){};
    
    /**
     * Constructor focusing on a single Column
     *
     * @param stargateHttpClient 
     *       stargateHttpClient
     * @param tableClient
     *       table resource client
     * @param columnId
     *      current column identifier
     */
    public ColumnsClient(StargateHttpClient stargateHttpClient, TableClient tableClient, String columnId) {
        this.tableClient    = tableClient;
        this.stargateClient = stargateHttpClient;
        this.columnId       = columnId;
        Assert.hasLength(columnId, "columnId");
    }
    
    
    /**
     * Retrieve a column.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getColumn">Reference Documentation</a>
     *
     * @return ColumnDefinition
     */
    public Optional<ColumnDefinition> find() {
        ApiResponseHttp res = stargateClient.GET(columnSchemaResource);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(
                    unmarshallType(res.getBody(), TYPE_COLUMN_DEF).getData());
        }
    }
    
    /**
     * Check if the column exist on the 
     * 
     * @return boolean
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Add a column.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createColumn">Reference Documentation</a>
     *
     * @param cd ColumnDefinition
     */
    public void create(ColumnDefinition cd) {
        Assert.notNull(cd, "ColumnDefinition");
        stargateClient.POST(tableClient.columnsSchemaResource, marshall(cd));
    }
    
    /**
     * Delete a column.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/deleteColumn">Reference Documentation</a>
     */
    public void delete() {
        ApiResponseHttp res = stargateClient.DELETE(columnSchemaResource);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            throw new ColumnsNotFoundException(columnId);
        }
    }
    
    /**
     * Update a column.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/replaceColumn">Reference Documentation</a>
     *
     * @param newName String
     */
    public void rename(String newName) {
        // Parameter validation
        Assert.hasLength(newName, "New columns name");
        Assert.isTrue(!newName.equalsIgnoreCase(columnId), "You should not rename with same name");
        // Build body
        String body = marshall(new ColumnDefinition(newName, find().get().getTypeDefinition()));
        // Invoke HTTP Endpoint
        stargateClient.PUT(columnSchemaResource, body);
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * /v2/schemas/keyspaces/{keyspace}/tables/{tableName}/columns/{columnName}
     */
    public Function<StargateClientNode, String> columnSchemaResource = 
            (node) -> tableClient.columnsSchemaResource.apply(node)  + "/" + columnId;
    
}
