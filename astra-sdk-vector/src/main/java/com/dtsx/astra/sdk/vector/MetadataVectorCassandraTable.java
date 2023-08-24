package com.dtsx.astra.sdk.vector;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j @Getter
public class MetadataVectorCassandraTable extends AbstractVectorTable {

    /**
     * Rable Structure
     */
    public static final String ROW_ID           = "row_id";
    public static final String ATTRIBUTES_BLOB  = "attributes_blob";
    public static final String BODY_BLOB        = "body_blob";
    public static final String METADATA_S       = "metadata_s";
    public static final String VECTOR           = "vector";

    private final int vectorDimension;

    public MetadataVectorCassandraTable(CqlSession session, String keyspaceName, String tableName, int vectorDimension) {
        super(session, keyspaceName, tableName);
        this.vectorDimension = vectorDimension;
    }

    public void createIfNotExist() {
        // Create Table
        String cql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                ROW_ID          + " text, " +
                ATTRIBUTES_BLOB + " text, " +
                BODY_BLOB       + " text, " +
                METADATA_S      + " map<text, text>, " +
                VECTOR          + " vector<float, " + vectorDimension + ">, " +
                "PRIMARY KEY (" +
                ROW_ID + ")" +
                ")";
        cqlSession.execute(cql);
        log.info("+ Table '{}' has been created (if needed).", tableName);
        // Create Vector Index
        cqlSession.execute(SchemaBuilder.createIndex("idx_vector_" + tableName)
                .ifNotExists()
                .custom(SAI_INDEX_CLASSNAME)
                .onTable(tableName)
                .andColumn(VECTOR)
                .build());
        log.info("+ Index '{}' has been created (if needed).", "idx_vector_" + tableName);
        // Create Metadata Index
        cqlSession.execute(SchemaBuilder.createIndex("eidx_metadata_s_" + tableName)
                .ifNotExists()
                .custom(SAI_INDEX_CLASSNAME)
                .onTable(tableName)
                .andColumnEntries(METADATA_S)
                .build());
        log.info("+ Index '{}' has been created (if needed).", "eidx_metadata_s_" + tableName);
    }

    public void put(String rowId, String bodyBlob, String attributesBlob, Map<String, String> metadata, List<Float> vector) {
        cqlSession.execute(QueryBuilder.insertInto(keyspaceName, tableName)
                .value(ROW_ID, QueryBuilder.literal(rowId))
                .value(BODY_BLOB, QueryBuilder.literal(bodyBlob))
                .value(ATTRIBUTES_BLOB, QueryBuilder.literal(attributesBlob))
                .value(METADATA_S, QueryBuilder.literal(metadata))
                .value(VECTOR, QueryBuilder.literal(CqlVector.newInstance(vector)))
                .build());
    }

    public void put(MetadataVectorCassandraRecord row) {
        cqlSession.execute(row.insertStatement(keyspaceName, tableName));
    }



    public List<MetadataVectorCassandraRecord> ann_search(List<Float> vector, int recordCount, Map<String, String > metatadata) {
        ResultSet rs = cqlSession.execute( SimpleStatement.builder(
                "SELECT * FROM " + tableName
                    + " ORDER BY vector ANN OF ? LIMIT ?")
                .addPositionalValue(CqlVector.newInstance(vector))
                .addPositionalValue(recordCount)
                .build());
        return rs.all().stream()
                .map(MetadataVectorCassandraRecord::fromRow)
                .collect(Collectors.toList());
    }

}
