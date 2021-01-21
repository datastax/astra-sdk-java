package org.datastax.astra.devops;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.datastax.astra.AstraClient;
import org.datastax.astra.api.AbstractApiClient;
import org.datastax.astra.utils.Assert;
import org.datastax.astra.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Class to interact with Astra Devops API
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class ApiDevopsClient extends AbstractApiClient {
    
    /** Default Endpoint. */
    public static final String ASTRA_ENDPOINT_DEVOPS = "https://api.astra.datastax.com/v2/";
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);
    
    /** Service Account client Identifier. */
    private String clientId;
    
    /** Service Account client name. */
    private String clientName;
    
    /** Service Account client secret. */
    private String clientSecret;
    
    /**
     * As immutable object use builder to initiate the object.
     */
    public ApiDevopsClient(String clientName, String clientId, String clientSecret) {
       this.clientId     = clientId;
       this.clientName   = clientName;
       this.clientSecret = clientSecret;
       Assert.hasLength(clientId, "clientId");
       Assert.hasLength(clientName, "clientName");
       Assert.hasLength(clientSecret, "clientSecret");
    }
    
    /** {@inheritDoc} */
    @Override
    public String renewToken() {
        try {
            String authRequestBody = new StringBuilder("{")
                .append("\"clientId\":")
                .append(JsonUtils.valueAsJson(clientId))
                .append(",\"clientName\":")
                .append(JsonUtils.valueAsJson(clientName))
                .append(",\"clientSecret\":")
                .append(JsonUtils.valueAsJson(clientSecret))
                .append("}").toString();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ASTRA_ENDPOINT_DEVOPS + "authenticateServiceAccount"))
                    .timeout(REQUEST_TIMOUT)
                    .header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON)
                    .header(HEADER_ACCEPT, CONTENT_TYPE_JSON)
                    .POST(BodyPublishers.ofString(authRequestBody)).build();
            
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            
            if (201 == response.statusCode() || 200 == response.statusCode()) {

               LOGGER.info("Success Authenticated, token will live for {} second(s).", tokenttl.getSeconds());
               return (String) objectMapper.readValue(response.body(), Map.class).get("token");
            } else {
                throw new IllegalStateException("Cannot generate authentication token " + response.body());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot generate authentication token", e);
        }
    }
    
    public Stream<AstraDatabaseInfos> databases() {
        return databases(DatabaseFilter.builder().build());
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
                    .timeout(REQUEST_TIMOUT)
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + getToken())
                    .GET().build();
            
            HttpResponse<String> response = httpClient.send(request, BodyHandlers.ofString());
            
            if (201 == response.statusCode() || 200 == response.statusCode()) {
                List<AstraDatabaseInfos> dbs = objectMapper.readValue(response.body(),
                       new TypeReference<List<AstraDatabaseInfos>>(){});
                LOGGER.info("{} database(s) have been retrieved ", dbs.size());
                return dbs.stream();
            }
            throw new IllegalArgumentException("Cannot list databases " + response.body());
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot generate authentication token", e);
        }
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
