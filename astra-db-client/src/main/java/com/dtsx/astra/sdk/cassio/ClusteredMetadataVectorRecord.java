package com.dtsx.astra.sdk.cassio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    /** Partition id. */
    String partitionId;

    /** Row identifier. */
    UUID rowId;

    /** Text body. */
    String body;

    /**
     * Store special attributes
     */
    String attributes;

    /**
     * Metadata (for metadata filtering)
     */
    Map<String, String> metadata = new HashMap<>();

    /**
     * Embeddings
     */
    List<Float> vector;

    /**
     * Default Constructor.
     */
    public ClusteredMetadataVectorRecord() {}
    
}