package com.datastax.astra.sdk;

import java.io.Closeable;
import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.devops.ApiDevopsClient;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.StargateClient.StargateClientBuilder;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.graphql.ApiGraphQLClient;
import com.datastax.stargate.sdk.rest.ApiRestClient;
import com.datastax.stargate.sdk.utils.Assert;
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
public class AstraClient implements Closeable {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);
    
    /** Initialize parameters from Environment variables. */
    public static final String ASTRA_DB_ID                = "ASTRA_DB_ID";
    public static final String ASTRA_DB_REGION            = "ASTRA_DB_REGION";
    public static final String ASTRA_DB_APPLICATION_TOKEN = "ASTRA_DB_APPLICATION_TOKEN";
    public static final String ASTRA_DB_CLIENT_ID         = "ASTRA_DB_CLIENT_ID";
    public static final String ASTRA_DB_CLIENT_SECRET     = "ASTRA_DB_CLIENT_SECRET";
    public static final String ASTRA_DB_KEYSPACE          = "ASTRA_DB_KEYSPACE";
    public static final String ASTRA_DB_SECURE_BUNDLE     = "ASTRA_DB_SECURE_BUNDLE";
    
    /** Building Astra base URL. */
    public static final String ASTRA_ENDPOINT_PREFIX  = "https://";
    public static final String ASTRA_ENDPOINT_REST_SUFFIX  = ".apps.astra.datastax.com/api/rest";
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
         * You must have provided an Application Token with admin rights
         * -----
         */
        if (Utils.paramsProvided(b.appToken)) {
            apiDevops = new ApiDevopsClient(b.appToken);  
            LOGGER.info("+ Devops API is enabled.");
        }
        
        if (Utils.paramsProvided(b.astraDatabaseId)) {
            /*
             * -----
             * ENABLE CQL API
             *
             * Stargate: 
             *  - username/password
             *  - contactPoints if neede
             * Astra: 
             *  - clientId/clientSecret or 'token'/appToken
             *  - will download secure bundle if possible
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
                    LOGGER.info("+ Downloading secureBundle for db '{}'", b.astraDatabaseId);
                    apiDevops.downloadSecureConnectBundle(b.astraDatabaseId, pathAstraSecureBundle);
                    cloudSecureBundle = pathAstraSecureBundle;
            }
            LOGGER.info("+ SecureBundle Path used: {}", cloudSecureBundle);
        
            /*
             * -----
             * ENABLE STARGATE APIS if possible (rest,graphql)
             * You must have provided user/passwd/dbId/bbRegion
             * -----
             */
            if (Utils.paramsProvided(b.astraDatabaseId, b.astraDatabaseRegion, b.appToken)) {
                
                String astraStargateRestEndpoint = new StringBuilder(ASTRA_ENDPOINT_PREFIX)
                        .append(b.astraDatabaseId).append("-").append(b.astraDatabaseRegion)
                        .append(ASTRA_ENDPOINT_REST_SUFFIX).toString();
                String username = "token";
                String password = b.appToken;
                if (Utils.paramsProvided(b.clientId, b.clientSecret)) {
                    LOGGER.info("+ Using clientId/clientSecret for CqlSession");
                    username = b.clientId;
                    password = b.clientSecret;
                } else {
                    LOGGER.info("+ Using 'token'/appToken for CqlSession");
                }
                
                /* 
                 * The Stargate in Astra is setup to use SSO. You generate a token from the
                 * user interface and use 'token' as username all the time
                 */
                StargateClientBuilder sBuilder = StargateClient.builder()
                              .endPointRest(astraStargateRestEndpoint)
                              // Used for CqlSession
                              .username(username)
                              .password(password)
                              // Use for HTTP Calls, required for Astra.
                              .appToken(b.appToken);
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
        return stargateClient.apiDocument();
    }
    
    /** 
     * Rest Api. 
     */
    public ApiRestClient apiRest() {
        if (stargateClient == null) {
            throw new IllegalStateException("Api Rest is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.apiRest();
    }
    
    /** 
     * GraphQL Api. 
     */
    public ApiGraphQLClient apiGraphQL() {
        if (stargateClient == null) {
            throw new IllegalStateException("GraphQL Api is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.apiGraphQL();
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
        if (stargateClient == null || stargateClient.cqlSession().isEmpty()) {
            throw new IllegalStateException("CQL not available  Rest is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.cqlSession().get();
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
        
        public String  clientId;
        public String  clientSecret;
        
        public String  appToken;
        public String  secureConnectBundle;
        public String  keyspace;
          
        /**
         * Load defaults from Emvironment variables
         */
        protected AstraClientBuilder() {
            LOGGER.info("Initializing [AstraClient]");
            
            // Configuration File
            if (AstraRc.exists()) {
                LOGGER.info("+ Load configuration from file ~/.astrarc");
                astraRc(AstraRc.load(), AstraRc.ASTRARC_DEFAULT);
            }
            
            // Environment Variables
            LOGGER.info("+ Load configuration from Environment Variables/Property");
            if (Utils.hasLength(System.getProperty(ASTRA_DB_ID))) {
                this.astraDatabaseId = System.getProperty(ASTRA_DB_ID);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_ID))) {
                this.astraDatabaseId = System.getenv(ASTRA_DB_ID);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_REGION))) {
                this.astraDatabaseRegion = System.getProperty(ASTRA_DB_REGION);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_REGION))) {
                this.astraDatabaseRegion = System.getenv(ASTRA_DB_REGION);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_APPLICATION_TOKEN))) {
                this.appToken = System.getProperty(ASTRA_DB_APPLICATION_TOKEN);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_APPLICATION_TOKEN))) {
                this.appToken = System.getenv(ASTRA_DB_APPLICATION_TOKEN);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_SECURE_BUNDLE))) {
                this.secureConnectBundle = System.getProperty(ASTRA_DB_SECURE_BUNDLE);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_SECURE_BUNDLE))) {
                this.secureConnectBundle = System.getenv(ASTRA_DB_SECURE_BUNDLE);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_KEYSPACE))) {
                this.keyspace = System.getProperty(ASTRA_DB_KEYSPACE);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_KEYSPACE))) {
                this.keyspace = System.getenv(ASTRA_DB_KEYSPACE);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_CLIENT_ID))) {
                this.clientId = System.getProperty(ASTRA_DB_CLIENT_ID);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_CLIENT_ID))) {
                this.clientId = System.getenv(ASTRA_DB_CLIENT_ID);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_CLIENT_SECRET))) {
                this.clientSecret = System.getProperty(ASTRA_DB_CLIENT_SECRET);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_CLIENT_SECRET))) {
                this.clientSecret = System.getenv(ASTRA_DB_CLIENT_SECRET);
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
                if (null == clientId) {
                    clientId = section.get(ASTRA_DB_CLIENT_ID);
                }
                if (null == clientSecret) {
                    clientSecret = section.get(ASTRA_DB_CLIENT_SECRET);
                }
                if (null == appToken) {
                    appToken = section.get(ASTRA_DB_APPLICATION_TOKEN);
                }
                if (null == secureConnectBundle) {
                    secureConnectBundle = section.get(ASTRA_DB_SECURE_BUNDLE);
                }
                if (null == keyspace) {
                    keyspace = section.get(ASTRA_DB_KEYSPACE);
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
        public AstraClientBuilder clientId(String clientId) {
            Assert.hasLength(clientId, "clientId");
            this.clientId = clientId;
            return this;
        }
        public AstraClientBuilder clientSecret(String clientSecret) {
            Assert.hasLength(clientSecret, "clientSecret");
            this.clientSecret = clientSecret;
            return this;
        }
        
        /**
         * Create the client
         */
        public AstraClient build() {
            return new AstraClient(this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
       if (null != stargateClient) {
           stargateClient.close();
       }
    }

}
