package com.dstx.stargate.client;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.oss.driver.api.core.CqlSession;
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
        LOGGER.info("Initializing the document API with base url {}", builder.endPointApiDocument);
        
        apiDoc = new ApiDocumentClient(builder.username, 
                builder.password, 
                builder.endPointAuthentication,
                builder.endPointApiDocument);
        
        apiRest = new ApiRestClient(builder.username, 
                builder.password, 
                builder.endPointAuthentication,
                builder.endPointApiRest);
        
        if (!builder.endPointCql.isEmpty()) {
            cqlSession = CqlSession.builder()
                    .withAuthCredentials(builder.username, builder.password)
                    .withLocalDatacenter(builder.localDataCenter)
                    .addContactPoints(builder.endPointCql.stream()
                                             .map(this::mapContactPoint)
                                             .collect(Collectors.toList())).build();
        }
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
        /** This the endPoint to invoke to work with different API(s). */
        private String endPointApiRest = "http://localhost:8082";
        /** This the endPoint to invoke to work with different API(s). */
        private String endPointApiDocument = "http://localhost:8082";
        /** working with local Cassandra. */
        private List<String> endPointCql = Arrays.asList("localhost:9042");
        /** Local data center. */
        private String localDataCenter = "dc1";
          
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
            Assert.hasLength(authenticationUrl, "authenticationUrl");
            this.endPointAuthentication = authenticationUrl;
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
        public StargateClientBuilder addCqlContactPoint(String ip, int port) {
            Assert.hasLength(ip, "ip");
            this.endPointCql.add(ip + ":" + port);
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
