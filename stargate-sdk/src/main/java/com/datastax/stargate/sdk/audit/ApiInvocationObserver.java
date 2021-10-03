package com.datastax.stargate.sdk.audit;

/**
 * If register in the HttpApisClient, will be triggered asynchronously
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface ApiInvocationObserver {

    void onCall(ApiInvocationEvent event);
    
    
}
