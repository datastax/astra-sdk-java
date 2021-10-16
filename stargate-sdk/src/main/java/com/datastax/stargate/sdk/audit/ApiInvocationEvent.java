package com.datastax.stargate.sdk.audit;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import com.datastax.stargate.sdk.core.ApiConstants;

/**
 * Event triggered for Api Invocation with input/output tracing.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiInvocationEvent implements ApiConstants {
    
    /** epoch time of the event generation. */
    private long timestamp;
    
    /** Client machine using the SDK. */
    private String host;
    
    // --- Request ---

    /** Unique identifier for a request. */
    private String requestId;
    
    /** Request HTTP method. */
    private String requestMethod;
    
    private String requestUrl;
    
    private Map<String, String> requestHeaders = new HashMap<>();
    
    private String requestBody;
    
    // --- Response ---
    
    private int responseCode;
    
    private Map<String, String> responseHeaders = new HashMap<>();
    
    private String responseBody;
    
    private long responseTime;
    
    private long responseTimestamp;
    
    private long responseElapsedTime;
    
    private int totalTries;
    
    // --- Error ---
    
    private String errorClass;
    
    private String errorMessage;
    
    private Exception lastException;
    
    /**
     * Getter accessor for attribute 'responseElapsedTime'.
     *
     * @return
     *       current value of 'responseElapsedTime'
     */
    public long getResponseElapsedTime() {
        return responseElapsedTime;
    }

    /**
     * Setter accessor for attribute 'responseElapsedTime'.
     * @param responseElapsedTime
     * 		new value for 'responseElapsedTime '
     */
    public void setResponseElapsedTime(long responseElapsedTime) {
        this.responseElapsedTime = responseElapsedTime;
    }
  
    /**
     * Getter accessor for attribute 'totalTries'.
     *
     * @return
     *       current value of 'totalTries'
     */
    public int getTotalTries() {
        return totalTries;
    }

    /**
     * Setter accessor for attribute 'totalTries'.
     * @param totalTries
     * 		new value for 'totalTries '
     */
    public void setTotalTries(int totalTries) {
        this.totalTries = totalTries;
    }

    /**
     * Getter accessor for attribute 'lastException'.
     *
     * @return
     *       current value of 'lastException'
     */
    public Exception getLastException() {
        return lastException;
    }

    /**
     * Setter accessor for attribute 'lastException'.
     * @param lastException
     * 		new value for 'lastException '
     */
    public void setLastException(Exception lastException) {
        this.lastException = lastException;
    }

    /**
     * Contructor with an http request.
     *
     * @param req
     *      current http request
     */
    public ApiInvocationEvent(ClassicHttpRequest req) {
        this.timestamp = System.currentTimeMillis();
        try {
            this.host      = InetAddress.getLocalHost().getHostName();
            if (req.containsHeader(HEADER_REQUEST_ID)) {
                this.requestId = req.getHeader(HEADER_REQUEST_ID).getValue();
            }
            this.requestMethod = req.getMethod();
            this.requestUrl    = req.getUri().toString();
            for(Header h : req.getHeaders()) {
                if (h.getName().equalsIgnoreCase(HEADER_AUTHORIZATION) || 
                    h.getName().equalsIgnoreCase(HEADER_CASSANDRA)) {
                    this.requestHeaders.put(h.getName(), "***");
                } else {
                    this.requestHeaders.put(h.getName(), h.getValue());
                }
            }
            if (req.getEntity() != null) {
                this.requestBody = EntityUtils.toString(req.getEntity());
            }
        } catch (Exception pe) {
            // Ignore errors in the monitoring process
        }
    }
    
    /**
     * Getter accessor for attribute 'timestamp'.
     *
     * @return
     *       current value of 'timestamp'
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Setter accessor for attribute 'timestamp'.
     * @param timestamp
     * 		new value for 'timestamp '
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter accessor for attribute 'host'.
     *
     * @return
     *       current value of 'host'
     */
    public String getHost() {
        return host;
    }

    /**
     * Setter accessor for attribute 'host'.
     * @param host
     * 		new value for 'host '
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Getter accessor for attribute 'requestMethod'.
     *
     * @return
     *       current value of 'requestMethod'
     */
    public String getRequestMethod() {
        return requestMethod;
    }

    /**
     * Setter accessor for attribute 'requestMethod'.
     * @param requestMethod
     * 		new value for 'requestMethod '
     */
    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    /**
     * Getter accessor for attribute 'requestUrl'.
     *
     * @return
     *       current value of 'requestUrl'
     */
    public String getRequestUrl() {
        return requestUrl;
    }

    /**
     * Setter accessor for attribute 'requestUrl'.
     * @param requestUrl
     * 		new value for 'requestUrl '
     */
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    /**
     * Getter accessor for attribute 'requestBody'.
     *
     * @return
     *       current value of 'requestBody'
     */
    public String getRequestBody() {
        return requestBody;
    }

    /**
     * Setter accessor for attribute 'requestBody'.
     * @param requestBody
     * 		new value for 'requestBody '
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * Getter accessor for attribute 'requestId'.
     *
     * @return
     *       current value of 'requestId'
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Setter accessor for attribute 'requestId'.
     * @param requestId
     * 		new value for 'requestId '
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Getter accessor for attribute 'requestHeaders'.
     *
     * @return
     *       current value of 'requestHeaders'
     */
    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }

    /**
     * Setter accessor for attribute 'requestHeaders'.
     * @param requestHeaders
     * 		new value for 'requestHeaders '
     */
    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }

    /**
     * Getter accessor for attribute 'responseCode'.
     *
     * @return
     *       current value of 'responseCode'
     */
    public int getResponseCode() {
        return responseCode;
    }

    /**
     * Setter accessor for attribute 'responseCode'.
     * @param responseCode
     * 		new value for 'responseCode '
     */
    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    /**
     * Getter accessor for attribute 'responseHeaders'.
     *
     * @return
     *       current value of 'responseHeaders'
     */
    public Map<String, String> getResponseHeaders() {
        return responseHeaders;
    }

    /**
     * Setter accessor for attribute 'responseHeaders'.
     * @param responseHeaders
     * 		new value for 'responseHeaders '
     */
    public void setResponseHeaders(Map<String, String> responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    /**
     * Getter accessor for attribute 'responseBody'.
     *
     * @return
     *       current value of 'responseBody'
     */
    public String getResponseBody() {
        return responseBody;
    }

    /**
     * Setter accessor for attribute 'responseBody'.
     * @param responseBody
     * 		new value for 'responseBody '
     */
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    
    /**
     * Getter accessor for attribute 'responseTime'.
     *
     * @return
     *       current value of 'responseTime'
     */
    public long getResponseTime() {
        return responseTime;
    }

    /**
     * Getter accessor for attribute 'errorClass'.
     *
     * @return
     *       current value of 'errorClass'
     */
    public String getErrorClass() {
        return errorClass;
    }

    /**
     * Setter accessor for attribute 'errorClass'.
     * @param errorClass
     * 		new value for 'errorClass '
     */
    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    /**
     * Getter accessor for attribute 'errorMessage'.
     *
     * @return
     *       current value of 'errorMessage'
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Setter accessor for attribute 'errorMessage'.
     * @param errorMessage
     * 		new value for 'errorMessage '
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }


    /**
     * Getter accessor for attribute 'responseTimestamp'.
     *
     * @return
     *       current value of 'responseTimestamp'
     */
    public long getResponseTimestamp() {
        return responseTimestamp;
    }


    /**
     * Setter accessor for attribute 'responseTimestamp'.
     * @param responseTimestamp
     * 		new value for 'responseTimestamp '
     */
    public void setResponseTimestamp(long responseTimestamp) {
        this.responseTimestamp = responseTimestamp;
        this.responseTime      = responseTimestamp - this.timestamp; 
    }
    
    
}
