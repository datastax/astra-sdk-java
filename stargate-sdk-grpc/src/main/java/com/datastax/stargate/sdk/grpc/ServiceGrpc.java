package com.datastax.stargate.sdk.grpc;

import com.datastax.stargate.sdk.Service;
import com.datastax.stargate.sdk.api.ApiConstants;
import com.datastax.stargate.sdk.http.RetryHttpClient;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.StargateGrpc;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.net.HttpURLConnection;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Implementation.
 */
public class ServiceGrpc extends Service {

    /** Retries. */
    protected int maxRetries = 3;

    /** Keep Alive. */
    protected long keepAliveTimeout = 100000;

    /** Keep Alive. */
    protected TimeUnit keepAliveTimeoutUnit = TimeUnit.MILLISECONDS;

    /** Channel. */
    private  ManagedChannel channel;

    /** stub for synchronous. */
    private  StargateGrpc.StargateBlockingStub syncStub;

    /** stub for reactive. */
    private  StargateGrpc.StargateStub reactiveStub;

    /** stub for asynchronous. */
    private  StargateGrpc.StargateFutureStub asyncStub;



    /**
     * Initialization with the Channel.
     *
     * @param grpcChannel
     *      current channel.
     *
    public ServiceGrpc(ManagedChannel grpcChannel) {
        this.channel = grpcChannel;
        this.syncStub = StargateGrpc.newBlockingStub(channel)
                .withCallCredentials(new StargateBearerToken(token))
                .withDeadlineAfter(5, TimeUnit.SECONDS);
        this.reactiveStub = StargateGrpc.newStub(channel)
                .withCallCredentials(new StargateBearerToken(token))
                .withDeadlineAfter(5, TimeUnit.SECONDS);
        this.asyncStub = StargateGrpc.newFutureStub(channel)
                .withCallCredentials(new StargateBearerToken(token))
                .withDeadlineAfter(5, TimeUnit.SECONDS);
    }*/

    /**
     * Initialize grpc Channel based on configuration.
     *
     * @return
     *      a new instance for the channel
     *
    private ManagedChannel initChannel() {
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forTarget(grpcService.getEndpoint())
                .enableRetry()
                // Apply same try policy
                .maxRetryAttempts(grpcService.getMaxRetries())
                // Apply same keep alive timeout
                .keepAliveTimeout(grpcService.getKeepAliveTimeout(), grpcService.getKeepAliveTimeoutUnit())
                // Apply headers
                .userAgent(ApiConstants.REQUEST_WITH);
        // Astra = 443 so secure transport
        if (443 == grpcService.getPort()) {
            channelBuilder.useTransportSecurity();
        } else {
            channelBuilder.usePlaintext();
        }
        return channelBuilder.build();
    }*/

    /**
     * Check that a service is alive.
     *
     * @return
     *      validate that the current service is alive
     */
    @Override
    public boolean isAlive() {
        try {
            return HttpURLConnection.HTTP_OK == HttpClients
                    .createDefault()
                    .execute(new HttpGet(healthCheckEndpoint))
                    .getCode();
        } catch(Exception re) {
            return false;
        }
    }

    /**
     * Set value for maxRetries
     *
     * @param maxRetries new value for maxRetries
     */
    public void setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    /**
     * Set value for keepAliveTimeout
     *
     * @param keepAliveTimeout new value for keepAliveTimeout
     */
    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    /**
     * Gets keepAliveTimeoutUnit
     *
     * @return value of keepAliveTimeoutUnit
     */
    public TimeUnit getKeepAliveTimeoutUnit() {
        return keepAliveTimeoutUnit;
    }

    /**
     * Set value for keepAliveTimeoutUnit
     *
     * @param keepAliveTimeoutUnit new value for keepAliveTimeoutUnit
     */
    public void setKeepAliveTimeoutUnit(TimeUnit keepAliveTimeoutUnit) {
        this.keepAliveTimeoutUnit = keepAliveTimeoutUnit;
    }

    /**
     * Constructor.
     * @param id                  identifier
     * @param endpoint            endpoint
     * @param healthCheckEndpoint health check
     */
    public ServiceGrpc(String id, String endpoint, String healthCheckEndpoint) {
        super(id, endpoint, healthCheckEndpoint);
    }

    /**
     * Gets maxRetries
     *
     * @return value of maxRetries
     */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Gets keepAliveTimeout
     *
     * @return value of keepAliveTimeout
     */
    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    /**
     * Set value for keepAliveTimeout
     *
     * @param keepAliveTimeout new value for keepAliveTimeout
     */
    public void setKeepAliveTimeout(int keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }
}
