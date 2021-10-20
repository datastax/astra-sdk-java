package com.datastax.stargate.sdk.gql;

import java.util.function.Function;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponseHttp;

/**
 * Implementations of GraphQL First Approaches, DDL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class GraphQLFirstClient {
    
    /** URL part. */
    public static final String PATH_GRAPHQLFIRST_DDL = "/graph-admin";
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     */
    public GraphQLFirstClient(StargateHttpClient stargateClient) {
        this.stargateHttpClient = stargateClient;
    }
    
    /**
     * Generic Query execution.
     *
     * @param input
     *      query to execute
     * @return
     *      (dynamic) response
     */
    public String query(String input) {
        ApiResponseHttp res = stargateHttpClient.POST(gqlSchemaResource, input);
        return res.getBody();
    }
    
    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<StargateClientNode, String> gqlSchemaResource = 
            (node) -> node.getApiGraphQLEndpoint() + PATH_GRAPHQLFIRST_DDL;
    

}
