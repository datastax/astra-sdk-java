package com.dtsx.astra.sdk.cassio;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.data.CqlVector;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static io.stargate.sdk.utils.AnsiUtils.cyan;
import static io.stargate.sdk.utils.AnsiUtils.yellow;

/**
 * Table representing persistence for LangChain operations.
 * - parition key: partitionId
 * - clustering key: rowId
 * - column: value
 */
@Slf4j
public class ClusteredMetadataVectorTable extends AbstractCassandraTable<ClusteredMetadataVectorRecord> {

    /**
     * Dimension of the vector in use
     */
    private final int vectorDimension;

    /**
     * Similarity Metric, Vector is indexed with this metric.
     */
    private final CassandraSimilarityMetric similarityMetric;

    /**
     * Prepared statements
     */
    private PreparedStatement findPartitionStatement;
    private PreparedStatement deletePartitionStatement;
    private PreparedStatement findRowStatement;
    private PreparedStatement deleteRowStatement;
    private PreparedStatement insertRowStatement;

    /**
     * Constructor with mandatory parameters.
     *
     * @param session         cassandra session
     * @param keyspaceName    keyspace name
     * @param tableName       table name
     * @param vectorDimension vector dimension
     * @param metric          similarity metric
     */
    public ClusteredMetadataVectorTable(
            @NonNull CqlSession session,
            @NonNull String keyspaceName,
            @NonNull String tableName,
            @NonNull Integer vectorDimension,
            @NonNull CassandraSimilarityMetric metric) {
        super(session, keyspaceName, tableName);
        this.vectorDimension = vectorDimension;
        this.similarityMetric = metric;
    }

    /**
     * Constructor with mandatory parameters.
     *
     * @param session         cassandra session
     * @param keyspaceName    keyspace name
     * @param tableName       table name
     * @param vectorDimension vector dimension
     */
    public ClusteredMetadataVectorTable(CqlSession session, String keyspaceName, String tableName, int vectorDimension) {
        this(session, keyspaceName, tableName, vectorDimension, CassandraSimilarityMetric.COSINE);
    }

    /**
     * Builder class for creating instances of {@link ClusteredMetadataVectorTable}.
     * This class follows the builder pattern to allow setting various parameters
     * before creating an instance of {@link ClusteredMetadataVectorTable}.
     */
    public static class Builder {
        private CqlSession session;
        private String keyspaceName;
        private String tableName;
        private Integer vectorDimension;
        private CassandraSimilarityMetric metric = CassandraSimilarityMetric.COSINE;

        /**
         * Sets the CqlSession.
         *
         * @param session The CqlSession to be used by the ClusteredMetadataVectorCassandraTable.
         * @return The current Builder instance for chaining.
         */
        public Builder withSession(CqlSession session) {
            this.session = session;
            return this;
        }

        /**
         * Sets the keyspace name.
         *
         * @param keyspaceName The name of the keyspace to be used.
         * @return The current Builder instance for chaining.
         */
        public Builder withKeyspaceName(String keyspaceName) {
            this.keyspaceName = keyspaceName;
            return this;
        }

        /**
         * Sets the table name.
         *
         * @param tableName The name of the table to be used.
         * @return The current Builder instance for chaining.
         */
        public Builder withTableName(String tableName) {
            this.tableName = tableName;
            return this;
        }

        /**
         * Sets the vector dimension.
         *
         * @param vectorDimension The vector dimension to be used.
         * @return The current Builder instance for chaining.
         */
        public Builder withVectorDimension(Integer vectorDimension) {
            this.vectorDimension = vectorDimension;
            return this;
        }

        /**
         * Sets the similarity metric.
         *
         * @param metric The SimilarityMetric to be used.
         * @return The current Builder instance for chaining.
         */
        public Builder withMetric(CassandraSimilarityMetric metric) {
            this.metric = metric;
            return this;
        }

        /**
         * Creates a new instance of ClusteredMetadataVectorCassandraTable with the current builder settings.
         *
         * @return A new instance of ClusteredMetadataVectorCassandraTable.
         */
        public ClusteredMetadataVectorTable build() {
            return new ClusteredMetadataVectorTable(session, keyspaceName, tableName, vectorDimension, metric);
        }

        /**
         * Default constructor for Builder.
         */
        public Builder() {}
    }

