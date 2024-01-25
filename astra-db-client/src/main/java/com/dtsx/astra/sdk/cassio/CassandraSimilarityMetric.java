package com.dtsx.astra.sdk.cassio;

import lombok.Getter;

/**
 * Option for the similarity metric.
 */
@Getter
public enum CassandraSimilarityMetric {

    /** dot product. */
    DOT_PRODUCT("DOT_PRODUCT","similarity_dot_product"),

    /** cosine. */
    COSINE("COSINE","similarity_cosine"),

    /** euclidean. */
    EUCLIDEAN("EUCLIDEAN","similarity_euclidean");

    /**
     * Option.
     */
    private final String option;

    /**
     * Function.
     */
    private final String function;

    /**
     * Constructor.
     *
     * @param option
     *     option in the index creation
     * @param function
     *      function to be used in the query
     */
    CassandraSimilarityMetric(String option, String function) {
        this.option = option;
        this.function = function;
    }


}
