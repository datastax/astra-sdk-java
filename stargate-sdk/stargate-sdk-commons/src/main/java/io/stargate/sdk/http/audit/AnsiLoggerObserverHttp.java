package io.stargate.sdk.http.audit;

import io.stargate.sdk.audit.ServiceCallObserver;
import io.stargate.sdk.audit.ServiceCallObserverAnsiLogger;
import io.stargate.sdk.http.ServiceHttp;
import com.evanlennick.retry4j.Status;
import io.stargate.sdk.utils.AnsiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;


/**
 * Listener that log call in the Db
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 */
public class AnsiLoggerObserverHttp implements ServiceCallObserver<String, ServiceHttp, ServiceHttpCallEvent> {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceCallObserverAnsiLogger.class);

    /** {@inheritDoc} */
    @Override
    public void onCall(ServiceHttpCallEvent event) {
        LOGGER.info("------------ AnsiLogger ---------------");

        LOGGER.info("Service [" + AnsiUtils.yellow(event.getService().getId()) + "]");
        LOGGER.info("[" + AnsiUtils.yellow(event.getRequestId()) + "] Endpoint         : [" + AnsiUtils.green(event.getService().getId()) + "]");

        LOGGER.info("Request [" + AnsiUtils.yellow(event.getRequestId()) + "]");
        LOGGER.info("[" + AnsiUtils.yellow(event.getRequestId()) + "] Date             : [" + AnsiUtils.green(new Date(event.getTimestamp()).toString()) + "]");
        LOGGER.info("[" + AnsiUtils.yellow(event.getRequestId()) + "] Url              : [" + AnsiUtils.green(event.getHttpRequestUrl()) + "]");
        LOGGER.info("[" + AnsiUtils.yellow(event.getRequestId()) + "] Headers          : [" + AnsiUtils.green(event.getHttpRequestHeaders().toString()) + "]");
        LOGGER.info("[" + AnsiUtils.yellow(event.getRequestId()) + "] Body             : [" + AnsiUtils.green(event.getHttpRequestBody()) + "]");

        LOGGER.info("Response [" + AnsiUtils.magenta(event.getRequestId()) + "]");
        LOGGER.info("[" + AnsiUtils.magenta(event.getRequestId()) + "] Http Times       : [" + AnsiUtils.green(String.valueOf(event.getResponseElapsedTime())) + "] millis");
        LOGGER.info("[" + AnsiUtils.magenta(event.getRequestId()) + "] Total Time       : [" + AnsiUtils.green(String.valueOf(event.getResponseTime())) + "] millis");
        LOGGER.info("[" + AnsiUtils.magenta(event.getRequestId()) + "] Response Code    : [" + AnsiUtils.green(String.valueOf(event.getHttpResponseCode())) + "]");
        LOGGER.info("[" + AnsiUtils.magenta(event.getRequestId()) + "] Response Headers : [" + AnsiUtils.green(String.valueOf(event.getHttpResponseHeaders())) + "]");
        LOGGER.info("[" + AnsiUtils.magenta(event.getRequestId()) + "] Response Body    : [" + AnsiUtils.green(String.valueOf(event.getHttpResponseBody())) + "]");
        LOGGER.info("[" + AnsiUtils.magenta(event.getRequestId()) + "] Total Tries      : [" + AnsiUtils.green(String.valueOf(event.getTotalTries())) + "]");
        if (event.getErrorClass() != null) {
            LOGGER.info("Errors [" + AnsiUtils.red(event.getRequestId()) + "]");
            LOGGER.error("[" + AnsiUtils.red(event.getRequestId()) + "] Error Class      : [" + AnsiUtils.green(event.getErrorClass()) + "]");
            LOGGER.error("[" + AnsiUtils.red(event.getRequestId()) + "] Error Message    : [" + AnsiUtils.green(event.getErrorMessage()) + "]");
            LOGGER.error("[" + AnsiUtils.red(event.getRequestId()) + "] Error Exception  : [" + AnsiUtils.green(event.getLastException().getClass().getName()) + "]");
        }
    }



    @Override
    public void onSuccess(Status<String> s) {
        LOGGER.info("SUCCESS");
    }

    @Override
    public void onCompletion(Status<String> s) {
        LOGGER.info("COMPLETION");
    }

    @Override
    public void onFailure(Status<String> s) {
        LOGGER.info("FAILURE");
    }

    @Override
    public void onFailedTry(Status<String> s) {
        LOGGER.info("FAILED_TRY");
    }
}
