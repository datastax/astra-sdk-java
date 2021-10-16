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
import java.util.function.Function;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
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
    
    /** Reference to http client. */
    private final StargateHttpClient stargateClient;
    
    /** Namespace. */
    private TableClient tableClient;
    
    /** Unique document identifer. */
    private String indexName;
    
    /**
     * Constructor focusing on a single Column
     *
     * @param stargateHttpClient 
     *        stargateHttpClient
     * @param tableClient
     *       table resource client
     * @param indexName
     *      current index identifier
     */
    public IndexClient(StargateHttpClient stargateHttpClient, TableClient tableClient, String indexName) {
        this.tableClient    = tableClient;
        this.stargateClient = stargateHttpClient;
        this.indexName      = indexName;
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
     * Create an index.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/createIndex">Reference Documentation</a>
     *
     * @param ci
     *      new index to create
     */
    public void create(CreateIndex ci) {
        Assert.notNull(ci, "CreateIndex");
        ci.setName(indexName);
        stargateClient.POST(tableClient.indexesSchemaResource, marshall(ci));
    }
    
    /**
     * Delete an index.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/deleteIndex">Reference Documentation</a>
     */
    public void delete() {
        ApiResponseHttp res = stargateClient.DELETE(indexSchemaResource);
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            throw new IndexNotFoundException(indexName);
        }
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
  
    /**
     * /v2/schemas/keyspaces/{keyspace}/tables/{tableName}/indexes/{indexName}
     */
    public Function<StargateClientNode, String> indexSchemaResource = 
            (node) -> tableClient.indexesSchemaResource.apply(node)  + "/" + indexName;
    
}
