package io.stargate.sdk.grpc;

import io.stargate.sdk.ServiceDatacenter;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.TokenProvider;
import io.stargate.sdk.core.Page;
import io.stargate.sdk.grpc.domain.ResultSetGrpc;
import io.stargate.sdk.grpc.domain.QueryGrpc;
import io.stargate.sdk.grpc.domain.RowGrpc;
import io.stargate.sdk.http.auth.TokenProviderHttpAuth;
import io.stargate.sdk.utils.Assert;
import io.stargate.sdk.utils.AnsiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Wrapper to interact with GRPC Client.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateGrpcApiClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateGrpcApiClient.class);

    /** default endpoint. */
    private static final String DEFAULT_HOST = "localhost";

    /** default endpoint. */
    private static final int DEFAULT_PORT = 8090;

    /** default endpoint. */
    private static final String PATH_HEALTH_CHECK = "/stargate/health";

    /** default endpoint. */
    private static final int DEFAULT_HEALTH_CHECK_PORT = 8084;

    /** default service id. */
    private static final String DEFAULT_SERVICE_ID = "sgv2-grpc";

    /** default datacenter id. */
    private static final String DEFAULT_DATACENTER = "dc1";

    /** Stargate grpc Client. */
    private StargateGrpcClient stargategrpcClient;

    /**
     * Default Constructor
     */
    public StargateGrpcApiClient() {
        this(DEFAULT_HOST + ":" + DEFAULT_PORT,
        DEFAULT_HOST + ":" + DEFAULT_HEALTH_CHECK_PORT + PATH_HEALTH_CHECK);
    }

    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargategrpcClient
     *      stargate http client
     */
    public StargateGrpcApiClient(StargateGrpcClient stargategrpcClient) {
        Assert.notNull(stargategrpcClient, "stargate client reference. ");
        this.stargategrpcClient = stargategrpcClient;
        LOGGER.info("+ API Grpc     :[" + AnsiUtils.green("{}") + "]", "ENABLED");
    }

    /**
     * Constructor with StargateClient as argument.
     *
     * @param serviceDeployment
     *      stargate deployment
     */
    public StargateGrpcApiClient(ServiceDeployment<ServiceGrpc> serviceDeployment) {
       this(new StargateGrpcClient(serviceDeployment));
    }

    /**
     * Single instance of Stargate, could be used for tests.
     *
     * @param endpoint
     *      service endpoint
     * @param healthCheckUrl
     *      service health checl
     */
    public StargateGrpcApiClient(String endpoint, String healthCheckUrl) {
        Assert.hasLength(endpoint, "stargate grpc endpoint");
        Assert.hasLength(healthCheckUrl, "stargate grpc health check");
        // Single instance running
        ServiceGrpc rest = new ServiceGrpc(DEFAULT_SERVICE_ID, endpoint, healthCheckUrl);
        // Api provider
        TokenProvider tokenProvider = new TokenProviderHttpAuth();
        // DC with default auth and single node
        ServiceDatacenter sDc = new ServiceDatacenter(DEFAULT_DATACENTER, tokenProvider, Arrays.asList(rest));
        // Deployment with a single dc
        ServiceDeployment deploy = new ServiceDeployment<ServiceGrpc>().addDatacenter(sDc);
        this.stargategrpcClient  = new StargateGrpcClient(deploy);
    }

    /**
     * Execute a query. Work with a pageableQuery probably.
     *
     * findAll()
     * executePage()
     *
     * @param query
     *      current query
     * @return
     *      value
     */
    public ResultSetGrpc execute(QueryGrpc query) {
        return null;
    }

    /**
     * Execute a request.
     *
     * @param cql
     *      cql query
     * @return
     *      list of value
     */
    public ResultSetGrpc execute(String cql) {
        return null;
    }

    /**
     * Execute a request externalizing items.
     *
     * @param cql
     *      cql query
     * @param params
     *      cql params
     * @return
     *      params
     */
    public ResultSetGrpc execute(String cql, Object... params) {
        return null;
    }

    /**
     * Execute a request externalizing items.
     *
     * @param cql
     *      cql query
     * @param params
     *      cql params
     * @return
     *      params
     */
    public ResultSetGrpc execute(String cql, Map<String, Object > params) {
        return null;
    }

    /**
     * Return a page.
     *
     * @param query
     *      current query
     * @param clazz
     *      current class
     * @param <T>
     *      parameters
     * @return
     *      first page
     */
    public <T> Page<T> execute(QueryGrpc query, Class<T> clazz) {
        return null;
    }

    /**
     * Execute a CQL Query asynchronously.
     *
     * @param query
     *      current query
     * @return
     *      callback
     */
    public CompletableFuture<ResultSetGrpc> executeAsync(QueryGrpc query) {
        return null;
    }

    /**
     * Execute a query getting back a flux
     * @param query
     *      input query
     * @return
     *      flux of data
     */
    public Flux<RowGrpc> executeReactive(QueryGrpc query) {
        return null;
    }





    /**
     * Execute a gRPC batch.
     *
     * @param cqlQueries
     *      queries
     * @return
     *      responses
     *
    public QueryOuterClass.Response executeBatch(String... cqlQueries) {
       Assert.notNull(cqlQueries, "queries");
       io.stargate.proto.QueryOuterClass.Batch.Builder batchBuilder = QueryOuterClass.Batch.newBuilder();
       for (String cqlQuery : cqlQueries) {
           batchBuilder.addQueries(
                   QueryOuterClass.BatchQuery.newBuilder().setCql(cqlQuery).build());
       }
       //return getGrpcConnection()
       //        .getSyncStub()
       //        .executeBatch(batchBuilder.build());
        return null;
    }*/

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
     *
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
    }*/

}
