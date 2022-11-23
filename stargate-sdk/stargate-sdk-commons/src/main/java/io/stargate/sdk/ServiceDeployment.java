package io.stargate.sdk;

import io.stargate.sdk.api.TokenProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The http client implements:
 * - load balancing in healthy nodes of the same datacenter
 * - Fail-over across datacenter when no node available
 */
public class ServiceDeployment<SERVICE extends Service> {

    /**
     * List of clients to interact with Nodes with others API (REST, GRAPHQL, ...).
     *
     * If multiple datacenter provided the datacenter name will use as an EXECUTION PROFILE
     * to SWITCH contact points or cloud secure bundle.
     **/
    private Map<String, ServiceDatacenter<SERVICE>> datacenters = new HashMap<>();

    /**
     * Preferred datacenter.
     */
    private String localDc;

    /**
     * Default constructor.
     */
    public ServiceDeployment() {}

    /**
     * Constructor with dcs.
     *
     * @param datacenters
     *      list of datacenter
     */
    @SafeVarargs
    public ServiceDeployment(ServiceDatacenter<SERVICE>... datacenters) {
        if (datacenters != null) {
            this.datacenters = Arrays
                    .stream(datacenters)
                    .collect(Collectors.toMap(ServiceDatacenter::getId, Function.identity()));
        }
        if (this.datacenters.size() == 1) {
            this.localDc = this.datacenters.keySet().iterator().next();
        }
    }

    /**
     * Builder Pattern.
     *
     * @param datacenters
     *      list of dc
     * @return
     *      current reference
     */
    public ServiceDeployment<SERVICE> setDatacenters(Map<String, ServiceDatacenter<SERVICE>> datacenters) {
        this.datacenters = datacenters;
        return this;
    }


    /**
     * design the local datacenter.
     *
     * @param localDc
     *      local datacenter
     * @return
     *      service deployment
     */
    public ServiceDeployment<SERVICE> setLocalDc(String localDc) {
        this.localDc = localDc;
        return this;
    }

    /**
     * Builder pattern.
     *
     * @param dcName
     *      dc name
     * @return
     *     current reference
     */
    public ServiceDeployment<SERVICE> addDatacenter(String dcName) {
        if (!datacenters.containsKey(dcName)) {
            datacenters.put(dcName, new ServiceDatacenter<>(dcName));
        }
        if (localDc == null) setLocalDc(dcName);
        return this;
    }

    /**
     * Builder pattern.
     *
     * @param dc
     *      datacenter
     * @return
     *     current reference
     */
    public ServiceDeployment<SERVICE> addDatacenter(ServiceDatacenter<SERVICE> dc) {
        datacenters.put(dc.getId(), dc);
        return this;
    }

    /**
     * Add a new service in the topology.
     *
     * @param dcName
     *      datacenter name
     * @param services
     *      current services
     * @return
     *      deployment reference
     */
    @SafeVarargs
    public final ServiceDeployment<SERVICE> addDatacenterServices(String dcName, SERVICE... services) {
        addDatacenter(dcName);
        Arrays.stream(services)
              .forEach(s -> datacenters.get(dcName).addService(s));
        return this;
    }

    /**
     * Initialized token provider for a dc.
     *
     * @param dcName
     *      datacenter name
     * @param apiTokenProvider
     *      api token provider
     * @return
     *      current deployment
     */
    public final ServiceDeployment<SERVICE> addDatacenterTokenProvider(String dcName, TokenProvider apiTokenProvider) {
        addDatacenter(dcName);
        datacenters.get(dcName).setTokenProvider(apiTokenProvider);
        return this;
    }

    /**
     * Gets datacenters
     *
     * @return value of datacenters
     */
    public Map<String, ServiceDatacenter<SERVICE>> getDatacenters() {
        return datacenters;
    }

    /**
     * Gets localDc
     *
     * @return value of localDc
     */
    public String getLocalDc() {
        return localDc;
    }

}
