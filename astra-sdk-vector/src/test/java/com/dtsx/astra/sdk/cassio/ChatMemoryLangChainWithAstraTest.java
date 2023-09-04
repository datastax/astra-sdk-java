package com.dtsx.astra.sdk.cassio;

import com.dtsx.astra.sdk.langchain.AstraDbChatMemoryStore;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.TokenWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import org.junit.jupiter.api.Test;

import static dev.langchain4j.data.message.UserMessage.userMessage;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static java.time.Duration.ofSeconds;

public class ChatMemoryLangChainWithAstraTest {

    String token = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
    String dbId  = "76859fb8-a2c3-4dac-978f-9b789c6c6791";
    String dbRegion = "us-east1";
    String chatSession = "test-session";
    String keyspaceName = "openai";

    @Test
    public void chatMemoryAstraTest() {

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_3_5_TURBO)
                .temperature(0.3)
                .timeout(ofSeconds(120))
                .logRequests(true)
                .logResponses(true)
                .build();

        ChatMemory chatMemory = TokenWindowChatMemory.builder()
                .chatMemoryStore(new AstraDbChatMemoryStore(token, dbId, dbRegion, keyspaceName))
                .id(chatSession)
                .maxTokens(300, new OpenAiTokenizer(GPT_3_5_TURBO))
                .build();

        chatMemory.add(userMessage("I will ask you a few question about ff4j. Response in a single sentence"));
        chatMemory.add(userMessage("Can I use it with Javascript ? "));
        System.out.println(model.sendMessages(chatMemory.messages()).text());

    }
}
