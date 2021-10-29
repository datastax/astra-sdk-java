package com.datastax.stargate.sdk.gql;

import java.util.function.Function;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;

/**
 * Work with Operations on keyspaces.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class CqlKeyspaceClient {
    
    /** URL part. */
    public static final String PATH_CQL_DML = "/graphql/";
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /** Name of the keyspace. */
    private String keyspace;
    
    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     * @param keyspace
     *      target keyspace
     */
    public CqlKeyspaceClient(StargateHttpClient stargateClient, String keyspace) {
        this.stargateHttpClient = stargateClient;
        this.keyspace = keyspace;
        Assert.hasLength(keyspace, keyspace);
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
        ApiResponseHttp res = stargateHttpClient.POST(cqlKeyspaceResource, input);
        return res.getBody();
    }
    
    /**
     * List tables.
     *
     * @return
     *      return list of table
     */
    public String listTables() {
        return query(GraphQLQueryBuilder.queryListTables(this.keyspace));
    }
    
    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<StargateClientNode, String> cqlKeyspaceResource = 
            (node) -> node.getApiGraphQLEndpoint() + PATH_CQL_DML + keyspace;

}
