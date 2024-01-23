package com.dtsx.astra.sdk.cassio;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.Row;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Table representing persistence for LangChain operations.
 * - parition key: partitionId
 * - clustering key: rowId
 * - column: value
 */
@Slf4j
public class ClusteredMetadataVectorCassandraTable
        extends AbstractCassandraTable<ClusteredMetadataVectorCassandraTable.Record> {

    /**
     * Dimension of the vector in use
     */
    private final int vectorDimension;

    /**
     * Similarity Metric, Vector is indexed with this metric.
     */
    private final SimilarityMetric similarityMetric;

    /**
     * Prepared statements
     */
    private PreparedStatement findPartitionStatement;
    private PreparedStatement deletePartitionStatement;
    private PreparedStatement deleteRowStatement;
    private PreparedStatement insertRowStatement;
    private PreparedStatement findRowStatement;

    /**
     * Constructor with mandatory parameters.
     *
     * @param session         cassandra session
     * @param keyspaceName    keyspace name
     * @param tableName       table name
     * @param vectorDimension vector dimension
     * @param metric          similarity metric
     */
    public ClusteredMetadataVectorCassandraTable(
            @NonNull CqlSession session,
            @NonNull  String keyspaceName,
            @NonNull String tableName,
            @NonNull Integer vectorDimension,
            @NonNull SimilarityMetric metric) {
        super(session, keyspaceName, tableName);
        this.vectorDimension = vectorDimension;
        this.similarityMetric = metric;
    }

    /**
     * Builder class for creating instances of {@link ClusteredMetadataVectorCassandraTable}.
     * This class follows the builder pattern to allow setting various parameters
     * before creating an instance of {@link ClusteredMetadataVectorCassandraTable}.
     */
    public static class Builder {
        private CqlSession session;
        private String keyspaceName;
        private String tableName;
        private Integer vectorDimension;
        private SimilarityMetric metric = SimilarityMetric.COS;

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
        public Builder withMetric(SimilarityMetric metric) {
            this.metric = metric;
            return this;
        }

        /**
         * Creates a new instance of ClusteredMetadataVectorCassandraTable with the current builder settings.
         *
         * @return A new instance of ClusteredMetadataVectorCassandraTable.
         */
        public ClusteredMetadataVectorCassandraTable build() {
            return new ClusteredMetadataVectorCassandraTable(session, keyspaceName, tableName, vectorDimension, metric);
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
    public static ClusteredMetadataVectorCassandraTable.Builder builder() {
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
            insertRowStatement = cqlSession.prepare(
                    "insert into " + keyspaceName + "." + tableName
                            + " (" + PARTITION_ID + ", " + ROW_ID + ", " + BODY_BLOB + ") "
                            + " values (?, ?, ?)");
        }
    }

    /* {@inheritDoc} */
    @Override
    public void create() {
        // Create Table
        cqlSession.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                PARTITION_ID + "uuid," +
                ROW_ID       + "text, " +
                ATTRIBUTES_BLOB + " text, " +
                BODY_BLOB + " text, " +
                METADATA_S + " map<text, text>, " +
                VECTOR + " vector<float, " + vectorDimension + ">, " +
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

    /* {@inheritDoc} */
    @Override
    public void put(Record row) {

    }

    /* {@inheritDoc} */
    @Override
    public Record mapRow(Row row) {
        return null;
    }

    /**
     * Table record.
     */
    public static class Record {

        /**
         * Default constructor.
         */
        public Record() {}

    }

}
