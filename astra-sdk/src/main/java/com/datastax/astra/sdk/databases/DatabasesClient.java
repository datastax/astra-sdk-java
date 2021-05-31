package com.datastax.astra.sdk.databases;


import java.net.HttpURLConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.datastax.astra.sdk.databases.domain.CloudProviderType;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.databases.domain.DatabaseCreationRequest;
import com.datastax.astra.sdk.databases.domain.DatabaseFilter;
import com.datastax.astra.sdk.databases.domain.DatabaseFilter.Include;
import com.datastax.astra.sdk.databases.domain.DatabaseRegion;
import com.datastax.astra.sdk.databases.domain.DatabaseTierType;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client for the Astra Devops API.
 * 
 * The JDK11 client http is used and as such jdk11+ is required
 * 
 * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class DatabasesClient extends ApiDevopsSupport {
    
    public static final String PATH_DATABASES = "databases";
    public static final String PATH_REGIONS   = "availableRegions";
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param authToken
     *      authenticated token
     */
    public DatabasesClient(String authToken) {
       super(authToken);
    }
     
    /**
     * Returns supported regions and availability for a given user and organization
     * 
     * @return
     *      supported regions and availability 
     */
    public Stream<DatabaseRegion> regions() {
        HttpResponse<String> res;
        try {
           // Invocation with no marshalling
           res = getHttpClient().send(
                   startRequest(PATH_REGIONS).GET().build(), 
                    BodyHandlers.ofString());
            
            // Parsing as list of Bean if OK
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return  getObjectMapper().readValue(res.body(),
                        new TypeReference<List<DatabaseRegion>>(){})
                                   .stream();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot list regions", e);
        }
        
        LOGGER.error("Error in 'availableRegions'");
        throw processErrors(res);
    }
    
    /**
     * Map regions from plain list to Tier/Cloud/Region Structure.
     *
     * @return
     *      regions organized by cloud providers
     */
    public Map <DatabaseTierType, Map<CloudProviderType,List<DatabaseRegion>>> regionsMap() {
        Map<DatabaseTierType, Map<CloudProviderType,List<DatabaseRegion>>> m = new HashMap<>();
        regions().forEach(dar -> {
            if (!m.containsKey(dar.getTier())) {
                m.put(dar.getTier(), new HashMap<CloudProviderType,List<DatabaseRegion>>());
            }
            if (!m.get(dar.getTier()).containsKey(dar.getCloudProvider())) {
                m.get(dar.getTier()).put(dar.getCloudProvider(), new ArrayList<DatabaseRegion>());
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
    public Stream<Database> databases() {
        return searchDatabases(DatabaseFilter.builder()
                .include(Include.ALL)
                .provider(CloudProviderType.ALL)
                .limit(1000)
                .build());
    }
    
    /**
     * Default Filter to find databases.
     *
     * @return
     *      list of non terminated db
     */
    public Stream<Database> databasesNonTerminated() {
        return searchDatabases(DatabaseFilter.builder().build());
    }
    
    /**
     * Retrieve list of all Databases of the account and filter on name
     * 
     * @param name
     *          a database name
     * @return
     *          list of db matching the criteria
     */
    public Stream<Database> databasesNonTerminatedByName(String name) {
        Assert.hasLength(name, "Database name");
        return databasesNonTerminated().filter(db->name.equals(db.getInfo().getName()));
    }
    
    /**
     * Find Databases matching the provided filter.
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/listDatabases
     * 
     * @param filter
     *      filter to search for db
     * @return
     *      list of db
     */
    public Stream<Database> searchDatabases(DatabaseFilter filter) {
        Assert.notNull(filter, "filter");
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = getHttpClient()
                    .send(startRequest(filter.urlParams())
                    .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return getObjectMapper()
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
     * Use the database part of the API.
     * 
     * @param dbId
     *          unique identifieer id
     * @return
     */
    public DatabaseClient database(String dbId) {
        Assert.hasLength(dbId, "Database Id should not be null nor empty");
        return new DatabaseClient(bearerAuthToken, dbId);
    }
    
    /**
     * Create a database base on some parameters.
     * 
     * @param dbCreationRequest
     *      creation request with tier and capacity unit
     * @return
     *      the new instance id.
     * 
     * https://docs.datastax.com/en/astra/docs/_attachments/devopsv1.html#operation/createDatabase
     */
    public String createDatabase(DatabaseCreationRequest dbCreationRequest) {
        Assert.notNull(dbCreationRequest, "Database creation request");
        HttpResponse<String> response ;
        try {
           response = getHttpClient()
                    .send(startRequest(PATH_DATABASES)
                    .POST(BodyPublishers.ofString(
                            getObjectMapper().writeValueAsString(dbCreationRequest)))
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

   
    

}
