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

import static com.datastax.stargate.sdk.core.ApiSupport.getHttpClient;
import static com.datastax.stargate.sdk.core.ApiSupport.getObjectMapper;
import static com.datastax.stargate.sdk.core.ApiSupport.handleError;
import static com.datastax.stargate.sdk.core.ApiSupport.startRequest;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import com.datastax.stargate.sdk.core.ApiResponse;
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

    /** Astra Client. */
    private final ApiRestClient restClient;
    
    /** Namespace. */
    private final KeyspaceClient keyspaceClient;
    
    /** Namespace. */
    private final TableClient tableClient;
    
    /** Unique document identifer. */
    private final String columnId;
    
    /**
     * Constructor focusing on a single Column
     *
     * @param restClient
     *      working with rest
     * @param keyspaceClient
     *      keyspace resource client
     * @param tableClient
     *       table resource client
     * @param columnId
     *      current column identifier
     */
    public ColumnsClient(ApiRestClient restClient, KeyspaceClient keyspaceClient, TableClient tableClient, String columnId) {
        this.restClient     = restClient;
        this.keyspaceClient = keyspaceClient;
        this.tableClient    = tableClient;
        this.columnId       = columnId;
    }
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointSchemaCurrentColumn() {
        return keyspaceClient.getEndPointSchemaKeyspace() 
                + "/tables/"  + tableClient.getTableName()
                + "/columns/" + columnId;
    }
    
    /**
     * Retrieve a column.
     *
     * @return ColumnDefinition
     */
    public Optional<ColumnDefinition> find() {
        HttpResponse<String> response;
        try {
            // Invoke
            response = getHttpClient().send(
                    startRequest(getEndPointSchemaCurrentColumn(), restClient.getToken())
                    .GET().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve table list", e);
        }
        
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            return Optional.empty();
        }
        
        handleError(response);
        
        if (HttpURLConnection.HTTP_OK == response.statusCode()) {
            try {
                TypeReference<ApiResponse<ColumnDefinition>> expectedType = new TypeReference<>(){};
                return Optional.ofNullable(getObjectMapper()
                        .readValue(response.body(), expectedType)
                        .getData());
            } catch (Exception e) {
                throw new RuntimeException("Cannot Marshall output in 'find keyspace()' body=" + response.body(), e);
            }
        }
        
        return Optional.empty();
        
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
        HttpResponse<String> response;
        try {
           String reqBody = getObjectMapper().writeValueAsString(cd);
           response = getHttpClient().send(startRequest(
                           keyspaceClient.getEndPointSchemaKeyspace() 
                           + "/tables/"  + tableClient.getTableName()
                           + "/columns", restClient.getToken())
                   .POST(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new column:", e);
        }
        handleError(response);
    }
    
    /**
     * Delete a column.
     */
    public void delete() {
        HttpResponse<String> response;
        try {
            // Invoke
            response = getHttpClient().send(
                    startRequest(getEndPointSchemaCurrentColumn(), restClient.getToken())
                    .DELETE().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve table list", e);
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            throw new ColumnsNotFoundException(columnId);
        }
        handleError(response);
    }
    
    /**
     * Update a column.
     *
     * @param newName String
     */
    public void rename(String newName) {
        Assert.hasLength(newName, "New columns name");
        Assert.isTrue(!newName.equalsIgnoreCase(columnId), 
                "You should not rename with same name");
        HttpResponse<String> response;
        try {
           String reqBody = getObjectMapper().writeValueAsString(
                   new ColumnDefinition(newName, find().get().getTypeDefinition()));
           response = getHttpClient()
                   .send(startRequest(
                          getEndPointSchemaCurrentColumn(), restClient.getToken())
                   .PUT(BodyPublishers.ofString(reqBody)).build(),
                        BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new column:", e);
        }
        handleError(response);
    }
}
