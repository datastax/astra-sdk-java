package com.dtsx.astra.sdk.db.domain.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represent a request to setup connectivity to Splunk.
 */
public class SplunkTelemetryRequest {

    /**
     * Path for Splunk endpoint, which should always be a full HTTPS address.
     */
    private String endpoint;

    /**
     * Splunk index to write metrics to. Index must be set so the Splunk token has permission to write to it.
     */
    private String index;

    /**
     * Token for Splunk Authentication
     */
    private String token;

    /**
     * Source of events sent to this sink. If unset, we set it to a default value, eg. "astradb".
     */
    private String source;

    /**
     * Source type of events sent to this sink. If unset, we set it to a default value, eg. "astradb-metrics".
     */
    @JsonProperty("sourcetype")
    private String sourceType;

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
     * @param endpoint
     *         new value for endpoint
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Gets index
     *
     * @return value of index
     */
    public String getIndex() {
        return index;
    }

    /**
     * Set value for index
     *
     * @param index
     *         new value for index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * Gets token
     *
     * @return value of token
     */
    public String getToken() {
        return token;
    }

    /**
     * Set value for token
     *
     * @param token
     *         new value for token
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Gets source
     *
     * @return value of source
     */
    public String getSource() {
        return source;
    }

    /**
     * Set value for source
     *
     * @param source
     *         new value for source
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets sourceType
     *
     * @return value of sourceType
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * Set value for sourceType
     *
     * @param sourceType
     *         new value for sourceType
     */
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
}
