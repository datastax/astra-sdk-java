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

package io.stargate.sdk;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import io.stargate.sdk.audit.ServiceCallObserver;
import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.gql.StargateGraphQLApiClient;
import io.stargate.sdk.grpc.ServiceGrpc;
import io.stargate.sdk.grpc.StargateGrpcApiClient;
import io.stargate.sdk.http.RetryHttpClient;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.utils.Utils;
import io.stargate.sdk.utils.AnsiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Global Client to interact with a Stargate instance.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClient implements Closeable {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClient.class);

    /** Configuration. */
    private StargateClientBuilder conf;

    /** The Cql Session to interact with Cassandra through the DataStax CQL Drivers. */
    private CqlSession cqlSession;
    
    /** 
     * Local DC. (load balancing on the nodes there and failover only if not available. 
     */
    protected String currentDatacenter;
    
    /** 
     * Wrapping Api REST DATA resources.
     */
    protected StargateRestApiClient apiDataClient;
    
    /**
     * Wrapping Api Document resources.
     */
    protected StargateDocumentApiClient apiDocumentClient;
    
    /**
     * Wrapping Api GraphQL resources.
     */
    protected StargateGraphQLApiClient apiGraphQLClient;
    
    /**
     * Wrapping Api GRPC resources.
     */
    protected StargateGrpcApiClient apiGrpcClient;

    // ------------------------------------------------
    // ---------------- Initializing   ----------------
    // ------------------------------------------------

    public StargateClient() {
        this(builder());
    }

    /**
     * Initialize the client from builder parameters.
     *
     * @param config
     *      current builder.
     */
    public StargateClient(StargateClientBuilder config) {
        if (config.isEnabledCql()) {
            cqlSession = initCqlSession();
            LOGGER.info("+ API Cql      :[" + AnsiUtils.green("ENABLED") + "]");
        } else {
            LOGGER.info("+ API Cql      :[" + AnsiUtils.yellow("DISABLED") + "]");
        }

        // -- Service deployments for 4 apis
        if (config.getStargateNodesDC().isEmpty()) {
            // No configuration = default (test)
            this.apiDataClient      = new StargateRestApiClient();
            this.apiDocumentClient  = new StargateDocumentApiClient();
            this.apiGraphQLClient   = new StargateGraphQLApiClient();
            this.apiGrpcClient      = new StargateGrpcApiClient();
        } else {
            // Creating deployement base on configuration
            ServiceDeployment restDeploy = new ServiceDeployment<ServiceHttp>();
            ServiceDeployment docDeploy = new ServiceDeployment<ServiceHttp>();
            ServiceDeployment gqlDeploy = new ServiceDeployment<ServiceHttp>();
            ServiceDeployment grpcDeploy = new ServiceDeployment<ServiceGrpc>();
            config.getStargateNodesDC().values().stream().forEach(dc -> {
                // Current dc
                restDeploy.addDatacenter(new ServiceDatacenter(dc.getId(), dc.getTokenProvider(), dc.getRestNodes()));
                docDeploy.addDatacenter(new ServiceDatacenter(dc.getId(), dc.getTokenProvider(), dc.getDocNodes()));
                gqlDeploy.addDatacenter(new ServiceDatacenter(dc.getId(), dc.getTokenProvider(), dc.getGraphqlNodes()));
                grpcDeploy.addDatacenter(new ServiceDatacenter(dc.getId(), dc.getTokenProvider(), dc.getGrpcNodes()));
            });
            this.apiDataClient = new StargateRestApiClient(restDeploy);
            //this.apiGraphQLClient = new StargateGraphQLApiClient(gqlDeploy);
            //this.apiDocumentClient = new StargateDocumentApiClient(docDeploy);
            this.apiGrpcClient = new StargateGrpcApiClient(grpcDeploy);
        }

        // ------------- HTTP ---------------------
        
        if (config.getRetryConfig() != null) {
            RetryHttpClient.withRetryConfig(config.getRetryConfig());
        }
        if (config.getRequestConfig() != null) {
            RetryHttpClient.withRequestConfig(config.getRequestConfig());
        }
        if (!config.getObservers().isEmpty()) {
            for (Map.Entry<String, ServiceCallObserver> obs : config.getObservers().entrySet()) {
                RetryHttpClient.registerListener(obs.getKey(), obs.getValue());
            }
        }
    }
    
    /**
     * Datacenter name can be retrieved on multiple ways:
     * - 1. Explicitely populated
     * - 2. As a cql property
     * - 3. In the Stargate node topology
     * - 4. Default Value dc1
     *
     * @param config
     *      current configuration
     * @return
     */
    private String resolveDataCenterName(StargateClientBuilder config) {
        // If not #1...
        if (!Utils.hasLength(config.getLocalDatacenter())) {
            LOGGER.info("Looking for local datacenter name");
            // #2. Read from CQL
            String cqlDc = config.getCqlOptions().get(TypedDriverOption.LOAD_BALANCING_LOCAL_DATACENTER);
            if (Utils.hasLength(cqlDc)) {
                config.withLocalDatacenter(cqlDc);
                LOGGER.info("+ Using value defined in cql configuration {}", cqlDc);
            // #3. Read from node topology
            } else if (!config.getStargateNodesDC().isEmpty()) {
                Set< String > dcs = config.getStargateNodesDC().keySet();
                String dcPicked = dcs.iterator().next();
                config.withLocalDatacenter(dcPicked);
                LOGGER.info("+ Using value from node topology '{}' ( '{}' dc found)", dcPicked, dcs.size());
            // #4. Default
            } else {
                LOGGER.warn("+ Using default '{}'", StargateClientBuilder.DEFAULT_DATACENTER);
                config.withLocalDatacenter(StargateClientBuilder.DEFAULT_DATACENTER);
            }
        }
        return config.getLocalDatacenter();
    }
    
    /**
     * Initializing  CqlSession based on current parameters.
     * 
     * - Parameters can be provided by the Builder populating Values
     * - Parameters can be provided by the configuration keys in Spring application.yaml
     *
     * @return
     *      a new cqlSession
     */
    public CqlSession initCqlSession() {

        /* ---------------------------------------
         * Close and cleaan up existing session:
         * This behaviour occurs when you failover from one DC to another in Astra
         * ---------------------------------------*/
        if (null != cqlSession && !cqlSession.isClosed()) {
            cqlSession.close();
            cqlSession = null;
        }
        
        // Only create if CQL Session if enabled, always.
        if (conf.isEnabledCql()) {

            // A CQL Session has been provided, we will reuse it
            if (conf.getCqlSession() != null) {
                cqlSession = conf.getCqlSession();
                
            } else {
                
                // SCB
                String scb = conf.getCqlOptions().get(this.currentDatacenter, TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE);
                if (Utils.hasLength(scb)) {
                    conf.withCqlCloudSecureConnectBundle(scb);
                    conf.setLocalDatacenter(currentDatacenter);
                }
                
                // CONTACT POINTS
                List<String> configContactPointsDC = conf.getCqlOptions().get(this.currentDatacenter, TypedDriverOption.CONTACT_POINTS);
                if (configContactPointsDC != null && !configContactPointsDC.isEmpty()) {
                    conf.withCqlContactPoints(configContactPointsDC.toArray(new String[0]));
                    conf.withLocalDatacenter(currentDatacenter);
                }
                
                // Configuration through Map values
                DriverConfigLoader configLoader = conf.getCqlDriverConfigLoaderBuilder().build();
                CqlSessionBuilder sessionBuilder = CqlSession.builder().withConfigLoader(configLoader);
                // Expand configuration
                if (null != conf.getCqlMetricsRegistry()) {
                    sessionBuilder.withMetricRegistry(conf.getCqlMetricsRegistry());
                }
                // Request Tracking
                if (null != conf.getCqlRequestTracker()) {
                    sessionBuilder.withRequestTracker(conf.getCqlRequestTracker());
                }
                cqlSession = sessionBuilder.build();
            }
            
        }
        
        // Testing CqlSession
        if (cqlSession != null) {
            String currentDC = cqlSession.execute("SELECT data_center from system.local").one().getString("data_center");
            if (cqlSession.getKeyspace().isPresent()) {
                LOGGER.info("+ CqlSession   :[" + AnsiUtils.green("ENABLED") + "] with keyspace [" + AnsiUtils.cyan("{}")  +"] and dc [" + AnsiUtils.cyan("{}")  + "]",
                        cqlSession.getKeyspace().get(), currentDC);
            } else {
                LOGGER.info("+ CqlSession   :[" + AnsiUtils.green("ENABLED") + "]");
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
            LOGGER.info("+ CqlSession   :[" + AnsiUtils.red("DISABLED") + "]");
        }
        return cqlSession;
    }
    
    /**
     * Builder Pattern
     * @return StargateClientBuilder
     */
    public static final StargateClientBuilder builder() {
        return new StargateClientBuilder();
    }
    
    // ------------------------------------------------
    // ---------------- Utilities   -------------------
    // ------------------------------------------------
    
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
    public StargateDocumentApiClient apiDocument() {
        if (apiDocumentClient == null) {
            throw new IllegalStateException("Document Api is not available please provide a service deployment for Documentxs");
        }
       return this.apiDocumentClient;
    }
    
    /**
     * Retrieve API Data, doing load balancing, failover and retries.
     *
     * @return
     *      Api REST DATA client
     */
    public StargateRestApiClient apiRest() {
        if (apiDataClient == null) {
            throw new IllegalStateException("REST Data Api is not available please provide a service deployment for Rest Data");
        }
        return this.apiDataClient;
    }
    
    /**
     * Retrieve API GraphQL, doing load balancing, failover and retries.
     * 
     * @return
     *      Api graphQL client
     */
    public StargateGraphQLApiClient apiGraphQL() {
        if (apiGraphQLClient == null) {
            throw new IllegalStateException("GraphQL Api is not available please provide a service deployment for GraphQL");
        }
        return this.apiGraphQLClient;
    }
    
    /**
     * Retrieve items using the gRPC interface
     * 
     * @return
     *      grpc interface
     */
    public StargateGrpcApiClient apiGrpc() {
        if (apiGrpcClient == null) {
            throw new IllegalStateException("GRPC Api is not available please provide a service deployment for gRPC");
        }
        return this.apiGrpcClient;
    }

    /**
     * Set value for currentDatacenter
     *
     * @param currentDatacenter new value for currentDatacenter
     */
    public void setCurrentDatacenter(String currentDatacenter) {
        this.currentDatacenter = currentDatacenter;
    }
}
