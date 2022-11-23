package io.stargate.sdk.loadbalancer;

/**
 * Load balancing startegy
 *
 * @author C&eacute;drick LUNVEN
 */
public enum LoadBalancingPolicy {

    /** Using weights. **/
    WEIGHT_LOAD_BALANCING,

    /** Load balancing **/
    ROUND_ROBIN,
    
    /** Pick a rsource randomly. */
    RANDOM
}
