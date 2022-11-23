package io.stargate.sdk.http.audit;

import io.stargate.sdk.utils.AnsiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


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
        LOGGER.info("Service [" + AnsiUtils.yellow(event.getService().getId()) + "]");
        LOGGER.info("Request [" + AnsiUtils.yellow(event.getRequestId()) + "]");
        LOGGER.info("Response [" + AnsiUtils.magenta(event.getRequestId()) + "]");
        LOGGER.info("[" + AnsiUtils.magenta(event.getRequestId()) + "] Response Code    : [" + AnsiUtils.green(String.valueOf(event.getHttpResponseCode())) + "]");
        if (event.getErrorClass() != null) {
            LOGGER.info("Errors [" + AnsiUtils.red(event.getRequestId()) + "]");
            LOGGER.error("[" + AnsiUtils.red(event.getRequestId()) + "] Error Class      : [" + AnsiUtils.green(event.getErrorClass()) + "]");
            LOGGER.error("[" + AnsiUtils.red(event.getRequestId()) + "] Error Message    : [" + AnsiUtils.green(event.getErrorMessage()) + "]");
            LOGGER.error("[" + AnsiUtils.red(event.getRequestId()) + "] Error Exception  : [" + AnsiUtils.green(event.getLastException().getClass().getName()) + "]");
        }
    }

}
