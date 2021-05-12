package com.datastax.astra.sdk.streaming;

import java.util.Optional;

import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;

public class TenantClient extends ApiDevopsSupport {

    /** Constants. */
    public static final String PATH_TENANT     = "/tenants";
    
    private final String tenantId;
    
    private final String resourceSuffix;
    
    /**
     * Full constructor.
     */
    public TenantClient(String token, String tenant) {
       super(token);
       this.tenantId = tenant;
       this.resourceSuffix = StreamingClient.PATH_STREAMING + PATH_TENANT + "/" + tenantId;
       Assert.hasLength(tenantId, "tenantName");
    }
    
    public Optional<Tenant> find() {
        return null;
    }
    
    public void create(CreateTenant ct) {
        // todo
    }
    
    // why this clusterId ?
    public void delete(String clusterId) {
        
    }
    

}
