package com.datastax.astra.sdk.config;

import static com.datastax.stargate.sdk.utils.Assert.hasLength;
import static com.datastax.stargate.sdk.utils.Utils.readEnvVariable;

import java.io.File;
import java.io.Serializable;
import java.time.Duration;
import java.util.List;
import java.util.Map;

import org.apache.hc.client5.http.config.RequestConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.oss.driver.api.core.tracker.RequestTracker;
import com.datastax.stargate.sdk.audit.ApiInvocationObserver;
import com.datastax.stargate.sdk.config.StargateClientConfig;
import com.datastax.stargate.sdk.utils.AnsiUtils;
import com.evanlennick.retry4j.config.RetryConfig;

/**
 * Helper and configurer for Astra.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraClientConfig implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 6950028057943051050L;
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClientConfig.class);
    
    /** User home folder. */
    public static final String ENV_USER_HOME              = "user.home";
    
    /** Where yo download cloud secure bundles. */
    public static final String DEFAULT_SCB_FOLDER = System.getProperty(ENV_USER_HOME) + File.separator + ".astra" + File.separator + "scb";
   
    /** Initialize parameters from Environment variables. */
    public static final String ASTRA_DB_ID                = "ASTRA_DB_ID";
    /** ENV VAR to get current or default region (local datacenter). */
    public static final String ASTRA_DB_REGION            = "ASTRA_DB_REGION";
    /** ENV VAR to get part of the token: client Id. */
    public static final String ASTRA_DB_CLIENT_ID         = "ASTRA_DB_CLIENT_ID";
    /** ENV VAR to get part of the token: client Secret. */
    public static final String ASTRA_DB_CLIENT_SECRET     = "ASTRA_DB_CLIENT_SECRET";
    /** ENV VAR to get part of the token: application token. */
    public static final String ASTRA_DB_APPLICATION_TOKEN = "ASTRA_DB_APPLICATION_TOKEN";
    /** ENV VAR to get the keyspace to be selected. */
    public static final String ASTRA_DB_KEYSPACE          = "ASTRA_DB_KEYSPACE";
    /** SECURE BUNDLE FOR EACH RECGIONS. */
    public static final String ASTRA_DB_SCB_FOLDER        = "ASTRA_DB_SCB_FOLDER";
    
    /** Port for grpc in Astra. */
    public static final int GRPC_PORT                     = 443;
    
    // ------------------------------------------------
    // ------------- Credentials ----------------------
    // ------------------------------------------------
   
    /** Token to authenticate. */
    private String token;
    
    /** Client identifier. */
    private String clientId;
    
    /** Client secret. */
    private String clientSecret;
   
    /**
     * Provide token.
     *
     * @param applicationToken
     *            token
     * @return self reference
     */
    public AstraClientConfig withToken(String applicationToken) {
        hasLength(applicationToken, "applicationToken");
        this.token = applicationToken;
        this.stargateConfig.withApiToken(token);
        return this;
    }
    
    /**
     * Provide clientId.
     *
     * @param clientId
     *            clientId
     * @return self reference
     */
    public AstraClientConfig withClientId(String clientId) {
        hasLength(clientId, "clientId");
        this.clientId = clientId;
        return this;
    }
    
    /**
     * Provide clientSecret.
     *
     * @param clientSecret
     *            clientSecret
     * @return self reference
     */
    public AstraClientConfig withClientSecret(String clientSecret) {
        hasLength(clientSecret, "clientSecret");
        this.clientSecret = clientSecret;
        return this;
    }
    
    /**
     * Getter accessor for attribute 'token'.
     *
     * @return
     *       current value of 'token'
     */
    public String getToken() {
        return token;
    }

    /**
     * Getter accessor for attribute 'clientId'.
     *
     * @return
     *       current value of 'clientId'
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * Getter accessor for attribute 'clientSecret'.
     *
     * @return
     *       current value of 'clientSecret'
     */
    public String getClientSecret() {
        return clientSecret;
    }
    
    
    // ------------------------------------------------
    // ---------------- Database ----------------------
    // ------------------------------------------------
   
    /** Attribute to describe the Astra instance. */
    private String databaseId;
    
    /** First and main region to use (others are failing over). */
    private String databaseRegion;
    
    /**
     * Provider database identifier
     *
     * @param databaseId
     *            databaseId
     * @return self reference
     */
    public AstraClientConfig withDatabaseId(String databaseId) {
        hasLength(databaseId, "databaseId");
        this.databaseId = databaseId;
        return this;
    }
    
    /**
     * Provider database identifier
     *
     * @param databaseRegion
     *            databaseRegion
     * @return self reference
     */
    public AstraClientConfig withDatabaseRegion(String databaseRegion) {
        hasLength(databaseRegion, "databaseRegion");
        this.databaseRegion = databaseRegion;
        return this;
    }
    
    /**
     * Getter accessor for attribute 'databaseId'.
     *
     * @return
     *       current value of 'databaseId'
     */
    public String getDatabaseId() {
        return databaseId;
    }

    /**
     * Getter accessor for attribute 'databaseRegion'.
     *
     * @return
     *       current value of 'databaseRegion'
     */
    public String getDatabaseRegion() {
        return databaseRegion;
    }
    
    // ------------------------------------------------
    // ---------------- Stargate ----------------------
    // ------------------------------------------------
     
    /** Configuring Stargate to work in Astra. */
    private StargateClientConfig stargateConfig;
    
    /**
     * Getter accessor for attribute 'stargateConfig'.
     *
     * @return
     *       current value of 'stargateConfig'
     */
    public StargateClientConfig getStargateConfig() {
        return stargateConfig;
    }
    
    // ------------------------------------------------
    // ------------- HTTP Client ----------------------
    // ------------------------------------------------
    
    /**
     * Enable fine Grained configuration of the HTTP Client.
     *
     * @param reqConfig
     *            request configuration
     * @return self reference
     */
    public AstraClientConfig withHttpRequestConfig(RequestConfig reqConfig) {
        stargateConfig.withHttpRequestConfig(reqConfig);
        return this;
    }
    
    /**
     * Enable fine Grained configuration of the HTTP Retries.
     *
     * @param retryConfig
     *            retry configuration
     * @return self reference
     */
    public AstraClientConfig withHttpRetryConfig(RetryConfig retryConfig) {
        stargateConfig.withHttpRetryConfig(retryConfig);
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
    public AstraClientConfig addHttpObserver(String name, ApiInvocationObserver observer) {
        stargateConfig.addHttpObserver(name, observer);
        return this;
    }
    
    /**
     * Api Invocations trigger some events processed in observer.
     * 
     * @param observers
     *           observers lists
     * @return self reference
     */
    public AstraClientConfig withHttpObservers(Map<String, ApiInvocationObserver> observers) {
        stargateConfig.withHttpObservers(observers);
        return this;
    }

    // ------------------------------------------------
    // ----------------- Grpc -------------------------
    // ------------------------------------------------
    
    /**
     * Enable gRPC
     * 
     * @return reference enforcing cqlsession disabled
     */
    public AstraClientConfig enableGrpc() {
        stargateConfig.enableGrpc();
        return this;
    }
    
    // ------------------------------------------------
    // -------------- CqlSession ----------------------
    // ------------------------------------------------
    
    /** Enable SCB download to target folder. */
    private boolean downloadSecureConnectBundle = true;
    
    /** Folder to load secure connect bundle with formated names scb_dbid_region.zip */
    private String secureConnectBundleFolder  = DEFAULT_SCB_FOLDER;
    
    /**
     * Getter accessor for attribute 'secureConnectBundleFolder'.
     *
     * @return
     *       current value of 'secureConnectBundleFolder'
     */
    public String getSecureConnectBundleFolder() {
        return secureConnectBundleFolder;
    }
    
    /**
     * Provide path of the SCB.
     * 
     * @param path
     *      target scb
     * @return
     *      reference to current scb
     */
    public AstraClientConfig secureConnectBundleFolder(String path) {
        this.secureConnectBundleFolder = path;
        return this;
    }
    
    /**
     * Getter for downloadSecureConnectBundle.
     *
     * @return
     *      downloadSecureConnectBundle value
     */
    public boolean isEnabledDownloadSecureConnectBundle() {
        return downloadSecureConnectBundle;
    }
    
    /**
     * Enable SCB downloads.
     * 
     * @return
     *      current reference.
     */
    public AstraClientConfig enableDownloadSecureConnectBundle() {
        this.downloadSecureConnectBundle = true;
        return this;
    }
    
    /**
     * Disable SCB downloads.
     * 
     * @return
     *      current reference.
     */
    public AstraClientConfig disableDownloadSecureConnectBundle() {
        this.downloadSecureConnectBundle = false;
        return this;
    }
    
    /**
     * Enable CqlSession
     * 
     * @return reference enforcing cqlsession disabled
     */
    public AstraClientConfig enableCql() {
        stargateConfig.enableCql();
        return this;
    }
    
    /**
     * Populate Secure connect bundle
     *
     * @param scbFile
     *            path of cloud secure bundle
     * @return current reference
     */
    public AstraClientConfig withCqlCloudSecureConnectBundle(String scbFile) {
        stargateConfig.withCqlCloudSecureConnectBundle(scbFile);
        return this;
    }
    
    /**
     * Populate config.
     * 
     * @param conf
     *      configuration
     * @return
     *      current reference
     */
    public AstraClientConfig withCqlDriverConfig(ProgrammaticDriverConfigLoaderBuilder conf) {
        stargateConfig.withCqlDriverConfigLoaderBuilder(conf);
        return this;
    }
    
    /**
     * Populate Consistency level
     *
     * @param cl
     *           consistency level
     * @return current reference
     */
    public AstraClientConfig withCqlConsistencyLevel(ConsistencyLevel cl) {
        stargateConfig.withCqlConsistencyLevel(cl);
        return this;
    }
    
    /**
     * Populate configuration file
     *
     * @param configFile
     *          configuration file
     * @return current reference
     */
    public AstraClientConfig withCqlDriverConfigurationFile(File configFile) {
        stargateConfig.withCqlDriverConfigurationFile(configFile);
        return this;
    }
    
    /**
     * Fill Keyspaces.
     *
     * @param keyspace
     *            keyspace name
     * @return current reference
     */
    public AstraClientConfig withCqlKeyspace(String keyspace) {
        stargateConfig.withCqlKeyspace(keyspace);
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
    public AstraClientConfig withCqlOptionBoolean(TypedDriverOption<Boolean> option, Boolean du) {
       stargateConfig.withCqlOptionBoolean(option, du);
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
    public AstraClientConfig withCqlOptionBooleanDC(String dc, TypedDriverOption<Boolean> option, Boolean du) {
        stargateConfig.withCqlOptionBooleanDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionBooleanList(TypedDriverOption<List<Boolean>> option, List<Boolean> du) {
        stargateConfig.withCqlOptionBooleanList(option, du);
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
    public AstraClientConfig withCqlOptionBooleanListDC(String dc, TypedDriverOption<List<Boolean>> option, List<Boolean> du) {
        stargateConfig.withCqlOptionBooleanListDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionClass(TypedDriverOption<Class<?>> option, Class<?> du) {
        stargateConfig.withCqlOptionClass(option, du);
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
    public AstraClientConfig withCqlOptionClassDC(String dc, TypedDriverOption<Class<?>> option, Class<?> du) {
        stargateConfig.withCqlOptionClassDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionClassList(TypedDriverOption<List<Class<?>>> option, List<Class<?>> du) {
        stargateConfig.withCqlOptionClassList(option, du);
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
    public AstraClientConfig withCqlOptionClassListDC(String dc, TypedDriverOption<List<Class<?>>> option, List<Class<?>> du) {
        stargateConfig.withCqlOptionClassListDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionDouble(TypedDriverOption<Double> option, Double du) {
        stargateConfig.withCqlOptionDouble(option, du);
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
    public AstraClientConfig withCqlOptionDoubleDC(String dc, TypedDriverOption<Double> option,Double du) {
        stargateConfig.withCqlOptionDoubleDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionDoubleList(TypedDriverOption<List<Double>> option, List<Double> du) {
        stargateConfig.withCqlOptionDoubleList(option, du);
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
    public AstraClientConfig withCqlOptionDoubleListDC(String dc, TypedDriverOption<List<Double>> option, List<Double> du) {
        stargateConfig.withCqlOptionDoubleListDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionDuration(TypedDriverOption<Duration> option, Duration du) {
        stargateConfig.withCqlOptionDuration(option, du);
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
    public AstraClientConfig withCqlOptionDurationDC(String dc, TypedDriverOption<Duration> option, Duration du) {
        stargateConfig.withCqlOptionDurationDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionDurationList(TypedDriverOption<List<Duration>> option, List<Duration> du) {
        stargateConfig.withCqlOptionDurationList(option, du);
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
    public AstraClientConfig withCqlOptionDurationListDC(String dc, TypedDriverOption<List<Duration>> option, List<Duration> du) {
        stargateConfig.withCqlOptionDurationListDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionInteger(TypedDriverOption<Integer> option, Integer du) {
        stargateConfig.withCqlOptionInteger(option, du);
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
    public AstraClientConfig withCqlOptionIntegerDC(String dc, TypedDriverOption<Integer> option, Integer du) {
        stargateConfig.withCqlOptionIntegerDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionIntegerList(TypedDriverOption<List<Integer>> option, List<Integer> du) {
        stargateConfig.withCqlOptionIntegerList(option, du);
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
    public AstraClientConfig withCqlOptionIntegerListDC(String dc, TypedDriverOption<List<Integer>> option, List<Integer> du) {
        stargateConfig.withCqlOptionIntegerListDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionLong(TypedDriverOption<Long> option, Long du) {
        stargateConfig.withCqlOptionLong(option, du);
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
    public AstraClientConfig withCqlOptionLongDC(String dc, TypedDriverOption<Long> option, Long du) {
        stargateConfig.withCqlOptionLongDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionLongList(TypedDriverOption<List<Long>> option, List<Long> du) {
        stargateConfig.withCqlOptionLongList(option, du);
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
    public AstraClientConfig withCqlOptionLongListDC(String dc, TypedDriverOption<List<Long>> option, List<Long> du) {
        stargateConfig.withCqlOptionLongListDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionString(TypedDriverOption<String> option, String du) {
        stargateConfig.withCqlOptionString(option, du);
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
    public AstraClientConfig withCqlOptionStringDC(String dc, TypedDriverOption<String> option, String du) {
        stargateConfig.withCqlOptionStringDC(dc, option, du);
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
    public AstraClientConfig withCqlOptionStringList(TypedDriverOption<List<String>> option, List<String> du) {
        stargateConfig.withCqlOptionStringList(option, du);
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
    public AstraClientConfig withCqlOptionStringListDC(String dc, TypedDriverOption<List<String>> option, List<String> du) {
        stargateConfig.withCqlOptionStringListDC(dc, option, du);
        return this;
    }
    
    
    /**
     * Provide a metrics registry.
     * 
     * @param mr
     *       metrics registry
     * @return
     *      self reference
     */
    public AstraClientConfig withCqlMetricsRegistry(Object mr) {
        stargateConfig.withCqlMetricsRegistry(mr);
        return this;
    }
    
    /**
     * Provide a request tracker.
     *
     * @param rt
     *       request tracker
     * @return
     *      self reference
     */
    public AstraClientConfig withCqlRequestTracker(RequestTracker rt) {
        stargateConfig.withCqlRequestTracker(rt);
        return this;
    }
    
    /**
     * Provide clientSecret.
     *
     * @param scbPath
     *            secure bundle
     * @return self reference
     */
    public AstraClientConfig withCqlSecureConnectBundleFolder(String scbPath) {
        hasLength(scbPath, "scbPath");
        this.secureConnectBundleFolder = scbPath;
        return this;
    }
    
    /**
     * Helper to build the path (use multiple times).
     * 
     * @param dId
     *      database id
     * @param dbRegion
     *      database region
     * @return
     *      filename
     */
    public static String buildScbFileName(String dId, String dbRegion) {
        return AstraClient.SECURE_CONNECT + dId + "_" + dbRegion + ".zip";
    }
    
    // ------------------------------------------------
    // -------------------- Core ----------------------
    // ------------------------------------------------
    
    /**
     * Reading environment variables
     */
    public AstraClientConfig() {
        LOGGER.info("Initializing [" + AnsiUtils.yellow("AstraClient") + "]");
        
        // Loading Stargate Environment variable
        stargateConfig = new StargateClientConfig();
        
        // Loading ~/.astrarc section default if present
        if (AstraRc.isDefaultConfigFileExists()) {
            loadFromAstraRc();
        }
        
        // Authentication
        readEnvVariable(ASTRA_DB_APPLICATION_TOKEN).ifPresent(this::withToken);
        readEnvVariable(ASTRA_DB_CLIENT_ID).ifPresent(this::withClientId);
        readEnvVariable(ASTRA_DB_CLIENT_SECRET).ifPresent(this::withClientSecret);
        
        // Database
        readEnvVariable(ASTRA_DB_ID).ifPresent(this::withDatabaseId);
        readEnvVariable(ASTRA_DB_REGION).ifPresent(this::withDatabaseRegion);
        readEnvVariable(ASTRA_DB_KEYSPACE).ifPresent(this::withCqlKeyspace);
        readEnvVariable(ASTRA_DB_SCB_FOLDER).ifPresent(this::withCqlSecureConnectBundleFolder);
        
    }
    
    // ------------------------------------------------
    // ----------------- AstraRC ----------------------
    // ------------------------------------------------
    
    /**
     * Some settings can be loaded from ~/.astrarc in you machine.
     * 
     * @return AstraClientBuilder
     *      configuration
     */
    public AstraClientConfig loadFromAstraRc() {
        AstraRc arc = new AstraRc();
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_ID).ifPresent(this::withDatabaseId);
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_ID).ifPresent(this::withDatabaseId);
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_REGION).ifPresent(this::withDatabaseRegion);
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_APPLICATION_TOKEN).ifPresent(this::withToken);
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_CLIENT_ID).ifPresent(this::withClientId);
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_CLIENT_SECRET).ifPresent(this::withClientSecret);
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_KEYSPACE).ifPresent(this::withCqlKeyspace);
        arc.getSectionKey(AstraRc.ASTRARC_DEFAULT, ASTRA_DB_SCB_FOLDER).ifPresent(this::withCqlSecureConnectBundleFolder);
        return this;
    }
    
    /**
     * Final build.
     * @return
     *      return the target object
     */
    public AstraClient build() {
        return new AstraClient(this);
    }
    
}
