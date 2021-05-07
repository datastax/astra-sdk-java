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

import static com.datastax.stargate.sdk.utils.Assert.hasLength;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiSupport;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Working with REST API and part of schemas with tables and keyspaces;
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiRestClient extends ApiSupport {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiRestClient.class);
    
    /** Schenma sub level. */
    public static final String PATH_SCHEMA_KEYSPACES  = "/keyspaces";
  
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointApiRest;
    
    /**
     * Constructor for ASTRA.
     * 
     * @param username String
     * @param password String
     * @param endPointAuthentication String
     * @param appToken String
     * @param endPointApiRest String
     */
    public ApiRestClient(String username, String password, String endPointAuthentication,  String appToken, String endPointApiRest) {
        hasLength(endPointApiRest, "endPointApiRest");
        hasLength(username, "username");
        hasLength(password, "password");
        this.username               = username;
        this.password               = password;
        this.endPointAuthentication = endPointAuthentication;
        this.endPointApiRest        = endPointApiRest;
        this.appToken               = appToken;
        LOGGER.info("+ Rest API: {}, ", endPointApiRest);
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getKeyspaces
     * 
     * @return Keyspace
     */
    public Stream<Keyspace> keyspaces() {
        
        HttpResponse<String> res;
        try {
           String      url = endPointApiRest + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES;
           HttpRequest req = startRequest(url, getToken()).GET().build();
           res             = httpClient.send(req, BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot list keyspaces", e);
        }
        
        // Http Call maybe successfull returning error code
        if (HttpURLConnection.HTTP_OK != res.statusCode()) {
            LOGGER.error("Error in 'keyspaces()' code={}", res.statusCode());
            handleError(res);
        } 
        
        // Response is 200, marshalling
        try {
            TypeReference<ApiResponse<List<Keyspace>>> expectedType = new TypeReference<>(){};
            return objectMapper.readValue(res.body(), expectedType)
                               .getData().stream();
        } catch (Exception e) {
            throw new RuntimeException("Cannot Marshall output in 'keyspaces()' body=" + res.body(), e);
        }
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @see Namespace
     * @return String
     */
    public Stream<String> keyspaceNames() {
        return keyspaces().map(Keyspace::getName);
    }
    
    /**
     * Move to the Rest API
     * 
     * @param keyspace String
     * @return KeyspaceClient
     */
    public KeyspaceClient keyspace(String keyspace) {
        return new KeyspaceClient(this, keyspace);
    }

    /**
     * Getter accessor for attribute 'endPointApiRest'.
     *
     * @return current value of 'endPointApiRest'
     */
    public String getEndPointApiRest() {
        return endPointApiRest;
    }
    
}
