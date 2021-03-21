package io.stargate.sdk.utils;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.concurrent.Executors;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

import io.stargate.sdk.exception.AuthenticationException;

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
                .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .setDateFormat(new SimpleDateFormat("dd/MM/yyyy"))
                .setAnnotationIntrospector(new JacksonAnnotationIntrospector());
    
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
     * Utility to process error Requests.
     */
    public static void handleError(HttpResponse<String> res) {
        if (res.statusCode() >=300) {
            try {
               StargateApiError apiErr = objectMapper.readValue(res.body(), StargateApiError.class);
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
    * @param suffix
    *      end of the url
    * @return
    *      builder for the query
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
