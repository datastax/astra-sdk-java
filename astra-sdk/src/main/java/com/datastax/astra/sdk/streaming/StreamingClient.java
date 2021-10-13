package com.datastax.astra.sdk.streaming;

import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallBean;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.datastax.astra.sdk.streaming.domain.Cluster;
import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.utils.ApiLocator;
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
    public static final String PATH_CLUSTERS   = "/clusters";
    
    /** Marshalling beans */
    private static final TypeReference<List<Tenant>> TYPE_LIST_TENANTS = 
            new TypeReference<List<Tenant>>(){};
            
    private static final TypeReference<List<Cluster>> TYPE_LIST_CLUSTERS = 
                    new TypeReference<List<Cluster>>(){};
    
    /** Singletong for tenants. */
    private Map<String, TenantClient> cacheTenants = new HashMap<>();
    
    /** hold a reference to the bearer token. */
    protected final String bearerAuthToken;
    
    /** Syntax sugar. */
    private HttpApisClient http = HttpApisClient.getInstance();
    
    /**
     * As immutable object use builder to initiate the object.
     * 
     * @param bearerAuthToken
     *      authenticated token
     */
    public StreamingClient(String bearerAuthToken) {
       this.bearerAuthToken = bearerAuthToken;
       Assert.hasLength(bearerAuthToken, "bearerAuthToken");
    } 
    
    // ---------------------------------
    // ----         Tenants         ----
    // ---------------------------------
    
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
            cacheTenants.put(tenantName, new TenantClient(this, tenantName));
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
        return unmarshallType(http
                .GET(getApiDevopsEndpointTenants(), bearerAuthToken)
                .getBody(), TYPE_LIST_TENANTS)
                .stream();
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
    
    // ---------------------------------
    // ----       Providers         ----
    // ---------------------------------
    
    /**
     * Operations on providers.
     * 
     * @return
     *      list of cloud providers and regions
     */
    @SuppressWarnings("unchecked")
    public Map<String, List<String>> providers() {
        return unmarshallBean(http
                .GET(getApiDevopsEndpointProviders(), bearerAuthToken)
                .getBody(), Map.class);
    }
    
    // ---------------------------------
    // ----       Clusters          ----
    // ---------------------------------
    
    /**
     * Operations on clusters.
     * 
     * @return
     *      list  clusters.
     */
    public Stream<Cluster> clusters() {
        return unmarshallType(http
                .GET(getApiDevopsEndpointClusters(), bearerAuthToken)
                .getBody(), TYPE_LIST_CLUSTERS)
                .stream();
    }
    
    /**
     * Operations on tenants.
     * 
     * @param clusterName
     *      current cluster identifier
     * @return
     *      client specialized for the tenant
     */
    public ClusterClient cluster(String clusterName) {
        Assert.hasLength(clusterName, "tenantName");
        return new ClusterClient(this, clusterName); 
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
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointClusters() {
        return getApiDevopsEndpointStreaming() + PATH_CLUSTERS;
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
