package com.datastax.stargate.sdk.gql;

import java.util.function.Function;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponseHttp;

/**
 * Implementations of CQL First Approaches, DDL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CqlSchemaClient {
    
    /** URL part. */
    public static final String PATH_CQLFIRST_DDL = "/graphql-schema";
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     */
    public CqlSchemaClient(StargateHttpClient stargateClient) {
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
        System.out.println(input);
        ApiResponseHttp res = stargateHttpClient.POST_GRAPHQL(cqlSchemaResource, input);
        return res.getBody();
    }
    
    /**
     * List keyspaces.
     *
     * @return
     *      list of keyspaces.
     */
    public String keyspaces() {
        return query(GraphQLQueryBuilder.queryListKeyspaces());
    }
    
    /**
     * Mutation to create a keyspace.
     * 
     * @param keyspaceName
     *      targate keyspace name
     * @param replicas
     *      replicas count
     * @return
     *      response from graphQL
     */
    public String mutationCreateKeyspace(String keyspaceName, int replicas) {
        StringBuilder sb = new StringBuilder();
        sb.append("mutation sdkCreateKeyspace" + keyspaceName + "{\n");
        sb.append("  createKeyspace(name:\"" + keyspaceName + "\"");
        sb.append(", replicas: " + replicas + ")\n");
        sb.append("}");
        return query(sb.toString());
    }
    
    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<StargateClientNode, String> cqlSchemaResource = 
            (node) -> node.getApiGraphQLEndpoint() + PATH_CQLFIRST_DDL;
}
