package com.dstx.stargate.sdk.doc;

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

import com.dstx.stargate.sdk.rest.Keyspace;
import com.dstx.stargate.sdk.utils.ApiResponse;
import com.dstx.stargate.sdk.utils.ApiSupport;
import com.dstx.stargate.sdk.utils.JsonUtils;
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
    
    /** Username - required all the time */
    private final String username;
    
    /** Password - required all the time */
    private final String password;
    
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointAuthentication;
  
    /** This the endPoint to invoke to work with different API(s). */
    private final String endPointApiDocument;
    
    /**
     * Constructor for ASTRA.
     */
    public ApiDocumentClient(String username, String password, String endPointAuthentication, String endPointApiDocument) {
        hasLength(endPointApiDocument, "endPointApiDocument");
        hasLength(username, "username");
        hasLength(password, "password");
        this.username               = username;
        this.password               = password;
        // if the authentication endpoint is null token is not
        this.endPointAuthentication = endPointAuthentication;
        this.endPointApiDocument    = endPointApiDocument;
        LOGGER.info("+ Document API:  {}, ", endPointApiDocument);
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
    public Stream<Namespace> namespaces() {
        try {
            // Build GET request
            HttpRequest request = HttpRequest.newBuilder()
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_CASSANDRA, getToken())
                    .uri(URI.create(endPointApiDocument + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES))
                    .GET().build();
            
            // Map as a stream of namespaces
            return objectMapper.readValue(httpClient.send(request, BodyHandlers.ofString()).body(), 
                    new TypeReference<ApiResponse<List<Namespace>>>(){}).getData().stream();
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot list namespaces", e);
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
