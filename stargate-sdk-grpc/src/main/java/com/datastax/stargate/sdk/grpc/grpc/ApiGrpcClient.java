package com.datastax.stargate.sdk.grpc.grpc;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.grpc.domain.ConnectionGrpc;
import com.datastax.stargate.sdk.grpc.domain.ResultSetGrpc;
import com.datastax.stargate.sdk.utils.Assert;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.Int32Value;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.QueryOuterClass.Consistency;
import io.stargate.proto.QueryOuterClass.ConsistencyValue;
import io.stargate.proto.QueryOuterClass.Query;
import io.stargate.proto.QueryOuterClass.QueryParameters;
import io.stargate.proto.QueryOuterClass.QueryParameters.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.datastax.stargate.sdk.utils.AnsiUtils.green;

/**
 * Wrapper to interact with GRPC Client.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGrpcClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGrpcClient.class);
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /** Current token. */
    private String savedToken;
    
    /** Initialized a context per node (channel and stubs)*/
    private Map <String , ConnectionGrpc> grpcConnections = new HashMap<>();
    
    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     */
    public ApiGrpcClient(StargateHttpClient stargateClient) {
        Assert.notNull(stargateClient, "stargate client reference. ");
        this.stargateHttpClient =  stargateClient;
        LOGGER.info("+ API Grpc     :[" + green("{}") + "]", "ENABLED");
    }
    
    /**
     * Execute a gRPC query.
     *
     * @param query
     *      gRPC query
     * @return
     *      Resultset
     */
    public ResultSetGrpc execute(Query query) {
        QueryOuterClass.Response res = getGrpcConnection().getSyncStub().executeQuery(query);
        return (res.hasResultSet()) ? new ResultSetGrpc(res.getResultSet()) : null;
    }
    
    /**
     * Execute a gRPC batch.
     *
     * @param cqlQueries
     *      queries
     * @return
     *      responses
     */
    public QueryOuterClass.Response executeBatch(String... cqlQueries) {
       Assert.notNull(cqlQueries, "queries");
       io.stargate.proto.QueryOuterClass.Batch.Builder batchBuilder = QueryOuterClass.Batch.newBuilder();
       for (String cqlQuery : cqlQueries) {
           batchBuilder.addQueries(
                   QueryOuterClass.BatchQuery.newBuilder().setCql(cqlQuery).build());
       }
       return getGrpcConnection()
               .getSyncStub()
               .executeBatch(batchBuilder.build());
    }
    
    /**
     * Execute a Prepared query.
     *
     * @param cql
     *      a CQL query
     * @return
     *      list of items
     */
    public ResultSetGrpc execute(String cql) {
        return this.execute(cql, null, 0, null);
    }
    
    /**
     * Execute a Prepared query.
     *
     * @param cql
     *      cql query
     * @param cl
     *      consistency level
     * @param pageSize
     *      page size
     * @param pageState
     *      page state
     * @return
     *      a grpc resultset
     */
    public ResultSetGrpc execute(String cql, Consistency cl, int pageSize, String pageState) {
        Builder queryParamsBuilder = QueryParameters.newBuilder();
        if (null != cl) {
            queryParamsBuilder.setConsistency(ConsistencyValue.newBuilder().setValue(cl).build());
        }
        if (pageSize > 0) {
            queryParamsBuilder.setPageSize(Int32Value.newBuilder().setValue(pageSize).build());
        }
        if (null != pageState) {
            queryParamsBuilder.setPagingState(BytesValue.newBuilder().setValue(ByteString.copyFromUtf8(pageState)).build());
        }
        return execute(QueryOuterClass.Query.newBuilder()
                    .setCql(cql)
                    .setParameters(queryParamsBuilder.build())
                    .build());
    }
    
    /**
     * If the token reached its limit we must rebuild the stubs.
     */
    private void renewTokenIfNeeded() {
        String currentToken = stargateHttpClient.lookupToken();
        if (!currentToken.equals(savedToken)) {
            grpcConnections.clear();
            savedToken = currentToken;
        }
    }
   
    /**
     * Creat the proper stub for a Stargate Node.
     *
     * @return
     *      grpc connection for the node
     */
    private ConnectionGrpc getGrpcConnection() {
        // Clear Stubs of old tokens
        renewTokenIfNeeded();
        // Lookup for an available node
        StargateClientNode node = stargateHttpClient.lookupStargateNode().getResource();
        // If no blocking stub for this node initialized
        if (!grpcConnections.containsKey(node.getNodeName())) {
            grpcConnections.put(node.getNodeName(), new ConnectionGrpc(node, savedToken));
        }
        return grpcConnections.get(node.getNodeName());
    }

}
