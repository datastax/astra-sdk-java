package com.datastax.astra.sdk.streaming;

import java.util.Optional;

import com.datastax.astra.sdk.streaming.domain.Cluster;
import com.datastax.stargate.sdk.utils.Assert;

public class ClusterClient {
    
    /** Tenant Identifier. */
    private final String clusterName;
    
    /** Streaming client. */
    private final StreamingClient streamClient;
    
    /**
     * Default constructor.
     *
     * @param client
     *         Streaming client
     * @param clusterName
     *          uniique cluster identifier
     */
    public ClusterClient(StreamingClient client, String clusterName) {
       this.streamClient = client;
       this.clusterName  = clusterName;
       Assert.hasLength(clusterName, "clusterName");
    }
    
    // ---------------------------------
    // ----       CRUD              ----
    // ---------------------------------
    
    /**
     * Find a tenant from ids name.
     * 
     * @return 
     *      tenant
     */
    public Optional<Cluster> find() {
        return streamClient.clusters()
                           .filter(c -> c.getClusterName().equalsIgnoreCase(clusterName))
                           .findFirst();
    }
    
    /**
     * Check if a role is present
     * 
     * @return
     *      if the tenant exist
     */
    public boolean exist() {
        return find().isPresent();
    }

}
