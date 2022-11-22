package com.datastax.stargate.sdk;

/**
 * SuperClass for ApiEndpoint.
 */
public abstract class Service {

    /** Instance id. */
    protected String id;

    /** Service Endpoint. */
    protected String endpoint;

    /** Health Check. */
    protected String healthCheckEndpoint;

    /**
     * Constructor.
     *
     * @param id
     *      identifier
     * @param endpoint
     *      endpoint
     * @param healthCheckEndpoint
     *      health-check
     */
    public Service(String id, String endpoint, String healthCheckEndpoint) {
        this.id = id;
        this.endpoint = endpoint;
        this.healthCheckEndpoint = healthCheckEndpoint;
    }

    /**
     * Invoke heath endpoint.
     *
     * @return
     *      is the service is up.
     */
    public abstract boolean isAlive();

    /**
     * Gets id
     *
     * @return value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Set value for id
     *
     * @param id new value for id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets endpoint
     *
     * @return value of endpoint
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Set value for endpoint
     *
     * @param endpoint new value for endpoint
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Gets healthCheckEndpoint
     *
     * @return value of healthCheckEndpoint
     */
    public String getHealthCheckEndpoint() {
        return healthCheckEndpoint;
    }

    /**
     * Set value for healthCheckEndpoint
     *
     * @param healthCheckEndpoint new value for healthCheckEndpoint
     */
    public void setHealthCheckEndpoint(String healthCheckEndpoint) {
        this.healthCheckEndpoint = healthCheckEndpoint;
    }

}
