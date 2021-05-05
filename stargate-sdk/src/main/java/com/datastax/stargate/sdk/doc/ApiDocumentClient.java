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

import static com.datastax.stargate.sdk.utils.Assert.hasLength;

import java.net.HttpURLConnection;
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
 * Client for the Astra/Stargate document (collections) API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class ApiDocumentClient extends ApiSupport {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocumentClient.class);
    
    /** Resource for document API schemas. */
    public static final String PATH_SCHEMA_NAMESPACES = "/namespaces";
    
    /** This the endPoint to invoke to work with different API(s). */
    protected String endPointApiDocument;
    
    /**
     * Constructor for ASTRA.
     */
    public ApiDocumentClient(String username, String password, String endPointAuthentication, String appToken, String endPointApiDocument) {
        hasLength(endPointApiDocument, "endPointApiDocument");
        hasLength(username, "username");
        hasLength(password, "password");
        this.appToken               = appToken;
        this.username               = username;
        this.password               = password;
        this.endPointAuthentication = endPointAuthentication;
        this.endPointApiDocument    = endPointApiDocument;
        LOGGER.info("+ Document API:  {}, ", endPointApiDocument);
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     */
    public Stream<Namespace> namespaces() {
        String endpoint = endPointApiDocument + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES;
        
        // Build and execute HTTP CALL
        HttpResponse<String> response;
        try {
           response = httpClient.send(
                   startRequest(endpoint, getToken()).GET().build(),
                   BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot list namespaces", e);
        }
        
        // Http Call maybe successfull returning error code
        if (HttpURLConnection.HTTP_OK != response.statusCode()) {
            LOGGER.error("Error in 'namespaces()' code={}", response.statusCode());
            handleError(response);
        } 
        
        // Response is 200, marshalling
        try {
            return objectMapper.readValue(response.body(), 
                        new TypeReference<ApiResponse<List<Namespace>>>(){}).getData().stream();
        } catch (Exception e) {
            throw new RuntimeException("Cannot Marshall output in 'namespaces()' body=" + response.body(), e);
        } 
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @see Namespace
     */
    public Stream<String> namespaceNames() {
        return namespaces().map(Keyspace::getName);
    }
    
    /**
     * Move the document API (namespace client)
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

}
