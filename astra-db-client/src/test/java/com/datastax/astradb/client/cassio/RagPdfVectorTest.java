package com.datastax.astradb.client.cassio;

import com.datastax.astradb.client.AstraDBAdmin;
import com.datastax.oss.driver.api.core.CqlSession;
import com.dtsx.astra.sdk.utils.TestUtils;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.dtsx.astra.sdk.utils.TestUtils.getAstraToken;
import static dev.langchain4j.model.openai.OpenAiModelName.GPT_3_5_TURBO;
import static java.util.stream.Collectors.joining;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RagPdfVectorTest {

    public static final String LLM_MODEL_CHAT_COMPLETION = "gpt-3.5-turbo";
    public static final String TEXT_EMBEDDING_ADA_002     = "text-embedding-ada-002";
    public static final int    LLM_MODEL_DIMENSION      = 1536;

    public static EmbeddingModel embeddingModel;
    public static EmbeddingStore<TextSegment> embeddingStore;

    /**
     * Settings from Astra Usage
     */
    private static final String ASTRA_DB_DATABASE = "test_java_astra_db_client";
    static CqlSession cqlSession;
    static ClusteredMetadataVectorTable cassandraTable;

    @BeforeAll
    public static void init() {

        // Initializing DV
        UUID databaseId = new AstraDBAdmin(getAstraToken()).createDatabase(ASTRA_DB_DATABASE);
        log.info("Astra Database is ready");

        // Initializing Session
        cqlSession = CassIO.init(getAstraToken(), databaseId, TestUtils.TEST_REGION, AstraDBAdmin.DEFAULT_KEYSPACE);
        log.info("Astra connection is opened");

        // Initializing table
        cassandraTable = CassIO.clusteredMetadataVectorTable("vector_store", LLM_MODEL_DIMENSION);
        CassIO.metadataVectorTable("", 1536);
        cassandraTable.create();
        log.info("Destination Table is created");

        // Initializing Embedding Store
        embeddingStore = new ClusteredMetadataVectorStore(cassandraTable);
        log.info("Embedding Store is ready");

        // Initializing Embedding Model
        embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(TEXT_EMBEDDING_ADA_002)
                .build();
        log.info("Embedding Model is ready");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldIngestPDF() {

        DocumentParser parser = new ApachePdfBoxDocumentParser();
        InputStream inputStream = RagPdfVectorTest.class.getClassLoader().getResourceAsStream("johnny.pdf");

        Document document = parser.parse(inputStream);

        DocumentSplitter splitter = DocumentSplitters
                .recursive(100, 10, new OpenAiTokenizer(GPT_3_5_TURBO));

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        ingestor.ingest(document);

    }

    @Test
    public void shouldDoRAG() {
        String question = "What animal is Johnny ?";

        // Embed the question
        Response<Embedding> questionEmbedding = embeddingModel.embed(question);

        // Find relevant embeddings in embedding store by semantic similarity
        // You can play with parameters below to find a sweet spot for your specific use case
        int maxResults = 5;
        double minScore = 0.8;
        List<EmbeddingMatch<TextSegment>> relevantEmbeddings =
                embeddingStore.findRelevant(questionEmbedding.content(), maxResults, minScore);


        // Create a prompt for the model that includes question and relevant embeddings
        PromptTemplate promptTemplate = PromptTemplate.from(
                "Answer the following question to the best of your ability:\n"
                        + "\n"
                        + "Question:\n"
                        + "{{question}}\n"
                        + "\n"
                        + "Base your answer on the following information:\n"
                        + "{{information}}");

        String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));

        Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", information);

        Prompt prompt = promptTemplate.apply(variables);

        // Send the prompt to the OpenAI chat model
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
                .apiKey(System.getenv("OPENAI_API_KEY"))
                .modelName(GPT_3_5_TURBO)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(15))
                .maxRetries(3)
                .logResponses(true)
                .logRequests(true)
                .build();

        Response<AiMessage> aiMessage = chatModel.generate(prompt.toUserMessage());

        // See an answer from the model
        String answer = aiMessage.content().text();
        System.out.println(answer);


    }

}
