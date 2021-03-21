package com.dstx.astra.sdk.devops;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dstx.astra.sdk.devops.req.DatabaseCreationRequest;
import com.dstx.astra.sdk.devops.req.DatabaseFilter;
import com.dstx.astra.sdk.devops.req.DatabaseFilter.Include;
import com.dstx.astra.sdk.devops.res.ApiResponseError;
import com.dstx.astra.sdk.devops.res.Database;
import com.dstx.astra.sdk.devops.res.DatabaseAvailableRegion;
import com.fasterxml.jackson.core.type.TypeReference;

import io.stargate.sdk.utils.ApiSupport;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.Utils;

/**
 * Client for the Astra Devops API.
 * 
 * The JDK11 client http is used and as such jdk11+ is required
 * 
 * Documentation:
 * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDevopsClient extends ApiSupport {
    
    /** Default Endpoint. */
    public static final String ASTRA_ENDPOINT_DEVOPS = "https://api.astra.datastax.com/v2/";
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDevopsClient.class);
    
    /** Service Account client Identifier. */
    private String bearerAuthToken;
    
    /**
     * As immutable object use builder to initiate the object.
     */
    public ApiDevopsClient(String authToken) {
       this.bearerAuthToken = authToken;
       Assert.hasLength(bearerAuthToken, "authToken");
    }
     
    /**
     * Returns supported regions and availability for a given user and organization
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/listAvailableRegions
     * 
     * @return
     *  supported regions and availability 
     */
    public Stream<DatabaseAvailableRegion> findAllAvailableRegions() {
        HttpResponse<String> res;
        try {
           // Invocation with no marshalling
           res = httpClient.send(
                    startRequest("availableRegions").GET().build(), 
                    BodyHandlers.ofString());
            
            // Parsing as list of Bean if OK
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return  objectMapper.readValue(res.body(),
                        new TypeReference<List<DatabaseAvailableRegion>>(){})
                                   .stream();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot list regions", e);
        }
        
        LOGGER.error("Error in 'availableRegions'");
        throw processErrors(res);
    }
    
    /**
     * Map regions from plain list to Tier/Cloud/Region Structure
     * @return
     */
    public Map <DatabaseTierType, Map<CloudProviderType,List<DatabaseAvailableRegion>>> mapAvailableRegions(Stream<DatabaseAvailableRegion> all) {
        Map<DatabaseTierType, Map<CloudProviderType,List<DatabaseAvailableRegion>>> m = new HashMap<>();
        all.forEach(dar -> {
            if (!m.containsKey(dar.getTier())) {
                m.put(dar.getTier(), new HashMap<CloudProviderType,List<DatabaseAvailableRegion>>());
            }
            if (!m.get(dar.getTier()).containsKey(dar.getCloudProvider())) {
                m.get(dar.getTier()).put(dar.getCloudProvider(), new ArrayList<DatabaseAvailableRegion>());
            }
            m.get(dar.getTier()).get(dar.getCloudProvider()).add(dar);
        });
        return m;
    }
    
    /**
     * Returns list of databases with default filter.
     * (include=nonterminated, provider=ALL,limit=25)
     *
     * @return
     *      matching db
     */
    public Stream<Database> findAllDatabases() {
        return findDatabases(DatabaseFilter.builder()
                .include(Include.ALL)
                .provider(CloudProviderType.ALL)
                .limit(1000)
                .build());
    }
    
    /**
     * Default Filter to find databases.
     *
     * @return
     *      value
     */
    public Stream<Database> findAllDatabasesNonTerminated() {
        return findDatabases(DatabaseFilter.builder().build());
    }
    
    /**
     * Find Databases matching the provided filter.
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/listDatabases
     */
    public Stream<Database> findDatabases(DatabaseFilter filter) {
        Assert.notNull(filter, "filter");
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = httpClient
                    .send(startRequest(filter.urlParams())
                    .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return objectMapper
                        .readValue(res.body(), new TypeReference<List<Database>>(){})
                        .stream();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        LOGGER.error("Error in 'findDatabases', params={}", filter.urlParams());
        throw processErrors(res);
    }
   
    /**
     * Retrieve a DB by its id.
     *
     * @param dbId
     *      unique db identifier
     * @return
     *      the database if present,
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/getDatabase
     */
    public Optional<Database> findDatabaseById(String dbId) {
        Assert.hasLength(dbId, "Database identifier");
        // Api Call
        HttpResponse<String> response;
        try {
           response = httpClient
                   .send(startRequest("databases/" + dbId).GET()
                   .build(), BodyHandlers.ofString());
           
           // Mashallinging 
           if (HttpURLConnection.HTTP_OK == response.statusCode()) {
               return Optional.ofNullable(objectMapper.readValue(response.body(),Database.class));
           } else if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
               return Optional.empty();
           }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        // Specializing error
        LOGGER.error("Error in 'findDatabaseById', with id={}", dbId);
        throw processErrors(response);
    }
    
    /**
     * Retrieve list of all Databases of the account and filter on name
     * 
     * @param name
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Stream<Database> findDatabasesNonTerminatedByName(String name) {
        Assert.hasLength(name, "Database name");
        return findAllDatabasesNonTerminated().filter(db->name.equals(db.getInfo().getName()));
    }
    
    /**
     * Evaluate if a database exists using the findById method.
     * 
     * @param dbId
     *      database identifier
     * @return
     *      database existence
     *      
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace
     */
    public boolean databaseExist(String dbId) {
        return findDatabaseById(dbId).isPresent();
    }
    
    /**
     * Syntax sugar for doc API
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace
     */
    public void createNamespace(String dbId, String namespace) {
        createKeyspace(dbId, namespace);
    }
    
    /**
     * Create a new keyspace in a DB.
     *
     * @param dbId
     *      unique identifier for database id
     * @param keyspace
     *      keyspace name to create
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace
     */
    public void createKeyspace(String dbId, String keyspace) {
        Assert.hasLength(dbId, "Datatasbe id");
        Assert.hasLength(keyspace, "Namespace");
        // HTTP CALL
        HttpResponse<String> response;
        try {
            response = httpClient
                    .send(startRequest("databases/" + dbId + "/keyspaces/" + keyspace)
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        // If not 201, process errors
        if (HttpURLConnection.HTTP_CREATED != response.statusCode()) {
            LOGGER.error("Error in 'createKeyspace', with id={} and keyspace={}, errorCode={}", dbId, keyspace, response.statusCode());
            throw processErrors(response);
        }
    }
    
    /**
     * Download SecureBundle.
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/generateSecureBundleURL
     */
    public void downloadSecureConnectBundle(String dbId, String destination) {
        Assert.hasLength(dbId, "Database id");
        Assert.hasLength(destination, "destination");
        
        // HTTP CALL
        HttpResponse<String> response;
        try {
            response = httpClient
                    .send(startRequest("databases/" + dbId  + "/secureBundleURL")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString()); 
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        // If not 200, specializing errors
        if (HttpURLConnection.HTTP_OK != response.statusCode()) {
            LOGGER.error("Error in 'downloadSecureConnectBundle', with id={}", dbId);
            throw processErrors(response);
        }
        
        // Download binary in target folder
        try {
             Utils.downloadFile((String) objectMapper
                  .readValue(response.body(), Map.class)
                  .get("downloadURL"), destination);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    /**
     * Create a database base on some parameters.
     * 
     * @param dbCreationRequest
     *      creation request with tier and capacity unit
     * @return
     *      the new instance id.
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/createDatabase
     */
    public String createDatabase(DatabaseCreationRequest dbCreationRequest) {
        Assert.notNull(dbCreationRequest, "Database creation request");
        HttpResponse<String> response ;
        try {
           response = httpClient
                    .send(startRequest("databases")
                    .POST(BodyPublishers.ofString(objectMapper.writeValueAsString(dbCreationRequest)))
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot create a new instance", e);
        }
        
        if (HttpURLConnection.HTTP_CREATED != response.statusCode()) {
            LOGGER.error("Error in 'createDatabase', with request={}", dbCreationRequest.toString());
            throw processErrors(response);
        } 
        return response.headers().map().get("location").get(0);
    }
    
    /**
     * Parks a database
     * 
     * @param dbId
     *      unique identifier for the db
     */
    public void parkDatabase(String dbId) {
        Assert.hasLength(dbId, "Database id");
        HttpResponse<String> response;
        try {
            response = httpClient
                    .send(startRequest("databases/" + dbId + "/park")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot park a database", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'parkDatabase', with id={}", dbId);
            throw processErrors(response);
        }
    }
    
    /**
     * Unparks a database.
     *
     * @param dbId
     *          unique identifier for the db
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/unparkDatabase
     */
    public void unparkDatabase(String dbId) {
        Assert.hasLength(dbId, "Database id");
        HttpResponse<String> response;
        try {
            response = httpClient
                    .send(startRequest("databases/" + dbId + "/unpark")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot unpark DB", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'unparkDatabase', with id={}", dbId);
            throw processErrors(response);
        }
    }
    
    /**
     * Terminates a database.
     *
     * @param dbId
     *          unique identifier for the db
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/terminateDatabase
     */
    public void terminateDatabase(String dbId) {
        Assert.hasLength(dbId, "Database id");
        HttpResponse<String> response;
        try {
            // Invocation
            response = httpClient
                    .send(startRequest("databases/" + dbId + "/terminate")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot terminate DB", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'terminateDatabase', with id={}", dbId);
            throw processErrors(response);
        }
    }
    
    /**
     * Resizes a database.
     *
     * @param dbId
     *          unique identifier for the db
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resizeDatabase
     */
    public void resizeDatase(String databaseID, int capacityUnits) {
        Assert.hasLength(databaseID, "Database id");
        Assert.isTrue(capacityUnits>0, "Capacity Unit");
        HttpResponse<String> response;
        try {
            response = httpClient
                    .send(startRequest("databases/" + databaseID + "/resize")
                    .POST(BodyPublishers.ofString("{ \"capacityUnits\":" + capacityUnits + "}"))
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot Resize DB ", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'resizeDatase', with id={} and capacity={}", databaseID, capacityUnits);
            throw processErrors(response);
        }
    }
    
    /**
     * Resets Password.
     *
     * @param dbId
     *      unique identifier for the db
     * @param username
     *      username
     * @param password
     *      password
     * 
     * @see https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resetPassword
     */
    public void resetPassword(String dbId, String username, String password) {
        Assert.hasLength(dbId, "Database id");
        HttpResponse<String> response;
        try {
            response = httpClient
                    .send(startRequest("databases/" + dbId + "/resetPassword")
                    .POST(BodyPublishers.ofString("{ "
                            + "\"username\": \"" + username + "\", "
                            + "\"password\": \"" + password + "\"  }"))
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot rerset password", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'resetPassword', with id={} and username={}", dbId, username);
            throw processErrors(response);
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
    private HttpRequest.Builder startRequest(String suffix) {
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
    private RuntimeException processErrors(HttpResponse<String> response) {
        if (response.statusCode() == HttpURLConnection.HTTP_FORBIDDEN || 
            response.statusCode() != HttpURLConnection.HTTP_INTERNAL_ERROR) {
            try {
                // Marshalling error block
                ApiResponseError apiErr = objectMapper.readValue(response.body(),  ApiResponseError.class);
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
     * Getter accessor for attribute 'bearerAuthToken'.
     *
     * @return
     *       current value of 'bearerAuthToken'
     */
    public String getBearerAuthToken() {
        return bearerAuthToken;
    }

    /** {@inheritDoc} */
    @Override
    public String renewToken() {
        return bearerAuthToken;
    }
    
}
