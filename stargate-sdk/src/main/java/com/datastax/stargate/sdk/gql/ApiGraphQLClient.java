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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Superclass to work with graphQL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGraphQLClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGraphQLClient.class);
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointApiGraphQL;
    
    /**
     * Initialized document API with an URL and a token.
     * 
     * @param endpoint
     *      http endpoint
     * @param token
     *      authentication token
     */
    public ApiGraphQLClient(String endpoint, String token) {
        Assert.hasLength(endpoint, "endpoint");
        Assert.hasLength(token, "token");
        this.endPointApiGraphQL =  endpoint;
        this.http = HttpApisClient.getInstance();
        http.setToken(token);
        LOGGER.info("+ GraphQL API:  {}, ", endPointApiGraphQL);
    }
    
    /**
     * Invoked when working with StandAlone Stargate.
     * @param username
     * @param password
     * @param endPointAuthentication
     * @param endPointApiDocument
     */
    public ApiGraphQLClient(String endpoint, ApiTokenProvider tokenProvider) {
        Assert.hasLength(endpoint, "endpoint");
        Assert.notNull(tokenProvider, "tokenProvider");
        this.endPointApiGraphQL =  endpoint;
        this.http = HttpApisClient.getInstance();
        http.setTokenProvider(tokenProvider);
        LOGGER.info("+ API(s) GraphQL [ENABLED] {}", endPointApiGraphQL);
    }
    
    /**
     * Build the schema endpoint.
     *
     * @return
     *      target endpoint
     */
    public String getEndpointSchema() {
        return endPointApiGraphQL + "-schema";
    }
    
    /**
     * Build URL for a keyspace.
     * 
     * @param keyspaceId
     *      keyspace identifier
     * @return
     *      target endpoint
     */
    public String getEndpointKeyspace(String keyspaceId) {
        return endPointApiGraphQL + "/" + keyspaceId;
    }
    

}
