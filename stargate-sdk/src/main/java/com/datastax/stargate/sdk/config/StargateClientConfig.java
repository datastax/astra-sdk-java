package com.datastax.stargate.sdk.config;

import static com.datastax.stargate.sdk.utils.Utils.readEnvVariable;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.config.RequestConfig;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.OptionsMap;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.oss.driver.internal.core.auth.PlainTextAuthProvider;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.core.ApiTokenProviderFixed;
import com.datastax.stargate.sdk.core.ApiTokenProviderSimple;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.datastax.stargate.sdk.utils.Utils;
import com.evanlennick.retry4j.config.RetryConfig;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * The Stargate SDK allows to connect to multiple APIS and provides a wide range of options. With the more advanced settings we
 * need an abtraction for the configuration. It can then be loaded from multiple sources like .. a builder, an application.yaml, a
 * service discovery with different configuration loader.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClientConfig implements Serializable {

    /** Provide username as environment variable. */
    public static String ENV_VAR_USERNAME = "STARGATE_USERNAME";

    /** Provide password as environment variable. */
    public static String ENV_VAR_PASSWORD = "STARGATE_PASSWORD";

    /** Provide localDatacenter name as environment variable. */
    public static String ENV_VAR_LOCAL_DC = "STARGATE_LOCAL_DC";

    /** Provide working keyspace as environment variable. */
    public static String ENV_VAR_KEYSPACE = "STARGATE_KEYSPACE";

    /**
     * Provide contact points as environment variable. Contact points should be provided as a list separated by ',':
     * localhost:9042,localhost:4045
     **/
    public static String ENV_VAR_CONTACTPOINTS = "STARGATE_CONTACTPOINTS";

    /** Provide fine tuning of the CqlSession as environment variable. */
    public static String ENV_VAR_DRIVERCONFIG_FILE = "STARGATE_DRIVERCONFIG";

    /**
     * Initializing Api Nodes with environment variables. Data needs a structure as you get multiple nodes and multiple DC
     * Map<String, List<StargateNode> as a JSON. 
     * { "dc1": [ 
     *    { "name": "dc1Stargate1", "restUrl": "http://127.0.0.1:8082", "graphqlUrl": "http://127.0.0.1:8080", "authUrl": "http://127.0.0.1:8081" }, 
     *    { "name": "dc1Stargate2", "restUrl": "http://127.0.0.2:8082", "graphqlUrl": "http://127.0.0.2:8080", "authUrl": "http://127.0.0.2:8081" } 
     *   ], 
     *   "dc2": ["127.0.0.1:8081", "127.0.0.2:8081"] 
     * }
     **/
    public static String ENV_VAR_APINODES = "STARGATE_APINODES";

    /** Serial. */
    private static final long serialVersionUID = -4662012136342903695L;

    // ------------------------------------------------
    // -----            CqlSession            ---------
    // ------------------------------------------------
    
    /** If the flag is enabled the Cqlsession will not be created even if parameters are ok. */
    protected boolean disableCqlSession = false;

    /** Providing Ad Hoc CqlSession. */
    protected CqlSession cqlSession = null;

    /** Local datacenter. */
    protected String localDC;

    /** Defined the default option map for the driver. */
    protected OptionsMap options = OptionsMap.driverDefaults();
    
    // ------------------------------------------------
    // ----- Tokens and Token Provider (AUTH) ---------
    // ------------------------------------------------

    /** user name. */
    protected String username;

    /** This the endPoint to invoke to work with different API(s). */
    protected String password;

    /** if provided the authentication URL is not use to get token. */
    protected String appToken = null;

    /** if an apiToken is provided it will be used for all nodes. */
    protected Map<String, ApiTokenProvider> apiTokenProviderDC = new HashMap<>();

    // ------------------------------------------------
    // ------------------- Nodes ----------------------
    // ------------------------------------------------

    /** Full node provided with URL and token providers. DC, list of Nodes */
    protected Map<String, List<StargateNodeConfig>> stargateNodes = new HashMap<>();

    // ------------------------------------------------
    // ------------- HTTP Client ----------------------
    // ------------------------------------------------
    
    /** Override Retry configuration. */
    protected RetryConfig retryConfig;
    
    /** Override Request configuration. */
    protected RequestConfig requestConfig;
    
    /** Observers. */ 
    protected Map<String, ApiInvocationObserver> observers = new HashMap<>();
    
    /**
     * Load defaults from Emvironment variables
     */
    public StargateClientConfig() {
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
                    new TypeReference<Map<String, List<StargateNodeConfig>>>() {});
        }
    }
    
    /**
     * Retrieve an {@link ApiTokenProvider} based on token.
     * 
     * @param dc
     *            current DC
     * @return the token provider
     */
    public ApiTokenProvider getApiTokenProvider(String dc) {
        ApiTokenProvider dcTokenProvider = null;
        // As a token is explicitly provided (e.g: ASTRA) no computation of token
        if (Utils.hasLength(appToken)) {
            dcTokenProvider = new ApiTokenProviderFixed(appToken);
        } else if (apiTokenProviderDC.containsKey(dc)) {
            // An ApiToken as been provided for the DC (can be different by DC, external config etc.)
            dcTokenProvider = apiTokenProviderDC.get(dc);
        } else {
            if (!stargateNodes.isEmpty() && stargateNodes.containsKey(dc)) {
                // Each node in DC expose and Auth URL that can ve part of the ApiTokenProvider
                return new ApiTokenProviderSimple(username, password, new ArrayList<String>(
                        stargateNodes.get(dc).stream().map(StargateNodeConfig::getAuthUrl).collect(Collectors.toSet())));
            }
            // No solution to enable authentication has been found
            throw new IllegalArgumentException("No token provider found for DC" + dc);
        }
        return dcTokenProvider;
    }
    
    /**
     * Enable fine Grained configuration of the HTTP Client.
     *
     * @param reqConfig
     *            request configuration
     * @return self reference
     */
    public StargateClientConfig withHttpRequestConfig(RequestConfig reqConfig) {
        this.requestConfig = reqConfig;
        return this;
    }

    /**
     * Enable fine Grained configuration of the HTTP Retries.
     *
     * @param reqConfig
     *            request configuration
     * @return self reference
     */
    public StargateClientConfig withHttpRetryConfig(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
        return this;
    }

    /**
     * Api Invocations trigger some events processed in observer.
     * 
     * @param name
     *            unique identiier
     * @param observer
     *            instance of your Observer
     * @return self reference
     */
    public StargateClientConfig addHttpObserver(String name, ApiInvocationObserver observer) {
        Assert.hasLength(name, "Observer name");
        if (this.observers.containsKey(name)) {
            throw new IllegalArgumentException("An observer with the same name already exists (type=" + 
                        this.observers.get(name).getClass().getName() + ")");
        }
        this.observers.put(name, observer);
        return this;
    }

    /**
     * Api Invocations trigger some events processed in observer.
     * 
     * @param name
     *            unique identiier
     * @param observer
     *            instance of your Observer
     * @return self reference
     */
    public StargateClientConfig withHttpObservers(Map<String, ApiInvocationObserver> observers) {
        this.observers = observers;
        return this;
    }

    /**
     * You want to keep the client stateless to be agile.
     * 
     * @return reference enforcing cqlsession disabled
     */
    public StargateClientConfig withoutCqlSession() {
        disableCqlSession = true;
        return this;
    }

    /**
     * Provide your own CqlSession skipping settings in the builder.
     *
     * @param cql
     *            existing session
     * @return
     */
    public StargateClientConfig withCqlSession(CqlSession cql) {
        this.cqlSession = cql;
        return this;
    }

    /**
     * You want to initialize CqlSession with a configuration file
     * 
     * @param configFile
     *            a configuration file
     * @return the current reference
     */
    public StargateClientConfig withCqlDriverConfigurationFile(File configFile) {
        this.cqlSession = CqlSession.builder().withConfigLoader(DriverConfigLoader.fromFile(configFile)).build();
        return this;
    }

    /**
     * You want to initialize CqlSession with your own configuration Loader.
     * 
     * @param loader
     *            a configuration loader
     * @return the current reference
     */
    public StargateClientConfig withCqlDriverConfigurationLoader(DriverConfigLoader loader) {
        this.cqlSession = CqlSession.builder().withConfigLoader(loader).build();
        return this;
    }

    /**
     * Provide fine grained configuration for the driver.
     *
     * @param <T>
     *            current type
     * @param option
     *            option name
     * @param value
     *            option value
     * @return self reference
     */
    public <T> StargateClientConfig withCqlDriverOption(TypedDriverOption<T> option, T value) {
        checkNoCqlSession();
        options.put(option, value);
        return this;
    }

    /**
     * Provide fine grained configuration for the driver.
     *
     * @param <T>
     *            current type
     * @param dc
     *            datacenter name
     * @param option
     *            option name
     * @param value
     *            option value
     * @return self reference
     */
    public <T> StargateClientConfig withCqlDriverOptionDC(String dc, TypedDriverOption<T> option, T value) {
        checkNoCqlSession();
        options.put(dc, option, value);
        return this;
    }

    /**
     * Set the consistency level
     * 
     * @param cl
     *            current consitency level
     * @return self reference
     */
    public StargateClientConfig withCqlConsistencyLevel(ConsistencyLevel cl) {
        return withCqlDriverOption(TypedDriverOption.REQUEST_CONSISTENCY, cl.name());
    }

    /**
     * Define consistency level for a DC.
     *
     * @param dc
     *            datacenter name
     * @param cl
     *            consistency level
     * @return self reference
     */
    public StargateClientConfig withCqlConsistencyLevelDC(String dc, ConsistencyLevel cl) {
        return withCqlDriverOptionDC(dc, TypedDriverOption.REQUEST_CONSISTENCY, cl.name());
    }

    /**
     * Fill Keyspaces.
     *
     * @param keyspace
     *            keyspace name
     * @return current reference
     */
    public StargateClientConfig withCqlContactPoints(String... contactPoints) {
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
     *            keyspace name
     * @return current reference
     */
    public StargateClientConfig withCqlContactPointsDC(String dc, String... contactPoints) {
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
     *            user identifier
     * @param password
     *            password
     * @return current reference
     */
    public StargateClientConfig withAuthCredentials(String username, String password) {
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
     *            keyspace name
     * @return current reference
     */
    public StargateClientConfig withCqlKeyspace(String keyspace) {
        return withCqlDriverOption(TypedDriverOption.SESSION_KEYSPACE, keyspace);
    }

    /**
     * Fill Keyspaces.
     *
     * @param localDc
     *            localDataCernter Name
     * @return current reference
     */
    public StargateClientConfig withLocalDatacenter(String localDc) {
        this.localDC = localDc;
        withCqlDriverOption(TypedDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, localDc);
        withCqlDriverOption(TypedDriverOption.LOAD_BALANCING_DC_FAILOVER_ALLOW_FOR_LOCAL_CONSISTENCY_LEVELS, true);
        return this;
    }

    /**
     * Fill Application name.
     *
     * @param appName
     *            appName
     * @return current reference
     */
    public StargateClientConfig withApplicationName(String appName) {
        return withCqlDriverOption(TypedDriverOption.APPLICATION_NAME, appName);
    }

    /**
     * Providing SCB. (note it is one per Region).
     *
     * @param cloudConfigUrl
     *            configuration
     * @return current reference
     */
    public StargateClientConfig withCqlCloudSecureConnectBundle(String cloudConfigUrl) {
        return withCqlDriverOption(TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
    }

    /**
     * Providing SCB. (note it is one per Region), define per DC.
     *
     * @param dc
     *            load dc
     * @param cloudConfigUrl
     *            configuration
     * @return current reference
     */
    public StargateClientConfig withCqlCloudSecureConnectBundleDC(String dc, String cloudConfigUrl) {
        return withCqlDriverOptionDC(dc, TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
    }

    /**
     * You will get one token provider for a DC
     * 
     * @param dc
     *            datacenter name
     * @param tokenProvider
     *            token provider name
     * @return slef reference
     */
    public StargateClientConfig withApiTokenProvider(String... url) {
        return withApiTokenProviderDC(localDC, url);
    }

    /**
     * You will get one token provider for a DC
     * 
     * @param dc
     *            datacenter name
     * @param tokenProvider
     *            token provider name
     * @return slef reference
     */
    public StargateClientConfig withApiTokenProviderDC(String dc, String... url) {
        if (!Utils.hasLength(username)) {
            throw new IllegalStateException("Username is empty please .withAuthCredentials() before .withApiTokenProvider()");
        }
        if (!Utils.hasLength(dc)) {
            throw new IllegalArgumentException("Datacenter name is required");
        }
        return withApiTokenProviderDC(dc, new ApiTokenProviderSimple(username, password, url));
    }

    /**
     * Provide token provider for a DC.
     *
     * @param dc
     *            datacentername
     * @param tokenProvider
     *            token provider
     * @return self reference
     */
    public StargateClientConfig withApiTokenProviderDC(String dc, ApiTokenProvider tokenProvider) {
        apiTokenProviderDC.put(dc, tokenProvider);
        return this;
    }

    /**
     * Api token available for all the nodes.
     * 
     * @param token
     *            current token
     * @return self reference
     */
    public StargateClientConfig withApiToken(String token) {
        this.appToken = token;
        return this;
    }

    /**
     * Adding all nodes to the local DC (frequently the only one).
     * 
     * @param nodes
     *            list of nodes
     * @return builder
     */
    public StargateClientConfig withApiNode(StargateNodeConfig node) {
        if (localDC == null || "".equals(localDC)) {
            throw new IllegalStateException(
                    "LocalDatacenter is empty please .withLocalDataCenter() before .withApiTokenProvider()");
        }
        return withApiNodeDC(localDC, node);
    }

    /**
     * Provide full feature api Node.
     *
     * @param dc
     *            current dc
     * @param nodes
     *            node fully form
     * @return self reference
     */
    public StargateClientConfig withApiNodeDC(String dc, StargateNodeConfig node) {
        if (!stargateNodes.containsKey(dc)) {
            stargateNodes.put(dc, new ArrayList<StargateNodeConfig>());
        }
        stargateNodes.get(dc).add(node);
        return this;
    }

    /**
     * When working with builder you do no want the Cqlsession provided ad hoc.
     */
    private void checkNoCqlSession() {
        if (cqlSession != null) {
            throw new IllegalArgumentException("You cannot provide CqlOptions if a external CqlSession is used.");
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

    /**
     * Getter accessor for attribute 'disableCqlSession'.
     *
     * @return
     *       current value of 'disableCqlSession'
     */
    public boolean isDisableCqlSession() {
        return disableCqlSession;
    }

    /**
     * Getter accessor for attribute 'cqlSession'.
     *
     * @return
     *       current value of 'cqlSession'
     */
    public CqlSession getCqlSession() {
        return cqlSession;
    }

    /**
     * Getter accessor for attribute 'localDC'.
     *
     * @return
     *       current value of 'localDC'
     */
    public String getLocalDC() {
        return localDC;
    }

    /**
     * Getter accessor for attribute 'options'.
     *
     * @return
     *       current value of 'options'
     */
    public OptionsMap getOptions() {
        return options;
    }

    /**
     * Getter accessor for attribute 'stargateNodes'.
     *
     * @return
     *       current value of 'stargateNodes'
     */
    public Map<String, List<StargateNodeConfig>> getStargateNodes() {
        return stargateNodes;
    }

    /**
     * Getter accessor for attribute 'retryConfig'.
     *
     * @return
     *       current value of 'retryConfig'
     */
    public RetryConfig getRetryConfig() {
        return retryConfig;
    }

    /**
     * Getter accessor for attribute 'requestConfig'.
     *
     * @return
     *       current value of 'requestConfig'
     */
    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    /**
     * Getter accessor for attribute 'observers'.
     *
     * @return
     *       current value of 'observers'
     */
    public Map<String, ApiInvocationObserver> getObservers() {
        return observers;
    }

}
