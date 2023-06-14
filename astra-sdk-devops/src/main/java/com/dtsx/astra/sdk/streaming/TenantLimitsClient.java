package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.streaming.domain.TenantLimit;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.stream.Stream;

/**
 * Client to operates on limits of one cluster.
 */
public class TenantLimitsClient extends AbstractApiClient {

    /**
     * Unique db identifier.
     */
    private final Tenant tenant;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param tenantId
     *     unique tenant identifier
     * @param token
     *      authenticated token
     */
    public TenantLimitsClient(String token, String tenantId) {
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
    public TenantLimitsClient(String token, ApiLocator.AstraEnvironment env, String tenantId) {
        super(token, env);
        Assert.hasLength(tenantId, "tenantId");
        this.tenant = new AstraStreamingClient(token, env).get(tenantId);
    }

    /**
     * List limits for a tenants.
     *
     * @return
     *      the list of limits
     */
    public Stream<TenantLimit> limits() {
        ApiResponseHttp res = GET(getEndpointTenantLimits());
        return JsonUtils.unmarshallType(res.getBody(), new TypeReference<List<TenantLimit>>(){}).stream();
    }

    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointTenantLimits() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming/tenants/" + tenant.getTenantName() + "/limits";
    }

}
