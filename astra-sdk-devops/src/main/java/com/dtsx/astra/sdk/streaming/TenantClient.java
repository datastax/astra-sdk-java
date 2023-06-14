package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class TenantClient extends AbstractApiClient {

    /**
     * Logger for our Client.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TenantClient.class);

    /**
     * unique tenant identifier.
     */
    private final String tenantId;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param tenantId
     *     unique tenant identifier
     * @param token
     *      authenticated token
     */
    public TenantClient(String token, String tenantId) {
        this(token, ApiLocator.AstraEnvironment.PROD, tenantId);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public TenantClient(String token, ApiLocator.AstraEnvironment env, String tenantId) {
        super(token, env);
        Assert.hasLength(tenantId, "tenantId");
        this.tenantId = tenantId;
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
}
