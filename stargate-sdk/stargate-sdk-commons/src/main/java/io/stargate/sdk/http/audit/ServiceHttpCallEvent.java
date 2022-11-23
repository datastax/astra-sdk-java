package io.stargate.sdk.http.audit;

import io.stargate.sdk.api.ApiConstants;
import io.stargate.sdk.audit.ServiceCallEvent;
import io.stargate.sdk.http.ServiceHttp;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Event triggered for Api Invocation with input/output tracing.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ServiceHttpCallEvent extends ServiceCallEvent<ServiceHttp> implements ApiConstants {

    // -- HTTP REQUEST --

    /** Request HTTP method. */
    protected String httpRequestMethod;

    /** Request HTTP. */
    protected String httpRequestUrl;

    /** Request HTTP . */
    protected Map<String, String> httpRequestHeaders = new HashMap<>();

    /** Request HTTP. */
    protected String httpRequestBody;

    // -- HTTP RESPONSE --

    /** Response HTTP. */
    protected int httpResponseCode;

    /** Response HTTP. */
    protected Map<String, String> httpResponseHeaders = new HashMap<>();

    /** Response HTTP. */
    protected String httpResponseBody;

    /**
     * Constructor with http request.
     *
     * @param req
     *      current http request
     */
    public ServiceHttpCallEvent(ServiceHttp service, ClassicHttpRequest req) {
        super();
        this.timestamp = System.currentTimeMillis();
        this.service  = service;
        try {
            if (req.containsHeader(HEADER_REQUEST_ID)) {
                this.requestId = req.getHeader(HEADER_REQUEST_ID).getValue();
            }
            this.httpRequestMethod = req.getMethod();
            this.httpRequestUrl = req.getUri().toString();
            for (Header h : req.getHeaders()) {
                if (h.getName().equalsIgnoreCase(HEADER_AUTHORIZATION) ||
                        h.getName().equalsIgnoreCase(HEADER_CASSANDRA)) {
                    this.httpRequestHeaders.put(h.getName(), "***");
                } else {
                    this.httpRequestHeaders.put(h.getName(), h.getValue());
                }
            }
            if (req.getEntity() != null) {
                this.httpRequestBody = EntityUtils.toString(req.getEntity());
            }
        } catch (Exception pe) {
            // Ignore errors in the monitoring process
        }
    }

    /**
     * Gets httpRequestMethod
     *
     * @return value of httpRequestMethod
     */
    public String getHttpRequestMethod() {
        return httpRequestMethod;
    }

    /**
     * Gets httpRequestUrl
     *
     * @return value of httpRequestUrl
     */
    public String getHttpRequestUrl() {
        return httpRequestUrl;
    }

    /**
     * Gets httpRequestHeaders
     *
     * @return value of httpRequestHeaders
     */
    public Map<String, String> getHttpRequestHeaders() {
        return httpRequestHeaders;
    }

    /**
     * Gets httpRequestBody
     *
     * @return value of httpRequestBody
     */
    public String getHttpRequestBody() {
        return httpRequestBody;
    }

    /**
     * Gets httpResponseCode
     *
     * @return value of httpResponseCode
     */
    public int getHttpResponseCode() {
        return httpResponseCode;
    }

    /**
     * Gets httpResponseHeaders
     *
     * @return value of httpResponseHeaders
     */
    public Map<String, String> getHttpResponseHeaders() {
        return httpResponseHeaders;
    }

    /**
     * Gets httpResponseBody
     *
     * @return value of httpResponseBody
     */
    public String getHttpResponseBody() {
        return httpResponseBody;
    }

    /**
     * Set value for httpRequestMethod
     *
     * @param httpRequestMethod new value for httpRequestMethod
     */
    public void setHttpRequestMethod(String httpRequestMethod) {
        this.httpRequestMethod = httpRequestMethod;
    }

    /**
     * Set value for httpRequestUrl
     *
     * @param httpRequestUrl new value for httpRequestUrl
     */
    public void setHttpRequestUrl(String httpRequestUrl) {
        this.httpRequestUrl = httpRequestUrl;
    }

    /**
     * Set value for httpRequestHeaders
     *
     * @param httpRequestHeaders new value for httpRequestHeaders
     */
    public void setHttpRequestHeaders(Map<String, String> httpRequestHeaders) {
        this.httpRequestHeaders = httpRequestHeaders;
    }

    /**
     * Set value for httpRequestBody
     *
     * @param httpRequestBody new value for httpRequestBody
     */
    public void setHttpRequestBody(String httpRequestBody) {
        this.httpRequestBody = httpRequestBody;
    }

    /**
     * Set value for httpResponseCode
     *
     * @param httpResponseCode new value for httpResponseCode
     */
    public void setHttpResponseCode(int httpResponseCode) {
        this.httpResponseCode = httpResponseCode;
    }

    /**
     * Set value for httpResponseHeaders
     *
     * @param httpResponseHeaders new value for httpResponseHeaders
     */
    public void setHttpResponseHeaders(Map<String, String> httpResponseHeaders) {
        this.httpResponseHeaders = httpResponseHeaders;
    }

    /**
     * Set value for httpResponseBody
     *
     * @param httpResponseBody new value for httpResponseBody
     */
    public void setHttpResponseBody(String httpResponseBody) {
        this.httpResponseBody = httpResponseBody;
    }
}
