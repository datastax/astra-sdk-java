package com.datastax.stargate.sdk;

import java.util.ArrayList;
import java.util.List;

import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.loadbalancer.LoadBalancingPolicy;
import com.datastax.stargate.sdk.loadbalancer.LoadBalancingResource;
import com.datastax.stargate.sdk.loadbalancer.Loadbalancer;

/**
 * CqlSession and Endpoints are associated to a dedicated DataCenter. The failover
 * will be perform by the SDK. As such a StargateClient will have multiple DC.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClientDC {
    
    /** datacenter name. */
    private final String datacenterName;
    
    /** Multiple Nodes of Stargate share their token in a Cassandra table to can be positionned as DC level. */
    private final ApiTokenProvider tokenProvider;

    /** Inside a single datacenter i will have multiple Stargate Nodes. We will load-balance our queries among those instances. */
    private Loadbalancer<StargateClientNode> stargateNodesLB;
    
    /**
     * Full constructor.
     *
     * @param dcName
     *      current dc name
     * @param tokenProvider
     *      token provider for the DC
     * @param nodes
     *      list of nodes
     */
    public StargateClientDC(String dcName, ApiTokenProvider tokenProvider, List<StargateClientNode> nodes) {
       this.tokenProvider   = tokenProvider;
       this.datacenterName  = dcName;
       // Creating explicitely the LB resources to provide Identifiers 
       List<LoadBalancingResource<StargateClientNode>> lbs = new ArrayList<>();
       for (StargateClientNode n : nodes) {
           LoadBalancingResource < StargateClientNode > lbRsc = new LoadBalancingResource<>(n);
           lbRsc.setId(n.getNodeName());
           lbRsc.setDefaultWeigth(100 / nodes.size());
           lbRsc.setCurrentWeight(lbRsc.getDefaultWeigth());
           lbRsc.setAvailable(true);
           lbRsc.setNbUse(0);
           lbs.add(lbRsc);
       }
       this.stargateNodesLB = new Loadbalancer<StargateClientNode>(LoadBalancingPolicy.ROUND_ROBIN, lbs);
    }
   
    

    /**
     * Getter accessor for attribute 'stargateNodesLB'.
     *
     * @return
     *       current value of 'stargateNodesLB'
     */
    public Loadbalancer<StargateClientNode> getStargateNodesLB() {
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
    public ApiTokenProvider getTokenProvider() {
        return tokenProvider;
    }
    
   

}
