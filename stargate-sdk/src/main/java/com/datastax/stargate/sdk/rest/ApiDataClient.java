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

import static com.datastax.stargate.sdk.utils.AnsiUtils.green;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Working with REST API and part of schemas with tables and keyspaces;
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDataClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDataClient.class);
    
    /** Schenma sub level. */
    public static final String PATH_KEYSPACES  = "/keyspaces";
    public static final String PATH_SCHEMA     = "/schemas";
    public static final String PATH_V2         = "/v2";
    
    /** Get Topology of the nodes. */
    protected final StargateHttpClient stargateHttpClient;
    
    /**
     * Initialized document API with an URL and a token.
     *
     * @param stargateHttoClient
     *      http client topology aware
     */
    public ApiDataClient(StargateHttpClient stargateHttoClient) {
        Assert.notNull(stargateHttoClient, "stargate client reference. ");
        this.stargateHttpClient = stargateHttoClient;
        LOGGER.info("+ API Data     :[" + green("{}") + "]", "ENABLED");
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/restv2.html#operation/getAllKeyspaces">Reference Documentation</a>
     * 
     * @return Keyspace
     */
    public Stream<Keyspace> keyspaces() {
        // Invoke Http with retries and failover
        ApiResponseHttp res = stargateHttpClient.GET(keyspacesSchemaResource);
        // Marshall String body as a list of Keyspaces
        ApiResponse<List<Keyspace>> res2 = unmarshallType(res.getBody(), 
                        new TypeReference<ApiResponse<List<Keyspace>>>(){});
        // Map to expected results
        return res2.getData().stream();
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @see Namespace
     * @return stream of keyspace
     */
    public Stream<String> keyspaceNames() {
        return keyspaces().map(Keyspace::getName);
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move to the Rest API
     * 
     * @param keyspace String
     * @return KeyspaceClient
     */
    public KeyspaceClient keyspace(String keyspace) {
        return new KeyspaceClient(this, keyspace);
    }
    
    // ---------------------------------
    // ----   Build Resources URLS  ----
    // ---------------------------------
 
    /**
     * /v2/keyspaces
     */
    public Function<StargateClientNode, String> keyspacesResource =
            (node) ->  node.getApiRestEndpoint() + PATH_V2 + PATH_KEYSPACES;
            
    /**
     * /v2/schemas/keyspaces
     */
    public Function<StargateClientNode, String> keyspacesSchemaResource = 
            (node) -> node.getApiRestEndpoint() + PATH_V2 + PATH_SCHEMA + PATH_KEYSPACES;
     
}
