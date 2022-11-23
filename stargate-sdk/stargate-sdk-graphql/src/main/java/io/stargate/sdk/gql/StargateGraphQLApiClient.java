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

package io.stargate.sdk.gql;

import io.stargate.sdk.ServiceDatacenter;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.TokenProvider;
import io.stargate.sdk.http.LoadBalancedHttpClient;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.http.auth.TokenProviderHttpAuth;
import io.stargate.sdk.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

import static io.stargate.sdk.utils.AnsiUtils.green;
/**
 * Superclass to work with graphQL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateGraphQLApiClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateGraphQLApiClient.class);

    /** default endpoint. */
    private static final String DEFAULT_ENDPOINT = "http://localhost:8080";

    /** default service id. */
    private static final String DEFAULT_SERVICE_ID = "sgv2-graphql";

    /** default datacenter id. */
    private static final String DEFAULT_DATACENTER = "dc1";

    /** default endpoint. */
    private static final String PATH_HEALTH_CHECK = "/stargate/health";

    /** Get Topology of the nodes. */
    protected final LoadBalancedHttpClient stargateHttpClient;

    /**
     * Default Constructor
     */
    public StargateGraphQLApiClient() {
        this(DEFAULT_ENDPOINT);
    }

    /**
     * Single instance of Stargate, could be used for tests.
     *
     * @param endpoint
     *      service endpoint
     */
    public StargateGraphQLApiClient(String endpoint) {
        Assert.hasLength(endpoint, "stargate endpoint");
        ServiceHttp rest = new ServiceHttp(DEFAULT_SERVICE_ID, endpoint, endpoint + PATH_HEALTH_CHECK);
        TokenProvider tokenProvider = new TokenProviderHttpAuth();
        ServiceDatacenter<ServiceHttp> sDc =
                new ServiceDatacenter<>(DEFAULT_DATACENTER, tokenProvider, Collections.singletonList(rest));
        ServiceDeployment<ServiceHttp> deploy = new ServiceDeployment<ServiceHttp>().addDatacenter(sDc);
        this.stargateHttpClient  = new LoadBalancedHttpClient(deploy);
    }

    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     */
    public StargateGraphQLApiClient(LoadBalancedHttpClient stargateClient) {
        Assert.notNull(stargateClient, "stargate client reference. ");
        this.stargateHttpClient =  stargateClient;
        LOGGER.info("+ API GraphQL  :[" + green("{}") + "]", "ENABLED");
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Access /graphql-schema endpoint.
     * 
     * @return
     *      working with DDL and graphQL
     */
    public GraphQLKeyspaceDDLClient keyspaceDDL() {
        return new GraphQLKeyspaceDDLClient(stargateHttpClient);
    }
    
    /**
     * Access /graphql/{keyspace} endpoint.
     *
     * @param keyspace
     *      target keyspace to work with
     * @return
     *      instance of CQLFirst
     */
    public GraphQLKeyspaceDMLClient keyspaceDML(String keyspace) {
        return new GraphQLKeyspaceDMLClient(stargateHttpClient, keyspace);
    }
    
    /**
     * Access /graphql-admin to deploy Schema.
     * 
     * @return
     *      working with DDL and graphQL
     */
    public GraphQLKeyspaceDDLClient graphQLFirst() {
        return new GraphQLKeyspaceDDLClient(stargateHttpClient);
    }

    

}
