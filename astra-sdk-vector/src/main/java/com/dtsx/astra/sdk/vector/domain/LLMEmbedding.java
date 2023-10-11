package com.dtsx.astra.sdk.vector.domain;


import lombok.Getter;

@Getter
public enum LLMEmbedding {

    // OpenAI
    OPENAI_TEXT_DAVINCI_003(LLmProvider.openAI,"text-davinci-003", 1536),
    OPENAI_TEXT_EMBEDDING_ADA_002(LLmProvider.openAI,"text-embedding-ada-002", 1536),

    // Google
    PALM2(LLmProvider.google,"text-bison", 768),
    CODEY(LLmProvider.google,"chat-bison", 768),
    VERTEX_AI(LLmProvider.google, "textembedding-gecko", 768);

    private final LLmProvider llmprovider;
    private final String name;
    private final int dimension;

    private LLMEmbedding(LLmProvider provider, String name, int dimension) {
        this.llmprovider = provider;
        this.name = name;
        this.dimension = dimension;
    }

}
