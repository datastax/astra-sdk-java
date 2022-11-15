package com.datastax.stargate.sdk.http;

import com.datastax.stargate.sdk.Service;
import org.apache.hc.client5.http.classic.methods.HttpGet;

import java.net.HttpURLConnection;

/**
 * The target service is an HTTP Endpoint. This services can be used for
 * graphQL, rest, docs but not gRPC for instance.
 */
public class ServiceHttp extends Service {

    /**
     * Constructor.
     * @param id                  identifier
     * @param endpoint            endpoint
     * @param healthCheckEndpoint health check
     */
    public ServiceHttp(String id, String endpoint, String healthCheckEndpoint) {
        super(id, endpoint, healthCheckEndpoint);
    }

    /**
     * Check that a service is alive.
     *
     * @return
     *      validate that the current service is alive
     */
    @Override
    public boolean isAlive() {
        return HttpURLConnection.HTTP_OK == RetryHttpClient
                .getInstance()
                .executeHttp(this, new HttpGet(healthCheckEndpoint), false)
                .getCode();
    }

}
