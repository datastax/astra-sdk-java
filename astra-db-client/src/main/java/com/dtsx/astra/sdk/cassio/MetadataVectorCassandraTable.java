package com.dtsx.astra.sdk.cassio;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Table representing persistence for Vector Stores support.
 */
@Slf4j
@Getter
public class MetadataVectorCassandraTable extends AbstractCassandraTable<MetadataVectorCassandraTable.Record> {

    /**
     * Dimension of the vector in use
     */
    private final int vectorDimension;

    /**
     * Similarity Metric, Vector is indexed with this metric.
     */
    private final SimilarityMetric similarityMetric;

    /**
     * Constructor with mandatory parameters.
     *
     * @param session         cassandra session
     * @param keyspaceName    keyspace name
     * @param tableName       table name
     * @param vectorDimension vector dimension
     */
    public MetadataVectorCassandraTable(CqlSession session, String keyspaceName, String tableName, int vectorDimension) {
        this(session, keyspaceName, tableName, vectorDimension, SimilarityMetric.DOT_PRODUCT);
    }

    /**
     * Constructor with mandatory parameters.
     *
     * @param session         cassandra session
     * @param keyspaceName    keyspace name
     * @param tableName       table name
     * @param vectorDimension vector dimension
     * @param metric          similarity metric
     */
    public MetadataVectorCassandraTable(CqlSession session, String keyspaceName, String tableName, int vectorDimension, SimilarityMetric metric) {
        super(session, keyspaceName, tableName);
        this.vectorDimension = vectorDimension;
        this.similarityMetric = metric;
    }

    /**
     * Create table and indexes if not exist.
     */
    public void create() {
        // Create Table
        cqlSession.execute("CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                ROW_ID + " text, " +
                ATTRIBUTES_BLOB + " text, " +
                BODY_BLOB + " text, " +
                METADATA_S + " map<text, text>, " +
                VECTOR + " vector<float, " + vectorDimension + ">, " +
                "PRIMARY KEY (" +
                ROW_ID + ")" +
                ")");
        log.info("+ Table '{}' has been created (if needed).", tableName);
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

    /** {@inheritDoc} */
    public void put(Record row) {
        cqlSession.execute(row.insertStatement(keyspaceName, tableName));
    }

    /**
     * Marshall a row as a result.
     *
     * @param cqlRow cql row
     * @return resul
     */
    private SimilaritySearchResult<Record> mapResult(Row cqlRow) {
        if (cqlRow == null) return null;
        SimilaritySearchResult<Record> res = new SimilaritySearchResult<>();
        res.setEmbedded(mapRow(cqlRow));
        res.setSimilarity(cqlRow.getFloat(COLUMN_SIMILARITY));
        return res;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Record mapRow(Row cqlRow) {
        if (cqlRow == null) return null;
        Record record = new Record();
        record.setRowId(cqlRow.getString(ROW_ID));
        record.setBody(cqlRow.getString(BODY_BLOB));
        record.setVector(((CqlVector<Float>) Objects.requireNonNull(cqlRow.getObject(VECTOR)))
                    .stream().collect(Collectors.toList()));
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
    public List<SimilaritySearchResult<Record>> similaritySearch(SimilaritySearchQuery query) {
        StringBuilder cqlQuery = new StringBuilder("SELECT " + ROW_ID + ","
                + VECTOR + "," + BODY_BLOB + ","
                + ATTRIBUTES_BLOB + "," + METADATA_S + ",");
        cqlQuery.append(query.getDistance().getFunction()).append("(vector, :vector) as ").append(COLUMN_SIMILARITY);
        cqlQuery.append(" FROM ").append(tableName);
        if (query.getMetaData() != null && !query.getMetaData().isEmpty()) {
            cqlQuery.append(" WHERE ");
            boolean first = true;
            for (Map.Entry<String, String> entry : query.getMetaData().entrySet()) {
                if (!first) {
                    cqlQuery.append(" AND ");
                }
                cqlQuery.append(METADATA_S).append("['")
                        .append(entry.getKey())
                        .append("'] = '")
                        .append(entry.getValue()).append("'");
                first = false;
            }
        }
        cqlQuery.append(" ORDER BY vector ANN OF :vector ");
        cqlQuery.append(" LIMIT :maxRecord");
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
     * Record for the Metadata Vector Table in Cassandra.
     */
    @Data
    public static class Record implements Serializable {

        /**
         * Identifier of the row in Cassandra
         */
        private String rowId;

        /**
         * Store special attributes
         */
        private String attributes;

        /**
         * Body, contains the Text Fragment.
         */
        private String body;

        /**
         * Metadata (for metadata filtering)
         */
        private Map<String, String> metadata = new HashMap<>();

        /**
         * Embeddings
         */
        private List<Float> vector;

        /**
         * Default Constructor
         */
        public Record() {
            this(Uuids.timeBased().toString(), null);
        }

        /**
         * Create a record with a vector.
         *
         * @param vector current vector.
         */
        public Record(List<Float> vector) {
            this(Uuids.timeBased().toString(), vector);
        }

        /**
         * Create a record with a vector.
         * @param rowId  identifier for the row
         * @param vector current vector.
         */
        public Record(String rowId, List<Float> vector) {
            this.rowId  = rowId;
            this.vector = vector;
        }

        /**
         * Build insert statement dynamically.
         *
         * @param keyspaceName
         *      keyspace name
         * @param tableName
         *      table bane
         * @return
         *      statement
         */
        public SimpleStatement insertStatement(String keyspaceName, String tableName) {
            if (rowId == null) throw new IllegalStateException("Row Id cannot be null");
            if (vector == null) throw new IllegalStateException("Vector cannot be null");
            return SimpleStatement.newInstance("INSERT INTO " + keyspaceName + "." + tableName + " ("
                    + ROW_ID + "," + VECTOR + "," + ATTRIBUTES_BLOB + "," + BODY_BLOB + "," + METADATA_S + ") VALUES (?,?,?,?,?)",
                    rowId, CqlVector.newInstance(vector), attributes, body, metadata);
        }
    }
}
