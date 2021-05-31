package com.datastax.astra.sdk.streaming;

import java.net.HttpURLConnection;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.utils.ApiDevopsSupport;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Group resources of streaming (tenants, providers).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StreamingClient extends ApiDevopsSupport {

    /** Constants. */
    public static final String PATH_STREAMING  = "/streaming";
    public static final String PATH_TENANTS    = "/tenants";
    public static final String PATH_PROVIDERS  = "/providers";
    
    // MAP TENANT
    private Map<String, TenantClient> cacheTenants = new HashMap<>();
    
    /**
     * Full constructor.
     * @param token
     *      authenticated user
     */
    public StreamingClient(String token) {
       super(token);
    }
    
    /**
     * Operations on tenants.
     * 
     * @param tenantName
     *      current tenant identifier
     * @return
     *      client specialized for the tenant
     */
    public TenantClient tenant(String tenantName) {
        Assert.hasLength(tenantName, "tenantName");
        if (!cacheTenants.containsKey(tenantName)) {
            cacheTenants.put(tenantName, new TenantClient(bearerAuthToken, tenantName));
        }
        return cacheTenants.get(tenantName); 
    }
    
    /**
     * List tenants in the current instance.
     * 
     * @return
     *      list of tenants.
     */
    public Stream<Tenant> tenants() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = http()
                    .send(req(PATH_STREAMING + PATH_TENANTS)
                    .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return om()
                        .readValue(res.body(), new TypeReference<List<Tenant>>(){})
                        .stream();
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        LOGGER.error("Error in 'Tenants'");
        throw processErrors(res);
    }
    
    /**
     * Syntax sugar to help
     *
     * @param ct
     *      creation request for tenant
     */
    public void createTenant(CreateTenant ct) {
        tenant(ct.getTenantName()).create(ct);
    }
    
    /**
     * Operations on providers.
     * 
     * @return
     *      list of cloud providers and regions
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> providers() {
        HttpResponse<String> res;
        try {
            // Invocation (no marshalling yet)
            res = http()
                    .send(req(PATH_STREAMING + PATH_PROVIDERS)
                    .GET().build(), BodyHandlers.ofString());
            if (HttpURLConnection.HTTP_OK == res.statusCode()) {
                return om()
                        .readValue(res.body(), Map.class);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        LOGGER.error("Error in 'providers'");
        throw processErrors(res);
    }
}
