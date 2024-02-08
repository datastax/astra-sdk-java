package com.dtsx.astra.sdk.cassio;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.data.CqlVector;
import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.dtsx.astra.sdk.cassio.AbstractCassandraTable.ATTRIBUTES_BLOB;
import static com.dtsx.astra.sdk.cassio.AbstractCassandraTable.BODY_BLOB;
import static com.dtsx.astra.sdk.cassio.AbstractCassandraTable.METADATA_S;
import static com.dtsx.astra.sdk.cassio.AbstractCassandraTable.PARTITION_ID;
import static com.dtsx.astra.sdk.cassio.AbstractCassandraTable.ROW_ID;
import static com.dtsx.astra.sdk.cassio.AbstractCassandraTable.VECTOR;

/**
 * Partitioned table with cluster and vector.
 */
@Data
@AllArgsConstructor
public class ClusteredMetadataVectorRecord {

    /** Partition id (clustered). */
    String partitionId = "default";

    /**
     * Metadata (for metadata filtering)
     */
    Map<String, String> metadata = new HashMap<>();

    /**
     * Vector Store
     */
    List<Float> vector;

    /** Row identifier. */
    UUID rowId;

    /** Text body. */
    String body;

    /**
     * Store special attributes
     */
    String attributes;

    /**
     * Default Constructor.
     */
    public ClusteredMetadataVectorRecord() {}

    /**
     * Create a record with a vector.
     *
     * @param vector current vector.
     */
    public ClusteredMetadataVectorRecord(List<Float> vector) {
        this.rowId = Uuids.timeBased();
        this.vector = vector;
    }



}