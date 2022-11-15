package com.datastax.stargate.sdk;

import com.datastax.stargate.sdk.api.ApiTokenProvider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CqlSession and Endpoints are associated to a dedicated DataCenter. The fail-over
 * will be performed by the SDK. As such a StargateClient will have multiple DC.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ServiceDatacenter<SERVICE extends Service> {

    /** datacenter unique id. */
    private final String id;

    /** Multiple Nodes of Stargate share their token in a Cassandra table to can be positioned as DC level. */
    private ApiTokenProvider tokenProvider;

    /** Inside a single datacenter I will have multiple Stargate Nodes. We will load-balance our queries among those instances. */
    private final Map<String, SERVICE> services = new HashMap<>();

    /**
     * Full constructor.
     *
     * @param id
     *      current dc name
     */
    public ServiceDatacenter(String id) {
        this.id  = id;
    }

    /**
     * Full constructor.
     *
     * @param id
     *      current dc id
     * @param tokenProvider
     *      token provider for the DC
     * @param nodes
     *      list of nodes
     */
    public ServiceDatacenter(String id, ApiTokenProvider tokenProvider, List<SERVICE> nodes) {
       this(id);
       this.tokenProvider   = tokenProvider;
       nodes.forEach(this::addService);
    }

    /**
     * Gets service by its id.
     *
     * @return value of services
     */
    public SERVICE getService(String dcId) {
        return getServices().get(dcId);
    }

    /**
     * Gets services
     *
     * @return value of services
     */
    public Map<String, SERVICE> getServices() {
        return services;
    }

    /**
     * Gets tokenProvider
     *
     * @return value of tokenProvider
     */
    public ApiTokenProvider getTokenProvider() {
        return tokenProvider;
    }

    /**
     * Gets id
     *
     * @return value of id
     */
    public String getId() {
        return id;
    }

    /**
     * Set value for tokenProvider
     *
     * @param tokenProvider new value for tokenProvider
     */
    public void setTokenProvider(ApiTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    /**
     * Add a service to the datacenter.
     *
     * @param s
     *      current service
     */
    public void addService(SERVICE s) {
        if (s != null) {
            services.put(s.getId(), s);
        }
    }
}
