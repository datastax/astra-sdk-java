package com.datastax.stargate.sdk.config;

import static com.datastax.stargate.sdk.utils.Assert.hasLength;
import static com.datastax.stargate.sdk.utils.Assert.isTrue;
import static com.datastax.stargate.sdk.utils.Assert.notNull;
import static com.datastax.stargate.sdk.utils.Utils.readEnvVariable;

import java.io.File;
import java.io.Serializable;
import java.time.Duration;
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
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.OptionsMap;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.oss.driver.api.core.tracker.RequestTracker;
import com.datastax.oss.driver.internal.core.auth.PlainTextAuthProvider;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.core.ApiTokenProviderFixed;
import com.datastax.stargate.sdk.core.ApiTokenProviderSimple;
import com.datastax.stargate.sdk.cql.DefaultSdkDriverConfigLoaderBuilder;
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
     * Initializing Api Nodes with environment variables.
     * 
     * { "dc1": [ 
     *    { "name": "dc1Stargate1", "restUrl": "http://127.0.0.1:8082", "graphqlUrl": "http://127.0.0.1:8080", "authUrl": "http://127.0.0.1:8081" }, 
     *    { "name": "dc1Stargate2", "restUrl": "http://127.0.0.2:8082", "graphqlUrl": "http://127.0.0.2:8080", "authUrl": "http://127.0.0.2:8081" } 
     *   ], 
     *   "dc2": ["127.0.0.1:8081", "127.0.0.2:8081"] 
     * }
     **/
    public static String ENV_VAR_APINODES = "STARGATE_APINODES";
    
    /** if nothing provided, overriding. */
    public static String DEFAULT_LOCALDC = "datacenter1";
    
    /** if nothing provided, overriding. */
    public static String DEFAULT_CONTACTPOINT = "localhost:9042";

    /** Serial. */
    private static final long serialVersionUID = -4662012136342903695L;
    
    // ------------------------------------------------
    // -----         Authentication           ---------
    // ------------------------------------------------

    /** user name. */
    protected String username;

    /** This the endPoint to invoke to work with different API(s). */
    protected String password;

    /** if provided the authentication URL is not use to get token. */
    protected String appToken = null;

    /** if an apiToken is provided it will be used for all nodes. */
    protected Map<String, ApiTokenProvider> apiTokenProviderDC = new HashMap<>();
    
    /** Dynamic configuration. */
    protected ProgrammaticDriverConfigLoaderBuilder driverConfig = new DefaultSdkDriverConfigLoaderBuilder();
    
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
        hasLength(username, "username");
        hasLength(password, "password");
        this.username = username;
        this.password = password;
        withCqlOptionString(TypedDriverOption.AUTH_PROVIDER_USER_NAME, username);
        withCqlOptionString(TypedDriverOption.AUTH_PROVIDER_PASSWORD, password);
        withCqlOptionString(TypedDriverOption.AUTH_PROVIDER_CLASS,  PlainTextAuthProvider.class.getName());
        return this;
    }
    
    
    
    /**
     * Retrieve an {@link ApiTokenProvider} based on token.
     * 
     * @param dc
     *            current DC
     * @return the token provider
     */
    public ApiTokenProvider getApiTokenProvider(String dc) {
        hasLength(dc, "dc");
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
     * Api token available for all the nodes.
     * 
     * @param token
     *            current token
     * @return self reference
     */
    public StargateClientConfig withApiToken(String token) {
        hasLength(token, "token");
        this.appToken = token;
        return this;
    }
    
    /**
     * You will get one token provider for a DC
     * 
     * @param url
     *      list of URL for authentication
     *      
     * @return slef reference
     */
    public StargateClientConfig withApiTokenProvider(String... url) {
        notNull(url, "url list");
        return withApiTokenProviderDC(localDatacenter, url);
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
        hasLength(dc, "dc");
        apiTokenProviderDC.put(dc, tokenProvider);
        return this;
    }

    /**
     * You will get one token provider for a DC
     * 
     * @param dc
     *            datacenter name
     * @param url
     *      list of URL for authentication
     * @return slef reference
     */
    public StargateClientConfig withApiTokenProviderDC(String dc, String... url) {
        hasLength(dc, "dc");
        notNull(url, "url list");
        if (!Utils.hasLength(username)) {
            throw new IllegalStateException("Username is empty please .withAuthCredentials() before .withApiTokenProvider()");
        }
        return withApiTokenProviderDC(dc, new ApiTokenProviderSimple(username, password, url));
    }
    
    // ------------------------------------------------
    // -------  Topology of Nodes per DC --------------
    // ------------------------------------------------
    
    /** Local datacenter. */
    protected String localDatacenter;
    
    /** Full node provided with URL and token providers. DC, list of Nodes */
    protected Map<String, List<StargateNodeConfig>> stargateNodes = new HashMap<>();
    
    /**
     * Populate current datacenter. Will be used as localDc in cqlSession if provided
     * or local DC at Http level.
     *
     * @param localDc
     *            localDataCernter Name
     * @return current reference
     */
    public StargateClientConfig withLocalDatacenter(String localDc) {
        hasLength(localDc, "localDc");
        setLocalDatacenter(localDc);
        // Only when you do not use SCB
        driverConfig.withString(DefaultDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, localDc);
        driverConfig.withBoolean(DefaultDriverOption.LOAD_BALANCING_DC_FAILOVER_ALLOW_FOR_LOCAL_CONSISTENCY_LEVELS, true);
        cqlOptions.put(TypedDriverOption.LOAD_BALANCING_LOCAL_DATACENTER, localDc);
        cqlOptions.put(TypedDriverOption.LOAD_BALANCING_DC_FAILOVER_ALLOW_FOR_LOCAL_CONSISTENCY_LEVELS, true);
        return this;
    }
    
    /**
     * Getter accessor for attribute 'localDC'.
     *
     * @return
     *       current value of 'localDC'
     */
    public String getLocalDatacenter() {
        return this.localDatacenter;
    }
    
    /**
     * Update local datacenter without the cqlOptions. To also update the options please use withLocalDatacenter.
     *
     * @param localDc
     *       current value of localDC
     */
    public void setLocalDatacenter(String localDc) {
        this.localDatacenter = localDc;
    }
    
    /**
     * Adding all nodes to the local DC (frequently the only one).
     * 
     * @param node
     *            list of nodes
     * @return builder
     */
    public StargateClientConfig withApiNode(StargateNodeConfig node) {
        notNull(node, "StargateNode");
        if (localDatacenter == null || "".equals(localDatacenter)) {
            throw new IllegalStateException(
                    "LocalDatacenter is empty please .withLocalDataCenter() before .withApiTokenProvider()");
        }
        return withApiNodeDC(localDatacenter, node);
    }

    /**
     * Provide full feature api Node.
     *
     * @param dc
     *            current dc
     * @param node
     *            node fully form
     * @return self reference
     */
    public StargateClientConfig withApiNodeDC(String dc, StargateNodeConfig node) {
        hasLength(dc, "dc");
        notNull(node, "StargateNode");
        if (!stargateNodes.containsKey(dc)) {
            stargateNodes.put(dc, new ArrayList<StargateNodeConfig>());
        }
        stargateNodes.get(dc).add(node);
        return this;
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
    
    // ------------------------------------------------
    // -----            CqlSession            ---------
    // ------------------------------------------------
    
    /** If the flag is enabled the Cql Session will not be created even if parameters are ok. */
    protected boolean enabledCql = false;
   
    /** Providing Ad Hoc CqlSession. */
    protected CqlSession cqlSession = null;
    
    /** 
     * Track options in the object to help retrive values. The configuration is loaded with a Programmatic Loader and not the options Map.
     **/
    protected OptionsMap cqlOptions = OptionsMap.driverDefaults();
    
    /** Metrics Registry if provided. */
    protected Object cqlMetricsRegistry;
    
    /** Request tracker. */ 
    protected RequestTracker cqlRequestTracker;
    
    /**
     * By default Cqlsession is not created, you can enable the flag or using any
     * withCql* operations to enableit.
     *
     * @return
     *      reference of current object
     */
    public StargateClientConfig enableCql() {
        this.enabledCql = true;
        return this;
    }
    
    /**
     * Provide your own CqlSession skipping settings in the builder.
     *
     * @param cql
     *            existing session
     * @return
     *      self reference
     */
    public StargateClientConfig withCqlSession(CqlSession cql) {
        notNull(cql, "cqlSession");
        checkNoCqlSession();
        enableCql();
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
        notNull(configFile, "configFile");
        isTrue(configFile.exists(), "Config file should exists");
        return withCqlSession(CqlSession.builder()
                .withConfigLoader(DriverConfigLoader.fromFile(configFile)).build());
    }
    
    /**
     * Provide the proper programmatic config loader builder. It will be orverriden when 
     * used with Spring.
     * 
     * @param pdclb
     *      builder
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlDriverConfigLoaderBuilder(ProgrammaticDriverConfigLoaderBuilder pdclb) {
        this.driverConfig = pdclb;
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionBoolean(TypedDriverOption<Boolean> option, Boolean du) {
        driverConfig.withBoolean(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionBooleanDC(String dc, TypedDriverOption<Boolean> option, Boolean du) {
        driverConfig.startProfile(dc).withBoolean(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionBooleanList(TypedDriverOption<List<Boolean>> option, List<Boolean> du) {
        driverConfig.withBooleanList(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionBooleanListDC(String dc, TypedDriverOption<List<Boolean>> option, List<Boolean> du) {
        driverConfig.startProfile(dc).withBooleanList(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionClass(TypedDriverOption<Class<?>> option, Class<?> du) {
        driverConfig.withClass(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionClassDC(String dc, TypedDriverOption<Class<?>> option, Class<?> du) {
        driverConfig.startProfile(dc).withClass(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }

    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionClassList(TypedDriverOption<List<Class<?>>> option, List<Class<?>> du) {
        driverConfig.withClassList(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionClassListDC(String dc, TypedDriverOption<List<Class<?>>> option, List<Class<?>> du) {
        driverConfig.startProfile(dc).withClassList(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }

    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDouble(TypedDriverOption<Double> option, Double du) {
        driverConfig.withDouble(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDoubleDC(String dc, TypedDriverOption<Double> option,Double du) {
        driverConfig.startProfile(dc).withDouble(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDoubleList(TypedDriverOption<List<Double>> option, List<Double> du) {
        driverConfig.withDoubleList(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDoubleListDC(String dc, TypedDriverOption<List<Double>> option, List<Double> du) {
        driverConfig.startProfile(dc).withDoubleList(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDuration(TypedDriverOption<Duration> option, Duration du) {
        driverConfig.withDuration(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDurationDC(String dc, TypedDriverOption<Duration> option, Duration du) {
        driverConfig.startProfile(dc).withDuration(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDurationList(TypedDriverOption<List<Duration>> option, List<Duration> du) {
        driverConfig.withDurationList(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionDurationListDC(String dc, TypedDriverOption<List<Duration>> option, List<Duration> du) {
        driverConfig.startProfile(dc).withDurationList(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionInteger(TypedDriverOption<Integer> option, Integer du) {
        driverConfig.withInt(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionIntegerDC(String dc, TypedDriverOption<Integer> option, Integer du) {
        driverConfig.startProfile(dc).withInt(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionIntegerList(TypedDriverOption<List<Integer>> option, List<Integer> du) {
        driverConfig.withIntList(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionIntegerListDC(String dc, TypedDriverOption<List<Integer>> option, List<Integer> du) {
        driverConfig.startProfile(dc).withIntList(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionLong(TypedDriverOption<Long> option, Long du) {
        driverConfig.withLong(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionLongDC(String dc, TypedDriverOption<Long> option, Long du) {
        driverConfig.startProfile(dc).withLong(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionLongList(TypedDriverOption<List<Long>> option, List<Long> du) {
        driverConfig.withLongList(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionLongListDC(String dc, TypedDriverOption<List<Long>> option, List<Long> du) {
        driverConfig.startProfile(dc).withLongList(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionString(TypedDriverOption<String> option, String du) {
        driverConfig.withString(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionStringDC(String dc, TypedDriverOption<String> option, String du) {
        driverConfig.startProfile(dc).withString(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Add a property to the Cql Context.
     *
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionStringList(TypedDriverOption<List<String>> option, List<String> du) {
        driverConfig.withStringList(option.getRawOption(), du);
        cqlOptions.put(option, du);
        return this;
    }
    
    /**
     * Add a propery to the Cql Context.
     *
     * @param dc
     *      targate datacenter
     * @param option
     *      current option
     * @param du
     *      option value
     * @return
     *      current reference
     */
    public StargateClientConfig withCqlOptionStringListDC(String dc, TypedDriverOption<List<String>> option, List<String> du) {
        driverConfig.startProfile(dc).withStringList(option.getRawOption(), du).endProfile();
        cqlOptions.put(dc, option, du);
        return this;
    }
    
    /**
     * Metrics registry.
     *
     * @param registry
     *      target metrics registry
     * @return
     *      client config
     */
    public StargateClientConfig withCqlMetricsRegistry(Object registry) {
        notNull(registry, "registry");
        checkNoCqlSession();
        cqlMetricsRegistry = registry;
        return this;
    }
    
    /**
     * CqlRequest Tracker registry.
     *
     * @param cqlReqTracker
     *      cql tracker
     * @return
     *      client config
     */
    public StargateClientConfig withCqlRequestTracker(RequestTracker cqlReqTracker) {
        notNull(cqlReqTracker, "RequestTracker");
        checkNoCqlSession();
        this.cqlRequestTracker = cqlReqTracker;
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
        notNull(cl, "consistency level");
        driverConfig.withString(DefaultDriverOption.REQUEST_CONSISTENCY,  cl.name());
        // To be reused when dc failover
        cqlOptions.put(this.localDatacenter, TypedDriverOption.REQUEST_CONSISTENCY, cl.name());
        return this;
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
        hasLength(dc, "dc, datacenter name");
        notNull(cl, "consistency level");
        driverConfig.startProfile(dc).withString(DefaultDriverOption.REQUEST_CONSISTENCY, cl.name()).endProfile();
        // To be reused when dc failover
        cqlOptions.put(dc, TypedDriverOption.REQUEST_CONSISTENCY, cl.name());
        return this;
    }

    /**
     * Fill contact points.
     *
     * @param contactPoints
     *           contact points
     * @return current reference
     */
    public StargateClientConfig withCqlContactPoints(String... contactPoints) {
        notNull(contactPoints, "contactPoints");
        isTrue(contactPoints.length>0, "contactPoints should not be null");
        hasLength(contactPoints[0], "one contact point");
        driverConfig.withStringList(DefaultDriverOption.CONTACT_POINTS, Arrays.asList(contactPoints));
        // To be reused when dc failover
        cqlOptions.put(this.localDatacenter, TypedDriverOption.CONTACT_POINTS, Arrays.asList(contactPoints));
        return this;
    }

    /**
     * Fill contact points.
     *
     * @param dc
     *            datacenter name
     * @param contactPoints
     *           contact points
     * @return current reference
     */
    public StargateClientConfig withCqlContactPointsDC(String dc, String... contactPoints) {
        notNull(contactPoints, "contactPoints");
        isTrue(contactPoints.length>0, "contactPoints should not be null");
        hasLength(contactPoints[0], "one contact point");
        hasLength(dc, "dc, datacenter name");
        driverConfig.startProfile(dc).withStringList(DefaultDriverOption.CONTACT_POINTS, Arrays.asList(contactPoints)).endProfile();
        // To be reused when dc failover
        cqlOptions.put(dc, TypedDriverOption.CONTACT_POINTS, Arrays.asList(contactPoints));
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
        hasLength(keyspace, "keyspace");
        driverConfig.withString(DefaultDriverOption.SESSION_KEYSPACE, keyspace);
        // To be reused when dc failover
        cqlOptions.put(TypedDriverOption.SESSION_KEYSPACE, keyspace);
        return this;
    }

    /**
     * Providing SCB. (note it is one per Region).
     *
     * @param cloudConfigUrl
     *            configuration
     * @return current reference
     */
    public StargateClientConfig withCqlCloudSecureConnectBundle(String cloudConfigUrl) {
        hasLength(cloudConfigUrl, "cloudConfigUrl");
        driverConfig.withString(DefaultDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
        cqlOptions.put(this.localDatacenter, TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
        return this;
    }
    
    /**
     * Providing SCB. (note it is one per Region).
     *
     * @param dc
     *          datacenter name
     * @param cloudConfigUrl
     *          configuration
     * @return current reference
     */
    public StargateClientConfig withCqlCloudSecureConnectBundleDC(String dc, String cloudConfigUrl) {
        hasLength(cloudConfigUrl, "cloudConfigUrl");
        driverConfig.withString(DefaultDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
        cqlOptions.put(dc, TypedDriverOption.CLOUD_SECURE_CONNECT_BUNDLE, cloudConfigUrl);
        return this;
    }

    /**
     * Getter accessor for attribute 'disableCqlSession'.
     *
     * @return
     *       current value of 'disableCqlSession'
     */
    public boolean isEnabledCql() {
        return this.enabledCql;
    }

    /**
     * Getter accessor for attribute 'cqlSession'.
     *
     * @return
     *       current value of 'cqlSession'
     */
    public CqlSession getCqlSession() {
        return this.cqlSession;
    }
    
    /**
     * Getter accessor for attribute 'cqloptions'.
     *
     * @return
     *      value for cql option
     */
    public OptionsMap getCqlOptions() {
        return this.cqlOptions;
    }

    /**
     * Getter accessor for attribute 'cqlRequestTracker'.
     *
     * @return
     *       current value of 'cqlRequestTracker'
     */
    public RequestTracker getCqlRequestTracker() {
        return this.cqlRequestTracker;
    }
    
    /**
     * Accessor to driver config builder.
     *
     * @return
     *      driver config builder
     */
    public ProgrammaticDriverConfigLoaderBuilder getCqlDriverConfigLoaderBuilder() {
        return driverConfig;
    }
    
    /**
     * Getter accessor for attribute 'metricsRegistry'.
     *
     * @return
     *       current value of 'metricsRegistry'
     */
    public Object getCqlMetricsRegistry() {
        return this.cqlMetricsRegistry;
    }
    
    /**
     * When working with builder you do no want the Cqlsession provided ad hoc.
     */
    private void checkNoCqlSession() {
        if (cqlSession != null) {
            throw new IllegalArgumentException("You cannot provide CqlOptions if a external CqlSession is used.");
        }
    }
   
    // ------------------------------------------------
    // -----            Grpc Session          ---------
    // ------------------------------------------------
    
    /** If the flag is enabled the Grpc Session will not be created even if parameters are ok. */
    protected boolean enabledGrpc = false;
    
    /**
     * By default Cqlsession is not created, you can enable the flag or using any
     * withCql* operations to enableit.
     *
     * @return
     *      reference of current object
     */
    public StargateClientConfig enableGrpc() {
        this.enabledGrpc = true;
        return this;
    }
    
    /**
     * Getter accessor for attribute 'disableCqlSession'.
     *
     * @return
     *       current value of 'disableCqlSession'
     */
    public boolean isEnabledGrpc() {
        return this.enabledGrpc;
    }
    
    
    // ------------------------------------------------
    // ------------- HTTP Client ----------------------
    // ------------------------------------------------
    
    /** Override Retry configuration. */
    protected RetryConfig httpRetryConfig;
    
    /** Override Request configuration. */
    protected RequestConfig httpRequestConfig;
    
    /** Observers. */ 
    protected Map<String, ApiInvocationObserver> httpObservers = new HashMap<>();
    
    /**
     * Enable fine Grained configuration of the HTTP Client.
     *
     * @param reqConfig
     *            request configuration
     * @return self reference
     */
    public StargateClientConfig withHttpRequestConfig(RequestConfig reqConfig) {
        notNull(reqConfig, "RequestConfig");
        this.httpRequestConfig = reqConfig;
        return this;
    }

    /**
     * Enable fine Grained configuration of the HTTP Retries.
     *
     * @param retryConfig
     *            request configuration
     * @return self reference
     */
    public StargateClientConfig withHttpRetryConfig(RetryConfig retryConfig) {
        notNull(retryConfig, "retryConfig");
        this.httpRetryConfig = retryConfig;
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
        hasLength(name, "Observer name");
        notNull(observer, "observer");
        if (this.httpObservers.containsKey(name)) {
            throw new IllegalArgumentException("An observer with the same name already exists (type=" + 
                        this.httpObservers.get(name).getClass().getName() + ")");
        }
        this.httpObservers.put(name, observer);
        return this;
    }

    /**
     * Api Invocations trigger some events processed in observer.
     * 
     * @param observers
     *            instance of your Observer
     * @return self reference
     */
    public StargateClientConfig withHttpObservers(Map<String, ApiInvocationObserver> observers) {
        notNull(observers, "observers");
        isTrue(observers.size()>0, "observers should not be empty");
        this.httpObservers = observers;
        return this;
    }
    
    /**
     * Getter accessor for attribute 'retryConfig'.
     *
     * @return
     *       current value of 'retryConfig'
     */
    public RetryConfig getRetryConfig() {
        return httpRetryConfig;
    }

    /**
     * Getter accessor for attribute 'requestConfig'.
     *
     * @return
     *       current value of 'requestConfig'
     */
    public RequestConfig getRequestConfig() {
        return httpRequestConfig;
    }

    /**
     * Getter accessor for attribute 'observers'.
     *
     * @return
     *       current value of 'observers'
     */
    public Map<String, ApiInvocationObserver> getObservers() {
        return httpObservers;
    }
    
    // ------------------------------------------------
    // ------------------  MAIN -----------------------
    // ------------------------------------------------
    
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
        readEnvVariable(ENV_VAR_LOCAL_DC)
            .ifPresent(this::withLocalDatacenter);

        // Keyspace
        readEnvVariable(ENV_VAR_KEYSPACE)
            .ifPresent(this::withCqlKeyspace);
        
        // Contact Points
        readEnvVariable(ENV_VAR_CONTACTPOINTS)
            .map(v -> v.split(","))
            .ifPresent(this::withCqlContactPoints);
        //Optional<String> envContactPoints = readEnvVariable(ENV_VAR_CONTACTPOINTS);
        //if (envContactPoints.isPresent()) {
        //    withCqlContactPoints(envContactPoints.get().split(","));
        //}

        // Configuration File
        readEnvVariable(ENV_VAR_DRIVERCONFIG_FILE)
            .map(File::new)
            .ifPresent(this::withCqlDriverConfigurationFile);
        
        //Optional<String> envDriverFile = readEnvVariable(ENV_VAR_DRIVERCONFIG_FILE);
        //if (envDriverFile.isPresent()) {
        //    withCqlDriverConfigurationFile(new File(envDriverFile.get()));
        //}

        // Api Nodes
        readEnvVariable(ENV_VAR_APINODES).ifPresent(nodes -> {
            this.stargateNodes = JsonUtils.unmarshallType(nodes,
                    new TypeReference<Map<String, List<StargateNodeConfig>>>() {});
        });
        //Optional<String> envDStargateNodes = readEnvVariable(ENV_VAR_APINODES);
        //if (envDStargateNodes.isPresent()) {
        //    this.stargateNodes = JsonUtils.unmarshallType(envDStargateNodes.get(),
        //            new TypeReference<Map<String, List<StargateNodeConfig>>>() {});
        //}
    }
    
    /**
     * Building a StargateClient from the values in the BUILDER
     *
     * @return
     *     instance of the client
     */
    public StargateClient build() {
        return new StargateClient(this);
    }
}
