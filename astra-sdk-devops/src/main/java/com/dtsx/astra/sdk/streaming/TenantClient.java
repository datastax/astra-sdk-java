package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.AstraDevopsApiClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.DatabaseNotFoundException;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.streaming.exception.TenantNotFoundException;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.JsonUtils;

import java.net.HttpURLConnection;
import java.util.Optional;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class TenantClient extends AbstractApiClient {

    /**
     * unique tenant identifier.
     */
    private final String tenantId;

    /**
     * Organization Id.
     */
    private final String organizationId;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param tenantId
     *     unique tenant identifier
     * @param token
     *      authenticated token
     */
    public TenantClient(String token, String tenantId) {
        this(token, AstraEnvironment.PROD, tenantId);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     * @param tenantId
     *      unique tenant identifier
     */
    public TenantClient(String token, AstraEnvironment env, String tenantId) {
        super(token, env);
        Assert.hasLength(tenantId, "tenantId");
        this.tenantId       = tenantId;
        this.organizationId = new AstraDevopsApiClient(token,env).getOrganizationId();
    }

    // ---------------------------------
    // ----       READ              ----
    // ---------------------------------

    /**
     * Retrieve a tenant by its id.
     *
     * @return the tenant if present,
     */
    public Optional<Tenant> find() {
        System.out.println("getEndpointTenant() = " + getEndpointTenant());
        ApiResponseHttp res = GET(getEndpointTenantWithOrganizationId());
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Optional.empty();
        } else {
            return Optional.of(JsonUtils.unmarshallBean(res.getBody(), Tenant.class));
        }
    }

    /**
     * Retrieve tenant or throw error.
     *
     * @return current db or error
     */
    public Tenant get() {
        return find().orElseThrow(() -> new TenantNotFoundException(tenantId));
    }

    /**
     * Evaluate if a tenant exists using the findById method.
     *
     * @return tenant existence
     */
    public boolean exist() {
        return find().isPresent();
    }


    // ---------------------------------
    // ----       Limits            ----
    // ---------------------------------

    /**
     * Access Limits of a tenant
     *
     * @return
     *      cdc component
     */
    public TenantLimitsClient limits() {
        return new TenantLimitsClient(token, tenantId);
    }

    // ---------------------------------
    // ----       CDC               ----
    // ---------------------------------

    /**
     * Access CDC Client.
     *
     * @return
     *      cdc component
     */
    public TenantCdcClient cdc() {
        return new TenantCdcClient(token, tenantId);
    }

    // ---------------------------------
    // ----       Stats             ----
    // ---------------------------------

    /**
     * Access Stats Client.
     *
     * @return
     *      cdc component
     */
    public TenantStatsClient stats() {
        return new TenantStatsClient(token, tenantId);
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
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming/tenants/" + tenantId;
    }

    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointTenantWithOrganizationId() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming/orgs/" + organizationId + "/tenants/" + tenantId;
    }
}
