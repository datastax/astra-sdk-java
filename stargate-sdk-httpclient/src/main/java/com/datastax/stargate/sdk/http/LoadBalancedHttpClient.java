package com.datastax.stargate.sdk.http;

import com.datastax.stargate.sdk.ManagedServiceDeployment;
import com.datastax.stargate.sdk.ServiceDeployment;
import com.datastax.stargate.sdk.api.ApiConstants;
import com.datastax.stargate.sdk.http.domain.ApiResponseHttp;

import com.datastax.stargate.sdk.loadbalancer.LoadBalancingResource;
import com.datastax.stargate.sdk.loadbalancer.NoneResourceAvailableException;
import com.datastax.stargate.sdk.loadbalancer.UnavailableResourceException;
import org.apache.hc.core5.http.Method;

import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Rest API is an Http Service of Stargate
 */
public class LoadBalancedHttpClient implements ApiConstants {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(LoadBalancedHttpClient.class);

    /**
     * Hold the configuration of the cluster with list of dc and service instances.
     */
    private final ManagedServiceDeployment<ServiceHttp> deployment;

    /**
     * Complete configuration.
     * @param conf
     *      configuration
     */
    public LoadBalancedHttpClient(ServiceDeployment<ServiceHttp> conf) {
        this.deployment = new ManagedServiceDeployment<>(conf);
    }

    /**
     * Execute a GET HTTP Call on a StargateNode
     *
     * @param mapper
     *      build the target URL
     * @return
     *      http response
     */
    public ApiResponseHttp GET(Function<ServiceHttp, String> mapper) {
        return GET(mapper, null);
    }

    /**
     * Syntax sugar.
     *
     * @param mapper
     *      mapper for the URL
     * @param suffix
     *      suffix for the URL
     * @return
     *      http response
     */
    public ApiResponseHttp GET(Function<ServiceHttp, String> mapper, String suffix) {
        return http(mapper, Method.GET, null, suffix, CONTENT_TYPE_JSON, false);
    }

    /**
     * Syntaxic sugar for a HEAD.
     *
     * @param mapper
     *       mapper for the URL
     * @return
     *      http response
     */
    public ApiResponseHttp HEAD(Function<ServiceHttp, String> mapper) {
        return http(mapper, Method.PATCH, null, null, CONTENT_TYPE_JSON, false);
    }

    /**
     * Syntaxic sugar for a HEAD.
     *
     * @param mapper
     *       mapper for the URL
     * @return
     *      http response
     */
    public ApiResponseHttp POST(Function<ServiceHttp, String> mapper) {
        return POST(mapper,  null);
    }

    /**
     * Syntaxic sugar for a HEAD.
     *
     * @param mapper
     *       mapper for the URL
     * @param body
     *      provide a request body
     * @return
     *      http response
     */
    public ApiResponseHttp POST(Function<ServiceHttp, String> mapper, String body) {
        return http(mapper, Method.POST, body, null, CONTENT_TYPE_JSON, true);
    }

    /**
     * Syntaxic sugar for a HEAD.
     *
     * @param mapper
     *       mapper for the URL
     * @param body
     *      provide a request body
     * @return
     *      http response
     */
    public ApiResponseHttp POST_GRAPHQL(Function<ServiceHttp, String> mapper, String body) {
        return http(mapper, Method.POST, body, null, CONTENT_TYPE_GRAPHQL, true);
    }

    /**
     * Syntaxic sugar.
     *
     * @param mapper
     *       mapper for the URL
     * @param body
     *      provide a request body
     * @param suffix
     *      URL suffix
     * @return
     *      http response
     */
    public ApiResponseHttp POST(Function<ServiceHttp, String> mapper, String body, String suffix) {
        return http(mapper, Method.POST, body, suffix, CONTENT_TYPE_JSON, true);
    }

    /**
     * Syntaxic sugar.
     *
     * @param mapper
     *      mapper for the URL
     * @return
     *       http response
     */
    public ApiResponseHttp DELETE(Function<ServiceHttp, String> mapper) {
        return http(mapper, Method.DELETE, null, null, CONTENT_TYPE_JSON, true);
    }

    /**
     * Syntaxic sugar.
     *
     * @param mapper
     *      mapper for the URL
     * @param suffix
     *      URL suffix
     * @return
     *       http response
     */
    public ApiResponseHttp DELETE(Function<ServiceHttp, String> mapper, String suffix) {
        return http(mapper, Method.DELETE, null, suffix, CONTENT_TYPE_JSON, true);
    }

    /**
     * Syntaxic sugar.
     *
     * @param mapper
     *       mapper for the URL
     * @param body
     *      provide a request body
     * @return
     *      http response
     */
    public ApiResponseHttp PUT(Function<ServiceHttp, String> mapper, String body) {
        return http(mapper, Method.PUT, body, null, CONTENT_TYPE_JSON, false);
    }

    /**
     * Syntaxic sugar.
     *
     * @param mapper
     *       mapper for the URL
     * @param body
     *      provide a request body
     * @param suffix
     *      URL suffix
     * @return
     *      http response
     */
    public ApiResponseHttp PUT(Function<ServiceHttp, String> mapper, String body, String suffix) {
        return http(mapper, Method.PUT, body, suffix, CONTENT_TYPE_JSON, false);
    }

    /**
     * Syntaxic sugar.
     *
     * @param mapper
     *       mapper for the URL
     * @param body
     *      provide a request body
     * @return
     *      http response
     */
    public ApiResponseHttp PATCH(Function<ServiceHttp, String> mapper, String body) {
        return http(mapper, Method.PATCH, body, null, CONTENT_TYPE_JSON, true);
    }

    /**
     * Syntaxic sugar.
     *
     * @param mapper
     *       mapper for the URL
     * @param body
     *      provide a request body
     * @param suffix
     *      URL suffix
     * @return
     *      http response
     */
    public ApiResponseHttp PATCH(Function<ServiceHttp, String> mapper, String body, String suffix) {
        return http(mapper, Method.PATCH, body, suffix, CONTENT_TYPE_JSON, true);
    }

    /**
     * Generic Method to build and execute http request with retries, load balancing and failover.
     *
     * @param mapper
     *      building the request from a node
     * @param method
     *      http method used
     * @param body
     *      request body (optional)
     * @param suffix
     *      URL suffix
     * @param mandatory
     *      handling 404 error code, could raise exception or not
     * @return
     */
    private ApiResponseHttp http(Function<ServiceHttp, String> mapper,
                                 final Method method, String body,
                                 String suffix, String contentType,
                                 boolean mandatory) {
        LoadBalancingResource<ServiceHttp> lb = null;
        while (true) {
            try {
                // Get an available node from LB
                lb = deployment.lookupStargateNode();
                // Build Parameters
                String targetEndPoint = mapper.apply(lb.getResource());
                //System.out.println(targetEndPoint);
                //System.out.println(body);
                //System.out.println(method);
                if (null != suffix) targetEndPoint+= suffix;
                // Invoke request
                return RetryHttpClient.getInstance()
                        .executeHttp(lb.getResource(), method, targetEndPoint, deployment.lookupToken(), body, contentType, mandatory);
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

    /**
     * Gets deployment
     *
     * @return value of deployment
     */
    public ManagedServiceDeployment<ServiceHttp> getDeployment() {
        return deployment;
    }
}

