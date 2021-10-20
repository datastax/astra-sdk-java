package com.datastax.stargate.sdk;

import static com.datastax.stargate.sdk.utils.AnsiUtils.cyan;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.config.StargateClientConfig;
import com.datastax.stargate.sdk.core.ApiConstants;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.core.ApiTokenProvider;
import com.datastax.stargate.sdk.loadbalancer.LoadBalancingResource;
import com.datastax.stargate.sdk.loadbalancer.NoneResourceAvailableException;
import com.datastax.stargate.sdk.loadbalancer.UnavailableResourceException;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Wrapper for calls handling: retries, load-balancing and failover. Delegating all HTTP
 * invocation to keep the main class also empty and exposing only public methods.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateHttpClient implements ApiConstants {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StargateClient.class);
    
    /** Reference to core Stargate Client. */
    private StargateClient sc;
    
    /** 
     * List of clients to interact with Nodes with others API (REST, GRAPHQL, ...).
     *
     * If multiple datacenter provided the datacenter name will used as an EXECUTION PROFILE
     * to SWITCH contact points or cloud secure bundle.
     **/
    private Map<String, StargateClientDC> datacenters = new HashMap<>();
    
    /**
     * If all resources in a DC failed we want to failover to another DC. 
     * To avoid switching to another DC also invalid we need to mark them here.
     * If all DC are unavailable and we fail, a {@link NoneResourceAvailableException} is thrown back to user. 
     */
    private Set< String > unavailableDatacenters = new HashSet<>();
    
    /**
     * Initializing from the {@link StargateClient} and the configuration.
     *
     * @param sc
     *      stargate client
     * @param config
     *      configuration
     */
    protected StargateHttpClient(StargateClient sc, StargateClientConfig config) {
        // Initializing nodes per datacenter based on StargateNodeConfig
        for(String dc : config.getStargateNodes().keySet()) {
            ApiTokenProvider apitokenDC = config.getApiTokenProvider(dc);
            datacenters.put(dc, new StargateClientDC(dc, apitokenDC, 
             config.getStargateNodes()
                    .get(dc).stream()
                    .map(node -> new StargateClientNode(node.getName(), node.getRestUrl(), node.getGraphqlUrl()))
                    .collect(Collectors.toList())));
        }
        // Logging topology
        datacenters.entrySet().stream().forEach(e -> {
                LOGGER.info("+ Stargate nodes #[" + cyan("{}") + "] in [" + cyan("{}") + "]", 
                        e.getValue().getStargateNodesLB().getResourceList().size(), e.getKey());
        });
        this.sc = sc;
    }
    
    // ------------------------------------------------
    // ---------------- Invocation  -------------------
    // ------------------------------------------------
     
    /**
     * Execute a GET HTTP Call on a StargateNode
     *
     * @param mapper
     *      build the target URL
     * @return
     *      http response
     */
    public ApiResponseHttp GET(Function<StargateClientNode, String> mapper) {
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
    public ApiResponseHttp GET(Function<StargateClientNode, String> mapper, String suffix) {
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
    public ApiResponseHttp HEAD(Function<StargateClientNode, String> mapper) {
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
    public ApiResponseHttp POST(Function<StargateClientNode, String> mapper) {
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
    public ApiResponseHttp POST(Function<StargateClientNode, String> mapper, String body) {
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
    public ApiResponseHttp POST_GRAPHQL(Function<StargateClientNode, String> mapper, String body) {
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
    public ApiResponseHttp POST(Function<StargateClientNode, String> mapper, String body, String suffix) {
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
    public ApiResponseHttp DELETE(Function<StargateClientNode, String> mapper) {
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
    public ApiResponseHttp DELETE(Function<StargateClientNode, String> mapper, String suffix) {
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
    public ApiResponseHttp PUT(Function<StargateClientNode, String> mapper, String body) {
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
    public ApiResponseHttp PUT(Function<StargateClientNode, String> mapper, String body, String suffix) {
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
    public ApiResponseHttp PATCH(Function<StargateClientNode, String> mapper, String body) {
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
    public ApiResponseHttp PATCH(Function<StargateClientNode, String> mapper, String body, String suffix) {
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
    private ApiResponseHttp http(Function<StargateClientNode, String> mapper, 
            final Method method, String body, String suffix, String contentType, boolean mandatory) {
        LoadBalancingResource<StargateClientNode> lb = null;
        while (true) {
            try {
                // Get an available node from LB
                lb = lookupStargateNode();
                // Build Parameters
                String targetEndPoint = mapper.apply(lb.getResource());
                if (null != suffix) targetEndPoint+= suffix;
                // Invoke request
                return HttpApisClient.getInstance()
                                      .executeHttp(method, targetEndPoint, lookupToken(), body, contentType, mandatory);
            } catch(UnavailableResourceException rex) {
                LOGGER.warn("A stargate node is down [{}], falling back to another node...", lb.getResource().getNodeName());
                failoverStargateNode(lb, rex);
            } catch(NoneResourceAvailableException nex) {
                LOGGER.warn("No node availables is localDc [{}], falling back to another DC if available ...",  sc.currentDatacenter);
                failoverDatacenter();
            }
        }
    }

    // ------------------------------------------------
    // -- Load Balancing & Failovers    ---------------
    // ------------------------------------------------
    
    /**
     * Implementing failover cross DC for API and CqlSession when available.
     * 
     * @param datacenter
     *      target datacenter
     */
    public void useDataCenter(String datacenter) {
        if (!datacenters.containsKey(datacenter)) {
            throw new IllegalArgumentException("'" + datacenter + "' is not a known datacenter please provides one "
                    + "in " + datacenters.keySet());
        }
        sc.currentDatacenter = datacenter;
    }
    
    /**
     * Provide the current Datacenterclient.
     *
     * @return
     *      the client for the current DC
     */
    private StargateClientDC getLocalDatacenterClient() {
        if (!datacenters.containsKey(sc.currentDatacenter)) {
            throw new IllegalStateException("Cannot retrieve datacenter [" + sc.currentDatacenter + "] from definition, check cluster topology");
        }
        return datacenters.get(sc.currentDatacenter);
    }
    
    /**
     * Get the ApiTokenProvider of current DC. 
     * The resource should have been picked first and localDC set in StargateClient.
     * 
     * @return
     *      a token
     */
    private String lookupToken() {
        return getLocalDatacenterClient() // Retrieve the currrent Dc based on localDc property
                .getTokenProvider()       // Retrieve the ApiTokenProvider for the DC (could be custom)
                .getToken();              // Ask the token provider to supply a token
    }
    
    /**
     * Retrieve an Api Rest URL still available in current DC or failover.
     *
     * @return
     *      an APi Rest URL available
     */
    public LoadBalancingResource<StargateClientNode> lookupStargateNode() {
        return getLocalDatacenterClient()   // Retrieve the currrent Dc based on localDc property
                .getStargateNodesLB()       // Retrieve the loadbalancer for node
                .getLoadBalancedResource(); // Get a resource, idea is to invalidate resource if KO
    }
    
    /**
     * Failing over from one DC to another
     * 
     * @param dc
     *      datacenter name
     */
    private void failoverDatacenter() {
        String dcDown = sc.currentDatacenter;
        // Mark as unavailable
        unavailableDatacenters.add(dcDown);
        // List available DC
        Set<String> dcAvailables =  new HashSet<>(datacenters.keySet());
        dcAvailables.removeAll(unavailableDatacenters);
        if (dcAvailables.size() == 0) {
            throw new NoneResourceAvailableException("No Resource available anymore on ");
        }
        // Pick one and fail over
        useDataCenter(dcAvailables.iterator().next());
        LOGGER.info("Fell back from DC {} to {}", dcDown, sc.currentDatacenter);
    }
    
    /**
     * Failing over from one Stargate node to another.
     *
     * @param lb
     *      current resource to be disabled
     * @param t
     *      source error
     */
    private void failoverStargateNode(LoadBalancingResource<StargateClientNode> lb, Throwable t) {
        getLocalDatacenterClient().getStargateNodesLB().handleComponentError(lb, t);
    }

}
