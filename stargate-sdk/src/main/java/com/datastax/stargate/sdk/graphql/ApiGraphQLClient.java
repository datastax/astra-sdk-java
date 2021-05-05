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

package com.datastax.stargate.sdk.graphql;

import static com.datastax.stargate.sdk.utils.Assert.hasLength;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.core.ApiSupport;

/**
 * Superclass to work with graphQL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGraphQLClient extends ApiSupport {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGraphQLClient.class);
    
    /** This the endPoint to invoke to work with different API(s). */
    protected final String endPointApiGraphQL;
    
    /**
     * Constructor for ASTRA.
     */
    public ApiGraphQLClient(String username, String password, String endPointAuthentication,  String appToken, String endPointApiGraphQL) {
        hasLength(endPointApiGraphQL, "endPointApiRest");
        hasLength(username, "username");
        hasLength(password, "password");
        this.username               = username;
        this.password               = password;
        this.endPointAuthentication = endPointAuthentication;
        this.endPointApiGraphQL     = endPointApiGraphQL;
        this.appToken               = appToken;
        LOGGER.info("+ Rest API: {}, ", endPointApiGraphQL);
    }
    

}
