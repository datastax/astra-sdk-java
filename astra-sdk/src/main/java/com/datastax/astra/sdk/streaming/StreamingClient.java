package com.datastax.astra.sdk.streaming;

import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallBean;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.utils.ApiLocator;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Group resources of streaming (tenants, providers).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StreamingClient {

    /** Constants. */
    public static final String PATH_STREAMING  = "/streaming";
    public static final String PATH_TENANTS    = "/tenants";
    public static final String PATH_PROVIDERS  = "/providers";
    
    // MAP TENANT
    private Map<String, TenantClient> cacheTenants = new HashMap<>();
    
    private static final TypeReference<List<Tenant>> TYPE_LIST_TENANTS = 
            new TypeReference<List<Tenant>>(){};
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** hold a reference to the bearer token. */
    private final String bearerAuthToken;
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param authToken
     *      authenticated token
     */
    public StreamingClient(HttpApisClient client) {
        this.http = client;
        this.bearerAuthToken = client.getToken();
    }
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param authToken
     *      authenticated token
     */
    public StreamingClient(String bearerAuthToken) {
       this.bearerAuthToken = bearerAuthToken;
       this.http = HttpApisClient.getInstance();
       http.setToken(bearerAuthToken);
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
            cacheTenants.put(tenantName, new TenantClient(this, http, tenantName));
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
        ApiResponseHttp res = http.GET(getApiDevopsEndpointTenants());
        return unmarshallType(res.getBody(), TYPE_LIST_TENANTS).stream();
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
        ApiResponseHttp res = http.GET(getApiDevopsEndpointProviders());
        return unmarshallBean(res.getBody(), Map.class);
    }
    
    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointStreaming() {
        return ApiLocator.getApiDevopsEndpoint() + PATH_STREAMING;
    }
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointTenants() {
        return getApiDevopsEndpointStreaming() + PATH_TENANTS;
    }
    
    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointProviders() {
        return getApiDevopsEndpointStreaming() + PATH_PROVIDERS;
    }
    
    /**
     * Access to the current authentication token.
     *
     * @return
     *      authentication token
     */
    public String getToken() {
       return bearerAuthToken;
    }
}
