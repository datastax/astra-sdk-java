package com.datastax.astra.sdk.streaming;

import static com.datastax.stargate.sdk.core.ApiSupport.handleError;

import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Optional;

import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class TenantClient extends ApiDevopsSupport {

    /** Constants. */
    public static final String PATH_TENANT     = "/tenants";
    
    private final String tenantId;
    
    @SuppressWarnings("unused")
    private final String resourceSuffix;
   
    /**
     * Full constructor.
     * 
     * @param token
     *      token
     * @param tenant
     *      tenant
     */
    public TenantClient(String token, String tenant) {
       super(token);
       this.tenantId = tenant;
       this.resourceSuffix = StreamingClient.PATH_STREAMING + PATH_TENANT + "/" + tenantId;
       Assert.hasLength(tenantId, "tenantName");
    }
    
    /**
     * Find a tenant from ids name.
     * 
     * @return 
     *      tenant
     */
    public Optional<Tenant> find() {
        return new StreamingClient(bearerAuthToken)
                        .tenants()
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
     * Create a new tenant.
     *
     * @param ct
     *      tenant creation request
     */
    public void create(CreateTenant ct) {
        // TODO
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
        HttpResponse<String> response;
        try {
            response = http().send(req(resourceSuffix + "/clusters/" + clusterId)
                     .DELETE().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_NO_CONTENT == response.statusCode()) {
                return;
            }
        } catch (Exception e) {
            throw new RuntimeException("Cannot invoke API to delete a tenant", e);
        }
        handleError(response);
    }
    

}
