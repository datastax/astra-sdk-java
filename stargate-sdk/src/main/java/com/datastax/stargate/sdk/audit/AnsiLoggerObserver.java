package com.datastax.stargate.sdk.audit;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.evanlennick.retry4j.Status;

import static com.datastax.stargate.sdk.utils.AnsiUtils.*;

/**
 * Listener that log call in the Db
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class AnsiLoggerObserver implements ApiInvocationObserver {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnsiLoggerObserver.class);
    
    /** {@inheritDoc} */
    @Override
    public void onCall(ApiInvocationEvent event) {
        LOGGER.info("------------ AnsiLogger ---------------");
        LOGGER.info("Request [" + yellow(event.getRequestId()) + "]");
        LOGGER.info("[" + yellow(event.getRequestId()) + "] Date             : [" + green(new Date(event.getTimestamp()).toString()) + "]");
        LOGGER.info("[" + yellow(event.getRequestId()) + "] Client           : [" + green(event.getHost()) + "]");
        LOGGER.info("[" + yellow(event.getRequestId()) + "] Request Method   : [" + green(event.getRequestMethod()) + "]");
        LOGGER.info("[" + yellow(event.getRequestId()) + "] Request URL      : [" + green(event.getRequestUrl())  + "]");
        LOGGER.info("[" + yellow(event.getRequestId()) + "] Request Headers  : [" + green(event.getRequestHeaders().toString())  + "]");
        LOGGER.info("[" + yellow(event.getRequestId()) + "] Request Body     : [" + green(event.getRequestBody())  + "]");
        LOGGER.info("Response [" + magenta(event.getRequestId()) + "]");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Http Times       : [" + green(String.valueOf(event.getResponseElapsedTime())) + "] millis");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Total Time       : [" + green(String.valueOf(event.getResponseTime())) + "] millis");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Response Code    : [" + green(String.valueOf(event.getResponseCode())) + "]");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Response Headers : [" + green(String.valueOf(event.getResponseHeaders())) + "]");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Response Body    : [" + green(String.valueOf(event.getResponseBody())) + "]");
        LOGGER.info("[" + magenta(event.getRequestId()) + "] Total Tries      : [" + green(String.valueOf(event.getTotalTries())) + "]");
        if (event.getErrorClass() != null) {
            LOGGER.info("Errors [" + red(event.getRequestId()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Class      : [" + green(event.getErrorClass()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Message    : [" + green(event.getErrorMessage()) + "]");
            LOGGER.error("[" + red(event.getRequestId()) + "] Error Exception  : [" + green(event.getLastException().getClass().getName()) + "]");
        }
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
