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

import java.io.Closeable;
import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.core.TokenProviderDefault;
import com.datastax.stargate.sdk.core.TokenProviderStatic;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Public interface to interact with ASTRA API.
 * 
 * .namespace("")          : will lead you to document (schemaless) API
 * .keyspace("")           : will lead you to rest API (table oriented) API
 * .devops(id,name,secret) : is the devops API
 * .cql()                  : Give you a CqlSession
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClient implements Closeable {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClient.class);
    
    /** Enviroment variables to setup connection. */
    public static final String STARGATE_USERNAME         = "STARGATE_USERNAME";
    public static final String STARGATE_PASSWORD         = "STARGATE_PASSWORD";
    public static final String STARGATE_ENDPOINT_AUTH    = "STARGATE_ENDPOINT_AUTH";
    public static final String STARGATE_ENDPOINT_REST    = "STARGATE_ENDPOINT_REST";
    public static final String STARGATE_ENDPOINT_GRAPHQL = "STARGATE_ENDPOINT_GRAPHQL";
    public static final String STARGATE_ENDPOINT_CQL     = "STARGATE_ENDPOINT_CQL";
    public static final String STARGATE_LOCAL_DC         = "STARGATE_LOCAL_DC";
    public static final String STARGATE_KEYSPACE         = "STARGATE_KEYSPACE";
    public static final String STARGATE_ENABLE_CQL       = "STARGATE_ENABLE_CQL";
    
    public static final String DEFAULT_REST_URL          = "http://localhost:8082";
    public static final String DEFAULT_GRAPHQL_URL       = "http://localhost:8080";
   
    public static final String DEFAULT_CONTACT_POINT     = "localhost:9042";
    public static final String DEFAULT_DATACENTERNAME    = "dc1";
  
    public static final int    DEFAULT_TIMEOUT_TOKEN     = 300;
    
    // -----------------------------------------------
    // Attributes to be populated by BUILDER
    // Api(s) to initialize based on those values
    // ----------------------------------------------
    
    /** Hold a reference for the ApiDocument. */
    private ApiDocumentClient apiDoc;
    
    /** Hold a reference for the ApiRest. */
    private ApiDataClient apiRest;
    
    /** Hold a reference for the ApiGraphQL. */
    private ApiGraphQLClient apiGraphQL;
    
    /** Hold a reference for the Api Devops. */
    private CqlSession cqlSession;
    
    /**
     * Accessing Document API
     * @return ApiDocumentClient
     */
    public ApiDocumentClient apiDocument() {
        return apiDoc;
    }
    
    /**
     * Accessing Rest API
     * @return ApiRestClient
     */
    public ApiDataClient apiRest() {
        return apiRest;
    }
    
    /**
     * Accessing Rest API
     * @return ApiGraphQLClient
     */
    public ApiGraphQLClient apiGraphQL() {
        return apiGraphQL;
    }
    
    /**
     * Accessing Cql Session.
     * @return CqlSession
     */
    public Optional<CqlSession> cqlSession() {
        return Optional.ofNullable(cqlSession);
    }
    
    /**
     * You can create on of {@link ApiDocumentClient}, {@link ApiDataClient}, {@link ApiDevopsClient}, {@link ApiCqlClient} with
     * a constructor. The full flegde constructor would took 12 pararms.
     */
    private StargateClient(StargateClientBuilder builder) {
        LOGGER.info("Initializing [StargateClient]");
        
        // Base on provided parameters we choose token provider
        ApiTokenProvider tokenProvider = null; 
        if (Utils.paramsProvided(builder.username, builder.password, builder.endPointAuthentication)) {
            tokenProvider = new TokenProviderDefault(builder.username, builder.password, builder.endPointAuthentication);
        }
        if (Utils.paramsProvided(builder.appToken)) {
            tokenProvider = new TokenProviderStatic(builder.appToken);
        }
        if (tokenProvider != null) {
            HttpApisClient.getInstance().setTokenProvider(tokenProvider);
        }
        // Document API and Data API
        if (tokenProvider != null && builder.endPointRest != null) {
            apiDoc  = new ApiDocumentClient(builder.endPointRest, tokenProvider);
            apiRest = new ApiDataClient(builder.endPointRest, tokenProvider);
        }
        // GraphQL API
        if (tokenProvider != null && builder.endPointGraphQL != null) {
            apiGraphQL = new ApiGraphQLClient(builder.endPointGraphQL,tokenProvider);
        }
        // For security reason you want to disable CQL
        if (builder.enableCql) {
            if (Utils.paramsProvided(builder.username, builder.password)) {
               
                CqlSessionBuilder cqlSessionBuilder = CqlSession.builder()
                        .withAuthCredentials(builder.username, builder.password);
                
                if (Utils.paramsProvided(builder.keyspaceName)) {
                    cqlSessionBuilder = cqlSessionBuilder.withKeyspace(builder.keyspaceName);
                }
                // Overriding contactPoints/LocalDataCenter when using ASTRA settings
                if (Utils.paramsProvided(builder.astraCloudSecureBundle)) {
                    cqlSessionBuilder.withCloudSecureConnectBundle(Paths.get(builder.astraCloudSecureBundle));
                } else if (!builder.endPointCql.isEmpty()) {
                    cqlSessionBuilder = cqlSessionBuilder
                            .addContactPoints(builder.endPointCql.stream()
                                                     .map(this::mapContactPoint)
                                                     .collect(Collectors.toList()));
                    if (Utils.paramsProvided(builder.localDataCenter)) {
                        cqlSessionBuilder = cqlSessionBuilder.withLocalDatacenter(builder.localDataCenter);
                    }
                }
                cqlSession = cqlSessionBuilder.build();
                
                // Sanity Check query
                cqlSession.execute("SELECT data_center from system.local");
                LOGGER.info("+ API(s) Cql is [ENABLED]");
                if (cqlSession.getKeyspace().isPresent()) {
                    LOGGER.info("+ Keyspace {}", cqlSession.getKeyspace().get());
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
            }
        } else {
            LOGGER.info("+ API(s) Cql is [DISABLED]");
        }
        LOGGER.info("[StargateClient] has been initialized");
    }
    
    private InetSocketAddress mapContactPoint(String contactPoint) {
        String[] chunks = contactPoint.split(":");
        if (chunks.length != 2) {
            throw new IllegalArgumentException(contactPoint 
                    + " is not a valid contactPoint expression: invalid format,expecting ip:port");
        }
        int port = 0;
        try {
            port = Integer.parseInt(chunks[1]);
            if (port <0 || port > 65536) {
                throw new IllegalArgumentException(contactPoint 
                        + " is not a valid contactPoint expression: port should be in [0,65536] range");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(contactPoint 
                    + " is not a valid contactPoint expression: port invalid expecting ip:port");
        }
        return new InetSocketAddress(chunks[0], port);
    }
    
    /**
     * Builder Pattern
     * @return StargateClientBuilder
     */
    public static final StargateClientBuilder builder() {
        return new StargateClientBuilder();
    }
    
    /**
     * Builder pattern
     */
    public static class StargateClientBuilder {
        public String username = TokenProviderDefault.DEFAULT_USERNAME;
        /** This the endPoint to invoke to work with different API(s). */
        public String password = TokenProviderDefault.DEFAULT_PASSWORD;
        /** This the endPoint to invoke to work with different API(s). */
        public String endPointAuthentication = TokenProviderDefault.DEFAULT_AUTH_URL;
        /** if provided the authentication URL is not use to get token. */
        public String appToken = null;
        /** This the endPoint to invoke to work with different API(s). */
        public String endPointRest = DEFAULT_REST_URL;
        /** This the endPoint to invoke to work with different API(s). */
        public String endPointGraphQL = DEFAULT_GRAPHQL_URL;
        /** If this flag is disabled no CQL session will be created. */
        public boolean enableCql = true;
        /** working with local Cassandra. */
        public List<String> endPointCql = new ArrayList<>(Arrays.asList(DEFAULT_CONTACT_POINT));
        /** Local data center. */
        public String localDataCenter = DEFAULT_DATACENTERNAME;
        /** Optional Keyspace to enable CqlSession. */
        public String keyspaceName; 
        /** SecureCloudBundle (ASTRA ONLY) overriding. */
        public String astraCloudSecureBundle = null;
        /** Token timeout in seconds (not used with Astra). */
        public int tokenTimeToLiveSeconds = DEFAULT_TIMEOUT_TOKEN;
          
        /**
         * Load defaults from Emvironment variables
         */
        protected StargateClientBuilder() {
            if (null != System.getenv(STARGATE_USERNAME)) {
                this.username = System.getenv(STARGATE_USERNAME);
            }
            if (null != System.getenv(STARGATE_PASSWORD)) {
                this.password = System.getenv(STARGATE_PASSWORD);
            }
            if (null != System.getenv(STARGATE_ENDPOINT_AUTH)) {
                this.endPointAuthentication = System.getenv(STARGATE_ENDPOINT_AUTH);
            }
            if (null != System.getenv(STARGATE_ENDPOINT_GRAPHQL)) {
                this.endPointGraphQL = System.getenv(STARGATE_ENDPOINT_GRAPHQL);
            }
            if (null != System.getenv(STARGATE_ENDPOINT_REST)) {
                this.endPointRest = System.getenv(STARGATE_ENDPOINT_REST);
            }
            if (null != System.getenv(STARGATE_ENDPOINT_CQL)) {
                this.endPointCql = Arrays.asList(System.getenv(STARGATE_ENDPOINT_CQL).split(","));
            }
            if (null != System.getenv(STARGATE_LOCAL_DC)) {
                this.localDataCenter = System.getenv(STARGATE_LOCAL_DC);
            }
            if (null != System.getenv(STARGATE_KEYSPACE)) {
                this.keyspaceName = System.getenv(STARGATE_KEYSPACE);
            }
            if (null != System.getenv(STARGATE_ENABLE_CQL)) {
                this.enableCql = Boolean.valueOf(System.getenv(STARGATE_ENABLE_CQL));
            }
        }
        
        public StargateClientBuilder username(String username) {
            Assert.hasLength(username, "username");
            this.username = username;
            return this;
        }
        public StargateClientBuilder password(String password) {
            Assert.hasLength(password, "password");
            this.password = password;
            return this;
        }
        public StargateClientBuilder endPointAuth(String endPointAuth) {
            // If null = Astra
            if ("".equals(endPointAuth)) {
                throw new IllegalArgumentException("Parameter 'endPointAuth' "
                        + "should be null nor empty");
            }
            this.endPointAuthentication = endPointAuth;
            return this;
        }
        public StargateClientBuilder appToken(String token) {
            this.endPointAuthentication = null;
            this.appToken               = token;
            return this;
        }
        public StargateClientBuilder endPointGraphQL(String endPointGraphQL) {
            Assert.hasLength(endPointGraphQL, "endPointGraphQL");
            this.endPointGraphQL = endPointGraphQL;
            return this;
        }
        public StargateClientBuilder endPointRest(String endPointRest) {
            Assert.hasLength(endPointRest, "restApiUrl");
            this.endPointRest = endPointRest;
            return this;
        }
        public StargateClientBuilder localDc(String localDc) {
            Assert.hasLength(localDc, "localDc");
            this.localDataCenter = localDc;
            return this;
        }
        public StargateClientBuilder keyspace(String keyspace) {
            Assert.hasLength(keyspace, "keyspace");
            this.keyspaceName = keyspace;
            return this;
        }
        public StargateClientBuilder disableCQL() {
            this.enableCql = false;
            return this;
        }
        public StargateClientBuilder addCqlContactPoint(String ip, int port) {
            Assert.hasLength(ip, "ip");
            this.endPointCql.add(ip + ":" + port);
            return this;
        }
        public StargateClientBuilder cqlContactPoint(String ip, int port) {
            Assert.hasLength(ip, "ip");
            this.endPointCql = new ArrayList<String>();
            endPointCql.add(ip + ":" + port);
            return this;
        }
        public StargateClientBuilder astraCloudSecureBundle(String bundle) {
            Assert.hasLength(bundle, "bundle");
            this.astraCloudSecureBundle = bundle;
            return this;
        }
        public StargateClientBuilder tokenTimeToLive(int timeout) {
            Assert.isTrue(timeout>0,  "Timeout should be a positive number");
            this.tokenTimeToLiveSeconds = timeout;
            return this;
        }
        
        /**
         * Create the client
         * @return StargateClient
         */
        public StargateClient build() {
            return new StargateClient(this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        if (null != cqlSession && !cqlSession.isClosed()) {
            cqlSession.close();
            LOGGER.info("Closing CqlSession.");
        }
    }
}
