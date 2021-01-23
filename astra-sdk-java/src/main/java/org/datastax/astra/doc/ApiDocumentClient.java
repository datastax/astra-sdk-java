package org.datastax.astra.doc;

import static org.datastax.astra.utils.Assert.hasLength;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.datastax.astra.ApiSupport;
import org.datastax.astra.ApiResponse;
import org.datastax.astra.schemas.Keyspace;
import org.datastax.astra.schemas.Namespace;
import org.datastax.astra.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final String baseUrl;
    
    /** 
     * Database unique identifier.
     * - Required when using Astra Doc API and Rest API
     */
    private final String astraDatabaseId;
    
    /** 
     * Astra database region
     * - Required when using Astra Doc API and Rest API
     */
    private final String astraDatabaseRegion;
    
    /** Username - required all the time */
    private final String username;
    
    /** Password - required all the time */
    private final String password;
    
    /**
     * Constructor for ASTRA.
     */
    public ApiDocumentClient(String astraDatabaseId, String astraDatabaseRegion, String username, String password) {
        hasLength(astraDatabaseId, "astraDatabaseId");
        hasLength(astraDatabaseRegion, "astraDatabaseRegion");
        hasLength(username, "username");
        hasLength(password, "password");
        this.username            = username;
        this.password            = password;
        this.astraDatabaseId     = astraDatabaseId;
        this.astraDatabaseRegion = astraDatabaseRegion;
        this.baseUrl = new StringBuilder(ASTRA_ENDPOINT_PREFIX)
                    .append(astraDatabaseId)
                    .append("-")
                    .append(astraDatabaseRegion)
                    .append(ASTRA_ENDPOINT_SUFFIX)
                    .toString();
        LOGGER.debug("Initializing Client with ASTRA: "
                + "BaseUrl={}, username={},passwordLenght={}", baseUrl, username, password.length());
    }
    
    /**
     * Constructor for StandAlone stargate mostly.
     */
    public ApiDocumentClient(String baseUrl, String username, String password) {
        hasLength(baseUrl, "baseUrl");
        hasLength(username, "username");
        hasLength(password, "password");
        this.astraDatabaseId     = null;
        this.astraDatabaseRegion = null;
        this.baseUrl             = baseUrl;
        this.username            = username;
        this.password            = password;
        LOGGER.debug("Initializing Client: "
                + "BaseUrl={}, username={},passwordLenght={}", baseUrl, username, password.length());
    }
    
    /**
     * Getter for Astra database identifier, can be null if we connect to standAlone stargate.
     */
    public Optional<String> getAstraDatabaseId() {
        return Optional.ofNullable(astraDatabaseId);
    }
    
    /**
     * Getter for Astra database region, can be null if we connect to standAlone stargate.
     */
    public Optional<String> getAstraDatabaseRegion() {
        return Optional.ofNullable(astraDatabaseRegion);
    }
    
    /**
     * Getter Api URL, alway populated either for standalone stargate of built for Astra.
     */
    public String getBaseUrl() {
        return baseUrl;
    }
    
    /** {@inheritDoc} */
    @Override
    public String renewToken() {
        try {
            // Auth request (https://docs.astra.datastax.com/reference#auth-2)
            String authRequestBody = new StringBuilder("{")
                .append("\"username\":").append(JsonUtils.valueAsJson(username))
                .append(", \"password\":").append(JsonUtils.valueAsJson(password))
                .append("}").toString();
            // Call with a POST
            HttpResponse<String> response = httpClient.send(HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/auth/"))
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .POST(BodyPublishers.ofString(authRequestBody)).build(), BodyHandlers.ofString());
            // Parse result, extract token
            if (201 == response.statusCode() || 200 == response.statusCode()) {
                LOGGER.info("Success Authenticated, token will live for {} second(s).", tokenttl.getSeconds());
                return (String) objectMapper.readValue(response.body(), Map.class).get("authToken");
            } else {
                throw new IllegalStateException("Cannot generate authentication token " + response.body());
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
                    .uri(URI.create(getBaseUrl() + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES))
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

}
