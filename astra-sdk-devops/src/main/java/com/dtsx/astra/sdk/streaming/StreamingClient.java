package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.streaming.domain.StreamingRegion;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponse;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.dtsx.astra.sdk.streaming.domain.Cluster;
import com.dtsx.astra.sdk.streaming.domain.CreateTenant;
import com.dtsx.astra.sdk.streaming.domain.Tenant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Group resources of streaming (tenants, providers).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StreamingClient {

    /** URL part.. */
    public static final String PATH_STREAMING  = "/streaming";
    
    /** URL part.. */
    public static final String PATH_TENANTS    = "/tenants";
    
    /** URL part.. */
    public static final String PATH_PROVIDERS  = "/providers";
    
    /** URL part.. */
    public static final String PATH_CLUSTERS   = "/clusters";

    /** Marshalling beans */
    private static final TypeReference<List<Tenant>> TYPE_LIST_TENANTS = 
            new TypeReference<List<Tenant>>(){};

    /** Marshalling beans */
    private static final TypeReference<List<Cluster>> TYPE_LIST_CLUSTERS = 
                    new TypeReference<List<Cluster>>(){};

    // -- List Serverless regions

    /** URL part.. */
    public static final String PATH_REGIONS_SERVERLESS  = "/serverless-regions";

    /** json key. */
    private static final String JSON_ORGANIZATION = "organization";
    /** json key. */
    private static final String JSON_SERVERLESS_REGIONS = "availableServerlessRegions";

    /** Marshalling beans data -> organization -> availableServerlessRegions */
    private static final TypeReference<ApiResponse<Map<String, Map<String, List<StreamingRegion>>>>> TYPE_LIST_REGIONS =
            new TypeReference<ApiResponse<Map<String, Map<String, List<StreamingRegion>>>>>(){};

    /** Singletong for tenants. */
    private final Map<String, TenantClient> cacheTenants = new HashMap<>();
    
    /** hold a reference to the bearer token. */
    protected final String bearerAuthToken;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

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
        return JsonUtils.unmarshallType(http
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
        return JsonUtils.unmarshallBean(http
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
        return JsonUtils.unmarshallType(http
                .GET(getApiDevopsEndpointClusters(), bearerAuthToken)
                .getBody(), TYPE_LIST_CLUSTERS)
                .stream();
    }

    /**
     * Get available serverless for Streaming.
     *
     * @return
     *      serverless regions
     */
    public Stream<StreamingRegion> serverlessRegions() {
        // Invoke api
        Map<String, Map<String, List<StreamingRegion>>> res = JsonUtils.unmarshallType(http
                .GET(getApiDevopsEndpointRegionsServerless(), bearerAuthToken)
                .getBody(), TYPE_LIST_REGIONS).getData();
        if (null != res &&
            null != res.get(JSON_ORGANIZATION) &&
            null != res.get(JSON_ORGANIZATION).get(JSON_SERVERLESS_REGIONS)) {
            return res.get(JSON_ORGANIZATION).get(JSON_SERVERLESS_REGIONS).stream();
        }
        return Stream.of();
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
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public static String getApiDevopsEndpointRegionsServerless() {
        return getApiDevopsEndpointStreaming() + PATH_REGIONS_SERVERLESS;
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
