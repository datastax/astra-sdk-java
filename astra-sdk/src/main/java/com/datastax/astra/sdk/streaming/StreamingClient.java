package com.datastax.astra.sdk.streaming;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;

/**
 * Group resources of streaming (tenants, providers).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StreamingClient extends ApiDevopsSupport {

    /** Constants. */
    public static final String PATH_STREAMING  = "/streaming";
    
    // MAP TENANT
    private Map<String, TenantClient> cacheTenants = new HashMap<>();
    
    /**
     * Full constructor.
     */
    public StreamingClient(String token) {
       super(token);
    }
    
    /**
     * Operations on tenants
     */
    public TenantClient tenant(String tenantName) {
        Assert.hasLength(tenantName, "tenantName");
        if (!cacheTenants.containsKey(tenantName)) {
            cacheTenants.put(tenantName, new TenantClient(bearerAuthToken, tenantName));
        }
        return cacheTenants.get(tenantName); 
    }
    
    public Stream<Tenant> tenants() {
        System.out.println(bearerAuthToken);
        return null;
    }
    
    /**
     * Syntax sugar to help
     */
    public void createTenant(CreateTenant ct) {
        tenant(ct.getTenantName()).create(ct);
    }
    
    /**
     * Operations on providers
     */
    public Map<String, List<String>> providers() {
        return null;
    }
}
