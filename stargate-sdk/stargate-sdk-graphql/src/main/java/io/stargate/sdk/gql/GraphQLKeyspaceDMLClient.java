package io.stargate.sdk.gql;

import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.http.LoadBalancedHttpClient;
import io.stargate.sdk.http.domain.ApiResponseHttp;
import io.stargate.sdk.utils.Assert;

import java.util.function.Function;

/**
 * Work with Operations on keyspaces.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class GraphQLKeyspaceDMLClient {
    
    /** URL part. */
    public static final String PATH_CQL_DML = "/graphql/";
    
    /** Get Topology of the nodes. */
    private final LoadBalancedHttpClient stargateHttpClient;
    
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
    public GraphQLKeyspaceDMLClient(LoadBalancedHttpClient stargateClient, String keyspace) {
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
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<ServiceHttp, String> cqlKeyspaceResource =
            (node) -> node.getEndpoint() + PATH_CQL_DML + keyspace;

}
