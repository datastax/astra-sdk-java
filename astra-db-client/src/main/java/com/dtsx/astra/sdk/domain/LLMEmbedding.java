package com.dtsx.astra.sdk.domain;


import lombok.Getter;

/**
 * List Embeddings Models.
 */
@Getter
public enum LLMEmbedding {

    /** openai. */
    ADA_002("openai","text-embedding-ada-002", 1536),

    /** google. */
    GECKO("google", "textembedding-gecko", 768),

    /** hf. */
    MINILM_L6_V2("hugging-face", "all-MiniLM-L6-v2", 384);

    /**
     * llm provider
     */
    final String llmprovider;

    /**
     * model name
     */
    final String name;

    /**
     * model dimension
     */
    private final int dimension;

    /**
     * Constructor with the mandatory parameters.
     * @param provider
     *      llm provider
     * @param name
     *      model name
     * @param dimension
     *      model dimension
     */
    LLMEmbedding(String provider, String name, int dimension) {
        this.llmprovider = provider;
        this.name = name;
        this.dimension = dimension;
    }

}
