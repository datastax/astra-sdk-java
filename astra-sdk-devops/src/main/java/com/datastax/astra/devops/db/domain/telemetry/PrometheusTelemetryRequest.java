package com.datastax.astra.devops.db.domain.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Externalization of monitoring.
 */
public class PrometheusTelemetryRequest {

    /** Endpoint. */
    private String endpoint;

    /** Authentication. */
    @JsonProperty("auth_strategy")
    private String authStrategy;

    /** User. */
    private String user;

    /** Password. */
    private String password;

    /**
     * Default constructor
     */
    public PrometheusTelemetryRequest() {
    }

    /**
     * Constructor full.
     *
     * @param endpoint
     *      prometheus endpoint
     * @param authStrategy
     *      authentication strategy
     * @param user
     *      username
     * @param password
     *      password
     */
    public PrometheusTelemetryRequest(String endpoint, String authStrategy, String user, String password) {
        this.endpoint = endpoint;
        this.authStrategy = authStrategy;
        this.user = user;
        this.password = password;
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
     * Gets authStrategy
     *
     * @return value of authStrategy
     */
    public String getAuthStrategy() {
        return authStrategy;
    }

    /**
     * Set value for authStrategy
     *
     * @param authStrategy new value for authStrategy
     */
    public void setAuthStrategy(String authStrategy) {
        this.authStrategy = authStrategy;
    }

    /**
     * Gets user
     *
     * @return value of user
     */
    public String getUser() {
        return user;
    }

    /**
     * Set value for user
     *
     * @param user new value for user
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Gets password
     *
     * @return value of password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Set value for password
     *
     * @param password new value for password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
