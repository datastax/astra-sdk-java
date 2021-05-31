package com.datastax.astra.sdk.utils;

import static com.datastax.stargate.sdk.core.ApiSupport.CONTENT_TYPE_JSON;
import static com.datastax.stargate.sdk.core.ApiSupport.HEADER_ACCEPT;
import static com.datastax.stargate.sdk.core.ApiSupport.HEADER_AUTHORIZATION;
import static com.datastax.stargate.sdk.core.ApiSupport.HEADER_CONTENT_TYPE;
import static com.datastax.stargate.sdk.core.ApiSupport.REQUEST_TIMOUT;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.core.ApiSupport;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Superclass to help building client for Devops API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class ApiDevopsSupport {
    
    /** Logger for our Client. */
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    
    /** Service Account client Identifier. */
    protected final String bearerAuthToken;
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param authToken
     *          authenticated token
     */
    public ApiDevopsSupport(String authToken) {
       this.bearerAuthToken = authToken;
       Assert.hasLength(bearerAuthToken, "authToken");
    }
    
    
    /** Default Endpoint. */
    public static final String ASTRA_ENDPOINT_DEVOPS = "https://api.astra.datastax.com/v2";
    
    /**
     * Mutualizing request headers/settings.
     *
     * @param suffix
     *      end of the url
     * @return
     *      builder for the query
     */
    public HttpRequest.Builder startRequest(String suffix) {
        return HttpRequest.newBuilder()
                .uri(URI.create(ASTRA_ENDPOINT_DEVOPS + suffix))
                .timeout(REQUEST_TIMOUT)
                .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                .header(HEADER_ACCEPT, CONTENT_TYPE_JSON)
                .header(HEADER_AUTHORIZATION, "Bearer " + bearerAuthToken);
    }
    
    /**
     * Any not excepted code will be routed to this methods.
     * 
     * @param response
     *      current HTTP Response
     * @return
     *      the specialized exception based on returned codes
     */
    public RuntimeException processErrors(HttpResponse<String> response) {
        if (response.statusCode() == HttpURLConnection.HTTP_FORBIDDEN || 
            response.statusCode() != HttpURLConnection.HTTP_INTERNAL_ERROR) {
            try {
                // Marshalling error block
                ApiResponseError apiErr = getObjectMapper().readValue(response.body(),  ApiResponseError.class);
                apiErr.getErrors().stream().forEach(err -> LOGGER.error(err.toString()));
                // Throw Specialized Exception
                if (response.statusCode() == HttpURLConnection.HTTP_FORBIDDEN) {
                    LOGGER.error("Http code 401: Forbidden, check you token");
                    return new IllegalStateException("401:" + apiErr.getErrors().get(0).getMessage());
                }
                if (response.statusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                    LOGGER.error("Http code 400: Check your parameters");
                    return new IllegalArgumentException("400:" + apiErr.getErrors().get(0).getMessage());
                }
                if (response.statusCode() == HttpURLConnection.HTTP_CONFLICT) {
                    LOGGER.error("Http code 409: Conflict either operation is not allowed or enities may already exists");
                    return new IllegalArgumentException("409:" + apiErr.getErrors().get(0).getMessage());
                }
                if (response.statusCode() == 422) {
                    LOGGER.error("Http code 422: Invalid information to create DB");
                    return new IllegalArgumentException("422:" + apiErr.getErrors().get(0).getMessage());
                }
                return new RuntimeException(response.statusCode() + ": " + apiErr.getErrors().get(0).getMessage());
            } catch (Exception e) {
                LOGGER.error("Cannot parse response " + response.body(), e);
            }
        }
        // Was not able to specialized error throw generic
        return new RuntimeException("Error code2=" +  response.statusCode()+ " response=" + response.body());
    }
    
    /**
     * Getter accessor for attribute 'httpclient'.
     *
     * @return
     *       current value of 'httpclient'
     */
    public HttpClient getHttpClient() {
        return ApiSupport.getHttpClient();
    }

    /**
     * Getter accessor for attribute 'objectmapper'.
     *
     * @return
     *       current value of 'objectmapper'
     */
    public ObjectMapper getObjectMapper() {
        return ApiSupport.getObjectMapper();
    }
    
    /**
     * Getter accessor for attribute 'bearerAuthToken'.
     *
     * @return
     *       current value of 'bearerAuthToken'
     */
    public String getBearerAuthToken() {
        return bearerAuthToken;
    }
    
    

}
