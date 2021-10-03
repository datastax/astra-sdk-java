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
import static com.datastax.stargate.sdk.utils.AnsiUtils.magenta;
import static com.datastax.stargate.sdk.utils.AnsiUtils.red;
import static com.datastax.stargate.sdk.utils.Utils.readEnvVariable;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.OptionsMap;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.oss.driver.internal.core.auth.PlainTextAuthProvider;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.core.TokenProviderDefault;
import com.datastax.stargate.sdk.core.TokenProviderStatic;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.datastax.stargate.sdk.utils.Utils;
import com.evanlennick.retry4j.config.RetryConfig;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Global Client to interact with a Stargate instance.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClient implements Closeable {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClient.class);
    
    /** Initializing a CqlSession with environment variables. */
    public static String ENV_VAR_USERNAME           = "STARGATE_USERNAME";
    public static String ENV_VAR_PASSWORD           = "STARGATE_PASSWORD";
    public static String ENV_VAR_LOCAL_DC           = "STARGATE_LOCAL_DC";
    public static String ENV_VAR_KEYSPACE           = "STARGATE_KEYSPACE";
    
    /** 
     * Contact points should be provided as a list separated by ','
     * 
     * localhost:9042,localhost:4045
     **/
    public static String ENV_VAR_CONTACTPOINTS      = "STARGATE_CONTACTPOINTS";
    
    /** Fine Tuning the CqlSession with options. */
    public static String ENV_VAR_DRIVERCONFIG_FILE  = "STARGATE_DRIVERCONFIG";
    
    /** 
     * Initializing Api Nodes with environment variables.
     * 
     * Data needs a structure as you get multiple nodes and multiple DC
     * Map<String, List<StargateNode> as a JSON.
     * {
     *   "dc1": [
     *     { "name":      "dc1Stargate1", 
     *       "restUrl":    "http://127.0.0.1:8082", 
     *       "graphqlUrl": "http://127.0.0.1:8080",
     *       "authUrl":    "http://127.0.0.1:8081" 
     *     },
     *     { "name":      "dc1Stargate2", 
     *       "restUrl":    "http://127.0.0.2:8082", 
     *       "graphqlUrl": "http://127.0.0.2:8080",
     *       "authUrl":    "http://127.0.0.2:8081" 
     *     }
     *   ],
     *   "DC2": ["127.0.0.1:8081", "127.0.0.2:8081"]
     * }
     **/
    public static String ENV_VAR_APINODES = "STARGATE_APINODES";
    
   
    /** The Cql Session to interact with Cassandra through the DataStax CQL Drivers. */
    private CqlSession cqlSession;
    
    /** Local DC. (load balancing on the nodes there and failover only if not available. */
    private String localDatacenter;
    
    /** 
     * List of clients to interact with Nodes with others API (REST, GRAPHQL, ...).
     *
     * If multiple datacenter provided the datacenter name will used as an EXECUTION PROFILE
     * to SWITCH contact points or cloud secure bundle.
     **/
    private Map<String, StargateClientDC> datacenters = new HashMap<>();
    
    /**
     * You can create on of {@link ApiDocumentClient}, {@link ApiDataClient}, {@link ApiDevopsClient}, {@link ApiCqlClient} with
     * a constructor. The full flegde constructor would took 12 pararms.
     */
    public StargateClient(StargateClientBuilder builder) {
        LOGGER.info("Initializing StargateClient...");
        
        /**
         * Initializing NODES
         */
        for(String dc : builder.stargateNodes.keySet()) {
            ApiTokenProvider apitokenDC = builder.getApiTokenProvider(dc);
            LOGGER.info("DC [" + magenta(dc) + "]");
            datacenters.put(dc, new StargateClientDC(dc, apitokenDC, 
             builder.stargateNodes
                    .get(dc).stream()
                    .map(node -> new StargateClientNode(apitokenDC, node.getName(), node.getRestUrl(), node.getGraphqlUrl()))
                    .collect(Collectors.toList())));
        }
        
        /**
         * Initializing LOCAL DATACENTER. 
         */
        if (!Utils.hasLength(builder.localDC)) {
            throw new IllegalArgumentException("Local Datacenter is required");
        }
        this.localDatacenter = builder.localDC;
        
        /**
         * Initializing CQLSESSION
         */
        if (!builder.disableCqlSession) {
            if (builder.cqlSession != null) {
                // A CQL Session has been provided, we will reuse it
                cqlSession = builder.cqlSession;
            }
            // Using a Config Map instead of CqlSessionBuilder to create Execution Profiles
            cqlSession = CqlSession.builder()
                                   .withConfigLoader(DriverConfigLoader.fromMap(builder.options))
                                   .build();
        }
        
        if (cqlSession != null) {
            LOGGER.info("Cql [" + green("ENABLED") + "]");
            cqlSession.execute("SELECT data_center from system.local");
            if (cqlSession.getKeyspace().isPresent()) {
                LOGGER.info("+ Keyspace        : [" + cyan("{}") + "]", cqlSession.getKeyspace().get());
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
            LOGGER.info("Cql [" + red("DISABLED") + "]");
        }
        LOGGER.info("+ Local DC        : [" + cyan("{}") + "]", localDatacenter);
       
        LOGGER.info("Stargate Topology:");
        datacenters.entrySet().stream().forEach(e -> {
            LOGGER.info("- [" + cyan("{}") + "] with [" + cyan("{}") + "] Stargate nodes", 
                    e.getKey(), e.getValue().getStargateNodesLB().getResourceList().size());
         });
    }   
    
    /**
     * Lookup for the client for a dedicated Data Center.
     * 
     * @return
     *      current datacenter
     */
    private StargateClientDC lookupDC() {
        // IMPLEMENTING DISCOVERY AND FAILOVER
        if (!datacenters.containsKey(localDatacenter)) {
            throw new IllegalStateException("LocalDC has been setup to " + localDatacenter + " but not stargate nodes found there");
        }
        return datacenters.get(localDatacenter);
    }
    
    /**
     * Builder Pattern
     * @return StargateClientBuilder
     */
    public static final StargateClientBuilder builder() {
        return new StargateClientBuilder();
    }
    
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
        return lookupDC().lookupNode().apiDocument();
    }
    
    /**
     * Retrieve API Data, doing load balancing, failover and retries.
     *
     * @return
     *      Api REST DATA client
     */
    public ApiDataClient apiRest() {
        return lookupDC().lookupNode().apiRest();
    }
    
    /**
     * Retrieve API GraphQL, doing load balancing, failover and retries.
     * 
     * @return
     *      Api graphQL client
     */
    public ApiGraphQLClient apiGraphQL() {
        return lookupDC().lookupNode().apiGraphQL();
    }
    
    /**
     * Implementing failover cross DC for API and CqlSession when available.
     * 
     * @param datacenter
     *      target datacenter
     */
    public void useDataCenter(String datacenter) {
        if (!datacenters.containsKey(datacenter)) {
            throw new IllegalArgumentException("'" + datacenter + "' is not a known datacenter please provides one "
                    + "in " + datacenters.keySet());
        }
        this.localDatacenter = datacenter;
    }
    
    /** {@inheritDoc} */
    @Override
    public void close() {
        if (null != cqlSession && !cqlSession.isClosed()) {
            cqlSession.close();
            LOGGER.info("Closing CqlSession.");
        }
    }
    
    /**
     * Embedding the BUILDER
     * 
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class StargateClientBuilder {
        
        // ------------------------------------------------
        // ---------------- Cql Session -------------------
        // ------------------------------------------------

        /** If the flag is enabled the Cqlsession will not be created even if parameters are ok. */
        boolean disableCqlSession = false;
        
        /** Providing Ad Hoc CqlSession. */
        CqlSession cqlSession = null;
        
        /** Local datacenter. */
        String localDC;
       
        /** Defined the default option map for the driver. */
        OptionsMap options = OptionsMap.driverDefaults();
        
        /**
         * Enable fine Grained configuration of the HTTP Client.
         *
         * @param reqConfig
         *      request configuration
         * @return
         *      self reference
         */
        public StargateClientBuilder withHttpRequestConfig(RequestConfig reqConfig) {
            HttpApisClient.withRequestConfig(reqConfig);
            return this;
        }
        
        /**
         * Enable fine Grained configuration of the HTTP Retries.
         *
         * @param reqConfig
         *      request configuration
         * @return
         *      self reference
         */
        public StargateClientBuilder withHttpRetryConfig(RetryConfig retryConfig) {
            HttpApisClient.withRetryConfig(retryConfig);
            return this;
        }
        
        /**
         * Api Invocations trigger some events processed in observer.
         * 
         * @param name
         *          unique identiier
         * @param observer
         *          instance of your Observer
         * @return
         *      self reference
         */
        public StargateClientBuilder addHttpObserver(String name, ApiInvocationObserver observer) {
            HttpApisClient.registerListener(name, observer);
            return this;
        }
        
        /**
         * Api Invocations trigger some events processed in observer.
         * 
         * @param name
         *          unique identiier
         * @param observer
         *          instance of your Observer
         * @return
         *      self reference
         */
        public StargateClientBuilder withHttpObservers(Map<String, ApiInvocationObserver> observers) {
            if (observers != null) {
                for (Map.Entry<String, ApiInvocationObserver> obs : observers.entrySet()) {
                    HttpApisClient.registerListener(obs.getKey(), obs.getValue());
                }
            }
            return this;
        }
        
        /**
         * You want to keep the client stateless to be agile.
         * 
         * @return
         *      reference enforcing cqlsession disabled
         */
        public StargateClientBuilder withoutCqlSession() {
            disableCqlSession = true;
            return this;
        }
        
        /**
         * Sometim you just want to provide your own CqlSession.
         *
         * @param cql
         *      existing session
         * @return
         */
        public StargateClientBuilder withCqlSession(CqlSession cql) {
            this.cqlSession = cql;
            return this;
        }
        
        /**
         * You want to initialize CqlSession with a configuration file
         * 
         * @param configFile
         *      a configuration file
         * @return
         *      the current reference
         */
        public StargateClientBuilder withCqlDriverConfigurationFile(File configFile) {
            this.cqlSession = CqlSession.builder()
                    .withConfigLoader(DriverConfigLoader
                    .fromFile(configFile))
                    .build();
            return this;
        }
        
        /**
         * You want to initialize CqlSession with your own configuration Loader.
         * 
         * @param loader
         *      a configuration loader
         * @return
         *      the current reference
         */
        public StargateClientBuilder withCqlDriverConfigurationLoader(DriverConfigLoader loader) {
            this.cqlSession = CqlSession.builder().withConfigLoader(loader).build();
            return this;
        }
        
        /**
         * Provide fine grained configuration for the driver.
         *
         * @param <T>
         *      current type
         * @param option
         *      option name
         * @param value
         *      option value
         * @return
         *      self reference
         */
        public <T> StargateClientBuilder withCqlDriverOption(TypedDriverOption<T> option, T value) {
            checkNoCqlSession();
            options.put(option, value);
            return this;
        }
        
        /**
         * Provide fine grained configuration for the driver.
         *
         * @param <T>
         *      current type
         * @param dc
         *      datacenter name     
         * @param option
         *      option name
         * @param value
         *      option value
         * @return
         *      self reference
         */
        public <T> StargateClientBuilder withCqlDriverOptionDC(String dc, TypedDriverOption<T> option, T value) {
            checkNoCqlSession();
            options.put(dc, option, value);
            return this;
        }
        
        /**
         * Set the consistency level
         * @param cl
         *      current consitency level
         * @return
         *      self reference
         */
        public StargateClientBuilder withCqlConsistencyLevel(ConsistencyLevel cl) {
            return withCqlDriverOption(TypedDriverOption.REQUEST_CONSISTENCY, cl.name());
        }
        
        /**
         * Define consistency level for a DC.
         *
         * @param dc
         *      datacenter name
         * @param cl
         *      consistency level
         * @return
         *      self reference
         */
        public StargateClientBuilder withCqlConsistencyLevelDC(String dc, ConsistencyLevel cl) {
            return withCqlDriverOptionDC(dc, TypedDriverOption.REQUEST_CONSISTENCY, cl.name());
        }
        
        /**
         * Fill Keyspaces.
         *
         * @param keyspace
         *      keyspace name
         * @return
         *       current reference
         */
        public StargateClientBuilder withCqlContactPoints(String... contactPoints) {
            checkNoCqlSession();
            if (contactPoints != null) {
                options.put(TypedDriverOption.CONTACT_POINTS, Arrays.asList(contactPoints));
            }
            return this;
        }
        
        /**
         * Fill Keyspaces.
         *
         * @param keyspace
         *      keyspace name
         * @return
         *       current reference
         */
        public StargateClientBuilder withCqlContactPointsDC(String dc, String... contactPoints) {
            checkNoCqlSession();
            if (contactPoints != null) {
                options.put(dc, TypedDriverOption.CONTACT_POINTS, Arrays.asList(contactPoints));
            }
            return this;
        }
        
        /**
         * Fill username and password.
         * 
         * @param username
         *      user identifier
         * @param password
         *      password
         * @return
         *      current reference
         */
        public StargateClientBuilder withAuthCredentials(String username, String password) {
            checkNoCqlSession();
            this.username = username;
            this.password = password;
            withCqlDriverOption(TypedDriverOption.AUTH_PROVIDER_USER_NAME, username);
            withCqlDriverOption(TypedDriverOption.AUTH_PROVIDER_PASSWORD, password);
            withCqlDriverOption(TypedDriverOption.AUTH_PROVIDER_CLASS, PlainTextAuthProvider.class.getName());
            return this;
        }
        
        /**
         * Fill Keyspaces.
         *
         * @param keyspace
         *      keyspace name
         * @return
         *       current reference
         */
        public StargateClientBuilder withCqlKeyspace(String keyspace) {
            return withCqlDriverOption(TypedDriverOption.SESSION_KEYSPACE, keyspace);
        }
        
        /**
         * Fill Keyspaces.
         *
         * @param localDc
         *      localDataCernter Name 
         * @return
         *       current reference
         */
        public StargateClientBuilder withLocalDatacenter(String localDc) {
            this.localDC = localDc;
            withCqlDriverOption(TypedDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, localDc);
            withCqlDriverOption(TypedDriverOption.LOAD_BALANCING_DC_FAILOVER_ALLOW_FOR_LOCAL_CONSISTENCY_LEVELS, true);
            return this;
        }
        
        /**
         * Fill Application name.
         *
         * @param appName
         *      appName
         * @return
         *       current reference
         */
        public StargateClientBuilder withApplicationName(String appName) {
            return withCqlDriverOption(TypedDriverOption.APPLICATION_NAME, appName);
        }
        
        /**
         * Providing SCB. (note it is one per Region).
         *
         * @param cloudConfigUrl
         *      configuration
         * @return
         *      current reference
         */
        public StargateClientBuilder withCqlCloudSecureConnectBundle(String cloudConfigUrl) {
            return withCqlDriverOption(TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
        }
        
        /**
         * Providing SCB. (note it is one per Region), define per DC.
         *
         * @param dc
         *       load dc
         * @param cloudConfigUrl
         *      configuration
         * @return
         *      current reference
         */
        public StargateClientBuilder withCqlCloudSecureConnectBundleDC(String dc, String cloudConfigUrl) {
            return withCqlDriverOptionDC(dc, TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
        }
        
        /**
         * When working with builder you do no want the Cqlsession provided ad hoc.
         */
        private void checkNoCqlSession() {
            if (cqlSession != null) {
                throw new IllegalArgumentException("You cannot use the CqlSessionBuilder as CqlSession is already provided");
            }
        }
        
        // ------------------------------------------------
        // ----- Tokens and Token Provider (AUTH) ---------
        // ------------------------------------------------

        /** user name. */
        private String username;
        
        /** This the endPoint to invoke to work with different API(s). */
        private String password;
        
        /** if provided the authentication URL is not use to get token. */
        private String appToken = null;
        
        /** if an apiToken is provided it will be used for all nodes. */
        private Map<String, ApiTokenProvider> apiTokenProviderDC = new HashMap<>();
        
        /**
         * You will get one token provider for a DC
         * 
         * @param dc
         *      datacenter name
         * @param tokenProvider
         *      token provider name
         * @return
         *      slef reference
         */
        public StargateClientBuilder withApiTokenProvider(String... url) {
            return withApiTokenProviderDC(localDC, url);
        }
        
        /**
         * You will get one token provider for a DC
         * 
         * @param dc
         *      datacenter name
         * @param tokenProvider
         *      token provider name
         * @return
         *      slef reference
         */
        public StargateClientBuilder withApiTokenProviderDC(String dc, String... url) {
            if (!Utils.hasLength(username)) {
                throw new IllegalStateException("Username is empty please .withAuthCredentials() before .withApiTokenProvider()");
            }
            if (!Utils.hasLength(dc)) {
                throw new IllegalArgumentException("Datacenter name is required");
            }
            return withApiTokenProviderDC(dc, new TokenProviderDefault(username, password, url));
        }
        
        /**
         * Provide token provider for a DC.
         *
         * @param dc
         *      datacentername
         * @param tokenProvider
         *      token provider
         * @return
         *      self reference
         */
        public StargateClientBuilder withApiTokenProviderDC(String dc, ApiTokenProvider tokenProvider) {
            apiTokenProviderDC.put(dc, tokenProvider);
            return this;
        }
        
        /**
         * Api token available for all the nodes.
         * 
         * @param token
         *      current token
         * @return
         *      self reference
         */
        public StargateClientBuilder withApiToken(String token) {
            this.appToken = token;
            return this;
        }
        
        /**
         * Retrive an {@link ApiTokenProvider} based on token.
         * 
         * @param dc
         *      current DC
         * @return
         *      the token provider
         */
        public ApiTokenProvider getApiTokenProvider(String dc) {
            ApiTokenProvider dcTokenProvider = null;
            if (Utils.hasLength(appToken)) {
                dcTokenProvider = new TokenProviderStatic(appToken);
            } else if (apiTokenProviderDC.containsKey(dc)) {
                dcTokenProvider = apiTokenProviderDC.get(dc);
            } else {
                if (!stargateNodes.isEmpty() && stargateNodes.containsKey(dc)) {
                    // Create a list of URL from all the ndoes in the DC
                    return new TokenProviderDefault(username, password, 
                            new ArrayList<String>(stargateNodes.get(dc).stream()
                                .map(StargateNode::getAuthUrl)
                                .collect(Collectors.toSet())));
                }
                throw new IllegalArgumentException("No token provider found for DC" + dc);
            }
            return dcTokenProvider;
        }
        
        // ------------------------------------------------
        // ------------------- Nodes ----------------------
        // ------------------------------------------------
        
        /** Full node provided with URL and token providers. DC, list of Nodes */
        private Map<String, List<StargateNode>> stargateNodes = new HashMap<>();
        
        /**
         * Adding all nodes to the local DC (frequently the only one).
         * 
         * @param nodes
         *      list of nodes
         * @return
         *      builder
         */
        public StargateClientBuilder withApiNode(StargateNode node) {
            if (localDC == null || "".equals(localDC)) {
                throw new IllegalStateException("LocalDatacenter is empty please .withLocalDataCenter() before .withApiTokenProvider()");
            }
            return withApiNodeDC(localDC, node);
        }
        
        /**
         * Provide full feature api Node.
         *
         * @param dc
         *      current dc
         * @param nodes
         *      node fully form
         * @return
         *      self reference
         */
        public StargateClientBuilder withApiNodeDC(String dc, StargateNode node) {
            if (!stargateNodes.containsKey(dc)) {
                stargateNodes.put(dc, new ArrayList<StargateNode>());
            }
            stargateNodes.get(dc).add(node);
            return this;
        }
      
        /**
         * Load defaults from Emvironment variables
         */
        protected StargateClientBuilder() {
            
            // Credentials
            Optional<String> envUsername = readEnvVariable(ENV_VAR_USERNAME);
            Optional<String> envPassword = readEnvVariable(ENV_VAR_PASSWORD);
            if (envUsername.isPresent() && envPassword.isPresent()) {
                withAuthCredentials(envUsername.get(), envPassword.get());
            }
            
            // Local DataCenter
            Optional<String> envLocalDc = readEnvVariable(ENV_VAR_LOCAL_DC);
            envLocalDc.ifPresent(this::withLocalDatacenter);
            
            // Keyspace
            Optional<String> envKeyspace = readEnvVariable(ENV_VAR_KEYSPACE);
            envKeyspace.ifPresent(this::withCqlKeyspace);
            
            // Contact Points
            Optional<String> envContactPoints = readEnvVariable(ENV_VAR_CONTACTPOINTS);
            if (envContactPoints.isPresent()) {
                withCqlContactPoints(envContactPoints.get().split(","));
            }
            
            // Configuration File
            Optional<String> envDriverFile = readEnvVariable(ENV_VAR_DRIVERCONFIG_FILE);
            if (envDriverFile.isPresent()) {
                withCqlDriverConfigurationFile(new File(envDriverFile.get()));
            }
            
            // Api Nodes
            Optional<String> envDStargateNodes = readEnvVariable(ENV_VAR_APINODES);
            if (envDStargateNodes.isPresent()) {
                this.stargateNodes = JsonUtils.unmarshallType(envDStargateNodes.get(), 
                        new TypeReference<Map<String, List<StargateNode>>>(){});
            }
        }
        
        /**
         * Building a StargateClient from the values in the BUILDER
         *
         * @return
         */
        public StargateClient build() {
            return new StargateClient(this);
        }
    }
    
    /**
     * Configure a stargate node.
     * 
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class StargateNode {
        
        /** Name of the node. */
        private String name;
        
        /** Rest API URL. */
        private String restUrl;
        
        /** GraphQL Api URL. */
        private String graphqlUrl;
        
        /** Authentication API URL. */
        private String authUrl;
        
        public StargateNode() {}
        
        /**
         * Syntaxic sugar.
         *
         * @param host
         *      current host
         */
        public StargateNode(String host) {
            this.name       = host;
            this.authUrl    = "http://" + host + ":8081";
            this.restUrl    = "http://" + host + ":8082";
            this.graphqlUrl = "http://" + host + ":8080";
        }
                
        /**
         * Constructor without URL.
         *
         * @param name
         *      node name
         * @param rest
         *      rest endpoint
         * @param graphQL
         *      graphql endpoint
         */
        public StargateNode(String name, String rest, String graphQL) {
            this(name, rest, graphQL, null);
        }
        
        /**
         * Full constructor.
         *
         * @param name
         *      node name
         * @param rest
         *      rest endpoint
         * @param graphQL
         *      graphql endpoint
         * @param auth
         *      authentication URL
         */
        public StargateNode(String name, String rest, String graphQL, String auth) {
            this.name       = name;
            this.restUrl    = rest;
            this.graphqlUrl = graphQL;
            this.authUrl    = auth;
        }
        
        /**
         * Getter accessor for attribute 'authUrl'.
         *
         * @return
         *       current value of 'authUrl'
         */
        public String getAuthUrl() {
            return authUrl;
        }

        /**
         * Getter accessor for attribute 'name'.
         *
         * @return
         *       current value of 'name'
         */
        public String getName() {
            return name;
        }
        
        /**
         * Getter accessor for attribute 'restUrl'.
         *
         * @return
         *       current value of 'restUrl'
         */
        public String getRestUrl() {
            return restUrl;
        }
        
        /**
         * Getter accessor for attribute 'graphqlUrl'.
         *
         * @return
         *       current value of 'graphqlUrl'
         */
        public String getGraphqlUrl() {
            return graphqlUrl;
        }
    }
    
}
