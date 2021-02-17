package com.dstx.stargate.sdk.utils;

import java.net.HttpURLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

/**
 * Mutualization of operations for doc,rest.devops API when possible.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class ApiSupport {
    
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
    
    /** Object <=> Json marshaller as a Jackson Mapper. */
    protected static final ObjectMapper objectMapper = new ObjectMapper()
                .configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector());
    
    // ----------------------------------
    //  Token Management    
    // ----------------------------------
    
    /** Storing an authentication token to speed up queries. */
    protected String token;
    
    /** Mark the token update. */
    protected long tokenCreatedtime = 0;
    
    /** Authentication token, time to live. */
    protected Duration tokenttl = TOKEN_TTL;
    
    /**
     * Generate or renew authentication token
     */
    public String getToken() {
        if ((System.currentTimeMillis() - tokenCreatedtime) > 1000 * tokenttl.getSeconds()) {
            token            = renewToken();
            tokenCreatedtime = System.currentTimeMillis();
        }
        return token;
    }
            
    public abstract String renewToken();
    
    /**
     * In test or at initialization we want to test credentials
     */
    public boolean testConnection() {
        return getToken().length() > 0;
    }
    
    /**
     * Utility to process error Requests.
     */
    public void handleError(HttpResponse<String> res) {
        
        if (HttpURLConnection.HTTP_GATEWAY_TIMEOUT == res.statusCode()) {
            System.out.println("TIMEOUT but result might be OK");
        }
        
        if (HttpURLConnection.HTTP_NOT_FOUND == res.statusCode()) {
            throw new IllegalArgumentException("Target object has not been found:" + res.body() );
        }
        
        if (HttpURLConnection.HTTP_CONFLICT == res.statusCode()) {
            throw new IllegalArgumentException("Object alrerady exist" + res.body() );
        }
        
        if (HttpURLConnection.HTTP_INTERNAL_ERROR == res.statusCode()) {
              throw new IllegalArgumentException("Internal Error" + res.body());
        }
        
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
