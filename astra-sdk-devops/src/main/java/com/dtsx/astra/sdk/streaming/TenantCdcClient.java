package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.db.AstraDBOpsClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.KeyspaceNotFoundException;
import com.dtsx.astra.sdk.streaming.domain.CdcDefinition;
import com.dtsx.astra.sdk.streaming.domain.CreateCdc;
import com.dtsx.astra.sdk.streaming.domain.DeleteCdc;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.HttpClientWrapper;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Operations to work with Cdc in a tenant.
 */
public class TenantCdcClient extends AbstractApiClient {

    /**
     * Unique db identifier.
     */
    private final Tenant tenant;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     * @param tenantId
     *      unique tenant identifier
     */
    public TenantCdcClient(String token, String tenantId) {
        this(token, AstraEnvironment.PROD, tenantId);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     * @param tenantId
     *      unique tenant identifier
     */
    public TenantCdcClient(String token, AstraEnvironment env, String tenantId) {
        super(token, env);
        Assert.hasLength(tenantId, "tenantId");
        // Test Db exists
        this.tenant = new AstraStreamingClient(token, environment).get(tenantId);
    }


    /** {@inheritDoc} */
    @Override
    public String getServiceName() {
        return "streaming.cdc";
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
        Database db = new AstraDBOpsClient(token, environment).database(databaseId).get();
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
        HttpClientWrapper.getInstance(getOperationName("create")).POST_PULSAR(getEndpointTenantCdc(),
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
        Database db = new AstraDBOpsClient(token, environment).database(databaseId).get();
        DeleteCdc deleteCdc = new DeleteCdc();
        deleteCdc.setOrgId(db.getOrgId());
        deleteCdc.setDatabaseId(db.getId());
        deleteCdc.setKeyspace(keyspace);
        deleteCdc.setTableName(table);
        HttpClientWrapper.getInstance(getOperationName("delete")).DELETE_PULSAR(getEndpointTenantCdc(),
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
        ApiResponseHttp res =  HttpClientWrapper.getInstance(getOperationName("list")).GET_PULSAR(getEndpointTenantCdc(),
                tenant.getPulsarToken(),
                tenant.getClusterName(),
                tenant.getOrganizationId().toString());
        return JsonUtils.unmarshallType(res.getBody(),  new TypeReference<List<CdcDefinition>>(){}).stream();
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
        return ApiLocator.getApiStreamingV3Endpoint(environment,
                tenant.getClusterName(),
                tenant.getTenantName()) + "/cdc";
    }

}
