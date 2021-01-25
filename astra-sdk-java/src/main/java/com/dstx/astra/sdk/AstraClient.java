package com.dstx.astra.sdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dstx.astra.sdk.cql.ApiCqlClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.document.ApiDocumentClient;
import com.dstx.astra.sdk.rest.ApiRestClient;
import com.dstx.astra.sdk.utils.Assert;

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
    
    /** 
     * Environment Variables names you can use to initiate values (by convention).
     * - Some keys prefix with 'ASTRA_' will sue the Saas
     * - Some keys are read wieh working with standAlone stargate
     **/
    
    //Rest,Doc,Grapql Apis
    public static final String ASTRA_DB_ID            = "ASTRA_DB_ID";
    public static final String ASTRA_DB_REGION        = "ASTRA_DB_REGION";
    public static final String ASTRA_DB_USERNAME      = "ASTRA_DB_USERNAME";
    public static final String ASTRA_DB_PASSWORD      = "ASTRA_DB_PASSWORD";

    // Devops API
    public static final String ASTRA_CLIENT_ID        = "ASTRA_CLIENT_ID";
    public static final String ASTRA_CLIENT_NAME      = "ASTRA_CLIENT_NAME";
    public static final String ASTRA_CLIENT_SECRET    = "ASTRA_CLIENT_SECRET";
    
    // Cql API
    public static final String ASTRA_SECURE_BUNDLE    = "ASTRA_SECURE_BUNDLE";
    public static final String STARGATE_USERNAME      = "STARGATE_USERNAME";
    public static final String STARGATE_PASSWORD      = "STARGATE_PASSWORD";

    public static final String BASE_URL               = "BASE_URL";
    public static final String DRIVER_CONFIG_FILE     = "DRIVER_CONFIG_FILE";
    public static final String CONTACT_POINTS         = "CONTACT_POINTS";

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);
   
    /** Hold a reference for the ApiDocument. */
    private ApiDocumentClient apiDoc;
    
    /** Hold a reference for the ApiRest. */
    private ApiRestClient apiRest;
    
    /** Hold a reference for the Api Devops. */
    private ApiDevopsClient apiDevops;
    
    /** Hold a reference for the Api Devops. */
    private ApiCqlClient apiCql;
    
    /**
     *  Accessing Document API
     */
    public synchronized ApiDocumentClient apiDocument() {
        if (null == apiDoc) {
            if (null != baseUrl) {
                LOGGER.info("Initializing the document API with base url {}", baseUrl);
                apiDoc = new ApiDocumentClient(baseUrl, username, password);
            } else {
                LOGGER.info("Initializing the document API with astra db {}", astraDatabaseId);
                apiDoc = new ApiDocumentClient(astraDatabaseId, astraDatabaseRegion, username, password);
            }
        }
        return apiDoc;
    }
    
    /**
     *  Accessing Rest API
     */
    public synchronized ApiRestClient apiRest() {
        if (null == apiRest) {
            if (null != baseUrl) {
                LOGGER.info("Initializing the rest API with base url {}", baseUrl);
                apiRest = new ApiRestClient(baseUrl, username, password);
            } else {
                LOGGER.info("Initializing the document API with astra db {}", astraDatabaseId);
                apiRest = new ApiRestClient(astraDatabaseId, astraDatabaseRegion, username, password);
            }
        }
        return apiRest;
    }
    
    /**
     *  Accessing Devops API
     */
    public synchronized ApiDevopsClient apiDevops() {
        if (null == apiDevops) {
            LOGGER.info("Initializing the devops API with client Name {}", clientName);
            apiDevops = new ApiDevopsClient(clientName, clientId, clientSecret);
        }
        return apiDevops;
    }
    
    /**
     * Accessing Cql Session.
     *  
     * Base on provided parameter we will initialized the CqlSession.
     */
    public synchronized ApiCqlClient apiCql() {
        if (null == apiCql) {
            ApiDevopsClient myDevopsCLient = null;
            try {
                myDevopsCLient = apiDevops();
            } catch(Exception e) {
                LOGGER.info("Cannot use devops Api, not enough info provided");
            }
            LOGGER.info("Initializing the Cql API with client");
            apiCql = new ApiCqlClient(driverConfigFile, username, password, 
                    secureConnectBundlePath, contactPoints, myDevopsCLient, astraDatabaseId);
        }
        return apiCql;
    }
    
    // -----------------------------------------------
    // Attributes to be populated by BUILDER
    // Api(s) to initialize based on those values
    // ----------------------------------------------
    
    // --- Doc and REST ---
    
    /** Database unique identifier.  */
    private final String astraDatabaseId;
    
    /** Astra database region. */
    private final String astraDatabaseRegion;
    
    /** This the endPoint to invoke to work with different API(s). */
    private final String baseUrl;
    
    /** Username - required all the time */
    private final String username;
    
    /** Password - required all the time */
    private final String password;
    
    // --- Devops---
    
    /** Service Account for Devops API. */
    private final String clientId;
    
    /** Service Account for Devops API. */
    private final String clientName;
    
    /** Service Account for Devops API. */
    private final String clientSecret;
    
    // --- Cql ---
    
    /** working with local Cassandra. */
    private final List<String> contactPoints;
    
    /** working with Astra. */
    private final String secureConnectBundlePath;
    
    /** setup Astra from an external file. */
    private final String driverConfigFile;
    
    /**
     * You can create on of {@link ApiDocumentClient}, {@link ApiRestClient}, {@link ApiDevopsClient}, {@link ApiCqlClient} with
     * a constructor. The full flegde constructor would took 12 pararms.
     */
    private AstraClient(AstraClientBuilder builder) {
        this.astraDatabaseId         = builder.astraDatabaseId;
        this.astraDatabaseRegion     = builder.astraDatabaseRegion;
        this.baseUrl                 = builder.baseUrl;
        this.username                = builder.username;
        this.password                = builder.password;
        this.clientId                = builder.clientId;
        this.clientName              = builder.clientName;
        this.clientSecret            = builder.clientSecret;
        this.secureConnectBundlePath = builder.secureConnectBundle;
        this.driverConfigFile       = builder.driverConfigFile;
        this.contactPoints           = builder.contactPoints;
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
        public String   astraDatabaseId;
        public String   astraDatabaseRegion;
        public String   baseUrl;
        public String   username;
        public String   password;
        public String   clientId;
        public String   clientName;
        public String   clientSecret;
        public String   secureConnectBundle;
        public String   driverConfigFile;
        public List<String> contactPoints;
          
        /**
         * Load defaults from Emvironment variables
         */
        protected AstraClientBuilder() {
            this.astraDatabaseId          = System.getenv(ASTRA_DB_ID);
            this.astraDatabaseRegion      = System.getenv(ASTRA_DB_REGION);
            this.clientId                 = System.getenv(ASTRA_CLIENT_ID);
            this.clientName               = System.getenv(ASTRA_CLIENT_NAME);
            this.clientSecret             = System.getenv(ASTRA_CLIENT_SECRET);
            this.secureConnectBundle      = System.getenv(ASTRA_SECURE_BUNDLE);
            this.driverConfigFile         = System.getenv(DRIVER_CONFIG_FILE);
            this.contactPoints            = new ArrayList<>();  
            this.baseUrl = System.getenv(BASE_URL);
            
            if (null != System.getenv(STARGATE_USERNAME)) {
                this.username = System.getenv(STARGATE_USERNAME);
            }
            if (null == this.username) {
                this.username = System.getenv(ASTRA_DB_USERNAME);
            }
            // 
            if (null != System.getenv(STARGATE_PASSWORD)) {
                this.password = System.getenv(STARGATE_PASSWORD);
            }
            if (null == password) {
                this.password  = System.getenv(ASTRA_DB_PASSWORD);
            }
            
            if (null != System.getenv(CONTACT_POINTS)) {
                this.contactPoints = Arrays.asList(System.getenv(CONTACT_POINTS).split(","));
            }
            
        }
        
        public AstraClientBuilder astraDatabaseId(String uid) {
            Assert.hasLength(uid, "astraDatabaseId");
            this.astraDatabaseId = uid;
            return this;
        }
        public AstraClientBuilder astraDatabaseRegion(String region) {
            Assert.hasLength(region, "astraDatabaseRegion");
            this.astraDatabaseRegion = region;
            return this;
        }
        public AstraClientBuilder username(String username) {
            Assert.hasLength(username, "username");
            this.username = username;
            return this;
        }
        public AstraClientBuilder password(String password) {
            Assert.hasLength(password, "password");
            this.password = password;
            return this;
        }
        public AstraClientBuilder baseUrl(String baseUrl) {
            Assert.hasLength(baseUrl, "baseUrl");
            this.baseUrl = baseUrl;
            return this;
        }
        public AstraClientBuilder clientId(String clientId) {
            Assert.hasLength(clientId, "clientId");
            this.clientId = clientId;
            return this;
        }
        public AstraClientBuilder clientName(String clientName) {
            Assert.hasLength(clientName, "clientName");
            this.clientName = clientName;
            return this;
        }
        public AstraClientBuilder clientSecret(String clientSecret) {
            Assert.hasLength(clientSecret, "clientSecret");
            this.clientSecret = clientSecret;
            return this;
        }
        public AstraClientBuilder secureConnectBundle(String secureConnectBundle) {
            Assert.hasLength(secureConnectBundle, "secureConnectBundle");
            this.secureConnectBundle = secureConnectBundle;
            return this;
        }
        public AstraClientBuilder driverConfigFile(String applicationConfigFile) {
            Assert.hasLength(applicationConfigFile, "applicationConfigFile");
            this.driverConfigFile = applicationConfigFile;
            return this;
        }
        public AstraClientBuilder addContactPoint(String ip, int port) {
            Assert.hasLength(ip, "ip");
            this.contactPoints.add(ip + ":" + port);
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
