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

package io.stargate.sdk.doc;


import com.fasterxml.jackson.core.type.TypeReference;
import io.stargate.sdk.ServiceDatacenter;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.ApiResponse;
import io.stargate.sdk.api.TokenProvider;
import io.stargate.sdk.doc.domain.Namespace;
import io.stargate.sdk.http.LoadBalancedHttpClient;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.http.auth.TokenProviderHttpAuth;
import io.stargate.sdk.utils.AnsiUtils;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Client for the Astra/Stargate document (collections) API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateDocumentApiClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateDocumentApiClient.class);
    
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
    public StargateDocumentApiClient() {
        this(DEFAULT_ENDPOINT);
    }

    /**
     * Single instance of Stargate, could be used for tests.
     *
     * @param endpoint
     *      service endpoint
     */
    public StargateDocumentApiClient(String endpoint) {
        Assert.hasLength(endpoint, "stargate endpoint");
        ServiceHttp rest = new ServiceHttp(DEFAULT_SERVICE_ID, endpoint, endpoint + PATH_HEALTH_CHECK);
        TokenProvider tokenProvider = new TokenProviderHttpAuth();
        ServiceDatacenter<ServiceHttp> sDc = new ServiceDatacenter<>(DEFAULT_DATACENTER, tokenProvider, Collections.singletonList(rest));
        ServiceDeployment<ServiceHttp> deploy = new ServiceDeployment<ServiceHttp>().addDatacenter(sDc);
        this.stargateHttpClient  = new LoadBalancedHttpClient(deploy);
    }

    /**
     * Initialized document API with a URL and a token.
     *
     * @param serviceDeployment
     *      http client topology aware
     */
    public StargateDocumentApiClient(ServiceDeployment<ServiceHttp> serviceDeployment) {
        Assert.notNull(serviceDeployment, "service deployment topology");
        this.stargateHttpClient = new LoadBalancedHttpClient(serviceDeployment);
        LOGGER.info("+ API Document :[" + AnsiUtils.green("{}") + "]", "ENABLED");
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
        return JsonUtils.unmarshallType(res, new TypeReference<ApiResponse<List<Namespace>>>(){})
              .getData()
              .stream();
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @return
     *  stream of the namespaces
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

    // ---------------------------------
    // ----   Build Resources URLS  ----
    // ---------------------------------

    /**
     * Gets stargateHttpClient
     *
     * @return value of stargateHttpClient
     */
    public LoadBalancedHttpClient getStargateHttpClient() {
        return stargateHttpClient;
    }

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
