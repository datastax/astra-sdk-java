package com.dstx.astra.sdk;

import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.utils.AstraRc;
import com.dstx.stargate.sdk.StargateClient;
import com.dstx.stargate.sdk.StargateClient.StargateClientBuilder;
import com.dstx.stargate.sdk.doc.ApiDocumentClient;
import com.dstx.stargate.sdk.rest.ApiRestClient;
import com.dstx.stargate.sdk.utils.Assert;
import com.dstx.stargate.sdk.utils.Utils;

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
public class AstraClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);
    
    /** Initialize parameters from Environment variables. */
    public static final String ASTRA_DB_ID                = "ASTRA_DB_ID";
    public static final String ASTRA_DB_REGION            = "ASTRA_DB_REGION";
    public static final String ASTRA_DB_APPLICATION_TOKEN = "ASTRA_DB_APPLICATION_TOKEN";
    public static final String ASTRA_DB_KEYSPACE          = "ASTRA_DB_KEYSPACE";
    public static final String ASTRA_DB_SECURE_BUNDLE     = "ASTRA_DB_SECURE_BUNDLE";
    
    /** Building Astra base URL. */
    public static final String ASTRA_ENDPOINT_PREFIX  = "https://";
    public static final String ASTRA_ENDPOINT_SUFFIX  = ".apps.astra.datastax.com/api/rest";
    public static final String ENV_USER_HOME          = "user.home";
    public static final String SECURE_CONNECT         = "secure_connect_bundle_";
    
    /** Stargate client wrapping DOC, REST,GraphQL and CQL APis. */
    private StargateClient stargateClient;
   
    /** Hold a reference for the Api Devops. */
    private ApiDevopsClient apiDevops;
  
    /**
     * You can create on of {@link ApiDocumentClient}, {@link ApiRestClient}, {@link ApiDevopsClient}, {@link ApiCqlClient} with
     * a constructor. The full flegde constructor would took 12 pararms.
     */
    private AstraClient(AstraClientBuilder b) {
        
        LOGGER.info("+ Load configuration from Builder parameters");
        
        /*
         * -----
         * ENABLE DEVOPS API (if possible) 
         * You must have provided clientId/clientName/clientSecrret
         * -----
         */
        if (Utils.paramsProvided(b.appToken)) {
            apiDevops = new ApiDevopsClient(b.appToken);  
            LOGGER.info("+ Devops API is enabled.");
        }
        
        if (Utils.paramsProvided(b.astraDatabaseId)) {
            /*
             * -----
             * ENABLE CQL API (through stargateif possible) 
             * You must have provided username/password + (cloudSecureBundle or devopsApi + dbid) 
             * -----
             */
            String cloudSecureBundle = null;
            String pathAstraFolder       = System.getProperty(ENV_USER_HOME) + File.separator + ".astra";
            String pathAstraSecureBundle = pathAstraFolder + File.separator + SECURE_CONNECT + b.astraDatabaseId + ".zip";
            // #1. A path has been provided for secureConnectBundle => use it
            if (Utils.paramsProvided(b.secureConnectBundle)) {
                if (!new File(b.secureConnectBundle).exists()) {
                    throw new IllegalArgumentException("Cannot read file " 
                            + b.secureConnectBundle + " provided for the cloud bundle");
                }
                cloudSecureBundle = b.secureConnectBundle;
    
            // #2. LOOK IN ~/.astra/secure_connect_bundle_${dbid}.zip
            } else if (new File(pathAstraSecureBundle).exists()) {
                cloudSecureBundle = pathAstraSecureBundle;
            
            // #3. Download zip if not present
            } else if (null != apiDevops && null != b.astraDatabaseId) {
                File folderAstra = new File(pathAstraFolder);
                if (!folderAstra.exists()) {
                        folderAstra.mkdir();
                        LOGGER.info("+ Creating folder .astra"); 
                    }
                    LOGGER.info("+ Downloading for secureBundle db '{}'", b.astraDatabaseId);
                    apiDevops.downloadSecureConnectBundle(b.astraDatabaseId, pathAstraSecureBundle);
                    cloudSecureBundle = pathAstraSecureBundle;
            }
            LOGGER.info("+ SecureBundle: {}", cloudSecureBundle);
        
            /*
             * -----
             * ENABLE STARGATE CLIENT (if possible)
             * You must have provided user/passwd/dbId/bbRegion
             * -----
             */
            if (Utils.paramsProvided(b.astraDatabaseId, b.astraDatabaseRegion, b.appToken)) {
                String astraStargateEndpint = new StringBuilder(ASTRA_ENDPOINT_PREFIX)
                        .append(b.astraDatabaseId).append("-").append(b.astraDatabaseRegion)
                        .append(ASTRA_ENDPOINT_SUFFIX).toString();
                /* 
                 * The Stargate in Astra is setup to use SSO. You generate a token from the
                 * user interface and use 'token' as username all the time
                 */
                StargateClientBuilder sBuilder = StargateClient.builder()
                              .documentApiUrl(astraStargateEndpint)
                              .restApiUrl(astraStargateEndpint)
                              // IAM v2, Not token refresh password is the token 
                              .authenticationUrl(null)
                              .username("token")
                              .password(b.appToken);
                if (Utils.paramsProvided(b.keyspace)) {
                    sBuilder = sBuilder.keypace(b.keyspace);
                }
                if (null != cloudSecureBundle) {
                    sBuilder = sBuilder.astraCloudSecureBundle(cloudSecureBundle);
                }
                
                this.stargateClient = sBuilder.build();
            }
        }
        LOGGER.info("[AstraClient] has been initialized.");
    }
    
    /** Document Api. */
    public ApiDocumentClient apiDocument() {
        if (stargateClient == null) {
            throw new IllegalStateException("Api Document is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.getApiDocument();
    }
    
    /** 
     * Rest Api. 
     */
    public ApiRestClient apiRest() {
        if (stargateClient == null) {
            throw new IllegalStateException("Api Rest is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.getApiRest();
    }
    
    /**
     *  Devops API
     */
    public ApiDevopsClient apiDevops() {
        if (apiDevops == null) {
            throw new IllegalStateException("Api Devops is not available "
                    + "you need to provide clientId/clientName/clientSecret at initialization.");
        }
        return apiDevops;
    }
    
    /**
     *  CQL API
     */
    public CqlSession cqlSession() {
        if (stargateClient == null || stargateClient.getCqlSession().isEmpty()) {
            throw new IllegalStateException("CQL not available  Rest is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.getCqlSession().get();
    }
    
    /**
     * Builder Pattern
     */
    public static final AstraClientBuilder builder() {
        return new AstraClientBuilder();
    }
    
    /**
     * Builder pattern
     */
    public static class AstraClientBuilder {
        public String  astraDatabaseId;
        public String  astraDatabaseRegion;
        public String  appToken;
        public String  secureConnectBundle;
        public String  keyspace;
          
        /**
         * Load defaults from Emvironment variables
         */
        protected AstraClientBuilder() {
            LOGGER.info("Initializing [AstraClient]");
            LOGGER.info("+ Load configuration from Environment Variables");
            this.astraDatabaseId          = System.getenv(ASTRA_DB_ID);
            this.astraDatabaseRegion      = System.getenv(ASTRA_DB_REGION);
            this.appToken                 = System.getenv(ASTRA_DB_APPLICATION_TOKEN);
            this.secureConnectBundle      = System.getenv(ASTRA_DB_SECURE_BUNDLE);
            this.keyspace                 = System.getenv(ASTRA_DB_KEYSPACE);
           
            // Load values from AstraRc if it exists
            // Only after initialization from environment variables 
            if (AstraRc.exists()) {
                LOGGER.info("+ Load configuration from AstraRc");
                astraRc(AstraRc.load(), AstraRc.ASTRARC_DEFAULT);
            }
        }
        
        public AstraClientBuilder astraRc(AstraRc arc, String sectionName) {
            Map<String,String> section = arc.getSections().get(sectionName);
            if (null != section) {
                if (null == astraDatabaseId) {
                    astraDatabaseId = section.get(ASTRA_DB_ID);
                }
                if (null == astraDatabaseRegion) {
                    astraDatabaseRegion = section.get(ASTRA_DB_REGION);
                }
                if (null == appToken) {
                    appToken = section.get(ASTRA_DB_APPLICATION_TOKEN);
                }
                if (null == secureConnectBundle) {
                    secureConnectBundle = section.get(ASTRA_DB_SECURE_BUNDLE);
                }
                if (null == secureConnectBundle) {
                    secureConnectBundle = section.get(ASTRA_DB_SECURE_BUNDLE);
                }
            }
            return this;
        }
        
        public AstraClientBuilder databaseId(String uid) {
            Assert.hasLength(uid, "astraDatabaseId");
            this.astraDatabaseId = uid;
            return this;
        }
        public AstraClientBuilder cloudProviderRegion(String region) {
            Assert.hasLength(region, "astraDatabaseRegion");
            this.astraDatabaseRegion = region;
            return this;
        }
        public AstraClientBuilder appToken(String token) {
            Assert.hasLength(token, "token");
            this.appToken = token;
            return this;
        }
        public AstraClientBuilder secureConnectBundle(String secureConnectBundle) {
            Assert.hasLength(secureConnectBundle, "secureConnectBundle");
            this.secureConnectBundle = secureConnectBundle;
            return this;
        }
        public AstraClientBuilder keyspace(String keyspace) {
            Assert.hasLength(keyspace, "keyspace");
            this.keyspace = keyspace;
            return this;
        }
        
        /**
         * Create the client
         */
        public AstraClient build() {
            return new AstraClient(this);
        }
    }  
     

}
