package com.datastax.stargate.sdk.loadbalancer;

/**
 * To disable a resources from the load balancer you will get 2 scenarios:
 * - The heartbit detect the failure and disable the resource
 * - The usage of the resource generates an exception that can be interpretad as resource not available
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class UnavailableResourceException extends RuntimeException {

    /** Serial. */
    private static final long serialVersionUID = 1L;
    
    /**
     * Error with message
     *
     * @param msg
     *      current message
     */
    public UnavailableResourceException(String msg) {
        super(msg);
    }
    
    /**
     * Error with message and error.
     *
     * @param msg
     *      current message
     * @param parent
     *      error
     */
    public UnavailableResourceException(String msg, Throwable parent) {
        super(msg, parent);
    }

}
