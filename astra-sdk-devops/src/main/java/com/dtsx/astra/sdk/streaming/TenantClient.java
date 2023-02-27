package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.streaming.domain.*;
import com.dtsx.astra.sdk.streaming.exception.TenantNotFoundException;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class TenantClient {
    
    /** Tenant Identifier. */
    private final String tenantId;
    
    /** Streaming client. */
    private final StreamingClient streamClient;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /** Load Database responses. */
    private static final TypeReference<List<TenantLimit>> TYPE_LIST_LIMIT =  
            new TypeReference<List<TenantLimit>>(){};

    /**
     * Default constructor.
     *
     * @param client
     *          streaming client
     *        
     * @param tenantId
     *          unique tenantId identifier
     */
    public TenantClient(StreamingClient client, String tenantId) {
       this.streamClient    = client;
       this.tenantId        = tenantId;
       Assert.hasLength(tenantId, "tenantId");
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
    public Optional<Tenant> find() {
        return streamClient.tenants()
                           .filter(t -> t.getTenantName().equalsIgnoreCase(tenantId))
                           .findFirst();
    }
    
    /**
     * Check if a role is present
     * 
     * @return
     *      if the tenant exist
     */
    public boolean exist() {
        return http
                .HEAD(getEndpointTenant(), streamClient.bearerAuthToken)
                .getCode() == HttpURLConnection.HTTP_OK;
    }
    
    /**
     * TODO Create a new tenant.
     *
     * @param ct
     *      tenant creation request
     */
    public void create(CreateTenant ct) {
        Assert.notNull(ct, "Create Tenant request");
        ct.setTenantName(tenantId);
        http.POST(StreamingClient.getApiDevopsEndpointTenants(), streamClient.bearerAuthToken, JsonUtils.marshall(ct));
    }

    /**
     * Deleting a tenant and cluster.
     */
    public void delete() {
        http.DELETE(getEndpointCluster(get().getClusterName()), streamClient.bearerAuthToken);
    }

    /**
     * Return tenant or return an error.
     *
     * @return
     *      tenant reference
     */
    public Tenant get() {
        return find().orElseThrow(() -> new TenantNotFoundException(tenantId));
    }

    // ---------------------------------
    // ----       Limits            ----
    // ---------------------------------

    /**
     * FIXME This endpoint does not work on ASTRA
     * @return
     *      the list of limits
     */
    public Stream<TenantLimit> limits() {
        ApiResponseHttp res = http.GET(getEndpointTenant() + "/limits", streamClient.bearerAuthToken);
        return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_LIMIT).stream();
    }

    // ---------------------------------
    // ----       CDC               ----
    // ---------------------------------

    /**
     * Access CDC Client.
     *
     * @return
     *      cdc component
     */
    public CdcClient cdc() {
        return new CdcClient(get(), streamClient.bearerAuthToken);
    }

    // ---------------------------------
    // ----       Stats             ----
    // ---------------------------------

    /**
     * Access Stats Client.
     *
     * @return
     *      cdc component
     */
    public StatsClient stats() {
        return new StatsClient(get());
    }

    // ---------------------------------
    // ----       Utilities         ----
    // ---------------------------------
    
    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointTenant() {
        return StreamingClient.getApiDevopsEndpointTenants() + "/" + tenantId;
    }
    
    /**
     * Endpoint to access cluster.
     *
     * @param clusterId
     *      identifier for the cluster.
     *     
     * @return
     *      database endpoint
     */
    public String getEndpointCluster(String clusterId) {
        return getEndpointTenant() + "/clusters/" + clusterId;
    }

}
