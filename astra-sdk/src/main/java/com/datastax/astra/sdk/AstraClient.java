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

package com.datastax.astra.sdk;

import java.io.Closeable;
import java.io.File;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.databases.domain.Datacenter;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.sdk.utils.ApiLocator;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.grpc.ApiGrpcClient;
import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.utils.AnsiUtils;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Public interface to interact with ASTRA APIs.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraClient implements Closeable {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);
    
    /** Cloud Secure bundle file prefix, scb_dbId_dbRegion.zip */
    public static final String SECURE_CONNECT = "scb_";
    
    // -----------------------------------------------------
    // --------- Devops API Endpoints  ---------------------
    // -----------------------------------------------------
    
    /** Hold a reference for the Api Devops. */
    private DatabasesClient apiDevopsDatabases;
    
    /** Hold a reference for the Api Devops. */
    private OrganizationsClient apiDevopsOrganizations;
    
    /** Hold a reference for the Api Devops. */
    private StreamingClient apiDevopsStreaming;

    // -----------------------------------------------------
    // --------- Stargate APIs Settings --------------------
    // -----------------------------------------------------
    
    /** Access to all Stargate sub Api (rest, graphQL, doc, gRPC). */
    protected StargateClient stargateClient;
    
    /** Keep some information realted to Astra Settings. */
    protected AstraClientConfig astraClientConfig;
   
    /** Hold a reference on current region used for the Failover. */
    protected String currentDatabaseRegion;
    
    /**
     * Create a client with the token on
     *
     * @param token
     *      current token
     */
    public AstraClient(String token) {
        this(builder().withToken(token));
    }
    
    /**
     * Simple initialization.
     *
     * @param dbId
     *      database id
     * @param dbRegion
     *      database region
     * @param token
     *      current token
     */
    public AstraClient(String dbId, String dbRegion, String token) {
        this(builder()
                .withToken(token)
                .withDatabaseId(dbId)
                .withDatabaseRegion(dbRegion));
    }
    
    /**
     * Initialization through builder.
     * 
     * @param config
     *      configuration extrated from builder
     */
    public AstraClient(AstraClientConfig config) {
        this.astraClientConfig = config;
        
        // ---------------------------------------------------
        //  Devops APIS
        // ---------------------------------------------------
        
        if (Utils.hasLength(config.getToken())) {
            apiDevopsOrganizations  = new OrganizationsClient(config.getToken());
            apiDevopsDatabases      = new DatabasesClient(config.getToken());  
            apiDevopsStreaming      = new StreamingClient(config.getToken());
            LOGGER.info("+ API(s) Devops     [" + AnsiUtils.green("ENABLED")+ "]");
        } else {
            LOGGER.info("+ API(s) Devops     [" + AnsiUtils.red("DISABLED")+ "]");
        }
       
        // ---------------------------------------------------
        //  Stargate 
        // ---------------------------------------------------
        
        if (Utils.hasAllLength(config.getDatabaseId(), config.getDatabaseRegion())) {
            LOGGER.info("+ Using db with id [" 
                        + AnsiUtils.cyan("{}")+ "] and region ["
                        + AnsiUtils.cyan("{}")+ "]", 
                    config.getDatabaseId(), 
                    config.getDatabaseRegion());
            this.currentDatabaseRegion = config.getDatabaseRegion();
            
            // ---------------------------------------------------
            //       CQL / Cloud Secure Bundles
            // ---------------------------------------------------
            
            if (!new File(config.getSecureConnectBundleFolder()).exists()) {
                new File(config.getSecureConnectBundleFolder()).mkdirs();
            }
            // Download secure bundles (if needed)
            LOGGER.info("+ Downloading bundles in: [" + AnsiUtils.cyan("{}") + "]", config.getSecureConnectBundleFolder());
            apiDevopsDatabases.database(config.getDatabaseId())
                              .downloadAllSecureConnectBundles(config.getSecureConnectBundleFolder());
            
            // Enforce secure bundle for each DC
            
            Set<Datacenter> regions = apiDevopsDatabases
                    .database(config.getDatabaseId())
                    .find().get().getInfo()
                    .getDatacenters();
            
            regions.stream().forEach(dc -> {
               config.getStargateConfig()
                     .withCqlCloudSecureConnectBundleDC(dc.getRegion(), 
                       config.getSecureConnectBundleFolder() + File.separator + 
                         AstraClientConfig.buildScbFileName(config.getDatabaseId(), dc.getRegion()));
            });
            // Setup the current region
            config.getStargateConfig().withCqlCloudSecureConnectBundle(config.getSecureConnectBundleFolder() + 
                    File.separator + AstraClientConfig.buildScbFileName(config.getDatabaseId(), config.getDatabaseRegion()));
            
            // ---------------------------------------------------
            //       CQL / Credentials
            // ---------------------------------------------------
            
            if (Utils.hasAllLength(config.getClientId(), config.getClientSecret())) {
                config.getStargateConfig().withAuthCredentials(config.getClientId(), config.getClientSecret());
                LOGGER.info("+ Credentials used for Cql connection are clientId/clientSecret");
            } else {
                config.getStargateConfig().withAuthCredentials("token", config.getToken());
                LOGGER.info("+ Credentials used for Cql are based on the token ");
            }
            
            // ---------------------------------------------------
            //       CQL / Timeouts
            // ---------------------------------------------------
            
            // Region to setup Stargate
            config.getStargateConfig()
                  .withCqlDriverOption(TypedDriverOption.REQUEST_CONSISTENCY, ConsistencyLevel.LOCAL_QUORUM.name())
                  .withCqlDriverOption(TypedDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10))
                  .withCqlDriverOption(TypedDriverOption.REQUEST_PAGE_SIZE, 100)
                  .withCqlDriverOption(TypedDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(10))
                  .withCqlDriverOption(TypedDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(10))
                  .withCqlDriverOption(TypedDriverOption.CONNECTION_SET_KEYSPACE_TIMEOUT, Duration.ofSeconds(10))
                  .withCqlDriverOption(TypedDriverOption.CONTROL_CONNECTION_TIMEOUT, Duration.ofSeconds(10))
                  // Failover options: 
                  // https://docs.datastax.com/en/developer/java-driver/4.13/manual/core/load_balancing/#cross-datacenter-failover
                  .withCqlDriverOption(TypedDriverOption.LOAD_BALANCING_DC_FAILOVER_ALLOW_FOR_LOCAL_CONSISTENCY_LEVELS, true)
                  .withCqlDriverOption(TypedDriverOption.LOAD_BALANCING_DC_FAILOVER_MAX_NODES_PER_REMOTE_DC, 3);
            
            // ---------------------------------------------------
            //     Stargate Node per region
            // ---------------------------------------------------
            regions.stream().forEach(dc -> {
                config.getStargateConfig().withApiNodeDC(dc.getRegion(), 
                        new StargateNodeConfig(
                                // node name = region, we got a single per region LB is done by Astra
                                dc.getRegion(), 
                                // url or rest api
                                ApiLocator.getApiRestEndpoint(config.getDatabaseId(), dc.getRegion()),
                                // url of graphql API
                                ApiLocator.getApiGraphQLEndPoint(config.getDatabaseId(), dc.getRegion()),
                                // host for grpc
                                ApiLocator.getApiGrpcEndPoint(config.getDatabaseId(), dc.getRegion()),
                                // port for grpc
                                AstraClientConfig.GRPC_PORT));
              }
            );
            // Set default region
            config.getStargateConfig().withLocalDatacenter(config.getDatabaseRegion());
            this.stargateClient =  config.getStargateConfig().build();
        } else {
           LOGGER.info("+ API(s) CqlSession [" + AnsiUtils.red("DISABLED")+ "]");
           LOGGER.info("+ API(s) Document   [" + AnsiUtils.red("DISABLED")+ "]");
           LOGGER.info("+ API(s) Rest       [" + AnsiUtils.red("DISABLED")+ "]");
           LOGGER.info("+ API(s) gRPC       [" + AnsiUtils.red("DISABLED")+ "]");
        }
        LOGGER.info("[" + AnsiUtils.yellow("AstraClient") + "] has been initialized.");
    }
    
    /**
     * Document Api.
     * 
     * @return ApiDocumentClient
     */
    public ApiDocumentClient apiStargateDocument() {
        if (stargateClient == null) {
            throw new IllegalStateException("Api Document is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.apiDocument();
    }
    
    /** 
     * Rest Api. 
     * 
     * @return ApiRestClient
     */
    public ApiDataClient apiStargateData() {
        if (stargateClient == null) {
            throw new IllegalStateException("Api Rest is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.apiRest();
    }
    
    /** 
     * GraphQL Api. 
     * 
     * @return ApiGraphQLClient
     */
    public ApiGraphQLClient apiStargateGraphQL() {
        if (stargateClient == null) {
            throw new IllegalStateException("GraphQL Api is not available "
                    + "you need to provide dbId/dbRegion/token at initialization.");
        }
        return stargateClient.apiGraphQL();
    }
    
    /**
     * Integration with grpc Api in Stargate.
     *
     * @return
     *      grpc Stargate API.
     */
    public ApiGrpcClient apiStargateGrpc() {
        if (stargateClient == null) {
            throw new IllegalStateException("GRPC Api is not available "
                    + "you need to provide dbId/dbRegion/token at initialization.");
        }
        return stargateClient.apiGrpc();
    }
    
    /**
     * Devops API
     * 
     * @return ApiDevopsClient
     */
    public OrganizationsClient apiDevopsOrganizations() {
        if (apiDevopsOrganizations == null) {
            throw new IllegalStateException("Api Devops is not available "
                    + "you need to provide a astra Token (AstraCS:...) at initialization.");
        }
        return apiDevopsOrganizations;
    }
    
    /**
     * Devops API
     * 
     * @return ApiDevopsClient
     */
    public DatabasesClient apiDevopsDatabases() {
        if (apiDevopsDatabases == null) {
            throw new IllegalStateException("Api Devops is not available "
                    + "you need to provide clientId/clientName/clientSecret at initialization.");
        }
        return apiDevopsDatabases;
    }
    
    
    /**
     * Devops API Streaming
     * 
     * @return ApiDevopsClient
     */
    public StreamingClient apiDevopsStreaming() {
        if (apiDevopsStreaming == null) {
            throw new IllegalStateException("Api Devops is not available "
                    + "you need to provide clientId/clientName/clientSecret at initialization.");
        }
        return apiDevopsStreaming;
    }
    
    /**
     * CQL API
     * 
     * @return CqlSession
     */
    public CqlSession cqlSession() {
        if (stargateClient == null || !stargateClient.cqlSession().isPresent()) {
            throw new IllegalStateException("CQL not available  Rest is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.cqlSession().get();
    }
    
    /**
     * Give access to token.
     * 
     * @return
     *      token value
     */
    public Optional<String> getToken() {
        return Optional.ofNullable(astraClientConfig.getToken());
    }
    
    /**
     * Getter to the configuratin, it should not be changed.
     * 
     * @return
     *      initial configuration
     */
    public AstraClientConfig getConfig() {
        return this.astraClientConfig;
    }
    
    /**
     * Change region the application is working on.
     * 
     * @param region
     *      new region
     */
    public void useRegion(String region) {
        LOGGER.info("Switch to region : {}", region);
        this.currentDatabaseRegion = region;
        this.stargateClient.setCurrentDatacenter(region);
        this.stargateClient.renewCqlSession();
    }
    
    /**
     * Builder Pattern
     * 
     * @return AstraClientBuilder
     */
    public static final AstraClientConfig builder() {
        return new AstraClientConfig();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
       if (null != stargateClient) {
           stargateClient.close();
       }
    }

    /**
     * Getter accessor for attribute 'stargateClient'.
     *
     * @return
     *       current value of 'stargateClient'
     */
    public StargateClient getStargateClient() {
        return stargateClient;
    }

}
