package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.DatabasesClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.KeyspaceNotFoundException;
import com.dtsx.astra.sdk.streaming.domain.CdcDefinition;
import com.dtsx.astra.sdk.streaming.domain.CreateCdc;
import com.dtsx.astra.sdk.streaming.domain.DeleteCdc;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Operations to work with Cdc in a tenant.
 */
public class CdcClient {

    /** Load Database responses. */
    private static final TypeReference<List<CdcDefinition>> TYPE_LIST_CDC =
            new TypeReference<List<CdcDefinition>>(){};

    /** Access tenant. */
    private final Tenant tenant;

    /** Astra Bearer token. */
    private final String token;

    /** Wrapper handling header and error management as a singleton. */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /**
     * Default constructor.
     *
     * @param tenant
     *          tenant client
     * @param bearerToken
     *          astra bearer token
     */
    public CdcClient(Tenant tenant, String bearerToken) {
        this.tenant = tenant;
        this.token  = bearerToken;
    }

    /**
     * Create a Cdc.
     *
     * @param databaseId
     *      database identifier
     * @param keyspace
     *      keyspace identifier
     * @param table
     *      table identifier
     * @param topicPartition
     *      partition in token
     */
    public void create(String databaseId, String keyspace, String table, int topicPartition) {
        Assert.hasLength(keyspace, "keyspace");
        Assert.hasLength(table, "table");
        Assert.isTrue(topicPartition > 0, "topic partition should be positive");
        Database db = new DatabasesClient(token).id(databaseId).get();
        if (!db.getInfo().getKeyspaces().contains(keyspace)) {
            throw new KeyspaceNotFoundException(databaseId, keyspace);
        }
        CreateCdc createCdc = new CreateCdc();
        createCdc.setOrgId(db.getOrgId());
        createCdc.setDatabaseId(db.getId());
        createCdc.setDatabaseName(db.getInfo().getName());
        createCdc.setKeyspace(keyspace);
        createCdc.setTableName(table);
        createCdc.setTopicPartitions(topicPartition);
        http.POST_PULSAR(getEndpointTenantCdc(),
                tenant.getPulsarToken(),
                JsonUtils.marshall(createCdc),
                tenant.getClusterName(),
                tenant.getOrganizationId().toString());
    }

    /**
     * Delete a cdc.
     *
     * @param databaseId
     *      database identifier
     * @param keyspace
     *      keyspace name
     * @param table
     *      table name
     */
    public void delete(String databaseId, String keyspace, String table) {
        Assert.hasLength(keyspace, "keyspace");
        Assert.hasLength(table, "table");
        Database db = new DatabasesClient(token).id(databaseId).get();
        DeleteCdc deleteCdc = new DeleteCdc();
        deleteCdc.setOrgId(db.getOrgId());
        deleteCdc.setDatabaseId(db.getId());
        deleteCdc.setKeyspace(keyspace);
        deleteCdc.setTableName(table);
        http.DELETE_PULSAR(getEndpointTenantCdc(),
                tenant.getPulsarToken(),
                JsonUtils.marshall(deleteCdc),
                tenant.getClusterName(),
                tenant.getOrganizationId().toString());
    }

    /**
     * Access CDC of a tenant.
     *
     * @return
     *      list of cdc.
     */
    public Stream<CdcDefinition> list() {
        ApiResponseHttp res = http.GET_PULSAR(getEndpointTenantCdc(),
                tenant.getPulsarToken(),
                tenant.getClusterName(),
                tenant.getOrganizationId().toString());
        return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_CDC).stream();
    }

    /**
     * Find a cdc by its id.
     *
     * @param cdcId
     *      identifier
     * @return
     *      cdc definition if exist
     */
    public Optional<CdcDefinition> findCdcById(String cdcId) {
        Assert.hasLength(cdcId, "cdc identifier");
        return list().filter(cdc -> cdc.getConnectorName().equals(cdcId)).findFirst();
    }

    /**
     * Find the cdc based on its components.
     *
     * @param keyspace
     *      keyspace name
     * @param table
     *      table name
     * @param databaseIdentifier
     *      database identifier
     * @return
     *      definition if present
     */
    public Optional<CdcDefinition> findCdcByDefinition(String keyspace, String table, String databaseIdentifier) {
        Assert.hasLength(keyspace, "keyspace");
        Assert.hasLength(table, "table");
        return list().filter(cdc ->
                cdc.getKeyspace().equals(keyspace)   &&
                cdc.getDatabaseTable().equals(table) &&
                cdc.getDatabaseId().equals(databaseIdentifier))
                .findFirst();
    }

    /**
     * Access Cdc endpoint.
     *
     * @return
     *      cdc endpoint
     */
    public String getEndpointTenantCdc() {
        return "https://" + tenant.getClusterName()
                + ".api.streaming.datastax.com/admin/v3/astra"
                + "/tenants/" + tenant.getTenantName() + "/cdc";
    }



}
