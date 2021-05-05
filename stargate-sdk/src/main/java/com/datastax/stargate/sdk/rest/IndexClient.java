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

import com.datastax.stargate.sdk.rest.domain.CreateIndex;
import com.datastax.stargate.sdk.rest.domain.IndexDefinition;
import com.datastax.stargate.sdk.rest.exception.IndexNotFoundException;
import com.datastax.stargate.sdk.utils.Assert;

/**
 * Working with indices in the classes.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class IndexClient {

    /** Astra Client. */
    private final ApiRestClient restClient;
    
    /** Namespace. */
    private final KeyspaceClient keyspaceClient;
    
    /** Namespace. */
    private final TableClient tableClient;
    
    /** Unique document identifer. */
    private final String indexName;
    
    /**
     * Constructor focusing on a single Column
     *
     * @param restClient
     *      working with rest
     * @param keyspaceClient
     *      keyspace resource client
     * @param tableClient
     *       table resource client
     * @param indexName
     *      current index identifier
     */
    public IndexClient(ApiRestClient restClient, KeyspaceClient keyspaceClient, TableClient tableClient, String indexName) {
        this.restClient     = restClient;
        this.keyspaceClient = keyspaceClient;
        this.tableClient    = tableClient;
        this.indexName       = indexName;
    }
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointSchemaCurrentIndex() {
        return keyspaceClient.getEndPointSchemaKeyspace() 
                + "/tables/"  + tableClient.getTableName()
                + "/indexes/" + indexName;
    }
    
    /**
     * Get metadata of the collection. There is no dedicated resources we
     * use the list and filter with what we need.
     *
     * @return
     *      metadata of the collection if its exist or empty
     */
    public Optional<IndexDefinition> find() {
        return tableClient.indexes()
                .filter(i -> indexName.equalsIgnoreCase(i.getIndex_name()))
                .findFirst();
    }
    
    /**
     * Check if the column exist on the 
     * 
     * @return boolean
     */
    public boolean exist() {
        return tableClient.indexesNames().anyMatch(indexName::equals);
    }
    
    /**
     * Add an index.
     *
     * @param ci
     *      new index to create
     */
    public void create(CreateIndex ci) {
        Assert.notNull(ci, "CreateIndex");
        ci.setName(indexName);
        HttpResponse<String> response;
        try {
           String reqBody = getObjectMapper().writeValueAsString(ci);
           System.out.println(reqBody);
           response = getHttpClient().send(startRequest(
                           keyspaceClient.getEndPointSchemaKeyspace() 
                           + "/tables/"  + tableClient.getTableName()
                           + "/indexes", restClient.getToken())
                   .POST(BodyPublishers.ofString(reqBody)).build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new index:", e);
        }
        handleError(response);
    }
    
    /**
     * Delete an index.
     */
    public void delete() {
        HttpResponse<String> response;
        try {
            // Invoke
            response = getHttpClient().send(
                    startRequest(getEndPointSchemaCurrentIndex(), restClient.getToken())
                    .DELETE().build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot retrieve table list", e);
        }
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            throw new IndexNotFoundException(indexName);
        }
        handleError(response);
    }
}
