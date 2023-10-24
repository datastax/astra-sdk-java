package com.dtsx.astra.sdk.db.domain.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Hold configuration to export metrics to datadog.
 */
public class DatadogTelemetryRequest {

    /**
     * API key to authenticate to the Datadog API
     */
    @JsonProperty("api_key")
    private String apiKey;

    /**
     * The Datadog site to send data to, which should be the site parameter corresponding to the Datadog site URL
     */
    private String site;

    /**
     * Default constructor.
     */
    public DatadogTelemetryRequest() {}

    /**
     * Gets apiKey
     *
     * @return value of apiKey
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * Set value for apiKey
     *
     * @param apiKey
     *         new value for apiKey
     */
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * Gets site
     *
     * @return value of site
     */
    public String getSite() {
        return site;
    }

    /**
     * Set value for site
     *
     * @param site
     *         new value for site
     */
    public void setSite(String site) {
        this.site = site;
    }
}
