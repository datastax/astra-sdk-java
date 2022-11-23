package io.stargate.sdk.grpc;

import io.stargate.sdk.ManagedServiceDeployment;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.ApiConstants;
import io.stargate.sdk.loadbalancer.LoadBalancingResource;
import io.stargate.sdk.loadbalancer.NoneResourceAvailableException;
import io.stargate.sdk.loadbalancer.UnavailableResourceException;
import com.evanlennick.retry4j.config.RetryConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Client to achieve load balancing and fail over across grpc endpoints.
 */
public class StargateGrpcClient  implements ApiConstants {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateGrpcClient.class);

    /** Default settings in Request and Retry */
    public static final int DEFAULT_TIMEOUT_REQUEST   = 20;

    /** Default settings in Request and Retry */
    public static final int DEFAULT_TIMEOUT_CONNECT   = 20;

    /** Default settings in Request and Retry */
    public static final int DEFAULT_RETRY_COUNT       = 3;

    /** Default settings in Request and Retry */
    public static final Duration DEFAULT_RETRY_DELAY  = Duration.ofMillis(100);

    /** Default retry configuration. */
    protected static RetryConfig retryConfig = new RetryConfigBuilder()
            //.retryOnSpecificExceptions(ConnectException.class, IOException.class)
            .retryOnAnyException()
            .withDelayBetweenTries(DEFAULT_RETRY_DELAY)
            .withExponentialBackoff()
            .withMaxNumberOfTries(DEFAULT_RETRY_COUNT)
            .build();

    /**
     * Hold the configuration of the cluster with list of dc and service instances.
     */
    private final ManagedServiceDeployment<ServiceGrpc> deployment;

    /**
     * Complete configuration.
     * @param conf
     *      configuration
     */
    public StargateGrpcClient(ServiceDeployment<ServiceGrpc> conf) {
        this.deployment = new ManagedServiceDeployment<>(conf);
    }

    /**
     * Execute query
     */
    public void executeQuery() {
        LoadBalancingResource<ServiceGrpc> lb = null;
        while (true) {
            try {
                // Get an available node from LB
                lb = deployment.lookupStargateNode();
                lb.getResource();
            } catch(UnavailableResourceException rex) {
                LOGGER.warn("A stargate node is down [{}], falling back to another node...", lb.getResource().getId());
                try {
                    deployment.failOverStargateNode(lb, rex);
                } catch (NoneResourceAvailableException nex) {
                    LOGGER.warn("No node availables is localDc [{}], falling back to another DC if available ...",
                            deployment.getLocalDatacenterClient().getDatacenterName());
                    deployment.failOverDatacenter();
                }
            } catch(NoneResourceAvailableException nex) {
                LOGGER.warn("No node availables is DataCenter [{}], falling back to another DC if available ...",
                        deployment.getLocalDatacenterClient().getDatacenterName());
                deployment.failOverDatacenter();

            }
        }
    }

    /*
    public void executeWithRetries() {
        Callable<CloseableHttpResponse> executeRequest = () -> {
            return httpClient.execute(req);
        };
        return new CallExecutorBuilder<String>()
                .config(retryConfig)
                .onSuccessListener(s -> {
                    CompletableFuture.runAsync(()-> notifyAsync(listener->listener.onSuccess(s)));
                })
                .onCompletionListener(s -> {
                    CompletableFuture.runAsync(()-> notifyAsync(listener->listener.onCompletion(s)));
                })
                .onFailureListener(s -> {
                    LOGGER.error("Calls failed after {} retries", s.getTotalTries());
                    CompletableFuture.runAsync(()-> notifyAsync(listener->listener.onFailure(s)));
                })
                .afterFailedTryListener(s -> {
                    LOGGER.error("Failure on attempt {}/{} ", s.getTotalTries(), retryConfig.getMaxNumberOfTries());
                    try {
                        LOGGER.error("Failed request {} on {}", req.getMethod() , req.getUri() );
                        LOGGER.error("+ Exception was ", s.getLastExceptionThatCausedRetry());
                    } catch (URISyntaxException e) {
                        LOGGER.error("Cannot display URI ", e);
                    }
                    CompletableFuture.runAsync(()-> notifyAsync(listener->listener.onFailedTry(s)));
                })
                .build()
                .execute(executeRequest);
    }*/
}
