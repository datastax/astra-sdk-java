package com.datastax.stargate.sdk.config;

/**
 * Configure a stargate node.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateNodeConfig {
    
    /** Default port. */
    public static final int DEFAULT_PORT_AUTH = 8081;
    
    /** Default port. */
    public static final int DEFAULT_PORT_REST = 8082;
    
    /** Default port. */
    public static final int DEFAULT_PORT_GQL  = 8080;
    
    /** Default port. */
    public static final int DEFAULT_PORT_GRPC = 8090;
    
    /** Name of the node. */
    private String name;
    
    /** Rest API URL. */
    private String restUrl;
    
    /** GraphQL Api URL. */
    private String graphqlUrl;
    
    /** Authentication API URL. */
    private String authUrl;
    
    /** grpc Host. */
    private String grpcHost;
    
    /** grpc Host. */
    private int grpcPort;
    
    /**
     * Default constructor.
     */
    public StargateNodeConfig() {}
    
    /**
     * Syntaxic sugar.
     *
     * @param host
     *      current host
     */
    public StargateNodeConfig(String host) {
        this(host, host, DEFAULT_PORT_AUTH, DEFAULT_PORT_REST, DEFAULT_PORT_GQL, DEFAULT_PORT_GRPC);
    }
    
    /**
     * Constructor for local node.
     *
     * @param host
     *      target host
     * @param portAuth
     *      port for authentication
     * @param portRest
     *      port for rest api 
     * @param portgraphQL
     *      port for graphQL api 
     */
    public StargateNodeConfig(String host, int portAuth, int portRest, int portgraphQL, int portGrpc) {
        this(host, host, portAuth, portRest, portgraphQL, portGrpc);
    }
    
    /**
     * Constructor for local node.
     *
     * @param name
     *      node name
     * @param host
     *      target host
     * @param portAuth
     *      port for authentication
     * @param portRest
     *      port for rest api 
     * @param portgraphQL
     *      port for graphQL api 
     */
    public StargateNodeConfig(String name,String host, int portAuth, int portRest, int portgraphQL, int portGrpc) {
        this.name       = name;
        this.authUrl    = "http://" + host + ":" + portAuth;
        this.restUrl    = "http://" + host + ":" + portRest;
        this.graphqlUrl = "http://" + host + ":" + portgraphQL;
        this.grpcHost   = host;
        this.grpcPort   = portGrpc;
    }
            
    /**
     * Constructor without URL.
     *
     * @param name
     *      node name
     * @param rest
     *      rest endpoint
     * @param graphQL
     *      graphql endpoint
     */
    public StargateNodeConfig(String name, String rest, String graphQL) {
        this(name, rest, graphQL, null);
    }
    
    /**
     * Constructor without URL.
     * 
     * @param name
     *      node name
     * @param urlRest
     *      api rest url
     * @param urlGraphQL
     *      api graphql urk
     * @param grpcHost
     * @param grpcPort
     */
    public StargateNodeConfig(String name, String urlRest, String urlGraphQL, String grpcHost, int grpcPort) {
        this.name       = name;
        this.restUrl    = urlRest;
        this.graphqlUrl = urlGraphQL;
        this.grpcHost   = grpcHost;
        this.grpcPort   = grpcPort;
    }
    
    /**
     * Full constructor.
     *
     * @param name
     *      node name
     * @param rest
     *      rest endpoint
     * @param graphQL
     *      graphql endpoint
     * @param auth
     *      authentication URL
     */
    public StargateNodeConfig(String name, String rest, String graphQL, String auth) {
        this.name       = name;
        this.restUrl    = rest;
        this.graphqlUrl = graphQL;
        this.authUrl    = auth;
    }
    
    /**
     * Getter accessor for attribute 'authUrl'.
     *
     * @return
     *       current value of 'authUrl'
     */
    public String getAuthUrl() {
        return authUrl;
    }

    /**
     * Getter accessor for attribute 'name'.
     *
     * @return
     *       current value of 'name'
     */
    public String getName() {
        return name;
    }
    
    /**
     * Getter accessor for attribute 'restUrl'.
     *
     * @return
     *       current value of 'restUrl'
     */
    public String getRestUrl() {
        return restUrl;
    }
    
    /**
     * Getter accessor for attribute 'graphqlUrl'.
     *
     * @return
     *       current value of 'graphqlUrl'
     */
    public String getGraphqlUrl() {
        return graphqlUrl;
    }

    /**
     * Getter accessor for attribute 'grpcHost'.
     *
     * @return
     *       current value of 'grpcHost'
     */
    public String getGrpcHost() {
        return grpcHost;
    }

    /**
     * Getter accessor for attribute 'grpcPort'.
     *
     * @return
     *       current value of 'grpcPort'
     */
    public int getGrpcPort() {
        return grpcPort;
    }
}
