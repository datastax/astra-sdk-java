package com.dtsx.astra.sdk.vector.domain;


import lombok.Getter;

@Getter
public enum LLMEmbedding {

    /** openai. */
    OPENAI_TEXT_DAVINCI_003(LLmProvider.openAI,"text-davinci-003", 1536),

    /** openai. */
    OPENAI_TEXT_EMBEDDING_ADA_002(LLmProvider.openAI,"text-embedding-ada-002", 1536),

    /** google. */
    PALM2(LLmProvider.google,"text-bison", 768),

    /** google. */
    CODEY(LLmProvider.google,"chat-bison", 768),

    /** google. */
    VERTEX_AI(LLmProvider.google, "textembedding-gecko", 768);

    /**
     * llm provider
     */
    final LLmProvider llmprovider;

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
    LLMEmbedding(LLmProvider provider, String name, int dimension) {
        this.llmprovider = provider;
        this.name = name;
        this.dimension = dimension;
    }

}
