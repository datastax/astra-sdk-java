package com.datastax.stargate.sdk.audit;

import com.evanlennick.retry4j.Status;

/**
 * If register in the HttpApisClient, will be triggered asynchronously
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface ApiInvocationObserver {

    /**
     * Get notified when the retry is in success.
     *
     * @param s
     *      invocation details
     */
    void onHttpSuccess(Status<String> s);
    
    /**
     * Get notified when http execution is done
     *
     * @param s
     *  invocation details
     */
    void onHttpCompletion(Status<String> s);
    
    /**
     * Get notified when error in http call.
     * 
     * @param s
     *      invocation details
     *      
     */
    void onHttpFailure(Status<String> s);
    
    /**
     * On try failed.
     *
     * @param s
     *      invocation details
     */
    void onHttpFailedTry(Status<String> s);
    
    /**
     * Process event.
     * 
     * @param event
     *      api invocation event
     */
    void onCall(ApiInvocationEvent event);
    
}
