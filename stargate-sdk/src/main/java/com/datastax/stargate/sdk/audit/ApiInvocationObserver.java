package com.datastax.stargate.sdk.audit;

/**
 * If register in the HttpApisClient, will be triggered asynchronously
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface ApiInvocationObserver {

    /**
     * Process event.
     * 
     * @param event
     *      api invocation event
     */
    void onCall(ApiInvocationEvent event);
    
    
}
