package com.datastax.stargate.sdk;

import com.datastax.stargate.sdk.loadbalancer.LoadBalancingResource;
import com.datastax.stargate.sdk.loadbalancer.NoneResourceAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The http client implements:
 * - load balancing in healthy nodes of the same datacenter
 * - Fail-over across datacenter when no node available
 */
public class ManagedServiceDeployment<SERVICE extends Service> {

    /** Logger the client side LB and fail over. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagedServiceDeployment.class);

    /**
     * List of clients to interact with Nodes with others API (REST, GRAPHQL, ...).
     *
     * If multiple datacenter provided the datacenter name will use as an EXECUTION PROFILE
     * to SWITCH contact points or cloud secure bundle.
     **/
    private final Map<String, ManagedServiceDatacenter<SERVICE>> datacenters = new HashMap<>();

    /** Working with a currentDC. */
    private String currentDatacenter;

    /**
     * Initialize a managed topology from its definition.
     * @param sDeploy
     *      deployment
     */
    public ManagedServiceDeployment(ServiceDeployment<SERVICE> sDeploy) {
        if (sDeploy !=null) {
            sDeploy.getDatacenters()
                   .values().stream()
                    .map(ManagedServiceDatacenter::new)
                    .forEach(mdc -> datacenters.put(mdc.getDatacenterName(), mdc));
            currentDatacenter = sDeploy.getLocalDc();
        }
        if (currentDatacenter == null)
            currentDatacenter = datacenters.keySet().iterator().next();
    }

    // ------------------------------------------------
    // -- Load Balancing & fail-over    ---------------
    // ------------------------------------------------

    /**
     * Implementing fail-over cross DC for API and CqlSession when available.
     *
     * @param datacenter
     *      target datacenter
     */
    public void useDataCenter(String datacenter) {
        if (!getDatacenters().containsKey(datacenter)) {
            throw new IllegalArgumentException("'" + datacenter + "' is not a known datacenter please provides one "
                    + "in " + getDatacenters().keySet());
        }
        LOGGER.info("Using DataCenter [" + datacenter + "]");
        this.currentDatacenter = datacenter;
    }
    /**
     * Provide the current Datacenter client.
     *
     * @return
     *      the client for the current DC
     */
    public ManagedServiceDatacenter<SERVICE> getLocalDatacenterClient() {
        if (!datacenters.containsKey(currentDatacenter)) {
            throw new IllegalStateException("Cannot retrieve datacenter [" + currentDatacenter + "] from definition, check cluster topology");
        }
        return datacenters.get(currentDatacenter);
    }

    /**
     * Get the ApiTokenProvider of current DC.
     * The resource should have been picked first and localDC set in StargateClient.
     *
     * @return
     *      a token
     */
    public String lookupToken() {
        return getLocalDatacenterClient() // Retrieve the current Dc based on localDc property
                .getTokenProvider()       // Retrieve the ApiTokenProvider for the DC (could be custom)
                .getToken();              // Ask the token provider to supply a token
    }

    /**
     * Retrieve an Api Rest URL still available in current DC or fail-over.
     *
     * @return
     *      an APi Rest URL available
     */
    public LoadBalancingResource<SERVICE> lookupStargateNode() {
        return getLocalDatacenterClient()   // Retrieve the current Dc based on localDc property
                .getStargateNodesLB()       // Retrieve the load-balancer for node
                .getLoadBalancedResource(); // Get a resource, idea is to invalidate resource if KO
    }

    /**
     * Failing over from one DC to another
     */
    public void failOverDatacenter() {
        getDatacenters().get(currentDatacenter).setAvailable(false);
        Set<String> availableDc = getAvailableDatacenters();
        if (availableDc.size() == 0) {
            throw new NoneResourceAvailableException("No Resource available anymore on ");
        }
        // Pick one and fail over
        String newDc = availableDc.iterator().next();
        LOGGER.info("Fail-over from {} to {}", currentDatacenter, newDc);
        useDataCenter(newDc);
    }

    /**
     * Failing over from one Stargate node to another.
     *
     * @param lb
     *      current resource to be disabled
     * @param t
     *      source error
     */
    public void failOverStargateNode(LoadBalancingResource<SERVICE> lb, Throwable t) {
        getLocalDatacenterClient().getStargateNodesLB().handleComponentError(lb, t);
    }

    /**
     * Gets datacenters
     *
     * @return value of datacenters
     */
    public Map<String, ManagedServiceDatacenter<SERVICE>> getDatacenters() {
        return datacenters;
    }

    /**
     * Get available DC.
     *
     * @return
     *      available dc
     */
    public Set<String> getUnavailableDatacenters() {
        return datacenters.values()
                   .stream()
                   .filter(ManagedServiceDatacenter::isNotAvailable)
                   .map(ManagedServiceDatacenter::getDatacenterName)
                   .collect(Collectors.toSet());
    }

    /**
     * Get available DC.
     *
     * @return
     *      available dc
     */
    public Set<String> getAvailableDatacenters() {
        return datacenters.values()
                .stream()
                .filter(ManagedServiceDatacenter::isAvailable)
                .map(ManagedServiceDatacenter::getDatacenterName)
                .collect(Collectors.toSet());
    }

}
