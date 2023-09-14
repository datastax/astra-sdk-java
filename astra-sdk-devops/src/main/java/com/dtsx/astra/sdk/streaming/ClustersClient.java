package com.dtsx.astra.sdk.streaming;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.streaming.domain.Cluster;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Client to work with clusters.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ClustersClient extends AbstractApiClient {

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public ClustersClient(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public ClustersClient(String token, AstraEnvironment env) {
        super(token, env);
    }

    /**
     * Operations on clusters.
     *
     * @return
     *      list  clusters.
     */
    public Stream<Cluster> findAll() {
        return JsonUtils
                .unmarshallType(GET(getApiDevopsEndpointClusters()).getBody(), new TypeReference<List<Cluster>>(){})
                .stream();
    }
    
    // ---------------------------------
    // ----       CRUD              ----
    // ---------------------------------
    
    /**
     * Find a tenant from ids name.
     *
     * @param clusterName
     *      name fo the cluster
     * @return 
     *      tenant
     */
    public Optional<Cluster> find(String clusterName) {
        return findAll()
                .filter(c -> c.getClusterName().equalsIgnoreCase(clusterName))
                .findFirst();
    }
    
    /**
     * Check if a role is present
     *
     * @param clusterName
     *      name fo the cluster
     * @return
     *      if the tenant exist
     */
    public boolean exist(String clusterName) {
        return find(clusterName).isPresent();
    }

    /**
     * Endpoint to access schema for namespace.
     *
     * @return
     *      endpoint
     */
    public String getApiDevopsEndpointClusters() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming" + "/clusters";
    }


}
