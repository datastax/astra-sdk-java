package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.HttpClientWrapper;
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
public class StatsClient {

    /** Load Database responses. */
    private static final TypeReference<Map<String, Statistics>> TYPE_LIST_STATISTICS =
            new TypeReference<Map<String, Statistics>>(){};

    /** Access tenant. */
    private final Tenant tenant;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /**
     * Default constructor.
     *
     * @param tenant
     *          tenant client
     */
    public StatsClient(Tenant tenant) {
        this.tenant = tenant;
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
                        http.GET_PULSAR(getEndpointStatisticsNamespaces(),
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
                        http.GET_PULSAR(getEndpointStatisticsNamespaces() + "/" + namespace,
                                tenant.getPulsarToken(), tenant.getClusterName(),
                                tenant.getOrganizationId().toString()).getBody(), TYPE_LIST_STATISTICS);
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
                        http.GET_PULSAR(getEndpointStatisticsTopics(),
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
                        http.GET_PULSAR(getEndpointStatisticsTopics() + "/" + namespace,
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
