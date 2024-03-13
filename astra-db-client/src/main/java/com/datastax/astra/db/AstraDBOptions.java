package com.datastax.astra.db;


import io.stargate.sdk.http.HttpClientOptions;
import lombok.Builder;
import lombok.Data;

import java.net.http.HttpClient;

/**
 * Options to set up http Client.
 */
@Data @Builder
public class AstraDBOptions {

    /** Default user agent. */
    public static final String DEFAULT_CALLER_NAME = "astra-db-java";

    /** Default user agent. */
    public static final String DEFAULT_CALLER_VERSION =
            AstraDBOptions.class.getPackage().getImplementationVersion() != null ?
            AstraDBOptions.class.getPackage().getImplementationVersion() : "dev";

    /** Default timeout for initiating connection. */
    public static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 20;

    /** Default timeout for initiating connection. */
    public static final int DEFAULT_REQUEST_TIMEOUT_SECONDS = 20;

    /** Default retry count. */
    public static final int DEFAULT_RETRY_COUNT = 3;

    /** Default retry delay. */
    public static final int DEFAULT_RETRY_DELAY_MILLIS  = 100;

    /** path for json api. */
    public static final String DEFAULT_VERSION = "v1";

    /** Caller name in User agent. */
    @Builder.Default
    String apiVersion = DEFAULT_VERSION;

    /** Caller name in User agent. */
    @Builder.Default
    String userAgentCallerName = DEFAULT_CALLER_NAME;

    /** Caller version in User agent. */
    @Builder.Default
    String userAgentCallerVersion = DEFAULT_CALLER_VERSION;

    /** Http Connection timeout. */
    @Builder.Default
    long connectionRequestTimeoutInSeconds = DEFAULT_CONNECT_TIMEOUT_SECONDS;

    /** Http Connection timeout. */
    @Builder.Default
    long responseTimeoutInSeconds = DEFAULT_REQUEST_TIMEOUT_SECONDS;

    /** Enable retry count. */
    @Builder.Default
    int retryCount = DEFAULT_RETRY_COUNT;

    /** How much to wait in between 2 calls. */
    @Builder.Default
    int retryDelay = DEFAULT_RETRY_DELAY_MILLIS;

    /** The http client could work through a proxy. */
    HttpClientOptions.HttpProxy proxy;

    /** Moving to HTTP/2. */
    @Builder.Default
    HttpClient.Version httpVersion = HttpClient.Version.HTTP_2;

    /** Redirect  */
    @Builder.Default
    HttpClient.Redirect httpRedirect = HttpClient.Redirect.NORMAL;

    /**
     * Map as a http client options.
     *
     * @return
     *      instance of HttpClientOptions
     */
    public HttpClientOptions asHttpClientOptions() {
        return HttpClientOptions.builder()
                .apiVersion(apiVersion)
                .userAgentCallerName(userAgentCallerName)
                .userAgentCallerVersion(userAgentCallerVersion)
                .connectionRequestTimeoutInSeconds(connectionRequestTimeoutInSeconds)
                .connectionRequestTimeoutInSeconds(connectionRequestTimeoutInSeconds)
                .responseTimeoutInSeconds(responseTimeoutInSeconds)
                .retryCount(retryCount)
                .retryDelay(retryDelay)
                .proxy(proxy)
                .httpVersion(httpVersion)
                .httpRedirect(httpRedirect)
                .build();
    }

}