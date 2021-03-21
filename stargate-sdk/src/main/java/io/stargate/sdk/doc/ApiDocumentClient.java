package io.stargate.sdk.doc;

import static io.stargate.sdk.utils.Assert.hasLength;

import java.net.HttpURLConnection;
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

import com.fasterxml.jackson.core.type.TypeReference;

import io.stargate.sdk.rest.Keyspace;
import io.stargate.sdk.utils.ApiResponse;
import io.stargate.sdk.utils.ApiSupport;
import io.stargate.sdk.utils.JsonUtils;

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
    
    /** Username - required all the time */
    private final String username;
    
    /** Password - required all the time */
    private final String password;
    
    /** Password - required all the time */
    private final String appToken;
    
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointAuthentication;
  
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointApiDocument;
    
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
    
    /** {@inheritDoc} */
    @Override
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
                // Use token no new to renew it
                return appToken;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot generate authentication token", e);
        }
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
