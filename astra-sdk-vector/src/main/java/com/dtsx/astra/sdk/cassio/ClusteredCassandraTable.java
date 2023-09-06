package com.dtsx.astra.sdk.cassio;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BatchStatement;
import com.datastax.oss.driver.api.core.cql.BatchStatementBuilder;
import com.datastax.oss.driver.api.core.cql.BatchType;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Table representing persistence for LangChain operations
 */
@Slf4j
public class ClusteredCassandraTable extends AbstractCassandraTable<ClusteredCassandraTable.Record> {

    /**
     * Prepared statements
     */
    private final PreparedStatement findPartitionStatement;
    private final PreparedStatement deletePartitionStatement;
    private final PreparedStatement deleteRowStatement;
    private final PreparedStatement insertRowStatement;
    private final PreparedStatement findRowStatement;

    /**
     * Constructor with the mandatory parameters.
     *
     * @param session
     *      cassandra Session
     * @param keyspaceName
     *      keyspace name
     * @param tableName
     *      table name
     */
    public ClusteredCassandraTable(@NonNull CqlSession session, @NonNull String  keyspaceName, @NonNull  String tableName) {
        super(session, keyspaceName, tableName);
        createSchema();
        findPartitionStatement = session.prepare(QueryBuilder.selectFrom(tableName).all()
                .whereColumn(PARTITION_ID).isEqualTo(QueryBuilder.bindMarker())
                .build());
        deletePartitionStatement = session.prepare(QueryBuilder.deleteFrom(tableName)
                .whereColumn(PARTITION_ID).isEqualTo(QueryBuilder.bindMarker())
                .build());
        findRowStatement = session.prepare(QueryBuilder.selectFrom(tableName).all()
                .whereColumn(PARTITION_ID).isEqualTo(QueryBuilder.bindMarker())
                .whereColumn(ROW_ID).isEqualTo(QueryBuilder.bindMarker())
                .build());
        deleteRowStatement = session.prepare(QueryBuilder.deleteFrom(tableName)
                .whereColumn(PARTITION_ID).isEqualTo(QueryBuilder.bindMarker())
                .whereColumn(ROW_ID).isEqualTo(QueryBuilder.bindMarker())
                .build());
        insertRowStatement = session.prepare(QueryBuilder.insertInto(tableName)
                .value(PARTITION_ID, QueryBuilder.bindMarker())
                .value(ROW_ID, QueryBuilder.bindMarker())
                .value(BODY_BLOB, QueryBuilder.bindMarker())
                .build());
    }

    @Override
    public void createSchema() {
        cqlSession.execute(SchemaBuilder.createTable(tableName)
                .ifNotExists()
                .withPartitionKey(PARTITION_ID, DataTypes.TEXT)
                .withClusteringColumn(ROW_ID, DataTypes.TIMEUUID)
                .withColumn(BODY_BLOB, DataTypes.TEXT)
                .withClusteringOrder(ROW_ID, ClusteringOrder.DESC)
                .build());
        log.info("+ Table '{}' has been created (if needed).", tableName);
    }

    /** {@inheritDoc} */
    @Override
    public void put(@NonNull ClusteredCassandraTable.Record row) {
        cqlSession.execute(insertRowStatement.bind(row.getPartitionId(), row.getRowId(), row.getBody()));
    }

    /** {@inheritDoc} */
    @Override
    public Record mapRow(@NonNull Row row) {
        return new Record(
                row.getString(PARTITION_ID),
                row.getUuid(ROW_ID),
                row.getString(BODY_BLOB));
    }

    /**
     * Find a partition.
     *
     * @param partitionDd
     *      partition id
     * @return
     *      list of rows
     */
    public List<Record> findPartition(@NonNull String partitionDd) {
        return cqlSession.execute(findPartitionStatement.bind(partitionDd))
                .all().stream()
                .map(this::mapRow)
                .collect(Collectors.toList());
    }

    /**
     * Update the history in one go.
     *
     * @param rows
     *      current rows.
     */
    public void upsertPartition(List<Record> rows) {
        if (rows != null && !rows.isEmpty()) {
            BatchStatementBuilder batch = BatchStatement.builder(BatchType.LOGGED);
            String currentPartitionId = null;
            for (Record row : rows) {
                if (currentPartitionId != null && !currentPartitionId.equals(row.getPartitionId())) {
                    log.warn("Not all rows are part of the same partition");
                }
                currentPartitionId = row.getPartitionId();
                batch.addStatement(insertRowStatement.bind(row.getPartitionId(), row.getRowId(), row.getBody()));
            }
            cqlSession.execute(batch.build());
        }
    }

    /**
     * Find a row by its id.
     * @param partition
     *      partition id
     * @param rowId
     *      row id
     * @return
     *      record if exists
     */
    public Optional<Record> findById(String partition, UUID rowId) {
        return Optional.ofNullable(cqlSession
                        .execute(findRowStatement.bind(partition, rowId))
                        .one()).map(this::mapRow);
    }

    /**
     * Delete Partition.
     *
     * @param partitionId
     *     delete the whole partition
     */
    public void deletePartition(@NonNull String partitionId) {
        cqlSession.execute(deletePartitionStatement.bind(partitionId));
    }

    /**
     * Delete one row.
     *
     * @param partitionId
     *      current session
     * @param rowId
     *      message id
     */
    public void delete(@NonNull String partitionId, @NonNull UUID rowId) {
        cqlSession.execute(deleteRowStatement.bind(partitionId, rowId));
    }

    /**
     * Insert Row.
     *
     * @param partitionId
     *      partition id
     * @param rowId
     *      rowId
     * @param bodyBlob
     *      body
     */
    public void insert(@NonNull String partitionId, @NonNull UUID rowId, @NonNull String bodyBlob) {
        cqlSession.execute(QueryBuilder.insertInto(keyspaceName, tableName)
                .value(PARTITION_ID, QueryBuilder.literal(partitionId))
                .value(ROW_ID, QueryBuilder.literal(rowId))
                .value(BODY_BLOB, QueryBuilder.literal(bodyBlob))
                .build());
    }

    /**
     * Represents a row of the Table
     */
    @Data @AllArgsConstructor @NoArgsConstructor
    public static class Record {

        /** Partition id. */
        private String partitionId;

        /** Row identifier. */
        private UUID rowId;

        /** Text body. */
        private String body;
    }


}
