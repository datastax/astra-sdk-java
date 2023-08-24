package com.dtsx.astra.sdk.vector;

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.insert.RegularInsert;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.dtsx.astra.sdk.vector.MetadataVectorCassandraTable.ATTRIBUTES_BLOB;
import static com.dtsx.astra.sdk.vector.MetadataVectorCassandraTable.BODY_BLOB;
import static com.dtsx.astra.sdk.vector.MetadataVectorCassandraTable.METADATA_S;
import static com.dtsx.astra.sdk.vector.MetadataVectorCassandraTable.ROW_ID;
import static com.dtsx.astra.sdk.vector.MetadataVectorCassandraTable.VECTOR;

@Data
public class MetadataVectorCassandraRecord {

    private String rowId;

    private String attributes;

    private String body;

    private Map<String, String> metadata = new HashMap<>();

    private List<Float> vector;

    @SuppressWarnings("unchecked")
    public static MetadataVectorCassandraRecord fromRow(Row cqlRow) {
        if (cqlRow == null) return null;
        MetadataVectorCassandraRecord record = new MetadataVectorCassandraRecord();
        record.setRowId(cqlRow.getString(ROW_ID));
        record.setAttributes(cqlRow.getString(ATTRIBUTES_BLOB));
        record.setBody(cqlRow.getString(BODY_BLOB));
        record.setMetadata(cqlRow.getMap(METADATA_S, String.class, String.class));
        record.setVector(((CqlVector<Float>) cqlRow.getObject(VECTOR)).stream().collect(Collectors.toList()));
        return record;
    }

    public SimpleStatement insertStatement(@NotNull String keyspaceName, @NotNull String tableName) {
        if (rowId == null) throw new IllegalStateException("Row Id cannot be null");
        if (vector == null) throw new IllegalStateException("Vector cannot be null");
        RegularInsert regularInser = QueryBuilder
            .insertInto(keyspaceName, tableName)
            .value(ROW_ID, QueryBuilder.literal(rowId))
            .value(VECTOR, QueryBuilder.literal(CqlVector.newInstance(vector)));
        if (attributes != null) {
            regularInser = regularInser.value(ATTRIBUTES_BLOB, QueryBuilder.literal(attributes));
        }
        if (body != null) {
            regularInser = regularInser.value(BODY_BLOB, QueryBuilder.literal(body));
        }
        if (metadata != null && !metadata.isEmpty()) {
            regularInser = regularInser.value(METADATA_S, QueryBuilder.literal(metadata));
        }
        return regularInser.build();
    }

}
