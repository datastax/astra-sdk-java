package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.HttpClientWrapper;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.exception.ChangeDataCaptureNotFoundException;
import com.dtsx.astra.sdk.db.exception.KeyspaceNotFoundException;
import com.dtsx.astra.sdk.streaming.StreamingClient;
import com.dtsx.astra.sdk.streaming.domain.CdcDefinition;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.net.HttpURLConnection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Group Operation regarding Cdc for a DB
 */
public class DbCdcClient {

    /**
     * Load Cdc responses.
     */
    private static final TypeReference<List<CdcDefinition>> TYPE_LIST_CDC =
            new TypeReference<List<CdcDefinition>>() {
    };

    /**
     * Wrapper handling header and error management as a singleton.
     */
    private final HttpClientWrapper http = HttpClientWrapper.getInstance();

    /**
     * unique db identifier.
     */
    private final String token;

    /**
     * Load database
     */
    private Database db;

    /**
     * Initialization of CDC.
     *
     * @param token
     *      current token
     * @param db
     *      database
     */
    public DbCdcClient(String token, Database db) {
        this.token = token;
        this.db    = db;
    }

    /**
     * Access Cdc component for a DB.
     *
     * @return list of cdc
     */
    public Stream<CdcDefinition> findAll() {
        ApiResponseHttp res = http.GET(getEndpointDatabaseCdc(), token);
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
     * Create cdcd from definition.
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
        new StreamingClient(token).tenant(tenant).cdc().create(db.getId(), keyspace, table, topicPartition);
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
     *         cdcd definition
     */
    private void delete(CdcDefinition cdc) {
        new StreamingClient(token)
                .tenant(cdc.getTenant()).cdc()
                .delete(db.getId(), cdc.getKeyspace(), cdc.getDatabaseTable());
    }

    /**
     * Http Client for Cdc list.
     *
     * @return url to invoke CDC
     */
    public String getEndpointDatabaseCdc() {
        return StreamingClient.getApiDevopsEndpointStreaming() + "/astra-cdc/databases/" + db.getId();
    }

}
