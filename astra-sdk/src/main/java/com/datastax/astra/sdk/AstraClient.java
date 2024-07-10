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

import com.datastax.astra.sdk.config.AstraClientConfig;
import com.datastax.oss.driver.api.core.CqlSession;
import com.dtsx.astra.sdk.AstraOpsClient;
import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.utils.ApiLocator;
import io.stargate.sdk.StargateClient;
import io.stargate.sdk.api.SimpleTokenProvider;
import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.gql.StargateGraphQLApiClient;
import io.stargate.sdk.grpc.ServiceGrpc;
import io.stargate.sdk.grpc.StargateGrpcApiClient;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.utils.AnsiUtils;
import io.stargate.sdk.utils.Utils;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.util.Optional;

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
    private AstraOpsClient apiDevops;

    /** Hold a reference for the Api Devops. */
    private AstraDBOpsClient apiDevopsDatabases;

    /** Hold a reference for the Api Devops. */
    private AstraStreamingClient apiDevopsStreaming;

    // -----------------------------------------------------
    // --------- Stargate APIs Settings --------------------
    // -----------------------------------------------------
    
    /** Access to all Stargate sub Api (rest, graphQL, doc, gRPC). */
    @Getter
    protected StargateClient stargateClient;
    
    /** Keep some information related to Astra Settings. */
    protected AstraClientConfig astraClientConfig;
   
    /** Hold a reference on current region used for the Fail-over. */
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
     *      configuration extracted from builder
     */
    public AstraClient(AstraClientConfig config) {
        this.astraClientConfig = config;

        // ---------------------------------------------------
        //  Devops APIS
        // ---------------------------------------------------
        if (Utils.hasLength(config.getToken())) {
            apiDevops           = new AstraOpsClient(config.getToken(), config.getEnvironmnt());
            apiDevopsDatabases  = apiDevops.db();
            apiDevopsStreaming  = apiDevops.streaming();
            LOGGER.info("+ API Devops    [" + AnsiUtils.green("ENABLED")+ "] on [" + config.getEnvironmnt() + "]");
        } else {
            LOGGER.info("+ API Devops    [" + AnsiUtils.red("DISABLED")+ "]");
        }
       
        // ---------------------------------------------------
        //  Stargate 
        // ---------------------------------------------------
        
        if (Utils.hasAllLength(config.getDatabaseId(), config.getDatabaseRegion())) {
            LOGGER.info("+ Db: id [" 
                        + AnsiUtils.cyan("{}")+ "] and region ["
                        + AnsiUtils.cyan("{}")+ "]", 
                    config.getDatabaseId(), 
                    config.getDatabaseRegion());
            
            this.currentDatabaseRegion = config.getDatabaseRegion();
            
            // Set default region (not in the cql as SCB is there)
            config.getStargateConfig().setLocalDatacenter(config.getDatabaseRegion());
            
            // CqlSession should be initialized only if the flag is on.
            if (config.getStargateConfig().isEnabledCql()) {
            
                // Downloading SCB is enabled (default is true)
                if (config.isEnabledDownloadSecureConnectBundle()) {
                    downloadAndSetupSecureConnectBundle(config);
                }
                
                // ---------------------------------------------------
                //       CQL / Credentials
                // ---------------------------------------------------
                
                if (Utils.hasAllLength(config.getClientId(), config.getClientSecret())) {
                    config.getStargateConfig().withAuthCredentials(config.getClientId(), config.getClientSecret());
                } else {
                    config.getStargateConfig().withAuthCredentials("token", config.getToken());
                }
            }
            
            // ---------------------------------------------------
            //     Stargate Node per region
            // ---------------------------------------------------
            
            if (config.isEnabledCrossRegionFailOver()) {
                Database db = apiDevopsDatabases.database(config.getDatabaseId()).get();

                // Loop on regions. for each region a DC.
                db.getInfo().getDatacenters().forEach(dc -> {
                    // Rest Api
                    config.getStargateConfig().addServiceRest(dc.getRegion(),
                            new ServiceHttp(dc.getRegion() + "-rest",
                            ApiLocator.getApiRestEndpoint(config.getEnvironmnt(), config.getDatabaseId(), dc.getRegion()),
                            ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(),config.getDatabaseId(), dc.getRegion())));
                    // Document API
                    config.getStargateConfig().addDocumentService(dc.getRegion(),
                            new ServiceHttp(dc.getRegion() + "-doc",
                                    ApiLocator.getApiDocumentEndpoint(config.getEnvironmnt(),config.getDatabaseId(), dc.getRegion()),
                                    ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(),config.getDatabaseId(), dc.getRegion())));
                    // GraphQL
                    config.getStargateConfig().addGraphQLService(dc.getRegion(),
                            new ServiceHttp(dc.getRegion() + "-gql",
                                    ApiLocator.getApiGraphQLEndPoint(config.getEnvironmnt(),config.getDatabaseId(), dc.getRegion()),
                                    ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(),config.getDatabaseId(), dc.getRegion())));
                    // Json
                    config.getStargateConfig().addServiceJson(dc.getRegion(),
                            new ServiceHttp(dc.getRegion() + "-json",
                                    ApiLocator.getApiJsonEndpoint(config.getEnvironmnt(),config.getDatabaseId(), dc.getRegion()),
                                    ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(),config.getDatabaseId(), dc.getRegion())));

                    if (config.getStargateConfig().isEnabledGrpc()) {
                        // Grpc
                        config.getStargateConfig().addGrpcService(dc.getRegion(), new ServiceGrpc(dc.getRegion() + "-grpc",
                                ApiLocator.getApiGrpcEndPoint(config.getEnvironmnt(), config.getDatabaseId(), dc.getRegion()) + ":" + AstraClientConfig.GRPC_PORT,
                                ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(), config.getDatabaseId(), dc.getRegion()), true));
                    }

                    if (config.getStargateConfig().isEnabledCql()) {
                        // Cloud Secure Bundle
                        config.getStargateConfig().withCqlCloudSecureConnectBundleDC(dc.getRegion(),
                                config.getSecureConnectBundleFolder()
                                        + File.separator
                                        + AstraClientConfig.buildScbFileName(config.getDatabaseId(), dc.getRegion()));
                    }

                    config.getStargateConfig().withApiTokenProviderDC(dc.getRegion(),
                            new SimpleTokenProvider(config.getToken()));

                });
                
            } else {

                LOGGER.info("+ Cross-region fallback is disabled.");
                // Authentication for the DB
                config.getStargateConfig().withApiTokenProviderDC(currentDatabaseRegion,
                        new SimpleTokenProvider(config.getToken()));
                // Rest Api
                config.getStargateConfig().addServiceRest(currentDatabaseRegion,
                        new ServiceHttp(currentDatabaseRegion+ "-rest",
                                ApiLocator.getApiRestEndpoint(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion),
                                ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion)));
                // Json Api
                config.getStargateConfig().addServiceJson(currentDatabaseRegion,
                        new ServiceHttp(currentDatabaseRegion+ "-rest",
                                ApiLocator.getApiJsonEndpoint(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion),
                                ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion)));
                // Document API
                config.getStargateConfig().addDocumentService(currentDatabaseRegion,
                        new ServiceHttp(currentDatabaseRegion + "-doc",
                                ApiLocator.getApiDocumentEndpoint(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion),
                                ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion)));
                // GraphQL
                config.getStargateConfig().addGraphQLService(currentDatabaseRegion,
                        new ServiceHttp(currentDatabaseRegion + "-gql",
                                ApiLocator.getApiGraphQLEndPoint(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion),
                                ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(), config.getDatabaseId(),currentDatabaseRegion)));
                // Grpc
                if (config.getStargateConfig().isEnabledGrpc()) {
                    config.getStargateConfig().addGrpcService(currentDatabaseRegion, new ServiceGrpc(currentDatabaseRegion + "-grpc",
                            ApiLocator.getApiGrpcEndPoint(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion) + ":" + AstraClientConfig.GRPC_PORT,
                            ApiLocator.getEndpointHealthCheck(config.getEnvironmnt(), config.getDatabaseId(), currentDatabaseRegion), true));
                }

                // CQL
                if (config.getStargateConfig().isEnabledCql()) {
                    config.getStargateConfig()
                            .withCqlCloudSecureConnectBundleDC(currentDatabaseRegion,
                                    config.getSecureConnectBundleFolder()
                                            + File.separator
                                            + AstraClientConfig.buildScbFileName(config.getDatabaseId(), currentDatabaseRegion));
                }
            }
            this.stargateClient = config.getStargateConfig().build();

        } else {
           LOGGER.info("+ API(s) CqlSession [" + AnsiUtils.red("DISABLED")+ "]");
           LOGGER.info("+ API(s) Document   [" + AnsiUtils.red("DISABLED")+ "]");
           LOGGER.info("+ API(s) Rest       [" + AnsiUtils.red("DISABLED")+ "]");
           LOGGER.info("+ API(s) Data       [" + AnsiUtils.red("DISABLED")+ "]");
           LOGGER.info("+ API(s) gRPC       [" + AnsiUtils.red("DISABLED")+ "]");
        }
        LOGGER.info("[" + AnsiUtils.yellow("AstraClient") + "] has been initialized.");
    }
    
    /**
     * Download the secure connect bundle files.
     *
     * @param config
     *      configuration for client
     */
    private void downloadAndSetupSecureConnectBundle(AstraClientConfig config) {
        if (!new File(config.getSecureConnectBundleFolder()).exists()) {
            if (new File(config.getSecureConnectBundleFolder()).mkdirs()) {
                LOGGER.info("+ Folder Created to hold SCB");
            }
        }
        // Download secure bundles (if needed)
        LOGGER.info("+ Downloading bundles in: [" + AnsiUtils.cyan("{}") + "]", config.getSecureConnectBundleFolder());
        apiDevopsDatabases.database(config.getDatabaseId())
                          .downloadAllSecureConnectBundles(config.getSecureConnectBundleFolder());
        
        // Set up the current region
        String scbFile = config.getSecureConnectBundleFolder() 
                + File.separator 
                + AstraClientConfig.buildScbFileName(config.getDatabaseId(), config.getDatabaseRegion());
        config.getStargateConfig().withCqlCloudSecureConnectBundle(scbFile);
    }
    
    /**
     * Document Api.
     * 
     * @return ApiDocumentClient
     */
    public StargateDocumentApiClient apiStargateDocument() {
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
    public StargateRestApiClient apiStargateData() {
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
    public StargateGraphQLApiClient apiStargateGraphQL() {
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
    public StargateGrpcApiClient apiStargateGrpc() {
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
    public AstraOpsClient apiDevops() {
        if (apiDevops == null) {
            throw new IllegalStateException("Api Devops is not available "
                    + "you need to provide a astra Token (AstraCS:...) at initialization.");
        }
        return apiDevops;
    }
    
    /**
     * Devops API
     * 
     * @return ApiDevopsClient
     */
    public AstraDBOpsClient apiDevopsDatabases() {
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
    public AstraStreamingClient apiDevopsStreaming() {
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
            throw new IllegalStateException("CQL Session is not available."
                    + " Make sure you enabled it with .enableCql() and provide all"
                    + " expected parameters: keyspace, contact points or SCB, user+password ");
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
     * Getter to the configuration, it should not be changed.
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
        this.stargateClient.initCqlSession();
    }
    
    /**
     * Builder Pattern
     * 
     * @return AstraClientBuilder
     */
    public static AstraClientConfig builder() {
        return new AstraClientConfig();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
       if (null != stargateClient) {
           stargateClient.close();
       }
    }
}
