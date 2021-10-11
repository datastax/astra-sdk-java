package com.datastax.stargate.sdk.config;

/**
 * Configure a stargate node.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateNodeConfig {
    
    /** Name of the node. */
    private String name;
    
    /** Rest API URL. */
    private String restUrl;
    
    /** GraphQL Api URL. */
    private String graphqlUrl;
    
    /** Authentication API URL. */
    private String authUrl;
    
    public StargateNodeConfig() {}
    
    /**
     * Syntaxic sugar.
     *
     * @param host
     *      current host
     */
    public StargateNodeConfig(String host) {
        this.name       = host;
        this.authUrl    = "http://" + host + ":8081";
        this.restUrl    = "http://" + host + ":8082";
        this.graphqlUrl = "http://" + host + ":8080";
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
}
