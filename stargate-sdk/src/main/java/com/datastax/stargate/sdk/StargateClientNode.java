package com.datastax.stargate.sdk;

import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.rest.ApiDataClient;

/**
 * Represents an Instance of Stargate.
 * You will have multiple instances per DC and multiple DC in your Cassandra Cluster.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClientNode {
    
    /** Node name. */
    private String nodeName;
    
    /** Hold a reference for the ApiDocument. */
    private ApiDocumentClient apiDoc;
    
    /** Hold a reference for the ApiRest. */
    private ApiDataClient apiRest;
    
    /** Hold a reference for the ApiGraphQL. */
    private ApiGraphQLClient apiGraphQL;
   
    /**
     * Provide parameters to initialize the Stargate interfaces.
     *
     * @param tokenProvider
     *          token provider (can be a static one)
     * @param urlRest
     *          endpoint for REST
     * @param urlGraphQL
     *          endpoint for graphQL
     * @param cql
     *          cqlSession initialized before (DC LEVEL)
     */
    public StargateClientNode(ApiTokenProvider tokenProvider, String name, String urlRest, String urlGraphQL) {
        this.nodeName   = name;
        this.apiDoc     = new ApiDocumentClient(urlRest, tokenProvider);
        this.apiRest    = new ApiDataClient(urlRest, tokenProvider);
        this.apiGraphQL = new ApiGraphQLClient(urlGraphQL, tokenProvider);
    }
    
    /**
     * Getter accessor for attribute 'nodeName'.
     *
     * @return
     *       current value of 'nodeName'
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Leveraging on the REST endpoint to evaluate a node as UP.
     *
     * @return
     *      status of the node
     */
    public boolean isAlive() {
        return apiRest.isAlive();
    }
    
    /**
     * Accessing Document API
     * @return ApiDocumentClient
     */
    public ApiDocumentClient apiDocument() {
        return apiDoc;
    }
    
    /**
     * Accessing Rest API
     * @return ApiRestClient
     */
    public ApiDataClient apiRest() {
        return apiRest;
    }
    
    /**
     * Accessing Rest API
     * @return ApiGraphQLClient
     */
    public ApiGraphQLClient apiGraphQL() {
        return apiGraphQL;
    }
    
    

}
