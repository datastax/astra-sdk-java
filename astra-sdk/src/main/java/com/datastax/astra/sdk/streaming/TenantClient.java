package com.datastax.astra.sdk.streaming;

import static com.datastax.stargate.sdk.utils.JsonUtils.marshall;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.api.AuthenticationFactory;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;
import com.datastax.astra.sdk.streaming.domain.TenantLimit;
import com.datastax.stargate.sdk.core.ApiResponseHttp;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.HttpApisClient;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * @author Cedrick LUNVEN (@clunven)
 */
public class TenantClient {
    
    /** Tenant Identifier. */
    private final String tenantId;
    
    /** Wrapper handling header and error management as a singleton. */
    private final HttpApisClient http;
    
    /** Streaming client. */
    private final StreamingClient streamClient;
    
    /** Load Database responses. */
    private static final TypeReference<List<TenantLimit>> TYPE_LIST_LIMIT =  
            new TypeReference<List<TenantLimit>>(){};
   
    /**
     * Default constructor.
     *
     * @param bearerAuthToken
     *          authentication token
     * @param databaseId
     *          uniique database identifier
     */
    public TenantClient(StreamingClient client, HttpApisClient http, String tenantId) {
       this.streamClient = client;
       this.http         = http;
       this.tenantId  = tenantId;
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
        return http.HEAD(getEndpointTenant()).getCode() == HttpURLConnection.HTTP_OK;
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
        http.POST(StreamingClient.getApiDevopsEndpointTenants(), marshall(ct));
    }
    
    /**
     * Deleting a tenant and cluster.
     *
     * @param clusterId
     *      cluster identifier
     */
    public void delete() {
        Optional< Tenant > opt = find();
        if (!opt.isPresent()) {
            throw new RuntimeException("Tenant '"+ tenantId + "' has not been found");
        }
        http.DELETE(getEndpointCluster(opt.get().getClusterName()));
    }
    
    /**
     * FIXME This endpoint does not work on ASTRA
     * @return
     */
    public Stream<TenantLimit> limits() {
        ApiResponseHttp res = http.GET(getEndpointTenant() + "/limits");
        return unmarshallType(res.getBody(), TYPE_LIST_LIMIT).stream();
    }
    
    // ---------------------------------
    // ----      PulsarClient       ----
    // ---------------------------------
    
    /**
     * Create a client.
     * 
     * @return
     *      pulsar client.
     */
    public PulsarClient pulsarClient() {
        Optional<Tenant> tenant = find();
        if (!tenant.isPresent()) {
            throw new IllegalArgumentException("Tenant " + tenantId + " cannot be found");
        }
        PulsarClient client;
        try {
            client = PulsarClient.builder()
                    .serviceUrl(tenant.get().getBrokerServiceUrl())
                    .authentication(AuthenticationFactory.token(tenant.get().getPulsarToken()))
                    .build();
        } catch (PulsarClientException e) {
            throw new IllegalArgumentException("Cannot connect to pulsar", e); 
        }
        return client;
    }
    
    // ---------------------------------
    // ----      Pulsar Admin       ----
    // ---------------------------------
    
    /**
     * Create pulsar admin.
     *
     * @return
     *      pulsar admin
     */
    public PulsarAdmin pulsarAdmin() {
        Optional<Tenant> tenant = find();
        if (!tenant.isPresent()) {
            throw new IllegalArgumentException("Tenant " + tenantId + " cannot be found");
        }
        PulsarAdmin admin;
        try {
            admin = PulsarAdmin.builder()
               .allowTlsInsecureConnection(false)
               .enableTlsHostnameVerification(true)
               .useKeyStoreTls(false)
               .tlsTrustStoreType("JKS")
               .tlsTrustStorePath("")
               .tlsTrustStorePassword("")
               .serviceHttpUrl(tenant.get().getWebServiceUrl())
               .authentication(AuthenticationFactory.token(tenant.get().getPulsarToken()))
               .build();
        } catch (PulsarClientException e) {
            throw new IllegalArgumentException("Cannot use Pulsar admin", e);
        }
        return admin;
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
        return getEndpointTenant(tenantId);
    }
    
    /**
     * Endpoint to access dbs.
     *
     * @return
     *      database endpoint
     */
    public String getEndpointCluster(String clusterId) {
        return getEndpointTenant() + "/clusters/" + clusterId;
    }
    
    /**
     * Endpoint to access dbs (static)
     *
     * @param dbId
     *      database identifer
     * @return
     *      database endpoint
     */
    public static String getEndpointTenant(String tenant) {
        return StreamingClient.getApiDevopsEndpointTenants() + "/" + tenant;
    }
    
    
    

}
