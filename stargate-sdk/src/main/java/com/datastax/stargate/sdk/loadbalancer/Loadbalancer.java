package com.datastax.stargate.sdk.loadbalancer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic implementation of a client-side load balancer. It will handle
 * multiple algorithms: RANDOM, LOAD BALANCING, WEIGHT BALANCING.
 * 
 * @param <RSC>
 *     resources
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Loadbalancer < RSC >  {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Loadbalancer.class);
   
    /** Constants to compute percentage. **/
    private static final double HUNDRED = 100.0;

    /** Constants for millis. **/
    private static final int THOUSAND   = 1000;

    /** Invocation count since last failure. **/
    private double totalCount = 0;

    /** Total number of Api call(s). **/
    private double globalCount = 0;

    /** How many resources are currently available. **/
    private int unavailableCount = 0;

    /** How much time a component should stay unvailable before another evaluation. **/
    private int unavailabilityPeriod = 10;

    /** Policy used. **/
    private final LoadBalancingPolicy lbPolicy;

    /** List of resources to load balance. **/
    private List < LoadBalancingResource < RSC > > resources = new ArrayList < LoadBalancingResource < RSC> >();
    
    /**
     * Constructor with default policy LOAD BALACING.
     *
     * @param lst
     *      list of resources
     */
    @SuppressWarnings("unchecked")
    public Loadbalancer(RSC... lst) {
        this(LoadBalancingPolicy.ROUND_ROBIN, lst);
    }
    
    /**
     * Initialization of the resources. Weights are part of {@link LoadBalancingResource}.
     *
     * @param policy
     *          current policy
     * @param listRsc
     *          list of resources
     */
    public Loadbalancer(LoadBalancingPolicy policy, List< LoadBalancingResource < RSC >> listRsc) {
        this.lbPolicy   = policy;
        this.resources  = listRsc;
        Collections.sort(this.resources);
    }
    
    /**
     * Initialization of the resources. Weights are part of {@link LoadBalancingResource}.
     *
     * @param policy
     *          current policy
     * @param resources
     *          list of resources
     */
    @SuppressWarnings("unchecked")
    public Loadbalancer(LoadBalancingPolicy policy, RSC... resources) {
        this.lbPolicy   = policy;
        for (RSC rsc : resources) {
            LoadBalancingResource < RSC > lbRsc = new LoadBalancingResource<RSC>(rsc);
            lbRsc.setAvailable(true);
            lbRsc.setNbUse(0);
            // Set coefficient all equals
            if (lbPolicy == LoadBalancingPolicy.ROUND_ROBIN) {
                lbRsc.setDefaultWeigth(HUNDRED / resources.length);
            }
            lbRsc.setCurrentWeight(lbRsc.getDefaultWeigth());
            this.resources.add(lbRsc);
        }
        // unavailable first, check unavaibility time and put i pback in the 
        // pool if available again.
        Collections.sort(this.resources);
    }
    
    /**
     * Main mthod to retrieve a resource from loadbalancing.
     *
     * @return
     *      current resource
     */
    public final synchronized LoadBalancingResource < RSC > getLoadBalancedResource() {
        totalCount++;
        globalCount++;
        switch(lbPolicy) {
            case WEIGHT_LOAD_BALANCING:
            case ROUND_ROBIN:
                if (unavailableCount == resources.size()) {
                    throw new NoneResourceAvailableException("Cannot retrieve a resource "
                            + "all '" + unavailableCount + "' resources are down.");
                }
                for (LoadBalancingResource < RSC > rsc : resources) {
                    // if resource need to be reintroduced 
                    if (shouldEnableResource(rsc)) {
                        rsc.setAvailable(true);
                        LOGGER.info("{} has reached ends of its unavailability period, putting it back in the pool", rsc.getId());
                        redistributeWeights();
                        return getLoadBalancedResource();
                    }
                    // the resource did not reached its limits
                    if ((HUNDRED * (rsc.getNbUse() / totalCount)) <= rsc.getCurrentWeight()) {
                        rsc.setNbUse(rsc.getNbUse() + 1);
                        return rsc;
                    }
                }
            break;
            case RANDOM:
                return resources.get(new java.util.Random().nextInt(resources.size()));
        }
        throw new NoneResourceAvailableException("Cannot retrieve a resource "
                + "with round robin weights all consumed or unavailable");
    }
    
    /**
     * Main method, provide an available resource.
     * 
     * @return
     *      resource name
     */
    public RSC get() {
        return getLoadBalancedResource().getResource();
    }
    
    /**
     * Recompute weight when one is unavailable.
     */
    private final void redistributeWeights() {
        double loadtoBalance = 0.0;
        totalCount       = 0;
        unavailableCount = 0;
        
        // Compute load distribution
        for (LoadBalancingResource < RSC > rsc : resources) {
            rsc.setNbUse(0);
            // Resource is NOT available the load need to be redistributed
            if (!rsc.isAvailable()) {
                unavailableCount++;
                loadtoBalance += rsc.getDefaultWeigth();
            }
        }
        /* 
         * Load to be redistributed equally among remaining nodes (and NOT reapply proportions)
         */
        double loadtoDistribute = loadtoBalance
                    / Double.valueOf(resources.size() - unavailableCount).doubleValue();
        /**
         * Add the load
         */
        for (LoadBalancingResource < RSC > wrapper2 : resources) {
            if (wrapper2.isAvailable()) {
                wrapper2.setCurrentWeight(wrapper2.getDefaultWeigth() + loadtoDistribute);
            } else {
                wrapper2.setCurrentWeight(0);
            }
            
        }
        /** Sorting with unavailable first to be tested. */
        Collections.sort(resources);
        LOGGER.info("Resources status after weight computation:");
        for (LoadBalancingResource < RSC > w : resources) {
            LOGGER.info(" + " + w.getId() + ": " + w.getCurrentWeight() );
        }
        
    }

    /**
     * Test unvailable resource. 
     * 
     * @param rsc
     *      current resources
     * @return
     *      if 
     */
    private boolean shouldEnableResource(LoadBalancingResource < RSC > rsc) {
        return !rsc.isAvailable() && 
                (System.currentTimeMillis() - rsc.getUnavailabilityTriggerDate().getTime()) > 
                  (THOUSAND * unavailabilityPeriod);
    }
    
    /** {@inheritDoc} **/
    @Override
    public final String toString() {
        StringBuilder strBuildDer = new StringBuilder();
        strBuildDer.append("\nLoadBalanced state : globalCount <" + globalCount + "> totalCount <" + totalCount + "> ");
        strBuildDer.append(" unavailableCount <" + unavailableCount + ">");
        for (LoadBalancingResource < RSC > wrapper : resources) {
            strBuildDer.append("\n" + wrapper.toString());
            if (wrapper.isAvailable()) {
                strBuildDer.append(" currentUse "
                        + Double.valueOf(HUNDRED * (wrapper.getNbUse() / totalCount)).intValue() + "%");
            }
        }
        return strBuildDer.toString();
    }

    /**
     * Ce composant permet la gestion des erreurs pour un composant. On le rend indisponible.
     * Pour que le load-balancing fonctionne il faut r�partir la charge sur les �l�ments restants
     * (s'il en reste) et remettre les compteurs d'utilisation � 0. IL faut �galement remettre le
     * total count � 1.
     *
     * @param component
     *          composant qui a rencontr� une erreur
     * @param parentException
     *          exception lev�e lors de l'ex�cution du composant
     * @return
     *          gestion des erreurs
     */
    public final LoadBalancingResource < RSC > handleComponentError(
            final LoadBalancingResource < RSC > component, 
            final Throwable parentException) {
        component.setAvailable(false);
        component.setUnavailabilityCause(parentException.getMessage());
        component.setUnavailabilityError(parentException);
        component.setUnavailabilityTriggerDate(new Date());
        redistributeWeights();
        
        return getLoadBalancedResource();
    }

    /**
     * Permet de rechercher la liste des �lemtents.
     *
     * @return
     *      la liste des �l�ments
     */
    public final List < LoadBalancingResource < RSC > > getResourceList() {
        return this.resources;
    }

    /**
     * Accesseur en lecture pour totalCount.
     *
     * @return the totalCount
     */
    public final double getTotalCount() {
        return totalCount;
    }

    /**
     * Accesseur en �criture pour totalCount.
     *
     * @param ptotalCount the totalCount to set
     */
    public final void setTotalCount(final int ptotalCount) {
        this.totalCount = ptotalCount;
    }

    /**
     * Accesseur en lecture pour mode.
     *
     * @return the mode
     */
    public final LoadBalancingPolicy getMode() {
        return lbPolicy;
    }

    /**
     * Accesseur en �criture pour wrappeeElementList.
     *
     * @param pwrappeeElementList the wrappeeElementList to set
     */
    public final void setWrappeeElementList(final List < LoadBalancingResource < RSC >> pwrappeeElementList) {
        this.resources = pwrappeeElementList;
    }

    /**
     * Accesseur en lecture pour unavailabilityPeriod.
     *
     * @return the unavailabilityPeriod
     */
    public final int getUnavailabilityPeriod() {
        return unavailabilityPeriod;
    }

    /**
     * Accesseur en �criture pour unavailabilityPeriod.
     *
     * @param punavailabilityPeriod the unavailabilityPeriod to set
     */
    public final void setUnavailabilityPeriod(final int punavailabilityPeriod) {
        this.unavailabilityPeriod = punavailabilityPeriod;
    }

    /**
     * Accesseur en lecture pour unavailableCount.
     *
     * @return the unavailableCount
     */
    public final int getUnavailableCount() {
        return unavailableCount;
    }

    /**
     * Accesseur en �criture pour unavailableCount.
     *
     * @param punavailableCount the unavailableCount to set
     */
    public final void setUnavailableCount(final int punavailableCount) {
        this.unavailableCount = punavailableCount;
    }

    /**
     * Accesseur en lecture pour globalCount.
     * @return the globalCount
     */
    public final double getGlobalCount() {
        return globalCount;
    }

}
