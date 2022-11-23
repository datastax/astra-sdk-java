package io.stargate.sdk.audit;

import io.stargate.sdk.Service;
import com.evanlennick.retry4j.Status;

/**
 * If register in the HttpApisClient, will be triggered asynchronously
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public interface ServiceCallObserver<RESPONSE, S extends Service, T extends ServiceCallEvent<S>> {

    /**
     * Process event.
     *
     * @param event
     *      api invocation event
     */
    void onCall(T event);

    /**
     * Get notified when the retry is in success.
     *
     * @param s
     *      invocation details
     */
    void onSuccess(Status<RESPONSE> s);
    
    /**
     * Get notified when http execution is done
     *
     * @param s
     *  invocation details
     */
    void onCompletion(Status<RESPONSE> s);
    
    /**
     * Get notified when error in http call.
     * 
     * @param s
     *      invocation details
     *      
     */
    void onFailure(Status<RESPONSE> s);
    
    /**
     * On try failed.
     *
     * @param s
     *      invocation details
     */
    void onFailedTry(Status<RESPONSE> s);
    
}
