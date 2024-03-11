package com.datastax.astra.devops.db;

import com.datastax.astra.devops.db.exception.ChangeDataCaptureNotFoundException;
import com.datastax.astra.devops.db.exception.KeyspaceNotFoundException;
import com.datastax.astra.devops.streaming.AstraStreamingClient;
import com.datastax.astra.devops.streaming.domain.CdcDefinition;
import com.datastax.astra.devops.utils.ApiResponseHttp;
import com.datastax.astra.devops.utils.Assert;
import com.datastax.astra.devops.utils.JsonUtils;
import com.datastax.astra.devops.AbstractApiClient;
import com.datastax.astra.devops.db.domain.Database;
import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.AstraEnvironment;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Group Operation regarding Cdc for a DB
 */
public class DbCdcsClient extends AbstractApiClient {

    /**
     * Load Cdc responses.
     */
    private static final TypeReference<List<CdcDefinition>> TYPE_LIST_CDC =
            new TypeReference<List<CdcDefinition>>() {
    };

    /**
     * unique db identifier.
     */
    private final Database db;

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DbCdcsClient(String token, String databaseId) {
        this(token, AstraEnvironment.PROD, databaseId);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     * @param databaseId
     *      database identifier
     */
    public DbCdcsClient(String token, AstraEnvironment env, String databaseId) {
        super(token, env);
        Assert.hasLength(databaseId, "databaseId");
        this.db = new DbOpsClient(token, env, databaseId).get();
    }

    /**
     * Access Cdc component for a DB.
     *
     * @return list of cdc
     */
    public Stream<CdcDefinition> findAll() {
        ApiResponseHttp res = GET(getEndpointDatabaseCdc());
        if (HttpURLConnection.HTTP_NOT_FOUND == res.getCode()) {
            return Stream.of();
        } else {
            return JsonUtils.unmarshallType(res.getBody(), TYPE_LIST_CDC).stream();
        }
    }

    /**
     * Find a cdc by its id.
     *
     * @param cdcId
     *         identifier
     * @return cdc definition if exist
     */
    public Optional<CdcDefinition> findById(String cdcId) {
        Assert.hasLength(cdcId, "cdc identifier");
        return findAll().filter(cdc -> cdc.getConnectorName().equals(cdcId)).findFirst();
    }

    /**
     * Find the cdc based on its components.
     *
     * @param keyspace
     *         keyspace name
     * @param table
     *         table name
     * @param tenant
     *         tenant identifier
     * @return definition if present
     */
    public Optional<CdcDefinition> findByDefinition(String keyspace, String table, String tenant) {
        Assert.hasLength(keyspace, "keyspace");
        Assert.hasLength(table, "table");
        Assert.hasLength(tenant, "tenant");
        return findAll().filter(cdc -> cdc.getKeyspace().equals(keyspace) && cdc.getDatabaseTable().equals(table) && cdc.getTenant().equals(tenant)).findFirst();
    }

    /**
     * Create cdc from definition.
     *
     * @param keyspace
     *         keyspace name
     * @param table
     *         table name
     * @param tenant
     *         tenant identifier
     * @param topicPartition
     *         topic partition
     */
    public void create(String keyspace, String table, String tenant, int topicPartition) {
        Assert.hasLength(keyspace, "keyspace");
        if (!db.getInfo().getKeyspaces().contains(keyspace)) {
            throw new KeyspaceNotFoundException(db.getId(), keyspace);
        }
        new AstraStreamingClient(token, environment).tenant(tenant).cdc().create(db.getId(), keyspace, table, topicPartition);
    }

    /**
     * Delete cdc from its identifier.
     *
     * @param cdcId
     *         cdc identifier
     */
    public void delete(String cdcId) {
        delete(findById(cdcId).orElseThrow(() -> new ChangeDataCaptureNotFoundException(cdcId, db.getId())));
    }

    /**
     * Delete cdc from its identifier.
     *
     * @param keyspace
     *         keyspace name
     * @param table
     *         table name
     * @param tenant
     *         tenant identifier
     */
    public void delete(String keyspace, String table, String tenant) {
        delete(findByDefinition(keyspace, table, tenant)
                .orElseThrow(() -> new ChangeDataCaptureNotFoundException(keyspace, table, tenant, db.getId())));
    }

    /**
     * Delete Cdc from its definition.
     *
     * @param cdc
     *         cdc definition
     */
    private void delete(CdcDefinition cdc) {
        new AstraStreamingClient(token, environment)
                .tenant(cdc.getTenant()).cdc()
                .delete(db.getId(), cdc.getKeyspace(), cdc.getDatabaseTable());
    }

    /**
     * Http Client for Cdc list.
     *
     * @return url to invoke CDC
     */
    private String getEndpointDatabaseCdc() {
        return ApiLocator.getApiDevopsEndpoint(environment) + "/streaming" + "/astra-cdc/databases/" + db.getId();
    }

}
