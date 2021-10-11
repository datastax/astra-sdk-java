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

import static com.datastax.stargate.sdk.utils.JsonUtils.*;
import java.net.HttpURLConnection;
import java.util.Optional;

import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.rest.domain.ColumnDefinition;
import com.datastax.stargate.sdk.rest.exception.ColumnsNotFoundException;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Working with a Column.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ColumnsClient {

    /** Namespace. */
    private final TableClient tableClient;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Unique document identifer. */
    private final String columnId;
    
    /** Marshall Column Definition. */
    private final TypeReference<ApiResponse<ColumnDefinition>> TYPE_COLUMN_DEF = 
            new TypeReference<ApiResponse<ColumnDefinition>>(){};
    
    /**
     * Constructor focusing on a single Column
     *
     * @param tableClient
     *       table resource client
     * @param columnId
     *      current column identifier
     */
    public ColumnsClient(StargateHttpClient stargateClient, TableClient tableClient, String columnId) {
        this.tableClient    = tableClient;
        this.columnId       = columnId;
        this.http           = HttpApisClient.getInstance();
        Assert.hasLength(columnId, "columnId");
    }
    
    
    /**
     * Retrieve a column.
     *
     * @return ColumnDefinition
     */
    public Optional<ColumnDefinition> find() {
        ApiResponseHttp res = http.GET(getEndPointSchemaCurrentColumn());
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
     * @param cd ColumnDefinition
     */
    public void create(ColumnDefinition cd) {
        Assert.notNull(cd, "ColumnDefinition");
        http.POST(tableClient.getEndPointSchemaColumns(), marshall(cd));
    }
    
    /**
     * Delete a column.
     */
    public void delete() {
        ApiResponseHttp res = http.DELETE(getEndPointSchemaCurrentColumn());
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            throw new ColumnsNotFoundException(columnId);
        }
    }
    
    /**
     * Update a column.
     *
     * @param newName String
     */
    public void rename(String newName) {
        // Parameter validation
        Assert.hasLength(newName, "New columns name");
        Assert.isTrue(!newName.equalsIgnoreCase(columnId), 
                "You should not rename with same name");
        // Build body
        String body = marshall(new ColumnDefinition(newName, find().get().getTypeDefinition()));
        // Invoke HTTP Endpoint
        http.PUT(getEndPointSchemaCurrentColumn(), body);
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointSchemaCurrentColumn() {
        return tableClient.getEndPointSchemaColumns() + "/" + columnId;
    }
    
}
