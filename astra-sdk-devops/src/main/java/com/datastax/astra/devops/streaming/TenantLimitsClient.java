package com.datastax.astra.devops.streaming;

import com.datastax.astra.devops.streaming.domain.Tenant;
import com.datastax.astra.devops.streaming.domain.TenantLimit;
import com.datastax.astra.devops.utils.Assert;
import com.datastax.astra.devops.AbstractApiClient;
import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.ApiResponseHttp;
import com.datastax.astra.devops.utils.AstraEnvironment;
import com.datastax.astra.devops.utils.JsonUtils;
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
    public TenantLimitsClient(String token, AstraEnvironment env, String tenantId) {
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
