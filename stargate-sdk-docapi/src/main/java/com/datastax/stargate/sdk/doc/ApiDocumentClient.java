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

package com.datastax.stargate.sdk.doc;


import com.datastax.stargate.sdk.ServiceDatacenter;
import com.datastax.stargate.sdk.ServiceDeployment;
import com.datastax.stargate.sdk.api.ApiResponse;
import com.datastax.stargate.sdk.api.ApiTokenProvider;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.http.ServiceHttp;
import com.datastax.stargate.sdk.http.LoadBalancedHttpClient;
import com.datastax.stargate.sdk.http.auth.ApiTokenProviderHttpAuth;
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
 * Client for the Astra/Stargate document (collections) API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocumentClient.class);
    
    /** Schema sub level. */
    public static final String PATH_SCHEMA_NAMESPACES = "/namespaces";
    
    /** Schema sub level. */
    public static final String PATH_SCHEMA            = "/schemas";
    
    /** Schema sub level. */
    public static final String PATH_V2                = "/v2";

    /** default endpoint. */
    private static final String DEFAULT_ENDPOINT = "http://localhost:8180";

    /** default service id. */
    private static final String DEFAULT_SERVICE_ID = "sgv2-doc";

    /** default datacenter id. */
    private static final String DEFAULT_DATACENTER = "dc1";

    /** default endpoint. */
    private static final String PATH_HEALTH_CHECK = "/stargate/health";

    /** Get Topology of the nodes. */
    protected final LoadBalancedHttpClient stargateHttpClient;

    /**
     * Default Constructor
     */
    public ApiDocumentClient() {
        this(DEFAULT_ENDPOINT);
    }

    /**
     * Single instance of Stargate, could be used for tests.
     *
     * @param endpoint
     *      service endpoint
     */
    public ApiDocumentClient(String endpoint) {
        Assert.hasLength(endpoint, "stargate endpoint");
        ServiceHttp rest = new ServiceHttp(DEFAULT_SERVICE_ID, endpoint, endpoint + PATH_HEALTH_CHECK);
        ApiTokenProvider tokenProvider = new ApiTokenProviderHttpAuth();
        ServiceDatacenter sDc = new ServiceDatacenter(DEFAULT_DATACENTER, tokenProvider, Arrays.asList(rest));
        ServiceDeployment deploy = new ServiceDeployment<ServiceHttp>().addDatacenter(sDc);
        this.stargateHttpClient  = new LoadBalancedHttpClient(deploy);
        System.out.println(this.stargateHttpClient.getDeployment().toString());
    }

    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateHttpClient
     *      stargate http client
     */
    public ApiDocumentClient(LoadBalancedHttpClient stargateHttpClient) {
        Assert.notNull(stargateHttpClient, "stargate client reference. ");
        this.stargateHttpClient = stargateHttpClient;
        LOGGER.info("+ API Document :[" + green("{}") + "]", "ENABLED");
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/getAllNamespaces">Reference Documentation</a>
     * 
     * @return Stream
     */
    public Stream<Namespace> namespaces() {
        String res = stargateHttpClient.GET(this.namespacesSchemaResource).getBody();
        return unmarshallType(res, new TypeReference<ApiResponse<List<Namespace>>>(){})
              .getData()
              .stream();
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @return Stream
     *      stream of the namespaces
     */
    public Stream<String> namespaceNames() {
        return namespaces().map(Namespace::getName);
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move the document API (namespace client)
     * 
     * @param namespace String
     * @return NamespaceClient
     */
    public NamespaceClient namespace(String namespace) {
        return new NamespaceClient(stargateHttpClient, this, namespace);
    }

    /**
     * Current Dc for the client.
     *
     * @return
     *      current Dc
     */
    public String getCurrentDatacenter() {
        return stargateHttpClient
                .getDeployment()
                .getLocalDatacenterClient()
                .getDatacenterName();
    }

    // ---------------------------------
    // ----   Build Resources URLS  ----
    // ---------------------------------
    
    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<ServiceHttp, String> namespacesSchemaResource =
            (node) -> node.getEndpoint() + PATH_V2 + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES;

    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<ServiceHttp, String> namespacesResource =
            (node) -> node.getEndpoint() + PATH_V2 + PATH_SCHEMA_NAMESPACES;
}
