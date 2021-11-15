/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk;

import static com.datastax.stargate.sdk.utils.AnsiUtils.cyan;
import static com.datastax.stargate.sdk.utils.AnsiUtils.green;
import static com.datastax.stargate.sdk.utils.AnsiUtils.red;

import java.io.Closeable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.config.StargateClientConfig;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.grpc.ApiGrpcClient;
import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.utils.AnsiUtils;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Global Client to interact with a Stargate instance.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClient implements Closeable {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClient.class);
    
    /** The Cql Session to interact with Cassandra through the DataStax CQL Drivers. */
    private CqlSession cqlSession;
    
    /** Local DC. (load balancing on the nodes there and failover only if not available. */
    protected String currentDatacenter;
    
    /** 
     * Wrapping Api REST DATA resources.
     */
    protected ApiDataClient apiDataClient;
    
    /**
     * Wrapping Api Document resources.
     */
    protected ApiDocumentClient apiDocumentClient;
    
    /**
     * Wrapping Api GraphQL resources.
     */
    protected ApiGraphQLClient apiGraphQLClient;
    
    /**
     * Wrapping Api GRPC resources.
     */
    protected ApiGrpcClient apiGrpcClient;
    
    /** 
     * Wrapping failover and Load balancer on a delegated wrapper. 
     */
    protected StargateHttpClient stargateHttpClient;
    
    /**
     * Cloud be use to recreate a CqlSession during DC fail-over
     */
    protected final StargateClientConfig conf;
    
    // ------------------------------------------------
    // ---------------- Initializing   ----------------
    // ------------------------------------------------
    
    /**
     * Initialize the client from builder parameters.
     *
     * @param config
     *      current builder.
     */
    public StargateClient(StargateClientConfig config) {
        LOGGER.info("Initializing [" + AnsiUtils.yellow("StargateClient") + "]");
        this.conf = config;
        // Local DataCenter (if no secureconnect bundle)
        if (!Utils.hasLength(config.getLocalDC())) {
            config.withLocalDatacenter(StargateClientConfig.DEFAULT_LOCALDC);
            LOGGER.warn("+ No local datacenter provided, using default {}", StargateClientConfig.DEFAULT_LOCALDC);
        }
        this.currentDatacenter = config.getLocalDC();
        
        // Initializing the Stargate Http Clients
        stargateHttpClient = new StargateHttpClient(this, config);
        
        // Initialize cqlSession
        cqlSession = renewCqlSession();
       
        // Initializing Apis
        if (!config.getStargateNodes().isEmpty()) {
            this.apiDataClient     = new ApiDataClient(stargateHttpClient);
            this.apiDocumentClient = new ApiDocumentClient(stargateHttpClient);
            this.apiGraphQLClient  = new ApiGraphQLClient(stargateHttpClient);
            this.apiGrpcClient     = new ApiGrpcClient(stargateHttpClient);
        } else {
            LOGGER.info("+ API Data     :[" + red("DISABLED") + "]");
            LOGGER.info("+ API Document :[" + red("DISABLED") + "]");
            LOGGER.info("+ API GraphQL  :[" + red("DISABLED") + "]");
            LOGGER.info("+ API Grpc     :[" + red("DISABLED") + "]");
        }
        
        /** HTTP. */
        if (config.getRetryConfig() != null) {
            HttpApisClient.withRetryConfig(config.getRetryConfig());
        }
        if (config.getRequestConfig() != null) {
            HttpApisClient.withRequestConfig(config.getRequestConfig());
        }
        if (!config.getObservers().isEmpty()) {
            for (Map.Entry<String, ApiInvocationObserver> obs : config.getObservers().entrySet()) {
                HttpApisClient.registerListener(obs.getKey(), obs.getValue());
            }
        }
    }   
    
    /**
     * Initializing  CqlSession based on current parameters.
     *
     * @return
     *      a new cqlSession
     */
    public CqlSession renewCqlSession() {
        if (null != cqlSession && !cqlSession.isClosed()) {
            cqlSession.close();
            cqlSession = null;
        }
        
        if (!conf.isDisableCqlSession()) {
            if (conf.getCqlSession() != null) {
                // A CQL Session has been provided, we will reuse it
                cqlSession = conf.getCqlSession();
            }
            // Check options and add default if needed
            if (null == conf.getOptions().get(TypedDriverOption.CONTACT_POINTS) || 
                    conf.getOptions().get(TypedDriverOption.CONTACT_POINTS).isEmpty()) {
                // Defaulting for contact points if no securebundle provided
                if (null == conf.getOptions().get(TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE)) {
                    LOGGER.info("+ No contact points provided, using default {}", StargateClientConfig.DEFAULT_CONTACTPOINT);
                    conf.getOptions().put(TypedDriverOption.CONTACT_POINTS, Arrays.asList(StargateClientConfig.DEFAULT_CONTACTPOINT));
                }
            }
            
            // Extract keys for dedicated DC on current when making send
            conf.getOptions().put(TypedDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, this.currentDatacenter);
            
            // CONTACT POINTS
            List<String> configContactPointsDC = conf.getOptions().get(this.currentDatacenter, TypedDriverOption.CONTACT_POINTS);
            if (configContactPointsDC != null && 
               !configContactPointsDC.isEmpty()) {
                conf.getOptions().put(TypedDriverOption.CONTACT_POINTS, configContactPointsDC);
            }
            
            // SCB
            String scb = conf.getOptions().get(this.currentDatacenter, TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE);
            if (Utils.hasLength(scb)) {
                conf.getOptions().put(TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, scb);
            }
           
            // Configuration through Map values
            CqlSessionBuilder sessionBuilder = CqlSession.builder()
                    .withConfigLoader(DriverConfigLoader.fromMap(conf.getOptions()));
            // Expand configuration
            if (null != conf.getMetricsRegistry()) {
                sessionBuilder.withMetricRegistry(conf.getMetricsRegistry());
            }
            // Request Tracking
            if (null != conf.getCqlRequestTracker()) {
                sessionBuilder.withRequestTracker(conf.getCqlRequestTracker());
            }
            // Final Customizations
            if (null != conf.getCqlSessionBuilderCustomizer()) {
                conf.getCqlSessionBuilderCustomizer().customize(sessionBuilder);
            }
            cqlSession = sessionBuilder.build();
        }
        
        // Testing CqlSession
        if (cqlSession != null) {
            String currentDC = cqlSession.execute("SELECT data_center from system.local").one().getString("data_center");
            if (cqlSession.getKeyspace().isPresent()) {
                LOGGER.info("+ CqlSession   :[" + green("ENABLED") + "] with keyspace [" + cyan("{}")  +"] and dc [" + cyan("{}")  + "]",
                        cqlSession.getKeyspace().get(), currentDC);
            } else {
                LOGGER.info("+ CqlSession   :[" + green("ENABLED") + "]");
            }
                
            // As we opened a cqlSession we may want to close it properly at application shutdown.
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    if (!cqlSession.isClosed()) {
                        cqlSession.close();
                        LOGGER.info("Closing CqlSession.");
                    }
                } 
            });
        } else {
            LOGGER.info("+ CqlSession   :[" + red("DISABLED") + "]");
        }
        return cqlSession;
    }
    
    /**
     * Builder Pattern
     * @return StargateClientBuilder
     */
    public static final StargateClientConfig builder() {
        return new StargateClientConfig();
    }
    
    // ------------------------------------------------
    // ---------------- Utilities   -------------------
    // ------------------------------------------------
    
    /**
     * Delegating to HTTP.
     *
     * @param datacenter
     *      target datacenter
     */
    public void useDataCenter(String datacenter) {
        stargateHttpClient.useDataCenter(datacenter);
    }
    
    /** {@inheritDoc} */
    @Override
    public void close() {
        if (null != cqlSession && !cqlSession.isClosed()) {
            cqlSession.close();
            LOGGER.info("Closing CqlSession.");
        }
    }
    
    // ------------------------------------------------
    // ---------------- Accessors   -------------------
    // ------------------------------------------------
    
    /**
     * Accessing Cql Session.
     * @return CqlSession
     */
    public Optional<CqlSession> cqlSession() {
        return Optional.ofNullable(cqlSession);
    }
    
    /**
     * Retrieve API Document, doing load balancing, failover and retries.
     *
     * @return
     *      current API Document
     */
    public ApiDocumentClient apiDocument() {
        if (apiDocumentClient == null) {
            throw new IllegalStateException("Api Document is not available "
                    + "you need to provide a node and credentials at initialization.");
        }
       return this.apiDocumentClient;
    }
    
    /**
     * Retrieve API Data, doing load balancing, failover and retries.
     *
     * @return
     *      Api REST DATA client
     */
    public ApiDataClient apiRest() {
        if (apiDataClient == null) {
            throw new IllegalStateException("Api Rest is not available "
                    + "you need to provide a node and credentials at initialization.");
        }
        return this.apiDataClient;
    }
    
    /**
     * Retrieve API GraphQL, doing load balancing, failover and retries.
     * 
     * @return
     *      Api graphQL client
     */
    public ApiGraphQLClient apiGraphQL() {
        if (apiGraphQLClient == null) {
            throw new IllegalStateException("Api GraphQL is not available "
                    + "you need to provide a node and credentials at initialization.");
        }
        return this.apiGraphQLClient;
    }
    
    /**
     * Retrieve items using the gRPC interface
     * 
     * @return
     *      grpc interface
     */
    public ApiGrpcClient apiGrpc() {
        if (apiGrpcClient == null) {
            throw new IllegalStateException("Api Grpc is not available "
                    + "you need to provide a node and credentials at initialization.");
        }
        return this.apiGrpcClient;
    }

    /**
     * Getter accessor for attribute 'currentDatacenter'.
     *
     * @return
     *       current value of 'currentDatacenter'
     */
    public String getCurrentDatacenter() {
        return this.currentDatacenter;
    }
    
    /**
     * Setter accessor for attribute 'currentDatacenter'.
     * @param currentDatacenter
     *      new value for 'currentDatacenter '
     */
    public void setCurrentDatacenter(String currentDatacenter) {
        this.currentDatacenter = currentDatacenter;
    }
    
    /**
     * Getter accessor for attribute 'stargateHttpClient'.
     *
     * @return
     *       current value of 'stargateHttpClient'
     */
    public StargateHttpClient getStargateHttpClient() {
        return this.stargateHttpClient;
    }
    
}
