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

import com.datastax.stargate.sdk.ServiceDatacenter;
import com.datastax.stargate.sdk.ServiceDeployment;
import com.datastax.stargate.sdk.api.ApiResponse;
import com.datastax.stargate.sdk.api.ApiTokenProvider;
import com.datastax.stargate.sdk.http.ServiceHttp;
import com.datastax.stargate.sdk.http.LoadBalancedHttpClient;
import com.datastax.stargate.sdk.http.auth.ApiTokenProviderHttpAuth;
import com.datastax.stargate.sdk.http.domain.ApiResponseHttp;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.datastax.stargate.sdk.utils.AnsiUtils.green;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

/**
 * Working with REST API and part of schemas with tables and keyspaces;
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDataClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDataClient.class);
    
    /** URL part. */
    public static final String PATH_KEYSPACES  = "/keyspaces";
    
    /** URL part. */
    public static final String PATH_SCHEMA     = "/schemas";
    
    /** URL part. */
    public static final String PATH_V2         = "/v2";

    /** default endpoint. */
    public static final String DEFAULT_ENDPOINT = "http://localhost:8082";

    /** default service id. */
    public static final String DEFAULT_SERVICE_ID = "sgv2-rest";

    /** default datacenter id. */
    public static final String DEFAULT_DATACENTER = "dc1";

    /** default endpoint. */
    public static final String PATH_HEALTH_CHECK = "/stargate/health";

    /** Get Topology of the nodes. */
    protected final LoadBalancedHttpClient stargateHttpClient;

    /**
     * Default Constructor
     */
    public ApiDataClient() {
        this(DEFAULT_ENDPOINT);
    }

    /**
     * Single instance of Stargate, could be used for tests.
     *
     * @param endpoint
     *      service endpoint
     */
    public ApiDataClient(String endpoint) {
        Assert.hasLength(endpoint, "stargate endpoint");
        // Single instance running
        ServiceHttp rest = new ServiceHttp(DEFAULT_SERVICE_ID, endpoint, endpoint + PATH_HEALTH_CHECK);
        // Api provider
        ApiTokenProvider tokenProvider =
                new ApiTokenProviderHttpAuth();
        // DC with default auth and single node
        ServiceDatacenter sDc =
                new ServiceDatacenter(DEFAULT_DATACENTER, tokenProvider, Arrays.asList(rest));
        // Deployment with a single dc
        ServiceDeployment deploy =
                new ServiceDeployment<ServiceHttp>().addDatacenter(sDc);
        this.stargateHttpClient  = new LoadBalancedHttpClient(deploy);
    }

    /**
     * Initialized document API with an URL and a token.
     *
     * @param serviceDeployment
     *      http client topology aware
     */
    public ApiDataClient(ServiceDeployment<ServiceHttp> serviceDeployment) {
        Assert.notNull(serviceDeployment, "servide deployment topology");
        this.stargateHttpClient = new LoadBalancedHttpClient(serviceDeployment);
        LOGGER.info("+ API Data     :[" + green("{}") + "]", "ENABLED");
    }
    
    /**
     * Return list of keyspaces available.
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
     * @return stream of keyspace
     */
    public Stream<String> keyspaceNames() {
        return keyspaces().map(Keyspace::getName);
    }

    /**
     * Gets stargateHttpClient
     *
     * @return value of stargateHttpClient
     */
    public LoadBalancedHttpClient getStargateHttpClient() {
        return stargateHttpClient;
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
    public Function<ServiceHttp, String> keyspacesResource =
            (node) ->  node.getEndpoint() + PATH_V2 + PATH_KEYSPACES;
            
    /**
     * /v2/schemas/keyspaces
     */
    public Function<ServiceHttp, String> keyspacesSchemaResource =
            (node) -> node.getEndpoint() + PATH_V2 + PATH_SCHEMA + PATH_KEYSPACES;
     
}
