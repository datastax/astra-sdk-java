package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.streaming.exception.TenantNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.dtsx.astra.sdk.streaming.domain.CreateTenant;
import com.dtsx.astra.sdk.streaming.domain.Tenant;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Group resources of streaming (tenants, providers).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraStreamingClient extends AbstractApiClient {

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public AstraStreamingClient(String token) {
        this(token, ApiLocator.AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public AstraStreamingClient(String token, ApiLocator.AstraEnvironment env) {
        super(token, env);
    }

    /**
     * List tenants in the current instance.
     * 
     * @return
     *      list of tenants.
     */
    public Stream<Tenant> findAll() {
        return JsonUtils
                .unmarshallType(
                        GET(getApiDevopsEndpointTenants()).getBody(),
                        new TypeReference<List<Tenant>>(){})
                .stream();
    }

    /**
     * Find a tenant from ids name.
     *
     * @param tenantName
     *      name of the tenant
     * @return
     *      tenant
     */
    public Optional<Tenant> find(String tenantName) {
        return findAll()
                .filter(t -> t.getTenantName().equalsIgnoreCase(tenantName))
                .findFirst();
    }

    /**
     * Assess a tenant exist and retrieve information.
     *
     * @param tenantName
     *      name of the tenant
     * @return
     *      tenant reference
     */
    public Tenant get(String tenantName) {
        return find(tenantName).orElseThrow(() -> new TenantNotFoundException(tenantName));
    }

    /**
     * Syntax sugar to help
     *
     * @param ct
     *      creation request for tenant
     */
    public void create(CreateTenant ct) {
        Assert.notNull(ct, "Create Tenant request");
        POST(getApiDevopsEndpointTenants(), JsonUtils.marshall(ct));
    }

    /**
     * Deleting a tenant and cluster.
     *
     * @param tenantName
     *      name of the tenant
     */
    public void delete(String tenantName) {
        Tenant tenant = get(tenantName);
        DELETE(getEndpointCluster(tenant.getTenantName(), tenant.getClusterName()));
    }

    /**
     * Check if a role is present
     *
     * @param tenantName
     *      name of the tenant
     * @return
     *      if the tenant exist
     */
    public boolean exist(String tenantName) {
        return HEAD(getEndpointTenant(tenantName)).getCode() == HttpURLConnection.HTTP_OK;
    }

    // ---------------------------------
    // ----   Tenant Specifics      ----
    // ---------------------------------

    /**
     * Access methods for a tenant.
     *
     * @param tenantName
     *      current tenant
     * @return
     *      client for a tenant
     */
    public TenantClient tenant(String tenantName) {
        return new TenantClient(token, environment, tenantName);
    }

    // ---------------------------------
    // ----       Clusters         ----
    // ---------------------------------

    /**
     * Operation on Streaming Clusters.
     *
     * @return
     *      streaming cluster client
     */
    public ClustersClient clusters() {
        return new ClustersClient(token, environment);
    }

    // ---------------------------------
    // ----       Providers         ----
    // ---------------------------------

    /**
     * Operation on Streaming Clusters.
     *
     * @return
     *      streaming cluster client
     */
    public ProvidersClient providers() {
        return new ProvidersClient(token, environment);
    }

    // ---------------------------------
    // ----       Regions           ----
    // ---------------------------------

    /**
     * Operation on Streaming regions.
     *
     * @return
     *      streaming cluster client
     */
    public RegionsClient regions() {
        return new RegionsClient(token, environment);
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
    public String getApiDevopsEndpointStreaming() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming";
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getApiDevopsEndpointTenants() {
        return getApiDevopsEndpointStreaming() + "/tenants";
    }

    /**
     * Endpoint to access dbs.
     *
     * @param tenantId
     *      identifier for tenant
     * @return
     *      database endpoint
     */
    public String getEndpointTenant(String tenantId) {
        return getApiDevopsEndpointTenants() + "/" + tenantId;
    }

    /**
     * Endpoint to access cluster.
     *
     * @param tenantName
     *      name of the tenant
     * @param clusterId
     *      identifier for the cluster.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointCluster(String tenantName, String clusterId) {
        return getEndpointTenant(tenantName) + "/clusters/" + clusterId;
    }
}
