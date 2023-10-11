package com.dtsx.astra.sdk.huggingface;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceEmbeddingModel;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static dev.langchain4j.data.message.SystemMessage.systemMessage;
import static dev.langchain4j.data.message.UserMessage.userMessage;
import static dev.langchain4j.model.huggingface.HuggingFaceModelName.TII_UAE_FALCON_7B_INSTRUCT;
import static java.time.Duration.ofSeconds;

public class HuggingFace {

    @Test
    public void embeddingHuggingFace() {
        HuggingFaceEmbeddingModel model = HuggingFaceEmbeddingModel.builder()
                .accessToken(System.getenv("HF_API_KEY"))
                .modelId("sentence-transformers/all-MiniLM-L6-v2")
                .build();
        Embedding embedding = model.embed("This is a demo embeddings").content();
        System.out.println(embedding.dimensions());
        System.out.println(Arrays.toString(embedding.vector()));
    }

    @Test
    public void generativeAiLlama2() {
        HuggingFaceChatModel model = HuggingFaceChatModel.builder()
                .accessToken(System.getenv("HF_API_KEY"))
                .modelId(TII_UAE_FALCON_7B_INSTRUCT)
                .timeout(ofSeconds(15))
                .temperature(0.7)
                .maxNewTokens(20)
                .waitForModel(true)
                .build();

        AiMessage aiMessage = model.generate(
                systemMessage("You are a good friend of mine, who likes to answer with jokes"),
                userMessage("Hey Bro, what are you doing?")
        ).content();
        System.out.println(aiMessage.text());
    }


}
