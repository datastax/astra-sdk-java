package com.datastax.astra.sdk.databases;

import static com.datastax.astra.sdk.databases.DatabasesClient.PATH_DATABASES;

import java.net.HttpURLConnection;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Working with the Database part of the devop API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabaseClient extends ApiDevopsSupport {
    
    /** unique db identifier. */
    private final String databaseId;
    
    /**
     * Default constructor.
     *
     * @param bearerAuthToken
     *          authentication token
     * @param databaseId
     *          uniique database identifier
     */
    public DatabaseClient(String bearerAuthToken, String databaseId) {
        super(bearerAuthToken);
        Assert.hasLength(databaseId, "databaseId");
        this.databaseId = databaseId;
    }
    
    /**
     * Leveraging on Asynchronous HTTP CLIENT.
     * @return
     */
    public CompletableFuture<Optional<Database>> findAsync() {
        return http()
              .sendAsync(find_buildRequest(), BodyHandlers.ofString())
              .thenApply(this::find_mapResponse);
    }
    
    /**
     * Retrieve a DB by its id.
     * 
     * @return
     *      the database if present,
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/getDatabase
     */
    public Optional<Database> find() {
       try {
        return find_mapResponse(http().send(find_buildRequest(), BodyHandlers.ofString()));
        } catch (Exception e) {
            throw new RuntimeException("Error during endpoint Invocation find():", e);
        }
    }
    
    /**
     * Building request to retrieve one database (if exist).
     *
     * @return
     *      the request to fire against the API.
     */
    private HttpRequest find_buildRequest() {
        return req(PATH_DATABASES + "/" + databaseId).GET().build();
    }
    
    /**
     * Mutualiization of response mapping to allow Async calls.
     * 
     * @param response
     *      current HTTP response
     * @return
     *      the database if present
     */
    private Optional<Database> find_mapResponse(HttpResponse<String> response) {
        
        if (HttpURLConnection.HTTP_OK == response.statusCode()) {
            try {
                return Optional.ofNullable(om().readValue(response.body(),Database.class));
            } catch(Exception e) {
                throw new RuntimeException("Cannot marshall output from the HTTP", e);
            }    
        }
        
        if (HttpURLConnection.HTTP_NOT_FOUND == response.statusCode()) {
            return Optional.empty();
        }
        
        // Specializing error
        LOGGER.error("Error in 'findDatabaseById', with id={}", databaseId);
        throw processErrors(response);
    }
    
    /**
     * Evaluate if a database exists using the findById method.
     * 
     * @return
     *      database existence
     *      
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * Check existence of a database using async communications.
     * 
     * @return
     *      if its exist.
     */
    public CompletableFuture<Boolean> existAsync() {
        return findAsync().thenApply(Optional::isPresent);
    }
    
    /**
     * Create a new keyspace in a DB.
     * 
     * @param keyspace
     *      keyspace name to create
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace
     */
    public void createKeyspace(String keyspace) {
        Assert.hasLength(keyspace, "Namespace");
        // HTTP CALL
        HttpResponse<String> response;
        try {
            response =  http()
                    .send(req(PATH_DATABASES + "/" + databaseId + "/keyspaces/" + keyspace)
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        // If not 201, process errors
        if (HttpURLConnection.HTTP_CREATED != response.statusCode()) {
            LOGGER.error("Error in 'createKeyspace', with id={} and keyspace={}, errorCode={}", databaseId, keyspace, response.statusCode());
            throw processErrors(response);
        }
    }
    
    /**
     * Syntax sugar for doc API
     * 
     * @param namespace
     *          target namespace
     *         
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/addKeyspace          
     */
    public void createNamespace(String namespace) {
        createKeyspace(namespace);
    }
    
    /**
     * Parks a database
     */
    public void park() {
        HttpResponse<String> response;
        try {
            response = http()
                    .send(req(PATH_DATABASES + "/" + databaseId + "/park")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot park a database", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'parkDatabase', code={}, with id={}", response.statusCode(), databaseId);
            throw processErrors(response);
        }
    }
    
    /**
     * Unparks a database.
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/unparkDatabase
     */
    public void unpark() {
        HttpResponse<String> response;
        try {
            response = http()
                    .send(req(PATH_DATABASES + "/" + databaseId + "/unpark")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot unpark DB", e);
        }
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'unparkDatabase', with id={}", databaseId);
            throw processErrors(response);
        }
    }
    
    /**
     * Terminates a database.
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/terminateDatabase
     */
    public void delete() {
        HttpResponse<String> response;
        try {
            // Invocation
            response = http()
                    .send(req(PATH_DATABASES + "/" + databaseId + "/terminate")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot terminate DB", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'terminateDatabase', with id={}", databaseId);
            throw processErrors(response);
        }
    }
    

    /**
     * Resizes a database.
     *
     * @param capacityUnits
     *          sizing of a 'classic' db in Astra
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resizeDatabase
     */
    public void resize(int capacityUnits) {
        Assert.isTrue(capacityUnits>0, "Capacity Unit");
        HttpResponse<String> response;
        try {
            response = http()
                    .send(req(PATH_DATABASES + "/" + databaseId + "/resize")
                    .POST(BodyPublishers.ofString("{ \"capacityUnits\":" + capacityUnits + "}"))
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot Resize DB ", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'resizeDatase', with id={} and capacity={}", databaseId, capacityUnits);
            throw processErrors(response);
        }
    }
    
    /**
     * Resets Password.
     *
     * @param username
     *      username
     * @param password
     *      password
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/resetPassword
     */
    public void resetPassword(String username, String password) {
        HttpResponse<String> response;
        try {
            response = http()
                    .send(req(PATH_DATABASES + "/" + databaseId + "/resetPassword")
                    .POST(BodyPublishers.ofString("{ "
                            + "\"username\": \"" + username + "\", "
                            + "\"password\": \"" + password + "\"  }"))
                    .build(), BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Cannot rerset password", e);
        }
        
        if (HttpURLConnection.HTTP_ACCEPTED != response.statusCode()) {
            LOGGER.error("Error in 'resetPassword', with id={} and username={}", databaseId, username);
            throw processErrors(response);
        }
    }
    
    /**
     * Download SecureBundle.
     * 
     * @param destination
     *      file to save the securebundle
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/generateSecureBundleURL
     */
    public void downloadSecureConnectBundle(String destination) {
        Assert.hasLength(destination, "destination");
        
        // HTTP CALL
        HttpResponse<String> response;
        try {
            response = http()
                    .send(req(PATH_DATABASES + "/" + databaseId + "/secureBundleURL")
                    .POST(BodyPublishers.noBody())
                    .build(), BodyHandlers.ofString()); 
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        
        // If not 200, specializing errors
        if (HttpURLConnection.HTTP_OK != response.statusCode()) {
            LOGGER.error("Error in 'downloadSecureConnectBundle', with id={}", databaseId);
            throw processErrors(response);
        }
        
        // Download binary in target folder
        try {
             Utils.downloadFile((String) om()
                  .readValue(response.body(), Map.class)
                  .get("downloadURL"), destination);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
}
