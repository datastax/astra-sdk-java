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

import java.io.Closeable;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.databases.DatabasesClient;
import com.datastax.astra.sdk.organizations.OrganizationsClient;
import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.sdk.utils.ApiLocator;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.utils.AnsiUtils;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Public interface to interact with ASTRA APIs.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraClient implements Closeable {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AstraClient.class);
    
    /** Initialize parameters from Environment variables. */
    public static final String ASTRA_DB_ID                = "ASTRA_DB_ID";
    /** ENV VAR to get current or default region (local datacenter). */
    public static final String ASTRA_DB_REGION            = "ASTRA_DB_REGION";
    /** ENV VAR to get regions list. */
    public static final String ASTRA_DB_REGIONS           = "ASTRA_DB_REGIONS";
    /** ENV VAR to get part of the token: client Id. */
    public static final String ASTRA_DB_CLIENT_ID         = "ASTRA_DB_CLIENT_ID";
    /** ENV VAR to get part of the token: client Secret. */
    public static final String ASTRA_DB_CLIENT_SECRET     = "ASTRA_DB_CLIENT_SECRET";
    /** ENV VAR to get part of the token: application token. */
    public static final String ASTRA_DB_APPLICATION_TOKEN = "ASTRA_DB_APPLICATION_TOKEN";
    /** ENV VAR to get the keyspace to be selected. */
    public static final String ASTRA_DB_KEYSPACE          = "ASTRA_DB_KEYSPACE";
    /** SECURE BUNDLE FOR EACH RECGIONS. */
    public static final String ASTRA_DB_SECURE_BUNDLES    = "ASTRA_DB_SECURE_BUNDLES";
    
    /** */
    public static final String ENV_USER_HOME              = "user.home";
    /** */
    public static final String SECURE_CONNECT             = "secure_connect_bundle_";
    /** */
    public static final int DEFAULT_POLLING               = 30;
    
    /** 
     * Stargate client wrapping DOC, REST,GraphQL and CQL APis. 
     * 
     * It will handle all instances of Stargates doing load balancing 
     * in between the instances in same DC and fail-over cross region 
     * using Ribbon and hystrix.
     * */
    private StargateClient stargateClient;
   
    // -----------------------------------------------------
    // --------- Devops API Endpoints  ---------------------
    // -----------------------------------------------------

    /** Hold a reference for the authentication token. */
    private final String token;
    
    /** Hold a reference for the Api Devops. */
    private DatabasesClient apiDevopsDatabases;
    
    /** Hold a reference for the Api Devops. */
    private OrganizationsClient apiDevopsOrganizations;
    
    /** Hold a reference for the Api Devops. */
    private StreamingClient apiDevopsStreaming;

    // -----------------------------------------------------
    // --------- Stargate APIs Settings --------------------
    // -----------------------------------------------------
    
    /** Hold a reference for clientId. */
    private final String clientId;
    
    /** Hold a reference for clientId. */
    private final String clientSecret;
    
    /** Hold a reference for databaseId. */
    private final String databaseId;
    
    /** Hold a reference for databaseId. */
    private final List<String> databaseRegions;
    
    /** Hold a reference on current region used for the Failover. */
    private String currentDatabaseRegion;
    
    /**
     * You can create on of {@link ApiDocumentClient}, {@link ApiDataClient}, {@link DatabasesClient}, {@link ApiCqlClient} with
     * a constructor. The full flegde constructor would took 12 pararms.
     */
    private AstraClient(AstraClientBuilder b) {
        LOGGER.info("+ Initializing Astra Client");
        
        /** 
         * Astra Credentials.
         */
        this.token          = b.appToken;
        this.clientId       = b.clientId;
        this.clientSecret   = b.clientSecret;
      
        /*
         * -----
         * ENABLE DEVOPS API (if possible) 
         * You must have provided an Application Token with admin rights
         * -----
         */
        if (Utils.paramsProvided(b.appToken)) {
            apiDevopsOrganizations  = new OrganizationsClient(b.appToken);
            apiDevopsDatabases      = new DatabasesClient(b.appToken);  
            apiDevopsStreaming      = new StreamingClient(b.appToken);
            LOGGER.info("+ API(s) Devops [" + AnsiUtils.green("ENABLED")+ "]");
        } else {
            LOGGER.info("+ API(s) Devops [" + AnsiUtils.red("DISABLED")+ "]");
        }
       
        if (Utils.paramsProvided(b.astraDatabaseId)) {
            this.databaseId      = b.astraDatabaseId;
            this.databaseRegions = b.astraDatabaseRegions;
            this.currentDatabaseRegion = b.astraDatabaseCurrentRegion;
            /*
             * -----
             * ENABLE CQL API
             *
             * Stargate: 
             *  - username/password
             *  - contactPoints if needed
             * Astra: 
             *  - clientId/clientSecret or 'token'/appToken
             *  - will download secure bundle for each region if possible
             * -----
             */
            String cloudSecureBundle = null;
            String pathAstraFolder       = System.getProperty(ENV_USER_HOME) + File.separator + ".astra";
            String pathAstraSecureBundle = pathAstraFolder + File.separator + SECURE_CONNECT + b.astraDatabaseId + ".zip";
            // #1. Path has been provided for secureConnectBundle => use it
            if (!b.secureConnectBundles.isEmpty()) {
                
                if (!new File(b.secureConnectBundles).exists()) {
                    throw new IllegalArgumentException("Cannot read file " 
                            + b.secureConnectBundles + " provided for the cloud bundle");
                }
                cloudSecureBundle = b.secureConnectBundles;
    
            // #2. LOOK IN ~/.astra/secure_connect_bundle_${dbid}.zip
            } else if (new File(pathAstraSecureBundle).exists()) {
                cloudSecureBundle = pathAstraSecureBundle;
            
            // #3. Download zip if not present
            } else if (null != apiDevopsDatabases && null != b.astraDatabaseId) {
                File folderAstra = new File(pathAstraFolder);
                if (!folderAstra.exists()) {
                        folderAstra.mkdir();
                        LOGGER.info("+ Creating folder .astra"); 
                    }
                    LOGGER.info("+ Downloading secureBundle for db '{}'", b.astraDatabaseId);
                    apiDevopsDatabases.database(b.astraDatabaseId).downloadSecureConnectBundle(pathAstraSecureBundle);
                    cloudSecureBundle = pathAstraSecureBundle;
            }
            LOGGER.info("+ Load Secure Connect: {}", cloudSecureBundle);
        
            /*
             * -----
             * ENABLE STARGATE APIS if possible (rest,graphql)
             * You must have provided user/passwd/dbId/bbRegion
             * -----
             */
            if (Utils.paramsProvided(b.astraDatabaseId, b.appToken) && !b.astraDatabaseRegions.isEmpty()) {
                String username = "token";
                String password = b.appToken;
                if (Utils.paramsProvided(b.clientId, b.clientSecret)) {
                    LOGGER.info("+ CQL Credentials: ${clientId}${/clientSecret}");
                    username = b.clientId;
                    password = b.clientSecret;
                } else {
                    LOGGER.info("+ CQL Credentials: 'token'/${token}");
                }
                
                /* 
                 * The Stargate in Astra is setup to use SSO. You generate a token from the
                 * user interface and use 'token' as username all the time
                 * 
                 * datastax-java-driver {
                 *   
                 *   basic {
                 *     request {
                 *       timeout     = 10 seconds
                 *       consistency = LOCAL_QUORUM
                 *       page-size   = 5000
                 *     }
                 *   }
                 *   
                 *   advanced {
                 *     connection {
                 *       init-query-timeout = 10 seconds
                 *       set-keyspace-timeout = 10 seconds
                 *     }
                 *     control-connection.timeout = 10 seconds
                 *   }
                 *   
                 * }
                 */
                StargateClientBuilder sBuilder = StargateClient.builder()
                              .withAuthCredentials(username, password)
                              .withLocalDatacenter(b.astraDatabaseCurrentRegion)
                              // Settings of CqlSession to work with Astra (extends timeouts)
                              .withCqlDriverOption(TypedDriverOption.REQUEST_CONSISTENCY, ConsistencyLevel.LOCAL_QUORUM.name())
                              .withCqlDriverOption(TypedDriverOption.REQUEST_TIMEOUT, Duration.ofSeconds(10))
                              .withCqlDriverOption(TypedDriverOption.REQUEST_PAGE_SIZE, 100)
                              .withCqlDriverOption(TypedDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(10))
                              .withCqlDriverOption(TypedDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(10))
                              .withCqlDriverOption(TypedDriverOption.CONNECTION_SET_KEYSPACE_TIMEOUT, Duration.ofSeconds(10))
                              .withCqlDriverOption(TypedDriverOption.CONTROL_CONNECTION_TIMEOUT, Duration.ofSeconds(10))
                              // Create dedicated execution profiles per DC changing scb and localDC
                              .withCqlOption("DC1", TypedDriverOption.CONTROL_CONNECTION_TIMEOUT, Duration.ofSeconds(10))
                              
                              
                
                
                              // Add a StargadeNode per DC (load balancing is already OK)
                              .withApiToken(b.appToken);
                              
                
                
                              //.endPointRest(ApiLocator.getApiRestEndpoint(b.astraDatabaseId, b.astraDatabaseRegions.get(0)))
                              //.endPointGraphQL(ApiLocator.getApiGraphQLEndPoint(b.astraDatabaseId,  b.astraDatabaseRegions.get(0)))
                              // Use for HTTP Calls, required for Astra.
                              //.appToken(b.appToken);
                if (Utils.paramsProvided(b.keyspace)) {
                    sBuilder = sBuilder.withKeyspace(b.keyspace);
                }
                if (null != cloudSecureBundle) {
                    sBuilder = sBuilder.astraCloudSecureBundle(cloudSecureBundle);
                }
                
                this.stargateClient = sBuilder.build();
            }
        } else {
            databaseId     = null;
            databaseRegion = null;
        }
        LOGGER.info("[AstraClient] has been initialized.");
    }
    
    /**
     * Document Api.
     * 
     * @return ApiDocumentClient
     */
    public ApiDocumentClient apiStargateDocument() {
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
    public ApiDataClient apiStargateData() {
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
    public ApiGraphQLClient apiStargateGraphQL() {
        if (stargateClient == null) {
            throw new IllegalStateException("GraphQL Api is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.apiGraphQL();
    }
    
    /**
     * Devops API
     * 
     * @return ApiDevopsClient
     */
    public OrganizationsClient apiDevopsOrganizations() {
        if (apiDevopsOrganizations == null) {
            throw new IllegalStateException("Api Devops is not available "
                    + "you need to provide clientId/clientName/clientSecret at initialization.");
        }
        return apiDevopsOrganizations;
    }
    
    /**
     * Devops API
     * 
     * @return ApiDevopsClient
     */
    public DatabasesClient apiDevopsDatabases() {
        if (apiDevopsDatabases == null) {
            throw new IllegalStateException("Api Devops is not available "
                    + "you need to provide clientId/clientName/clientSecret at initialization.");
        }
        return apiDevopsDatabases;
    }
    
    /**
     * Devops API
     * 
     * @return ApiDevopsClient
     */
    public StreamingClient streaming() {
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
            throw new IllegalStateException("CQL not available  Rest is not available "
                    + "you need to provide dbId/dbRegion/username/password at initialization.");
        }
        return stargateClient.cqlSession().get();
    }
    
    /**
     * Builder Pattern
     * 
     * @return AstraClientBuilder
     */
    public static final AstraClientBuilder builder() {
        return new AstraClientBuilder();
    }
    
    /**
     * Builder pattern
     */
    public static class AstraClientBuilder {
        
        /** Astra Credentials. */
        private String  clientId;
        
        /** Astra Credentials. */
        private String  clientSecret;
        
        /** Astra Credentials. */
        private String  appToken;
        
        /** Database ids. */
        private String  astraDatabaseId;
        
        /** Default region when you connect. */
        private String astraDatabaseCurrentRegion;
        
        /** A Database can now live in multilple regions*/
        private List<String> astraDatabaseRegions = new ArrayList<>();
        
        /** Associate a SCB for each region. */
        private Map<String, String>  secureConnectBundles;
        
        /** CqlSession target keyspace. */
        private String  keyspace;
          
        /**
         * Load defaults from Emvironment variables
         */
        protected AstraClientBuilder() {
            LOGGER.info("Initializing [" + AnsiUtils.green("AstraClient") + "]");
            
            // Loading configuration File 
            if (AstraRc.exists()) {
                LOGGER.info("+ Loading ~/.astrarc section {}", AstraRc.ASTRARC_DEFAULT);
                astraRc(AstraRc.load(), AstraRc.ASTRARC_DEFAULT);
            }
            
            // Environment Variables
            LOGGER.info("+ Load Environment Variables");
            if (Utils.hasLength(System.getProperty(ASTRA_DB_ID))) {
                this.astraDatabaseId = System.getProperty(ASTRA_DB_ID);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_ID))) {
                this.astraDatabaseId = System.getenv(ASTRA_DB_ID);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_REGION))) {
                this.astraDatabaseCurrentRegion = System.getProperty(ASTRA_DB_REGION);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_REGION))) {
                this.astraDatabaseCurrentRegion = System.getenv(ASTRA_DB_REGION);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_REGIONS))) {
                this.astraDatabaseRegions = Arrays.asList(System.getProperty(ASTRA_DB_REGIONS).split(","));
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_REGIONS))) {
                this.astraDatabaseRegions = Arrays.asList(System.getenv(ASTRA_DB_REGIONS).split(","));
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_APPLICATION_TOKEN))) {
                this.appToken = System.getProperty(ASTRA_DB_APPLICATION_TOKEN);
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_APPLICATION_TOKEN))) {
                this.appToken = System.getenv(ASTRA_DB_APPLICATION_TOKEN);
            }
            if (Utils.hasLength(System.getProperty(ASTRA_DB_SECURE_BUNDLES))) {
                setSCB(System.getProperty(ASTRA_DB_SECURE_BUNDLES));
            } else if (Utils.hasLength(System.getenv(ASTRA_DB_SECURE_BUNDLES))) {
                setSCB(System.getenv(ASTRA_DB_SECURE_BUNDLES));
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
        
        /**
         * Mutualizing code to parse the secure connect bundles.
         *
         * @param bundles
         *      list of bundles
         */
        private void setSCB(String bundleKey) {
            String[] bundles = bundleKey.split(",");
            if (bundles.length != astraDatabaseRegions.size()) {
                throw new IllegalArgumentException("You need to provide a SCB for each region regionCount="
                        + astraDatabaseRegions.size() + "but bundles count=" + bundles.length);
            }
            for(int i=0;i<astraDatabaseRegions.size();i++) {
                this.secureConnectBundles.put(astraDatabaseRegions.get(i), bundles[i]);
            } 
        }
        
        /**
         * Load the default ~/.astrarc file and load section X.
         *
         * @param sectionName
         * @return
         */
        public AstraClientBuilder astraRc(String sectionName) {
            return astraRc(AstraRc.load(), sectionName);
        }
        
        /**
         * Some settings can be loaded from ~/.astrarc in you machine.
         * 
         * @param arc AstraRc
         * @param sectionName String
         * @return AstraClientBuilder
         */
        public AstraClientBuilder astraRc(AstraRc arc, String sectionName) {
            Map<String,String> section = arc.getSections().get(sectionName);
            if (null != section) {
                if (null == astraDatabaseId) {
                    astraDatabaseId = section.get(ASTRA_DB_ID);
                }
                if (null == astraDatabaseCurrentRegion) {
                    astraDatabaseCurrentRegion = section.get(ASTRA_DB_REGION);
                }
                if (null == astraDatabaseRegions) {
                    astraDatabaseRegions = Arrays.asList(section.get(ASTRA_DB_REGIONS).split(","));
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
                if (null == secureConnectBundles) {
                    setSCB(section.get(ASTRA_DB_SECURE_BUNDLES));
                }
                if (null == keyspace) {
                    keyspace = section.get(ASTRA_DB_KEYSPACE);
                }
            }
            return this;
        }
        
        /**
         * databaseId
         * 
         * @param uid String
         * @return AstraClientBuilder
         */
        public AstraClientBuilder databaseId(String uid) {
            Assert.hasLength(uid, "astraDatabaseId");
            this.astraDatabaseId = uid;
            return this;
        }

        /**
         * cloudProviderRegion
         * 
         * @param region String
         * @return AstraClientBuilder
         */
        @Deprecated
        public AstraClientBuilder cloudProviderRegion(String region) {
            Assert.hasLength(region, "astraDatabaseRegion");
            this.astraDatabaseCurrentRegion = region;
            this.astraDatabaseRegions       = Collections.singletonList(region);
            return this;
        }
        
        /**
         * Provide the list of regions.
         *
         * @param regions
         *      list of regions
         * @return
         *      self reference
         */
        public AstraClientBuilder databaseRegions(String... regions) {
            Assert.notNull(regions, "Region list");
            Assert.isTrue(regions.length>0, "Region should not be null");
            this.astraDatabaseCurrentRegion = regions[0];
            this.astraDatabaseRegions = Arrays.asList(regions);
            return this;
        }

        /**
         * appToken
         * 
         * @param token String
         * @return AstraClientBuilder
         */
        public AstraClientBuilder appToken(String token) {
            Assert.hasLength(token, "token");
            this.appToken = token;
            return this;
        }
       
        /**
         * Add the secure bundle for the region.
         *
         * @param region
         *      astra region
         * @param scb
         *      path of secure connect bundle
         * @return
         *      self reference
         */
        public AstraClientBuilder secureConnectBundle(String region, String scb) {
            Assert.hasLength(region, "region");
            Assert.hasLength(scb, "scb");
            this.secureConnectBundles.put(region, scb);
            return this;
        }

        /**
         * keyspace
         * 
         * @param keyspace String
         * @return AstraClientBuilder
         */
        public AstraClientBuilder keyspace(String keyspace) {
            Assert.hasLength(keyspace, "keyspace");
            this.keyspace = keyspace;
            return this;
        }

        /**
         * clientId
         * 
         * @param clientId String
         * @return AstraClientBuilder
         */
        public AstraClientBuilder clientId(String clientId) {
            Assert.hasLength(clientId, "clientId");
            this.clientId = clientId;
            return this;
        }

        /**
         * clientSecret
         * 
         * @param clientSecret String
         * @return AstraClientBuilder
         */
        public AstraClientBuilder clientSecret(String clientSecret) {
            Assert.hasLength(clientSecret, "clientSecret");
            this.clientSecret = clientSecret;
            return this;
        }
        
        /**
         * Create the client
         * 
         * @return AstraClient
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
    
    /**
     * Getter accessor for attribute 'clientId'.
     *
     * @return
     *       current value of 'clientId'
     */
    public Optional<String> getToken() {
        return Optional.ofNullable(token);
    }
    
    /**
     * Getter accessor for attribute 'clientId'.
     *
     * @return
     *       current value of 'clientId'
     */
    public Optional<String> getClientId() {
        return Optional.ofNullable(clientId);
    }

    /**
     * Getter accessor for attribute 'clientSecret'.
     *
     * @return
     *       current value of 'clientSecret'
     */
    public Optional<String> getClientSecret() {
        return Optional.ofNullable(clientSecret);
    }

    /**
     * Getter accessor for attribute 'databaseId'.
     *
     * @return
     *       current value of 'databaseId'
     */
    public Optional<String> getDatabaseId() {
        return Optional.ofNullable(databaseId);
    }
    
    /**
     * Getter accessor for attribute 'databaseRegion'.
     *
     * @return
     *       current value of 'databaseRegion'
     */
    public List<String> getDatabaseRegions() {
        return databaseRegions;
    }
    
    /**
     * Getter accessor for attribute 'currentDatabaseRegion'.
     *
     * @return
     *       current value of 'currentDatabaseRegion'
     */
    public String getCurrentDatabaseRegion() {
        return currentDatabaseRegion;
    }
    
    /**
     * Getter accessor for attribute 'stargateClient'.
     *
     * @return
     *       current value of 'stargateClient'
     */
    public StargateClient getStargateClient() {
        return stargateClient;
    }

}
