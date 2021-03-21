package io.stargate.sdk;

import java.net.InetSocketAddress;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;

import io.stargate.sdk.doc.ApiDocumentClient;
import io.stargate.sdk.rest.ApiRestClient;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.Utils;

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
public class StargateClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClient.class);
    
    /** Enviroment variables to setup connection. */
    public static final String STARGATE_USERNAME       = "STARGATE_USERNAME";
    public static final String STARGATE_PASSWORD       = "STARGATE_PASSWORD";
    public static final String STARGATE_ENDPOINT_AUTH  = "STARGATE_ENDPOINT_AUTH";
    public static final String STARGATE_ENDPOINT_REST  = "STARGATE_ENDPOINT_REST";
    public static final String STARGATE_ENDPOINT_DOC   = "STARGATE_ENDPOINT_DOC";
    public static final String STARGATE_ENDPOINT_CQL   = "STARGATE_ENDPOINT_CQL";
    public static final String STARGATE_CQL_DC         = "STARGATE_CQL_DC";
    public static final String STARGATE_KEYSPACE       = "STARGATE_KEYSPACE";
    public static final String STARGATE_ENABLE_CQL     = "STARGATE_ENABLE_CQL";
    
    // -----------------------------------------------
    // Attributes to be populated by BUILDER
    // Api(s) to initialize based on those values
    // ----------------------------------------------
    
    /** Hold a reference for the ApiDocument. */
    private ApiDocumentClient apiDoc;
    
    /** Hold a reference for the ApiRest. */
    private ApiRestClient apiRest;
    
    /** Hold a reference for the Api Devops. */
    private CqlSession cqlSession;
    
    /**
     *  Accessing Document API
     */
    public ApiDocumentClient getApiDocument() {
        return apiDoc;
    }
    
    /**
     *  Accessing Rest API
     */
    public  ApiRestClient getApiRest() {
        return apiRest;
    }
    
    /**
     * Accessing Cql Session.
     */
    public Optional<CqlSession> getCqlSession() {
        return Optional.ofNullable(cqlSession);
    }
    
    /**
     * You can create on of {@link ApiDocumentClient}, {@link ApiRestClient}, {@link ApiDevopsClient}, {@link ApiCqlClient} with
     * a constructor. The full flegde constructor would took 12 pararms.
     */
    private StargateClient(StargateClientBuilder builder) {
        LOGGER.info("Initializing [StargateClient]");
        
        if (Utils.paramsProvided(builder.username, builder.password, builder.endPointApiDocument)) {
            apiDoc = new ApiDocumentClient(builder.username, 
                    builder.password, 
                    builder.endPointAuthentication, 
                    builder.appToken,
                    builder.endPointApiDocument);
        }
        
        if (Utils.paramsProvided(builder.username, 
                builder.password, 
                builder.endPointApiRest)) {
            apiRest = new ApiRestClient(builder.username, 
                builder.password, 
                builder.endPointAuthentication,
                builder.appToken,
                builder.endPointApiRest);
        }
        
        // For security reason you want to disable CQL
        if (builder.enableCql) {
            if (Utils.paramsProvided(builder.username, builder.password)) {
                CqlSessionBuilder cqlSessionBuilder = CqlSession.builder()
                        .withAuthCredentials(builder.username, builder.password);
                if (Utils.paramsProvided(builder.keyspaceName)) {
                    LOGGER.info("Using Keyspace {}", builder.keyspaceName);
                    cqlSessionBuilder = cqlSessionBuilder.withKeyspace(builder.keyspaceName);
                }
                // Overriding contactPoints/LocalDataCenter when using ASTRA settings
                if (Utils.paramsProvided(builder.astraCloudSecureBundle)) {
                    cqlSessionBuilder.withCloudSecureConnectBundle(Paths.get(builder.astraCloudSecureBundle));
                } else if (Utils.paramsProvided(builder.localDataCenter) &&
                    !builder.endPointCql.isEmpty()) {
                    cqlSessionBuilder = cqlSessionBuilder
                            .withLocalDatacenter(builder.localDataCenter)
                            .addContactPoints(builder.endPointCql.stream()
                                                     .map(this::mapContactPoint)
                                                     .collect(Collectors.toList()));
                }
                cqlSession = cqlSessionBuilder.build();
                
                // Sanity Check query
                cqlSession.execute("SELECT data_center from SYSTEM.LOCAL");
                LOGGER.info("+ Cql API: Enabled");
                
                // As we opened a cqlSession we may want to close it properly at application shutdown.
                Runtime.getRuntime().addShutdownHook(new Thread() { 
                    public void run() { 
                        cqlSession.close();
                        LOGGER.info("Closing CqlSession.");
                      } 
                });
            }
        } else {
            LOGGER.info("+ Cql API: Disabled");
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
     */
    public static final StargateClientBuilder builder() {
        return new StargateClientBuilder();
    }
    
    /**
     * Builder pattern
     */
    public static class StargateClientBuilder {
        /** Username - required all the time */
        private String username = "cassandra";
        /** Password - required all the time */
        private String password = "cassandra";
        /** This the endPoint to invoke to work with different API(s). */
        private String endPointAuthentication = "http://localhost:8081";
        /** if provided the authentication URL is not use to get token. */
        private String appToken = null;
        /** This the endPoint to invoke to work with different API(s). */
        private String endPointApiRest = "http://localhost:8082";
        /** This the endPoint to invoke to work with different API(s). */
        private String endPointApiDocument = "http://localhost:8082";
        /** If this flag is disabled no CQL session will be created. */
        private boolean enableCql = true;
        /** working with local Cassandra. */
        private List<String> endPointCql = Arrays.asList("localhost:9042");
        /** Local data center. */
        private String localDataCenter = "dc1";
        /** Optional Keyspace to enable CqlSession. */
        private String keyspaceName; 
        /** SecureCloudBundle (ASTRA ONLY) overriding. */
        private String astraCloudSecureBundle = null;
          
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
            if (null != System.getenv(STARGATE_ENDPOINT_DOC)) {
                this.endPointApiDocument = System.getenv(STARGATE_ENDPOINT_DOC);
            }
            if (null != System.getenv(STARGATE_ENDPOINT_REST)) {
                this.endPointApiRest = System.getenv(STARGATE_ENDPOINT_REST);
            }
            if (null != System.getenv(STARGATE_ENDPOINT_CQL)) {
                this.endPointCql = Arrays.asList(System.getenv(STARGATE_ENDPOINT_CQL).split(","));
            }
            if (null != System.getenv(STARGATE_CQL_DC)) {
                this.localDataCenter = System.getenv(STARGATE_CQL_DC);
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
        public StargateClientBuilder authenticationUrl(String authenticationUrl) {
            this.endPointAuthentication = authenticationUrl;
            return this;
        }
        public StargateClientBuilder appToken(String token) {
            this.endPointAuthentication = null;
            this.appToken               = token;
            return this;
        }
        public StargateClientBuilder documentApiUrl(String documentApiUrl) {
            Assert.hasLength(documentApiUrl, "documentApiUrl");
            this.endPointApiDocument = documentApiUrl;
            return this;
        }
        public StargateClientBuilder restApiUrl(String restApiUrl) {
            Assert.hasLength(restApiUrl, "restApiUrl");
            this.endPointApiRest = restApiUrl;
            return this;
        }
        public StargateClientBuilder localDc(String localDc) {
            Assert.hasLength(localDc, "localDc");
            this.localDataCenter = localDc;
            return this;
        }
        public StargateClientBuilder keypace(String keyspace) {
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
        public StargateClientBuilder astraCloudSecureBundle(String bundle) {
            Assert.hasLength(bundle, "bundle");
            this.astraCloudSecureBundle = bundle;
            return this;
        }
        
        /**
         * Create the client
         */
        public StargateClient build() {
            return new StargateClient(this);
        }
    }  
     

}
