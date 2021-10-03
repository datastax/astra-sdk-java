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

import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

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
 * Client for the Astra/Stargate document (collections) API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocumentClient.class);
    
    /** Resource for document API schemas. */
    public static final String PATH_SCHEMA_NAMESPACES = "/namespaces";
    
    /** Resource for document API schemas. */
    public static final String PATH_SCHEMA            = "/v2/schemas";
    
    /** Marshalling types. */
    private static final TypeReference<ApiResponse<List<Namespace>>> RESPONSE_LIST_NAMESPACE = 
            new TypeReference<ApiResponse<List<Namespace>>>(){};
    
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointApiDocument;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
   
    /**
     * Initialized document API with an URL and a token.
     * 
     * @param endpoint
     *      http endpoint
     * @param token
     *      authentication token
     */
    public ApiDocumentClient(String endpoint, String token) {
        Assert.hasLength(endpoint, "endpoint");
        Assert.hasLength(token, "token");
        this.endPointApiDocument =  endpoint;
        this.http = HttpApisClient.getInstance();
        http.setToken(token);
        LOGGER.info("+ API Document    :[" + cyan("{}") + "]", endPointApiDocument);
    }
    
    /**
     * Invoked when working with StandAlone Stargate.
     * 
     * @param endpoint
     *      provide the URL
     * @param tokenProvider
     *      how to load the token
     */
    public ApiDocumentClient(String endpoint, ApiTokenProvider tokenProvider) {
        Assert.hasLength(endpoint, "endpoint");
        Assert.notNull(tokenProvider, "tokenProvider");
        this.endPointApiDocument =  endpoint;
        this.http = HttpApisClient.getInstance();
        http.setTokenProvider(tokenProvider);
        LOGGER.info("+ API Document    :[" + cyan("{}") + "]", endPointApiDocument);
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     * 
     * @return Stream
     */
    public Stream<Namespace> namespaces() {
        ApiResponseHttp res = http.GET(getEndpointSchemaNamespaces());
        return unmarshallType(res.getBody(), RESPONSE_LIST_NAMESPACE).getData().stream();
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @return Stream
     *      stream of the namespaces
     */
    public Stream<String> namespaceNames() {
        return namespaces().map(Keyspace::getName);
    }
    
    /**
     * Move the document API (namespace client)
     * 
     * @param namespace String
     * @return NamespaceClient
     */
    public NamespaceClient namespace(String namespace) {
        return new NamespaceClient(this, namespace);
    }
    
    /**
     * Getter accessor for attribute 'endPointApiDocument'.
     *
     * @return
     *       current value of 'endPointApiDocument'
     */
    public String getEndPointApiDocument() {
        return endPointApiDocument;
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      url to build schema for namespaces
     */
    public String getEndpointSchemaNamespaces() {
        return getEndPointApiDocument() + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES;
    }

}
