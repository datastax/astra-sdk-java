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

import java.net.HttpURLConnection;
import java.util.Optional;

import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.rest.domain.CreateIndex;
import com.datastax.stargate.sdk.rest.domain.IndexDefinition;
import com.datastax.stargate.sdk.rest.exception.IndexNotFoundException;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Working with indices in the classes.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class IndexClient {
    
    /** Namespace. */
    private final TableClient tableClient;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Unique document identifer. */
    private final String indexName;
    
    /**
     * Constructor focusing on a single Column
     *
     * @param tableClient
     *       table resource client
     * @param indexName
     *      current index identifier
     */
    public IndexClient(StargateHttpClient stargateClient, TableClient tableClient, String indexName) {
        this.tableClient    = tableClient;
        this.indexName      = indexName;
        this.http           = HttpApisClient.getInstance();
        Assert.hasLength(indexName, "indexName");
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
        http.POST(tableClient.getEndPointSchemaIndexes(), marshall(ci));
    }
    
    /**
     * Delete an index.
     */
    public void delete() {
        ApiResponseHttp res = http.DELETE(getEndPointSchemaCurrentIndex());
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            throw new IndexNotFoundException(indexName);
        }
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Syntax sugar
     * 
     * @return String
     */
    public String getEndPointSchemaCurrentIndex() {
        return tableClient.getEndPointSchemaIndexes() + "/" + indexName;
    }
}
