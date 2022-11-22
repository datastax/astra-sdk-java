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

package com.datastax.stargate.sdk.gql;

import com.datastax.stargate.sdk.ServiceDatacenter;
import com.datastax.stargate.sdk.ServiceDeployment;
import com.datastax.stargate.sdk.api.ApiTokenProvider;
import com.datastax.stargate.sdk.http.ServiceHttp;
import com.datastax.stargate.sdk.http.LoadBalancedHttpClient;
import com.datastax.stargate.sdk.http.auth.ApiTokenProviderHttpAuth;
import com.datastax.stargate.sdk.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.datastax.stargate.sdk.utils.AnsiUtils.green;
/**
 * Superclass to work with graphQL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGraphQLClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGraphQLClient.class);

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
    public ApiGraphQLClient() {
        this(DEFAULT_ENDPOINT);
    }

    /**
     * Single instance of Stargate, could be used for tests.
     *
     * @param endpoint
     *      service endpoint
     */
    public ApiGraphQLClient(String endpoint) {
        Assert.hasLength(endpoint, "stargate endpoint");
        // Single instance running
        ServiceHttp rest =
                new ServiceHttp(DEFAULT_SERVICE_ID, endpoint, endpoint + PATH_HEALTH_CHECK);
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
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     */
    public ApiGraphQLClient(LoadBalancedHttpClient stargateClient) {
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
    public CqlSchemaClient cqlSchema() {
        return new CqlSchemaClient(stargateHttpClient);
    }
    
    /**
     * Access /graphql/{keyspace} endpoint.
     *
     * @param keyspace
     *      target keyspace to work with
     * @return
     *      instance of CQLFirst
     */
    public CqlKeyspaceClient cqlKeyspace(String keyspace) {
        return new CqlKeyspaceClient(stargateHttpClient, keyspace);
    }
    
    /**
     * Access /graphql-admin to deploy Schema.
     * 
     * @return
     *      working with DDL and graphQL
     */
    public CqlSchemaClient graphQLFirst() {
        return new CqlSchemaClient(stargateHttpClient);
    }
    
    /**
     * Return list of keyspaces available.
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getKeyspaces
     * 
     * @return Keyspace
     */
    public List<Map<String, Object>> keyspaces() {
        stargateHttpClient.GET(null);
        //String query = "";
        // Invoke gql endpoints
        // Parse output
        return null;
    }
    
   
    

}
