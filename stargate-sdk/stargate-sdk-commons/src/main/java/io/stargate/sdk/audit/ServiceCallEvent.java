package io.stargate.sdk.audit;

import io.stargate.sdk.Service;

/**
 * Event triggered for Api Invocation with input/output tracing.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ServiceCallEvent<SERVICE extends Service> {

    /** Related service. */
    protected transient SERVICE service;

    // --- Request ---

    /** epoch time of the event generation. */
    protected long timestamp;

    /** Unique identifier for a request. */
    protected String requestId;

    // --- Response ---

    protected long responseTime;

    protected long responseTimestamp;

    protected long responseElapsedTime;

    protected int totalTries;
    
    // --- Error ---

    protected String errorClass;

    protected String errorMessage;

    protected Exception lastException;
    
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
     * Default constructor.
     */
    public ServiceCallEvent() {}
    
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

    /**
     * Gets requestId
     *
     * @return value of requestId
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Set value for requestId
     *
     * @param requestId new value for requestId
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * Set value for responseTime
     *
     * @param responseTime new value for responseTime
     */
    public void setResponseTime(long responseTime) {
        this.responseTime = responseTime;
    }

    /**
     * Gets service
     *
     * @return value of service
     */
    public SERVICE getService() {
        return service;
    }

    /**
     * Set value for service
     *
     * @param service new value for service
     */
    public void setService(SERVICE service) {
        this.service = service;
    }
}