    /**
     * Builder for the class.
     *
     * @return
     *      builder for the class
     */
    public static ClusteredMetadataVectorTable.Builder builder() {
        return new Builder();
    }

    /**
     * Prepare statements on first request.
     */
    private synchronized void prepareStatements() {
        if (findPartitionStatement == null) {
            findPartitionStatement = cqlSession.prepare(
                    "select * from " + keyspaceName + "." + tableName
                            + " where " + PARTITION_ID + " = ? ");
            deletePartitionStatement = cqlSession.prepare(
                    "delete from " + keyspaceName + "." + tableName
                            + " where " + PARTITION_ID + " = ? ");
            findRowStatement = cqlSession.prepare(
                    "select * from " + keyspaceName + "." + tableName
                            + " where " + PARTITION_ID + " = ? "
                            + " and " + ROW_ID + " = ? ");
            deleteRowStatement = cqlSession.prepare(
                    "delete from " + keyspaceName + "." + tableName
                            + " where " + PARTITION_ID + " = ? "
                            + " and " + ROW_ID + " = ? ");
            insertRowStatement = cqlSession.prepare("INSERT INTO " + keyspaceName + "." + tableName + " ("
                    + PARTITION_ID + "," + ROW_ID + "," + VECTOR + "," + ATTRIBUTES_BLOB + "," + BODY_BLOB + "," + METADATA_S + ") VALUES (?,?,?,?,?,?)");
        }
    }

