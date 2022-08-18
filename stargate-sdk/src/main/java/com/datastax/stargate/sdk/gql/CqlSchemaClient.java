package com.datastax.stargate.sdk.gql;

import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import com.datastax.stargate.graphql.client.KeyspaceGraphQLQuery;
import com.datastax.stargate.graphql.client.KeyspaceProjectionRoot;
import com.datastax.stargate.graphql.client.KeyspacesGraphQLQuery;
import com.datastax.stargate.graphql.client.KeyspacesProjectionRoot;
import com.datastax.stargate.graphql.types.Keyspace;
import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.gql.domain.Keyspaces;
import com.fasterxml.jackson.core.type.TypeReference;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;

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
        ApiResponseHttp res = stargateHttpClient.POST_GRAPHQL(cqlSchemaResource, input);
        return res.getBody();
    }
    
    /**
     * List keyspaces.
     *
     * @return
     *      list of keyspaces.
     */
    public Stream<Keyspace> keyspaces() {
        return this.keyspaces(new KeyspacesProjectionRoot().name());
    }
    
    /**
     * List keyspaces.
     *
     * @param projection
     *      projection to get output in Java
     * @return
     *      list of keyspaces.
     */
    public Stream<Keyspace> keyspaces(KeyspacesProjectionRoot projection) {
        // Shape your request
        String graphQLRequest = new GraphQLQueryRequest(
                new KeyspacesGraphQLQuery.Builder().build(),
                projection).serialize();
        // Invoke endpoint
        ApiResponseHttp res = stargateHttpClient.POST_GRAPHQL(cqlSchemaResource, graphQLRequest);
        // Marshall output
        return unmarshallType(res.getBody(), new TypeReference<ApiResponse<Keyspaces>>(){})
                .getData()
                .getKeyspaces()
                .stream();
    }
    
    /**
     * Using the keyspace(...) functions about cqlShema.
     *
     * @param keyspaceName
     *      keyspace name
     * @param projection
     *      projection
     * @return
     *      keyspace if exists
     */
    public Optional<Keyspace> keyspace(String keyspaceName, KeyspaceProjectionRoot projection) {
        String graphQLRequest = new GraphQLQueryRequest(
                new KeyspaceGraphQLQuery.Builder().name(keyspaceName).build(), 
                projection).serialize();
        // Invoke endpoint
        ApiResponseHttp res = stargateHttpClient.POST_GRAPHQL(cqlSchemaResource, graphQLRequest);
        // Marshall output
        return Optional.ofNullable(unmarshallType(res.getBody(), 
                new TypeReference<ApiResponse<Keyspace>>(){}).getData());
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
