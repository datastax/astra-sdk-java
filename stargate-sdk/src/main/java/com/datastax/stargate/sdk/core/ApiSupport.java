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

package com.datastax.stargate.sdk.core;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.exception.AuthenticationException;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Mutualization of operations for doc,rest.devops API when possible.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class ApiSupport {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiSupport.class);
    
    public static final String HEADER_ACCEPT          = "Accept";
    public static final String HEADER_CASSANDRA       = "X-Cassandra-Token";
    public static final String HEADER_CONTENT_TYPE    = "Content-Type";
    public static final String HEADER_AUTHORIZATION   = "Authorization";
    public static final String CONTENT_TYPE_JSON      = "application/json";
    public static final String PATH_SCHEMA            = "/v2/schemas";
    
    /** Set a timeout for Http requests. */
    public static final Duration REQUEST_TIMOUT = Duration.ofSeconds(10);
    
    /** Set a timeout for Http requests. */
    public static final Duration TOKEN_TTL = Duration.ofSeconds(300);
    
    // ----------------------------------
    //  Http Client   
    // ----------------------------------
    
    /** Core Java 11 Http Client (limiting dependencies to third-party and ensure portability). **/
    protected static final HttpClient httpClient = HttpClient.newBuilder()
                .version(Version.HTTP_2)
                .followRedirects(Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(10))
                .executor(Executors.newFixedThreadPool(5))
                .build();
    
    /** Object to Json marshaller as a Jackson Mapper. */
    protected static final ObjectMapper objectMapper = new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
                // Specially for partial updates do not put null values
                .setSerializationInclusion(Include.NON_NULL)
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector());
    
    /** Storing an authentication token to speed up queries. */
    protected String token;
    
    /** Mark the token update. */
    protected long tokenCreatedtime = 0;
    
    /** Authentication token, time to live. */
    protected Duration tokenttl = TOKEN_TTL;
    
    /** Username - required all the time */
    protected  String username;
    
    /** Password - required all the time */
    protected  String password;
    
    /** Password - required all the time */
    protected  String appToken;
    
    /** This the endPoint to invoke to work with different API(s). */
    protected  String endPointAuthentication;
    
    /**
     * Generate or renew authentication token
     * @return String
     */
    public String getToken() {
        if ((System.currentTimeMillis() - tokenCreatedtime) > 1000 * tokenttl.getSeconds()) {
            token = renewToken();
            tokenCreatedtime = System.currentTimeMillis();
        }
        return token;
    }
            
    public String renewToken() {
        try {
            if (appToken == null) {
                if (null == endPointAuthentication) {
                    throw new IllegalStateException("No application token provided, please provide authentication endpoint");
                }
                // Auth request (https://docs.astra.datastax.com/reference#auth-2)
                String authRequestBody = new StringBuilder("{")
                    .append("\"username\":").append(JsonUtils.valueAsJson(username))
                    .append(", \"password\":").append(JsonUtils.valueAsJson(password))
                    .append("}").toString();
                
                // Call with a POST
                System.out.println(endPointAuthentication);
                HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                        .uri(URI.create(endPointAuthentication + "/v1/auth"))
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
                // Use token no new to renew it
                return appToken;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot generate authentication token", e);
        }
    }
    
    /**
     * Utility to process error Requests.
     * @param res HttpResponse
     */
    public static void handleError(HttpResponse<String> res) {
        if (res.statusCode() >=300) {
            try {
               ApiError apiErr = objectMapper.readValue(res.body(), ApiError.class);
               if (res.statusCode() == HttpURLConnection.HTTP_FORBIDDEN || 
                   res.statusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                   throw new AuthenticationException(apiErr.getCode() + ":" + apiErr.getDescription());
               }
               if (HttpURLConnection.HTTP_CONFLICT == res.statusCode()) {
                   throw new IllegalArgumentException("Object alrerady exist" + apiErr.getCode() + ":" + apiErr.getDescription());
               }
               if (HttpURLConnection.HTTP_INTERNAL_ERROR == res.statusCode()) {
                   throw new IllegalStateException("Internal Error" + apiErr.getCode() + ":" + apiErr.getDescription());
               }
             } catch (Exception e) {}
             throw new RuntimeException("Error code=" + res.statusCode() + " response=" + res.body());
        }
    }
    
    /**
    * Mutualizing request headers/settings.
    *
    * @param url end of the url
    * @param token String
    * @return builder for the query
    */
    public static HttpRequest.Builder startRequest(String url, String token) {
       return HttpRequest.newBuilder()
               .timeout(REQUEST_TIMOUT)
               .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
               .header(HEADER_CASSANDRA, token)
               .uri(URI.create(url));
   }

    /**
     * Getter accessor for attribute 'httpclient'.
     *
     * @return
     *       current value of 'httpclient'
     */
    public static HttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * Getter accessor for attribute 'objectmapper'.
     *
     * @return
     *       current value of 'objectmapper'
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

}
