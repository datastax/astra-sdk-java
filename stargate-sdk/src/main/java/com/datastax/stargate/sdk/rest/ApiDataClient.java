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

import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.fasterxml.jackson.core.type.TypeReference;

import static com.datastax.stargate.sdk.utils.AnsiUtils.*;

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
    public static final String PATH_HEALTH     = "/health";
    public static final String PATH_V2         = "/v2";
    
    /** Marshalling type. */
    private static final TypeReference<ApiResponse<List<Keyspace>>> RESPONSE_LIST_KEYSPACE = 
            new TypeReference<ApiResponse<List<Keyspace>>>(){};
            
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointApiRest;
    
    /**
     * Initialized document API with an URL and a token.
     * 
     * @param endpoint
     *      http endpoint
     * @param token
     *      authentication token
     */
    public ApiDataClient(String endpoint, String token) {
        Assert.hasLength(endpoint, "endpoint");
        Assert.hasLength(token, "token");
        this.endPointApiRest =  endpoint;
        this.http = HttpApisClient.getInstance();
        http.setToken(token);
        LOGGER.info("+ API Data        :[" + cyan("{}") + "]", endPointApiRest);
    }
    /**
     * Initialized document API with an URL and a token.
     * 
     * @param endpoint
     *      http endpoint
     * @param tokenProvider
     *      provide a token
     */
    public ApiDataClient(String endpoint, ApiTokenProvider tokenProvider) {
        Assert.hasLength(endpoint, "endpoint");
        Assert.notNull(tokenProvider, "tokenProvider");
        this.endPointApiRest =  endpoint;
        this.http = HttpApisClient.getInstance();
        http.setTokenProvider(tokenProvider);
        LOGGER.info("+ API(s) Data     :[" + cyan("{}") + "]", endPointApiRest);
    }
    
    /**
     * Invoke heath endpoint.
     *
     * @return
     *      is the service is up.
     */
    public boolean isAlive() {
        ApiResponseHttp res = http.GET(getEndPointHealth());
        return HttpURLConnection.HTTP_OK == res.getCode();
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getKeyspaces
     * 
     * @return Keyspace
     */
    public Stream<Keyspace> keyspaces() {
        ApiResponseHttp res = http.GET(getEndpointSchemaKeyspaces());
        return unmarshallType(res.getBody(), RESPONSE_LIST_KEYSPACE).getData().stream();
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
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Getter accessor for attribute 'endPointApiRest'.
     *
     * @return current value of 'endPointApiRest'
     */
    public String getEndPointApiRest() {
        return endPointApiRest;
    }
    
    /**
     * Getter accessor for attribute 'endPointApiRest'.
     *
     * @return current value of 'endPointApiRest'
     */
    public String getEndPointHealth() {
        return endPointApiRest + PATH_HEALTH;
    }
    
    /**
     * Endpoint to access schema for all keyspaces.
     * 
     * @return
     *      url as String
     */
    public String getEndpointSchemaKeyspaces() {
        return getEndPointApiRest() + PATH_V2 + PATH_SCHEMA + PATH_KEYSPACES;
    }
    
    /**
     * Endpoint to access schema for one keyspace.
     * 
     * @return
     *      url as String
     */
    public String getEndPointKeyspaces() {
        return getEndPointApiRest() + PATH_V2 + PATH_KEYSPACES;
    }
    
}
