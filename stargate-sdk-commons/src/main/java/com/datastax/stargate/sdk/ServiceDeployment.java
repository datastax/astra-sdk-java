package com.datastax.stargate.sdk;

import java.util.HashMap;
import java.util.Map;

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
     * Full constructor
     * @param datacenters
     *      list of datacenter
     * @param localDc
     *      local datacenter
     */
    public ServiceDeployment(Map<String, ServiceDatacenter<SERVICE>> datacenters, String localDc) {
        this.datacenters = datacenters;
        this.localDc = localDc;
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
     * @param s
     *      current service
     * @return
     *      deployment reference
     */
    public ServiceDeployment<SERVICE> addService(String dcName, SERVICE s) {
        addDatacenter(dcName);
        datacenters.get(dcName).addService(s);
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
