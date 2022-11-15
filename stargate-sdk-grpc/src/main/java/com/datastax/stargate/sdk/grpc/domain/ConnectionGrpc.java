package com.datastax.stargate.sdk.grpc.domain;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.core.ApiConstants;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.evanlennick.retry4j.config.RetryConfig;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.StargateGrpc;
import org.apache.hc.client5.http.config.RequestConfig;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

/**
 * Represent the grpc Connection context to a Stargate node.
 * - Channel is inialized once
 * - Stubs should be renew with the token
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ConnectionGrpc implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 9203216175966375038L;
    
    /** Channel. */
    private final ManagedChannel channel;
    
    /** keep reference to the node config. */
    private final StargateClientNode nodeConfig;
    
    /** stub for synchronous. */
    private final StargateGrpc.StargateBlockingStub syncStub;
    
    /** stub for reactive. */
    private final StargateGrpc.StargateStub reactiveStub;
    
    /** stub for asynchronous. */
    private final StargateGrpc.StargateFutureStub asyncStub;
    
    /**
     * Initialization of the connection with the node.
     *
     * @param node
     *      current stargate node.
     * @param token
     *      authentication token
     */
    public ConnectionGrpc(StargateClientNode node, String token) {
        this.nodeConfig = node;
        this.channel    = initChannel();
        
        this.syncStub   = StargateGrpc.newBlockingStub(channel)
                .withCallCredentials(new StargateBearerToken(token))
                .withDeadlineAfter(5, TimeUnit.SECONDS);
        this.reactiveStub = StargateGrpc.newStub(channel)
                .withCallCredentials(new StargateBearerToken(token))
                .withDeadlineAfter(5, TimeUnit.SECONDS);
        this.asyncStub = StargateGrpc.newFutureStub(channel)
                .withCallCredentials(new StargateBearerToken(token))
                .withDeadlineAfter(5, TimeUnit.SECONDS);
    }
    
    /**
     * Intialize grpc Channel based on configuration.
     *
     * @return
     *      a new instance for the channel
     */
    private ManagedChannel initChannel() {
        // Reusing parameters of the request and retry to adapt to grpc
        RequestConfig reqConf = HttpApisClient.getRequestConfig();
        RetryConfig retryConf = HttpApisClient.getRetryConfig();
        
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder
                .forAddress(nodeConfig.getGrpcHostName(), nodeConfig.getGrpcPort())
                .enableRetry()
                // Apply same try policy
                .maxRetryAttempts(retryConf.getMaxNumberOfTries())
                // Apply same keep alive timeout
                .keepAliveTimeout(reqConf.getConnectionKeepAlive().convert(TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                // Apply headers
                .userAgent(ApiConstants.REQUEST_WITH);
        // Astra = 443 so secure transport
        if (443 == nodeConfig.getGrpcPort()) {
             channelBuilder.useTransportSecurity(); 
         } else {
             channelBuilder.usePlaintext(); 
         }
         return channelBuilder.build();
    }

    
    /**
     * Getter accessor for attribute 'channel'.
     *
     * @return
     *       current value of 'channel'
     */
    public ManagedChannel getChannel() {
        return channel;
    }

    /**
     * Getter accessor for attribute 'nodeConfig'.
     *
     * @return
     *       current value of 'nodeConfig'
     */
    public StargateClientNode getNodeConfig() {
        return nodeConfig;
    }

    /**
     * Getter accessor for attribute 'syncStub'.
     *
     * @return
     *       current value of 'syncStub'
     */
    public StargateGrpc.StargateBlockingStub getSyncStub() {
        return syncStub;
    }

    /**
     * Getter accessor for attribute 'reactiveStub'.
     *
     * @return
     *       current value of 'reactiveStub'
     */
    public StargateGrpc.StargateStub getReactiveStub() {
        return reactiveStub;
    }

    /**
     * Getter accessor for attribute 'asyncStub'.
     *
     * @return
     *       current value of 'asyncStub'
     */
    public StargateGrpc.StargateFutureStub getAsyncStub() {
        return asyncStub;
    }
  
}
