package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.utils.AstraRc;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiModelName;
import io.stargate.sdk.json.domain.odm.Document;
import io.stargate.sdk.json.vector.VectorStore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class VectorClientPhilosopherTest {

    public static final String DBNAME_VECTOR_CLIENT = "vector_client_test";
    public static final String VECTOR_STORE_NAME = "demo_philosophers";
    public static final String DATASET = "/philosopher-quotes.csv";

    @Data @AllArgsConstructor @NoArgsConstructor
    private static class Quote {
        private String philosopher;
        private String quote;
        private Set<String> tags;
    }

    static OpenAiEmbeddingModel openaiVectorizer = OpenAiEmbeddingModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(OpenAiModelName.TEXT_EMBEDDING_ADA_002)
            .timeout(Duration.ofSeconds(15))
            .logRequests(true)
            .logResponses(true)
            .build();

    public static float[] vectorize(String inputText) {
        return openaiVectorizer.embed(inputText).content().vector();
    }

    public static VectorStore<Quote> vectorStore;

    @BeforeAll
    public static void setup() {
        if (System.getenv(AstraRc.ASTRA_DB_APPLICATION_TOKEN) == null) {
            throw new IllegalStateException("Please setup 'ASTRA_DB_APPLICATION_TOKEN' env variable");
        }
        AstraVectorClient vectorClient = new AstraVectorClient();
        UUID databaseId = vectorClient.createDatabase(DBNAME_VECTOR_CLIENT);
        log.info("{} is created and active", databaseId);
    }

    @Test
    @Order(1)
    @DisplayName("01. Import Data")
    @EnabledIfEnvironmentVariable(named = "ASTRA_DB_APPLICATION_TOKEN", matches = "Astra.*")
    public void shouldIngestCsv() {
        // Init the Store
        VectorDatabase dbClient = new AstraVectorClient().vectorDatabase(DBNAME_VECTOR_CLIENT);
        dbClient.deleteVectorStore(VECTOR_STORE_NAME);
        vectorStore = dbClient.createVectorStore(VECTOR_STORE_NAME, 1536, Quote.class);
        log.info("store {} is created ", VECTOR_STORE_NAME);
        assertTrue(dbClient.isVectorStoreExist(VECTOR_STORE_NAME));

        // Populate the Store
        AtomicInteger rowId = new AtomicInteger();
        loadQuotesFromCsv(DATASET).forEach(quote -> {
            log.info("Inserting {}: {}", rowId.get(), quote.getQuote());
            vectorStore.insert(
                    String.valueOf(rowId.incrementAndGet()),
                    quote, vectorize(quote.getQuote()));
        });
    }

    @Test
    @Order(2)
    @DisplayName("02. Should Similarity Search")
    public void shouldSimilaritySearch() {

        vectorStore = new AstraVectorClient()
                .vectorDatabase(DBNAME_VECTOR_CLIENT)
                .vectorStore(VECTOR_STORE_NAME, Quote.class);

        float[] embeddings = vectorize("We struggle all our life for nothing");
        vectorStore.similaritySearch(embeddings, 3)
                .stream()
                .map(Document::getData)
                .map(Quote::getQuote)
                .forEach(System.out::println);
    }


    // --- Utilities (loading CSV) ---

    private List<Quote> loadQuotesFromCsv(String filePath) {
        List<Quote> quotes = new ArrayList<>();
        File csvFile = new File(VectorClientPhilosopherTest.class.getResource(filePath).getFile());
        try (Scanner scanner = new Scanner(csvFile)) {
            while (scanner.hasNextLine()) {
                Quote q = mapCsvLine(scanner.nextLine());
                if (q != null) quotes.add(q);
            }
        } catch (FileNotFoundException fex) {
            throw new IllegalArgumentException("file is not in the classpath", fex);
        }
        return quotes;
    }

    private Quote mapCsvLine(String line) {
        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
        if (parts.length >= 3) {
            String author    = parts[0];
            String quote     = parts[1].replaceAll("\"", "");
            Set<String> tags = new HashSet<>(Arrays.asList(parts[2].split("\\;")));
            return new Quote(author, quote, tags);
        }
        return null;
    }


}
