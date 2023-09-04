package com.dtsx.astra.sdk.cassio;

public enum SimilarityMetric {

    DOT_PRODUCT("DOT_PRODUCT","similarity_dot_product"),

    COS("COSINE","similarity_cosine"),

    DOT("EUCLIDEAN","similarity_euclidean");

    /**
     * Option.
     */
    private String option;

    /**
     * Function.
     */
    private String function;

    /**
     * Constructor.
     *
     * @param option
     *     option in the index creation
     * @param function
     *      function to be used in the query
     */
    SimilarityMetric(String option, String function) {
        this.option = option;
        this.function = function;
    }

    /**
     * Gets option
     *
     * @return value of option
     */
    public String getOption() {
        return option;
    }

    /**
     * Gets function
     *
     * @return value of function
     */
    public String getFunction() {
        return function;
    }


}
