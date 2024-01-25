package com.dtsx.astra.sdk.cassio;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Wrap query parameters as a Bean.
 */
@Data @Builder
public class AnnQuery {

    /**
     * Maximum number of item returned
     */
    private int recordCount;

    /**
     * Minimum distance computation
     */
    private double threshold = 0.0;

    /**
     * Embeddings to be searched.
     */
    private List<Float> embeddings;

    /**
     * Default distance is cosine
     */
    private CassandraSimilarityMetric metric = CassandraSimilarityMetric.COSINE;

    /**
     * If provided search on metadata
     */
    private Map<String, String> metaData;

    /**
     * Default constructor
     */
    protected AnnQuery() {}

}
