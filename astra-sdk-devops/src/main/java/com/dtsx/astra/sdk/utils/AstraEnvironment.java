package com.dtsx.astra.sdk.utils;

/**
 * SDk to be used on multiple Astra Environment.
 */
public enum AstraEnvironment {

    /** Production environment (default). */
    PROD("https://api.astra.datastax.com/v2",
            ".apps.astra.datastax.com",
            ".api.streaming.datastax.com"),

    /** Development Environment. */
    DEV("https://api.dev.cloud.datastax.com/v2",
            ".apps.astra-dev.datastax.com",
            ".api.dev.streaming.datastax.com"),

    /** Test Environment. */
    TEST("https://api.test.cloud.datastax.com/v2",
            ".apps.astra-test.datastax.com",
            ".api.staging.streaming.datastax.com");

    private String endpoint;

    private String appsSuffix;

    private String streamingV3Suffix;

    /**
     * Hide previous constructor.
     *
     * @param endpoint
     *      target attribute
     */
    private AstraEnvironment(String endpoint, String appsSuffix, String streaming) {
        this.endpoint = endpoint;
        this.appsSuffix = appsSuffix;
        this.streamingV3Suffix = streaming;
    }

    /**
     * Access attribute.
     * @return
     *      prefix for the URL
     */
    public String getEndPoint() {
        return endpoint;
    }

    /**
     * Access attribute.
     * @return
     *      prefix for the URL
     */
    public String getAppsSuffix() {
        return appsSuffix;
    }

    /**
     * Access attribute.
     * @return
     *      prefix for the URL
     */
    public String getStreamingV3Suffix() {
        return streamingV3Suffix;
    }
}