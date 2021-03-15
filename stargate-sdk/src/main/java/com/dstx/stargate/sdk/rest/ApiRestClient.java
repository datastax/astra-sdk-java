package com.dstx.stargate.sdk.rest;

import static com.dstx.stargate.sdk.utils.Assert.hasLength;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dstx.stargate.sdk.doc.Namespace;
import com.dstx.stargate.sdk.utils.ApiResponse;
import com.dstx.stargate.sdk.utils.ApiSupport;
import com.dstx.stargate.sdk.utils.JsonUtils;
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
    
    /** Username - required all the time */
    private final String username;
    
    /** Password - required all the time */
    private final String password;
  
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointAuthentication;
  
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointApiRest;
    
    /**
     * Constructor for ASTRA.
     */
    public ApiRestClient(String username, String password, String endPointAuthentication, String endPointApiRest) {
        hasLength(endPointApiRest, "endPointApiRest");
        hasLength(username, "username");
        hasLength(password, "password");
        this.username               = username;
        this.password               = password;
        this.endPointAuthentication = endPointAuthentication;
        this.endPointApiRest        = endPointApiRest;
        LOGGER.info("+ Res API: {}, ", endPointApiRest);
    }
   
    
    /** {@inheritDoc} */
    @Override
    public String renewToken() {
        try {
            if (endPointAuthentication !=null) {
                // Auth request (https://docs.astra.datastax.com/reference#auth-2)
                String authRequestBody = new StringBuilder("{")
                    .append("\"username\":").append(JsonUtils.valueAsJson(username))
                    .append(", \"password\":").append(JsonUtils.valueAsJson(password))
                    .append("}").toString();
                
                // Call with a POST
                HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                        .uri(URI.create(endPointAuthentication + "/v1/auth/"))
                        .timeout(REQUEST_TIMOUT)
                        .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                        .POST(BodyPublishers.ofString(authRequestBody)).build(), BodyHandlers.ofString());
                
                // Parse result, extract token
                if (201 == response.statusCode() || 200 == response.statusCode()) {
                    LOGGER.info("Successfully authenticated, token ttl {} s.", tokenttl.getSeconds());
                    return (String) objectMapper.readValue(response.body(), Map.class).get("authToken");
                } else {
                    throw new IllegalStateException("Cannot generate authentication token " + response.body());
                }
            } else {
                // the Password is the token to use in APIS in ASTRA
                return password;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot generate authentication token", e);
        }
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     */
    public Stream<Keyspace> keyspaces() {
        try {
            // Build GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, getToken())
                    .uri(URI.create(endPointApiRest + PATH_SCHEMA + PATH_SCHEMA_KEYSPACES))
                    .GET().build();
            
            // Map as a stream of namespaces
            return objectMapper.readValue(httpClient.send(request, BodyHandlers.ofString()).body(), 
                    new TypeReference<ApiResponse<List<Keyspace>>>(){}).getData().stream();
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot list namespaces", e);
        }
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @see Namespace
     */
    public Stream<String> keyspaceNames() {
        return keyspaces().map(Keyspace::getName);
    }
    
    /**
     * Move to the Rest API
     */
    public KeyspaceClient keyspace(String keyspace) {
        return new KeyspaceClient(this, keyspace);
    }


    /**
     * Getter accessor for attribute 'endPointApiRest'.
     *
     * @return
     *       current value of 'endPointApiRest'
     */
    public String getEndPointApiRest() {
        return endPointApiRest;
    }
    
}
