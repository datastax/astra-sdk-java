package com.dstx.astra.sdk;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.dstx.astra.sdk.cql.ApiCqlClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.utils.AstraRc;
import com.dstx.astra.sdk.utils.Utils;
import com.dstx.stargate.client.StargateClient;
import com.dstx.stargate.client.doc.ApiDocumentClient;
import com.dstx.stargate.client.rest.ApiRestClient;
import com.dstx.stargate.client.utils.Assert;

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
    public static final String ASTRA_DB_ID            = "ASTRA_DB_ID";
    public static final String ASTRA_DB_REGION        = "ASTRA_DB_REGION";
    public static final String ASTRA_DB_USERNAME      = "ASTRA_DB_USERNAME";
    public static final String ASTRA_DB_PASSWORD      = "ASTRA_DB_PASSWORD";
    
    public static final String ASTRA_CLIENT_ID        = "ASTRA_CLIENT_ID";
    public static final String ASTRA_CLIENT_NAME      = "ASTRA_CLIENT_NAME";
    public static final String ASTRA_CLIENT_SECRET    = "ASTRA_CLIENT_SECRET";
    public static final String ASTRA_SECURE_BUNDLE    = "ASTRA_SECURE_BUNDLE";
    
    /** Building Astra base URL. */
    public static final String ASTRA_ENDPOINT_PREFIX  = "https://";
    public static final String ASTRA_ENDPOINT_SUFFIX  = ".apps.astra.datastax.com/api/rest";
    public static final String ENV_USER_HOME          = "user.home";
    public static final String SECURE_CONNECT         = "secure_connect_bundle_";
    
    /** Stargate client wrapping DOC, REST,GraphQL and CQL APis. */
    private StargateClient stargateClient;
   
    /** Hold a reference for the Api Devops. */
    private ApiDevopsClient apiDevops;
    
    /** Use the CqlApi through the cqlSession object. */
    private CqlSession cqlSession;
    
    /**
     * You can create on of {@link ApiDocumentClient}, {@link ApiRestClient}, {@link ApiDevopsClient}, {@link ApiCqlClient} with
     * a constructor. The full flegde constructor would took 12 pararms.
     */
    private AstraClient(AstraClientBuilder b) {
        
        /*
         * -----
         * ENABLE STARGATE CLIENT
         * You must have provided user/passwd/dbId/bbRegion
         * -----
         */
        if (Utils.paramsProvided(b.astraDatabaseId, b.astraDatabaseRegion, b.username, b.password)) {
            String astraStargateEndpint = new StringBuilder(ASTRA_ENDPOINT_PREFIX)
                    .append(b.astraDatabaseId).append("-").append(b.astraDatabaseRegion)
                    .append(ASTRA_ENDPOINT_SUFFIX).toString();
            this.stargateClient = StargateClient.builder()
                          .authenticationUrl(astraStargateEndpint)
                          .documentApiUrl(astraStargateEndpint)
                          .restApiUrl(astraStargateEndpint)
                          .username(b.username)
                          .password(b.password)
                          .build();
        }
        
        // User provide parameters required to enable the devopsAPI
        if (Utils.paramsProvided(b.clientName,  b.clientId , b.clientSecret)) {
            LOGGER.info("Initializing the devops API with client Name {}", b.clientName);
            apiDevops = new ApiDevopsClient(b.clientName, 
                    b.clientId, 
                    b.clientSecret);      
        }
        
        if (Utils.paramsProvided(b.username, b.password)) {
            // #1/ Path provided used it
            // #2/ LOOK IN ~/.astra/cloudbundle_dbid.zip
            // #3/ Download zip if not present
            // #4/ do not enable cql
            String p = b.secureConnectBundle;
            
            // create .astra
            //String fileName = System.getProperty(ENV_USER_HOME) 
            //        + File.separator + ".astra" + File.separator 
            //        + SECURE_CONNECT + builder.astraDatabaseId + ".zip", fileName);
            
            //apiDevops.downloadSecureConnectBundle(builder.astraDatabaseId);
            
            cqlSession = CqlSession.builder()
                    .withAuthCredentials(b.username, b.password)
                    .build();
                    //.withCloudSecureConnectBundle("");
        }
    }
    
    /** Document Api. */
    public ApiDocumentClient apiDocument() {
        return stargateClient.getApiDocument();
    }
    
    /** Rest Api. */
    public ApiRestClient apiRest() {
        return stargateClient.getApiRest();
    }
    
    /**
     *  Devops API
     */
    public ApiDevopsClient apiDevops() {
        return apiDevops;
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
        public String  username;
        public String  password;
        public String  clientId;
        public String  clientName;
        public String  clientSecret;
        public String  secureConnectBundle;
          
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
            // only is no stargate username
            if (null == this.username) {
                this.username = System.getenv(ASTRA_DB_USERNAME);
            }
            if (null == password) {
                this.password  = System.getenv(ASTRA_DB_PASSWORD);
            }
            // Load values from AstraRc if it exists
            // Only after initialization from environment variables 
            if (AstraRc.exists()) {
                LOGGER.info("Loading configuration from AstraRc");
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
                if (null == password) {
                    password = section.get(ASTRA_DB_PASSWORD);
                }
                if (null == username) {
                    username = section.get(ASTRA_DB_USERNAME);
                }
                if (null == clientId) {
                    clientId = section.get(ASTRA_CLIENT_ID);
                }
                if (null == clientName) {
                    clientName = section.get(ASTRA_CLIENT_NAME);
                }
                if (null == clientName) {
                    clientSecret = section.get(ASTRA_CLIENT_SECRET);
                }
            }
            return this;
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
        
        /**
         * Create the client
         */
        public AstraClient build() {
            return new AstraClient(this);
        }
    }  
     

}
