package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.streaming.domain.Statistics;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Access metrics on a tenant.
 */
public class TenantStatsClient extends AbstractApiClient {

    /** Load Database responses. */
    private static final TypeReference<Map<String, Statistics>> TYPE_LIST_STATISTICS =
            new TypeReference<Map<String, Statistics>>(){};

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
    public TenantStatsClient(String token, String tenantId) {
        super(token);
        Assert.hasLength(tenantId, "tenantId");
        // Test Db exists
        this.tenant = new AstraStreamingClient(token).get(tenantId);
    }

    /**
     * Retrieve Statistics for all namespaces.
     *
     * @return
     *      statistics
     */
    public Stream<Statistics> namespaces() {
        return JsonUtils
                .unmarshallType(
                        HttpClientWrapper.getInstance().GET_PULSAR(getEndpointStatisticsNamespaces(),
                        tenant.getPulsarToken(), tenant.getClusterName(),
                        tenant.getOrganizationId().toString()).getBody(), TYPE_LIST_STATISTICS)
                .values()
                .stream();
    }

    /**
     * Retrieve Statistics for one namespace.
     *
     * @param namespace
     *      current pulsar namespace
     * @return
     *      statistics
     */
    public Optional<Statistics> namespace(String namespace) {
        Map<String, Statistics> map = JsonUtils
                .unmarshallType(
                        HttpClientWrapper
                                .getInstance()
                                .GET_PULSAR(
                                    getEndpointStatisticsNamespaces() + "/" + namespace,
                                    tenant.getPulsarToken(), tenant.getClusterName(),
                                    tenant.getOrganizationId().toString())
                                .getBody(), TYPE_LIST_STATISTICS);
        return Optional.ofNullable(map.get(tenant.getTenantName() + "/" + namespace));
    }

    /**
     * Retrieve Statistics for all topics.
     *
     * @return
     *      statistics
     */
    public Stream<Statistics> topics() {
        return JsonUtils
                .unmarshallType(
                        HttpClientWrapper.getInstance().GET_PULSAR(getEndpointStatisticsTopics(),
                                tenant.getPulsarToken(), tenant.getClusterName(),
                                tenant.getOrganizationId().toString()).getBody(), TYPE_LIST_STATISTICS)
                .values()
                .stream();
    }

    /**
     * Retrieve Statistics for topics of a namespace
     *
     * @param namespace
     *      current pulsar namespace
     * @return
     *      statistics
     */
    public Stream<Statistics> topics(String namespace) {
        return JsonUtils
                .unmarshallType(
                        HttpClientWrapper.getInstance().GET_PULSAR(getEndpointStatisticsTopics() + "/" + namespace,
                                tenant.getPulsarToken(), tenant.getClusterName(),
                                tenant.getOrganizationId().toString()).getBody(), TYPE_LIST_STATISTICS)
                .values()
                .stream();
    }

    /**
     * Endpoint for admin
     *
     * @return
     *      database endpoint
     */
    public String getEndpointStreamingAdminV2() {
        return "https://" + tenant.getClusterName() + ".api.streaming.datastax.com/admin/v2";
    }

    /**
     * Endpoint for namespace statistics.
     *
     * @return
     *      endpoint
     */
    public String getEndpointStatisticsNamespaces() {
        return getEndpointStreamingAdminV2() + "/stats/namespaces/" + tenant.getTenantName();
    }

    /**
     * Endpoint for topics statistics.
     *
     * @return
     *      endpoint
     */
    public String getEndpointStatisticsTopics() {
        return getEndpointStreamingAdminV2() + "/stats/topics/" +  tenant.getTenantName();
    }

}
