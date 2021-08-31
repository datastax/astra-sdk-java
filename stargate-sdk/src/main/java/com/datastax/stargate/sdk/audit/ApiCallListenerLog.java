package com.datastax.stargate.sdk.audit;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Listener that log call in the Db
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class ApiCallListenerLog implements ApiCallListener {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiCallListenerLog.class);
    
    /** {@inheritDoc} */
    @Override
    public void onCall(ApiCallEvent event) {
        LOGGER.info("Event: {}" + event.getRequestId());
        LOGGER.info("+ Timestamp {} or {}", event.getTimestamp(), new Date(event.getTimestamp()));
        LOGGER.info("+ Request Method: {}" , event.getRequestMethod());
        LOGGER.info("+ Request URL: {}" , event.getRequestUrl());
        
        
    }

}
