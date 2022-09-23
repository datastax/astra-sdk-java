package com.datastax.stargate.sdk.audit;

import com.evanlennick.retry4j.Status;

/**
 * Default class for console.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AnsiConsoleLogger implements ApiInvocationObserver {
    
    /** {@inheritDoc} */
    @Override
    public void onCall(ApiInvocationEvent event) {
       System.out.println("Http (" + String.valueOf(event.getResponseCode()) + ") on " + event.getRequestUrl());
    }
    
    /** {@inheritDoc} */
    @Override
    public void onHttpSuccess(Status<String> s) {
    }

    /** {@inheritDoc} */
    @Override
    public void onHttpCompletion(Status<String> s) {
    }

    /** {@inheritDoc} */
    @Override
    public void onHttpFailure(Status<String> s) {
    }

    /** {@inheritDoc} */
    @Override
    public void onHttpFailedTry(Status<String> s) {
    }

}
