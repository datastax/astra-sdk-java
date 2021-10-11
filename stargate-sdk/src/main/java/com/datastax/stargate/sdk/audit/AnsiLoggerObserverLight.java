package com.datastax.stargate.sdk.audit;

import static com.datastax.stargate.sdk.utils.AnsiUtils.green;
import static com.datastax.stargate.sdk.utils.AnsiUtils.magenta;
import static com.datastax.stargate.sdk.utils.AnsiUtils.red;
import static com.datastax.stargate.sdk.utils.AnsiUtils.yellow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Listener that log call in the Db
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class AnsiLoggerObserverLight implements ApiInvocationObserver {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnsiLoggerObserverLight.class);
    
    /** {@inheritDoc} */
    @Override
    public void onCall(ApiInvocationEvent event) {
        LOGGER.info("[" + yellow(event.getRequestId()) + "] Request URL      : [" + green(event.getRequestUrl())  + "]");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Response Code    : [" + green(String.valueOf(event.getResponseCode())) + "]");
        if (event.getErrorClass() != null) {
            LOGGER.info("Errors [" + red(event.getRequestId()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Class      : [" + green(event.getErrorClass()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Message    : [" + green(event.getErrorMessage()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Exception  : [" + green(event.getLastException().getClass().getName()) + "]");
        }
    }
}
