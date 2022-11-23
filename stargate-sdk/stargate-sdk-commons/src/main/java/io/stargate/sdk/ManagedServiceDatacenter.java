package io.stargate.sdk;

import io.stargate.sdk.api.TokenProvider;
import io.stargate.sdk.loadbalancer.LoadBalancingPolicy;
import io.stargate.sdk.loadbalancer.LoadBalancingResource;
import io.stargate.sdk.loadbalancer.Loadbalancer;

import java.util.ArrayList;
import java.util.List;

/**
 * CqlSession and Endpoints are associated to a dedicated DataCenter. The fail-over
 * will be performed by the SDK. As such a StargateClient will have multiple DC.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ManagedServiceDatacenter<SERVICE extends Service> {

    /** mark the dc as unavailable. */
    private boolean available = true;

    /** datacenter name. */
    private final String datacenterName;
    
    /** Multiple Nodes of Stargate share their token in a Cassandra table to can be positioned as DC level. */
    private final TokenProvider tokenProvider;

    /** Inside a single datacenter I will have multiple Stargate Nodes. We will load-balance our queries among those instances. */
    private final Loadbalancer<SERVICE> stargateNodesLB;

    /**
     * Gets available
     *
     * @return value of available
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Gets available
     *
     * @return value of available
     */
    public boolean isNotAvailable() {
        return !available;
    }

    /**
     * Full constructor.
     *
     * @param sc
     *      current dc name
     */
    public ManagedServiceDatacenter(ServiceDatacenter<SERVICE> sc) {
       this.tokenProvider   = sc.getTokenProvider();
       this.datacenterName  = sc.getId();
       // Creating explicitly the LB resources to provide Identifiers
       List<LoadBalancingResource<SERVICE>> lbs = new ArrayList<>();
       for (SERVICE n : sc.getServices().values()) {
           LoadBalancingResource<SERVICE> lbRsc = new LoadBalancingResource<>(n);
           lbRsc.setId(n.getId());
           lbRsc.setDefaultWeight(100d / sc.getServices().values().size());
           lbRsc.setCurrentWeight(lbRsc.getDefaultWeight());
           lbRsc.setAvailable(true);
           lbRsc.setNbUse(0);
           lbs.add(lbRsc);
       }
       this.stargateNodesLB = new Loadbalancer<>(LoadBalancingPolicy.ROUND_ROBIN, lbs);
    }

    /**
     * Set value for available
     *
     * @param available new value for available
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Getter accessor for attribute 'stargateNodesLB'.
     *
     * @return
     *       current value of 'stargateNodesLB'
     */
    public Loadbalancer<SERVICE> getStargateNodesLB() {
        return stargateNodesLB;
    }

    /**
     * Getter accessor for attribute 'datacenterName'.
     *
     * @return
     *       current value of 'datacenterName'
     */
    public String getDatacenterName() {
        return datacenterName;
    }

    /**
     * Getter accessor for attribute 'tokenProvider'.
     *
     * @return
     *       current value of 'tokenProvider'
     */
    public TokenProvider getTokenProvider() {
        return tokenProvider;
    }

    @Override
    public String toString() {
        return "ManagedServiceDatacenter{" +
                "available=" + available +
                ", datacenterName='" + datacenterName + '\'' +
                ", tokenProvider=" + tokenProvider +
                ", stargateNodesLB=" + stargateNodesLB +
                '}';
    }
}
