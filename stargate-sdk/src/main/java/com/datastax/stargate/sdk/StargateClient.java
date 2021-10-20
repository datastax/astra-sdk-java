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
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.config.StargateClientConfig;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
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
     * Wrapping failover and Load balancer on a delegated wrapper. 
     */
    protected StargateHttpClient stargateHttpClient;
    
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
        // Local DataCenter (if no secureconnect bundle)
        if (!Utils.hasLength(config.getLocalDC())) {
            config.withLocalDatacenter(StargateClientConfig.DEFAULT_LOCALDC);
            LOGGER.warn("+ No local datacenter provided, using default {}", StargateClientConfig.DEFAULT_LOCALDC);
        }
        this.currentDatacenter = config.getLocalDC();
        
        // Initializing the Stargate Http Clients
        stargateHttpClient = new StargateHttpClient(this, config);
        
        // CqlSession
        if (!config.isDisableCqlSession()) {
            if (config.getCqlSession() != null) {
                // A CQL Session has been provided, we will reuse it
                cqlSession = config.getCqlSession();
            }
            // Check options and add default if needed
            if (null == config.getOptions().get(TypedDriverOption.CONTACT_POINTS) || 
                config.getOptions().get(TypedDriverOption.CONTACT_POINTS).isEmpty()) {
                // Defaulting for contact points if no securebundle provided
                if (null == config.getOptions().get(TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE)) {
                    LOGGER.info("+ No contact points provided, using default {}", StargateClientConfig.DEFAULT_CONTACTPOINT);
                    config.getOptions().put(TypedDriverOption.CONTACT_POINTS, Arrays.asList(StargateClientConfig.DEFAULT_CONTACTPOINT));
                }
            }
            // Using a Config Map instead of CqlSessionBuilder to create Execution Profiles
            cqlSession = CqlSession.builder()
                                   .withConfigLoader(DriverConfigLoader.fromMap(config.getOptions()))
                                   .build();
        }
        
        // Testing CqlSession
        if (cqlSession != null) {
            cqlSession.execute("SELECT data_center from system.local");
            if (cqlSession.getKeyspace().isPresent()) {
                LOGGER.info("+ CqlSession  : [" + green("ENABLED") + "] with keyspace [" + cyan("{}")  +"]",  cqlSession.getKeyspace().get());
                
            } else {
                LOGGER.info("+ CqlSession  : [" + green("ENABLED") + "]");
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
            LOGGER.info("+ CqlSession  : [" + red("DISABLED") + "]");
        }
       
        // Initializing Apis
        this.apiDataClient     = new ApiDataClient(stargateHttpClient);
        this.apiDocumentClient = new ApiDocumentClient(stargateHttpClient);
        this.apiGraphQLClient  = new ApiGraphQLClient(stargateHttpClient);
        
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
       return apiDocumentClient;
    }
    
    /**
     * Retrieve API Data, doing load balancing, failover and retries.
     *
     * @return
     *      Api REST DATA client
     */
    public ApiDataClient apiRest() {
        return apiDataClient;
    }
    
    /**
     * Retrieve API GraphQL, doing load balancing, failover and retries.
     * 
     * @return
     *      Api graphQL client
     */
    public ApiGraphQLClient apiGraphQL() {
        return apiGraphQLClient;
    }

    /**
     * Getter accessor for attribute 'currentDatacenter'.
     *
     * @return
     *       current value of 'currentDatacenter'
     */
    public String getCurrentDatacenter() {
        return currentDatacenter;
    }
    
}
