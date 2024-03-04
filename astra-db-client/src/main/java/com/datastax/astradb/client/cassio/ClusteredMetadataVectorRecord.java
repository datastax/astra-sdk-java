package com.datastax.astradb.client.cassio;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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