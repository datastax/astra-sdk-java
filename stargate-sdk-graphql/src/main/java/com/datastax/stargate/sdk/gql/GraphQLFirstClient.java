package com.datastax.stargate.sdk.gql;

import com.datastax.stargate.sdk.http.ServiceHttp;
import com.datastax.stargate.sdk.http.LoadBalancedHttpClient;
import com.datastax.stargate.sdk.http.domain.ApiResponseHttp;

import java.util.function.Function;

/**
 * Implementations of GraphQL First Approaches, DDL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class GraphQLFirstClient {
    
    /** URL part. */
    public static final String PATH_GRAPHQLFIRST_DDL = "/graph-admin";
    
    /** Get Topology of the nodes. */
    private final LoadBalancedHttpClient stargateHttpClient;
    
    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     */
    public GraphQLFirstClient(LoadBalancedHttpClient stargateClient) {
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
    public Function<ServiceHttp, String> gqlSchemaResource =
            (node) -> node.getEndpoint() + PATH_GRAPHQLFIRST_DDL;
    

}
