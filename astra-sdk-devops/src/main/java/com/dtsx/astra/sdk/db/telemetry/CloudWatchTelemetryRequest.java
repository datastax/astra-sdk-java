package com.dtsx.astra.sdk.db.telemetry;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Externalization of monitoring.
 */
public class CloudWatchTelemetryRequest {

    /** json field. */
    @JsonProperty("access_key")
    private String accessKey;

    /** json field. */
    private String secret;

    /** json field. */
    private String region;

    /**
     * Default constructor.
     */
    public CloudWatchTelemetryRequest() {
    }

    /**
     * Full constructor.
     *
     * @param accessKey
     *      access key
     * @param secret
     *      secret
     * @param region
     *      region
     */
    public CloudWatchTelemetryRequest(String accessKey, String secret, String region) {
        this.accessKey = accessKey;
        this.secret = secret;
        this.region = region;
    }
}
