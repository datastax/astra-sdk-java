package org.datastax.astra.devops;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.datastax.astra.AstraClient;
import org.datastax.astra.utils.Assert;
import org.datastax.astra.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to interact with Astra Devops API
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class AstraDevopsClient {
    
    /** Default Endpoint. */
    public static final String ASTRA_ENDPOINT_DEVOPS = "https://api.astra.datastax.com/v2/";
    
    /** Retention period for authToken in seconds, default is 5 MIN */
    public static final Duration DEFAULT_TTL          = Duration.ofSeconds(300);
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);
    
    private String clientId;
    
    private String clientName;
    
    private String clientSecret;
    
    private String endPoint = ASTRA_ENDPOINT_DEVOPS;
    
    // ------------------------
    //  Bearer Token
    // ------------------------
    
    /** Storing an authentication token to speed up queries. */
    private String bearerToken;
    
    /** Mark the token update. */
    private long bearerTokenCreationDate = 0;
    
    /** Authentication token, time to live. */
    protected final Duration bearerTokenTokenTtl = AstraClient.DEFAULT_TTL;
    
    /**
     * As immutable object use builder to initiate the object.
     */
    public AstraDevopsClient(String clientName, String clientId, String clientSecret) {
       this.clientId     = clientId;
       this.clientName   = clientName;
       this.clientSecret = clientSecret;
    }
    
    /**
     * Generate or renew authentication token
     */
    protected String getBearerToken() {
        if ((System.currentTimeMillis() - bearerTokenCreationDate) > 1000 * bearerTokenTokenTtl.getSeconds()) {
            
            try {
                // Escaping special chars and preventing JSON injection
                String authRequestBody = new StringBuilder("{")
                    .append("\"clientId\":")
                    .append(JsonUtils.valueAsJson(clientId))
                    .append(", \"clientName\":")
                    .append(JsonUtils.valueAsJson(clientName))
                    .append(", \"clientSecret\":")
                    .append(JsonUtils.valueAsJson(clientSecret))
                    .append("}").toString();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(ASTRA_ENDPOINT_DEVOPS + "authenticateServiceAccount"))
                        .timeout(AstraClient.REQUEST_TIMOUT)
                        .header("Content-Type", "application/json")
                        .POST(BodyPublishers.ofString(authRequestBody)).build();
                
                HttpResponse<String> response = AstraClient.httpClient.send(request, BodyHandlers.ofString());
                
                if (201 == response.statusCode() || 200 == response.statusCode()) {
                   bearerToken = (String) AstraClient.objectMapper.readValue(response.body(), Map.class).get("token");
                   bearerTokenCreationDate = System.currentTimeMillis();
                   LOGGER.info("Success Authenticated, token will live for {} second(s).", bearerTokenTokenTtl.getSeconds());
                } else {
                    throw new IllegalStateException("Cannot generate authentication token HTTP_CODE=" 
                                    + response.statusCode() + ", " + response.body());
                }
                
            } catch (Exception e) {
                throw new IllegalArgumentException("Cannot generate authentication token", e);
            }
        }
        return bearerToken;
    }
    
    public Stream<AstraDatabaseInfos> databases() {
        return databases(new DatabaseFilter());
    }
    
    public Stream<AstraDatabaseInfos> databases(DatabaseFilter filter) {
        Assert.notNull(filter, "filter");
        try {
            StringBuilder sbURL = new StringBuilder(ASTRA_ENDPOINT_DEVOPS + "databases?")
                    .append("include=" + filter.getInclude().name().toLowerCase())
                    .append("&provider=" + filter.getProvider().name().toLowerCase())
                    .append("&limit=" + filter.getLimit());
            if (!filter.getStartingAfterDbId().isEmpty()) {
                sbURL.append("&starting_after=" + filter.getStartingAfterDbId().get());
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(sbURL.toString()))
                    .timeout(AstraClient.REQUEST_TIMOUT)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getBearerToken())
                    .GET().build();
            
            HttpResponse<String> response = AstraClient.httpClient.send(request, BodyHandlers.ofString());
            
            if (201 == response.statusCode() || 200 == response.statusCode()) {
               bearerToken = (String) AstraClient.objectMapper.readValue(response.body(), Map.class).get("token");
               bearerTokenCreationDate = System.currentTimeMillis();
               LOGGER.info("Success Authenticated, token will live for {} second(s).", bearerTokenTokenTtl.getSeconds());
            } else {
                throw new IllegalStateException("Cannot generate authentication token HTTP_CODE=" 
                                + response.statusCode() + ", " + response.body());
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot generate authentication token", e);
        }
        
        return null;
    }
    
    public Optional<AstraDatabaseInfos> findDatbaseById(String dbId) {
        return null;
    }
    
    public void createNewKeyspace(String dbId, String keyspaceName) {
        // BEARER TOKEN
        // POST
        //https://api.astra.datastax.com/v2/databases/<dbId>/keyspaces/<keyspaceName>
        // 201 created, 409 db not AV
        
    }
    
    public void downloadSecureConnectBundle(String dbId, String destination) {
        
    }
    
    
    
    

}
