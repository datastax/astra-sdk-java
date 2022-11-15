package com.datastax.stargate.sdk.http.audit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.datastax.stargate.sdk.utils.AnsiUtils.*;


/**
 * Listener that log call in the Db
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AnsiLoggerObserverHttpLight extends AnsiLoggerObserverHttp {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnsiLoggerObserverHttpLight.class);

    /** {@inheritDoc} */
    @Override
    public void onCall(ServiceHttpCallEvent event) {
        LOGGER.info("Service [" + yellow(event.getService().getId()) + "]");
        LOGGER.info("Request [" + yellow(event.getRequestId()) + "]");
        LOGGER.info("Response [" + magenta(event.getRequestId()) + "]");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Response Code    : [" + green(String.valueOf(event.getHttpResponseCode())) + "]");
        if (event.getErrorClass() != null) {
            LOGGER.info("Errors [" + red(event.getRequestId()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Class      : [" + green(event.getErrorClass()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Message    : [" + green(event.getErrorMessage()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Exception  : [" + green(event.getLastException().getClass().getName()) + "]");
        }
    }

}