    /* {@inheritDoc} */
    @Override
    public void create() {
        // Create Table
        cqlSession.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                PARTITION_ID    + " text, " +
                ROW_ID          + " timeuuid, " +
                ATTRIBUTES_BLOB + " text, " +
                BODY_BLOB       + " text, " +
                METADATA_S      + " map<text, text>, " +
                VECTOR          + " vector<float, " + vectorDimension + ">, " +
                "PRIMARY KEY ((" + PARTITION_ID + "), " + ROW_ID + ")) " +
                "WITH CLUSTERING ORDER BY (" + ROW_ID + " DESC)");
        cqlSession.execute(
                "CREATE CUSTOM INDEX IF NOT EXISTS idx_vector_" + tableName
                        + " ON " + tableName + " (" + VECTOR + ") "
                        + "USING 'org.apache.cassandra.index.sai.StorageAttachedIndex' "
                        + "WITH OPTIONS = { 'similarity_function': '" +  similarityMetric.getOption() + "'};");
        log.info("+ Index '{}' has been created (if needed).", "idx_vector_" + tableName);
        // Create Metadata Index
        cqlSession.execute(
                "CREATE CUSTOM INDEX IF NOT EXISTS eidx_metadata_s_" + tableName
                        + " ON " + tableName + " (ENTRIES(" + METADATA_S + ")) "
                        + "USING 'org.apache.cassandra.index.sai.StorageAttachedIndex' ");
        log.info("+ Index '{}' has been created (if needed).", "eidx_metadata_s_" + tableName);
    }

    /**
     * Find a partition.
     *
     * @param partitionDd
     *      partition id
     * @return
     *      list of rows
     */
    public List<ClusteredMetadataVectorRecord> findPartition(@NonNull String partitionDd) {
        prepareStatements();
        return cqlSession.execute(findPartitionStatement.bind(partitionDd))
                .all().stream()
                .map(this::mapRow)
                .collect(Collectors.toList());
    }

    /**
     * Delete a partition.
     *
     * @param partitionDd
     *      partition id
     */
    public void deletePartition(@NonNull String partitionDd) {
        prepareStatements();
        cqlSession.execute(deletePartitionStatement.bind(partitionDd));
    }

    /**
     * Access a record by its id
     *
     * @param partitionId
     *      partition id
     * @param rowId
     *      rowId
     * @return
     *      record if exists
     */
    public Optional<ClusteredMetadataVectorRecord> get(String partitionId, UUID rowId) {
        prepareStatements();
        return Optional
                .ofNullable(cqlSession.execute(findRowStatement.bind(partitionId, rowId))
                .one())
                .map(this::mapRow);
    }

    /**
     * Access a record by its id
     *
     * @param partitionId
     *      partition id
     * @param rowId
     *      rowId
     */
    public void  delete(String partitionId, UUID rowId) {
        prepareStatements();
        cqlSession.execute(deleteRowStatement.bind(partitionId, rowId));
    }

    public void save(ClusteredMetadataVectorRecord row) {
        put(row);
    }

    /* {@inheritDoc} */
    @Override
    public void put(ClusteredMetadataVectorRecord row) {
        prepareStatements();
        cqlSession.execute(insertRowStatement.bind(
                row.getPartitionId(),
                row.getRowId(),
                CqlVector.newInstance(row.getVector()),
                row.getAttributes(),
                row.getBody(),
                row.getMetadata()));
    }

    /* {@inheritDoc} */
    @Override
    @SuppressWarnings("unchecked")
    public ClusteredMetadataVectorRecord mapRow(Row cqlRow) {
        if (cqlRow == null ) return null;
        ClusteredMetadataVectorRecord record = new ClusteredMetadataVectorRecord();
        // Clustered
        record.setPartitionId(cqlRow.getString(PARTITION_ID));
        // Vector
        record.setVector(((CqlVector<Float>) Objects.requireNonNull(cqlRow.getObject(VECTOR)))
                .stream().collect(Collectors.toList()));
        // Always There
        record.setRowId(cqlRow.getUuid(ROW_ID));
        record.setBody(cqlRow.getString(BODY_BLOB));
        if (cqlRow.getColumnDefinitions().contains(ATTRIBUTES_BLOB)) {
            record.setAttributes(cqlRow.getString(ATTRIBUTES_BLOB));
        }
        if (cqlRow.getColumnDefinitions().contains(METADATA_S)) {
            record.setMetadata(cqlRow.getMap(METADATA_S, String.class, String.class));
        }
        return record;
    }

    /**
     * Compute Similarity Search.
     *
     * @param query
     *    current query
     * @return
     *      results
     */
    public List<AnnResult<ClusteredMetadataVectorRecord>> similaritySearch(AnnQuery query) {
        StringBuilder cqlQuery = new StringBuilder("SELECT ")
                .append(String.join(",", PARTITION_ID, ROW_ID, VECTOR, BODY_BLOB, ATTRIBUTES_BLOB, METADATA_S, ""))
                .append(query.getMetric().getFunction()).append("(vector, :vector) as ").append(COLUMN_SIMILARITY)
                .append(" FROM ").append(tableName);

        List<String> conditions = new ArrayList<>();

        // Add metadata conditions
        if (query.getMetaData() != null && !query.getMetaData().isEmpty()) {
            if (query.getMetaData().containsKey(PARTITION_ID)) {
                conditions.add(PARTITION_ID + " = '" + query.getMetaData().get(PARTITION_ID) + "'");
                // remove partition id (if any
                query.getMetaData().remove(PARTITION_ID);
            }
            query.getMetaData().forEach((key, value) ->
                    conditions.add(METADATA_S + "['" + key + "'] = '" + value + "'"));
        }

        if (!conditions.isEmpty()) {
            cqlQuery.append(" WHERE ").append(String.join(" AND ", conditions));
        }

        cqlQuery.append(" ORDER BY vector ANN OF :vector LIMIT :maxRecord");

        log.debug("Query on table '{}' with vector size '{}' and max record='{}'",
                yellow(tableName),
                cyan("[" + query.getEmbeddings().size() + "]"),
                cyan("" + (query.getRecordCount() > 0 ? query.getRecordCount() : DEFAULT_RECORD_COUNT)));
        return cqlSession.execute(SimpleStatement.builder(cqlQuery.toString())
                        .addNamedValue("vector", CqlVector.newInstance(query.getEmbeddings()))
                        .addNamedValue("maxRecord", query.getRecordCount() > 0 ? query.getRecordCount() : DEFAULT_RECORD_COUNT)
                        .build())
                .all().stream() // max record is small and pagination is not needed
                .map(this::mapResult)
                .filter(r -> r.getSimilarity() >= query.getThreshold())
                .collect(Collectors.toList());
    }

    /**
     * Marshall a row as a result.
     *
     * @param cqlRow cql row
     * @return resul
     */
    private AnnResult<ClusteredMetadataVectorRecord> mapResult(Row cqlRow) {
        if (cqlRow == null) return null;
        AnnResult<ClusteredMetadataVectorRecord> res = new AnnResult<>();
        res.setEmbedded(mapRow(cqlRow));
        res.setSimilarity(cqlRow.getFloat(COLUMN_SIMILARITY));
        log.debug("Result similarity '{}' for embedded id='{}'", res.getSimilarity(), res.getEmbedded().getRowId());
        return res;
    }

}
