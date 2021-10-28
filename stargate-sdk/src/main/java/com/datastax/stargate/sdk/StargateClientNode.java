package com.datastax.stargate.sdk;

import java.io.Serializable;
import java.net.HttpURLConnection;

import org.apache.hc.client5.http.classic.methods.HttpGet;

import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Definition of a Stargate Node (multiple per DC, multiple DC)
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateClientNode implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = 1L;
    
    /** Node will be monitored with this endpoint. */
    public static final String PATH_HEALTH = "/health";
    
    /** Node name. */
    private final String nodeName;
    
    /** Hold a reference for the ApiRest. */
    private final String apiRestEndpoint;
    
    /** Hold a reference for the ApiGraphQL. */
    private final String apiGraphQLEndpoint;
    
    /** Hold a reference for the ApiGrpc Endpoint. */
    private final String grpcHostName;
    
    /** Hold a reference for the ApiGrpc Endpoint. */
    private final int grpcPort;
    
    /**
     * Full fledge constructor for a node. The host is not enough as a stargate node
     * could have a load balancer on top of it changing the default host:8082...
     * 
     * @param nodeName
     *      node identifier
     * @param apiRestUrl
     *      rest Api URL http://localhost:8082
     * @param apiGraphQLUrl
     *      graphQL Api URL, eg http://localhost:8080
     * @param grpcHostName
     *      grpc hostname
     * @param grpcPort
     *      grpc port
     */
    public StargateClientNode(String nodeName, String apiRestUrl, String apiGraphQLUrl, String grpcHostName, int grpcPort) {
        super();
        this.nodeName           = nodeName;
        this.apiRestEndpoint    = apiRestUrl;
        this.apiGraphQLEndpoint = apiGraphQLUrl;
        this.grpcHostName       = grpcHostName;
        this.grpcPort           = grpcPort;
    }

    /**
     * Invoke heath endpoint.
     *
     * @return
     *      is the service is up.
     */
    public boolean isAlive() {
        // The heartbit resource does not required any token, we can let it blank
        return HttpURLConnection.HTTP_OK ==  HttpApisClient
                .getInstance()
                .executeHttp(new HttpGet(getApiRestEndpoint() + PATH_HEALTH), false)
                .getCode();
    }
    
    /**
     * Getter accessor for attribute 'nodeName'.
     *
     * @return
     *       current value of 'nodeName'
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * Getter accessor for attribute 'apiRestEndpoint'.
     *
     * @return
     *       current value of 'apiRestEndpoint'
     */
    public String getApiRestEndpoint() {
        return apiRestEndpoint;
    }

    /**
     * Getter accessor for attribute 'apiGraphQLEndpoint'.
     *
     * @return
     *       current value of 'apiGraphQLEndpoint'
     */
    public String getApiGraphQLEndpoint() {
        return apiGraphQLEndpoint;
    }

    /**
     * Getter accessor for attribute 'grpcHostName'.
     *
     * @return
     *       current value of 'grpcHostName'
     */
    public String getGrpcHostName() {
        return grpcHostName;
    }

    /**
     * Getter accessor for attribute 'grpcPort'.
     *
     * @return
     *       current value of 'grpcPort'
     */
    public int getGrpcPort() {
        return grpcPort;
    }

}
