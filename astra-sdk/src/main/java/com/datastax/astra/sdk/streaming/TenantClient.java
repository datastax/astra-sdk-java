package com.datastax.astra.sdk.streaming;

import java.util.Optional;

import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class TenantClient {
    
    /** Tenant Identifier. */
    private final String tenantId;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Streaming client. */
    private final StreamingClient streamClient;
   
    /**
     * Default constructor.
     *
     * @param bearerAuthToken
     *          authentication token
     * @param databaseId
     *          uniique database identifier
     */
    public TenantClient(StreamingClient client, HttpApisClient http, String tenantId) {
       this.streamClient = client;
       this.http         = http;
       this.tenantId  = tenantId;
       Assert.hasLength(tenantId, "tenantId");
    }
    
    /**
     * Find a tenant from ids name.
     * 
     * @return 
     *      tenant
     */
    public Optional<Tenant> find() {
        return streamClient.tenants()
                           .filter(t -> t.getTenantName().equalsIgnoreCase(tenantId))
                           .findFirst();
    }
    
    /**
     * Check if a role is present
     * 
     * @return
     *      if the tenant exist
     */
    public boolean exist() {
        return find().isPresent();
    }
    
    /**
     * TODO Create a new tenant.
     *
     * @param ct
     *      tenant creation request
     */
    public void create(CreateTenant ct) {
        throw new RuntimeException("This function is not yet implemented");
    }
    
    /**
     * Deleting a tenant and cluster.
     *
     * @param clusterId
     *      cluster identifier
     */
    public void delete(String clusterId) {
        if (!exist()) {
            throw new RuntimeException("Tenant '"+ tenantId + "' has not been found");
        }
        http.DELETE(getEndpointTenant() + "/clusters/" + clusterId);
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointTenant() {
        return getEndpointTenant(tenantId);
    }
    
    /**
     * Endpoint to access dbs (static)
     *
     * @param dbId
     *      database identifer
     * @return
     *      database endpoint
     */
    public static String getEndpointTenant(String tenant) {
        return StreamingClient.getApiDevopsEndpointTenants() + "/" + tenant;
    }
    
    
    

}
