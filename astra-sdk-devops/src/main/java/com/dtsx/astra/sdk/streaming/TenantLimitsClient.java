package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.streaming.domain.TenantLimit;
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
     * Constructor.
     *
     * @param token
     *      token
     * @param tenantId
     *      tenantId
     */
    public TenantLimitsClient(String token, String tenantId) {
        super(token);
        Assert.hasLength(tenantId, "tenantId");
        // Test Db exists
        this.tenant = new AstraStreamingClient(token).get(tenantId);
    }

    /**
     * List limits for a tenants.
     *
     * @return
     *      the list of limits
     */
    public Stream<TenantLimit> limits() {
        ApiResponseHttp res = GET(AstraStreamingClient.getEndpointTenant(tenant.getTenantName()) + "/limits");
        return JsonUtils.unmarshallType(res.getBody(), new TypeReference<List<TenantLimit>>(){}).stream();
    }

}
