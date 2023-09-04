package com.dtsx.astra.sdk.cassio;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.embedding.Embedding;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * This app is a tentative to implement a equivalent of the CassIO example
 * direct usage with Java.
 */
@Slf4j
public class PhilosophersDirectCassIOSample {

    /**
     * Settings from Astra Usage
     */
    public static final String  ASTRA_DB_APPLICATION_TOKEN = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
    public static final String ASTRA_DB_ID = "76859fb8-a2c3-4dac-978f-9b789c6c6791";
    public static final String ASTRA_DB_REGION = "us-east1";
    public static final String ASTRA_DB_KEYSPACE = "openai";

    /**
     * Settings from OpenAI Usage
     */
    public static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");

    public static final String LLM_MODEL_CHAT_COMPLETION = "gpt-3.5-turbo";
    public static final String LLM_MODEL_EMBEDDINGS     = "text-embedding-ada-002";
    public static final int    LLM_MODEL_DIMENSION      = 1536;

    /**
     * DataSet to load
     */
    public static final String DATASET = "/Users/cedricklunven/dev/datastax/sdk/astra-sdk-java/" +
            "astra-sdk-vector/src/main/resources/philo_quotes.json";

    /**
     * Hold Cassandra connection
     */
    private static AstraClient astraClient;

    /**
     * Open AI Client
     */
    private static OpenAiService openai;

    /**
     * Main method to run the sample.
     *
     * @param args
     *      no arguments needed in the command line
     */
    public static void main(String[] args) {

        // Creation of the DB connection
        try(CqlSession cqlSession = getCqlSession()) {

            // Create destination table suited for the model.
            MetadataVectorCassandraTable v_table = new MetadataVectorCassandraTable(
                    cqlSession, ASTRA_DB_KEYSPACE,"philosophers", LLM_MODEL_DIMENSION);

            // Compute test embedding and show results
            testCallEmbedded("This is a sentence","A second sentence");

            // Load a JSON and insert embeddings for each entry
            loadQuotes(v_table);

            // Similarity Search
            List<String> quotes1 = findQuotes(v_table, "We struggle all our life for nothing", 3);
            logQuotes(quotes1);

            // Similarity Search + Metadata author (hybrid search, meta data filtering)
            List<String> quotes2 =  findQuotesWithAuthor(v_table, "We struggle all our life for nothing", 2, "nietzsche");
            logQuotes(quotes2);

            // Similarity Search + Metadata tags (hybrid search, meta data filtering)
            List<String> quotes3 =  findQuotesWithATags(v_table, "We struggle all our life for nothing", 2, "politics");
            logQuotes(quotes3);

            // Cut Results providing a threshold for the similarity
            List<String> quotes4 =  findQuotesWithThreshold(v_table, "Animals are our equals", 8, 0.8);
            logQuotes(quotes4);

            List<String> generatedQuotes = generateQuotes(v_table, "politics and virtue", 2, "nietzsche");
            logQuotes(generatedQuotes);

            

        }
        catch (Exception e) {
            log.error("An Error occurred", e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Generate Query based on a retrieval.
     *
     * @param v_table
     *      current table
     * @param topic
     *      current topic
     * @param author
     *      current author
     * param n
     *      number of generated quotes
     * @return
     *      generated
     */
    private static List<String> generateQuotes(MetadataVectorCassandraTable v_table, String topic, int n, String author) {
        log.info("Generate Quotes");
        String promptTemplate =
                "Generate a single short philosophical quote on the given topic,\n" +
                "similar in spirit and form to the provided actual example quotes.\n" +
                "Do not exceed 20-30 words in your quote.\n" +
                "REFERENCE TOPIC: \n{topic}" +
                "\nACTUAL EXAMPLES:\n{examples}"
                        .replace("{topic}", topic)
                        .replace("{examples}", String.join(",", findQuotesWithAuthor(v_table, topic, 4, author)));

        ChatCompletionRequest req = ChatCompletionRequest.builder()
                .model(LLM_MODEL_CHAT_COMPLETION)
                .messages(Collections.singletonList(new ChatMessage("user", promptTemplate)))
                .temperature(0.7)
                .maxTokens(320)
                .n(n)
                .build();

        return getOpenAI().createChatCompletion(req)
                .getChoices().stream()
                .map(ChatCompletionChoice::getMessage)
                .map(ChatMessage::getContent)
                .collect(Collectors.toList());
    }

    /**
     * Load quotes into the Vector Store
     * Insert quotes into vector store.
     * <p>
     * You will compute the embeddings for the quotes and save them into the Vector Store,
     * along with the text itself and the metadata planned for later use. Note that the author is added
     * as a metadata field along with the "tags" already found with the quote itself.
     * </p>
     * To optimize speed and reduce the calls, you'll perform batched calls to the embedding OpenAI service,
     * with one batch per author.
     *
     * @param v_table
     *      table in use
     */
    private static void loadQuotes(MetadataVectorCassandraTable v_table)
    throws IOException {
        log.info("Loading Dataset");
        loadQuotes(DATASET).forEach((author, quoteList) -> {
            log.info("Processing '{}' :", author);
            AtomicInteger quote_idx = new AtomicInteger(0);
            quoteList.stream()
                 .map(quote -> mapQuote(quote_idx, author, quote))
                 .forEach(v_table::put);
            System.out.println();
            log.info(" Done (inserted " + quote_idx.get() + " quotes).");
        });
        log.info("Finished inserting.");
    }

    /**
     * A test call for embeddings.
     * Quickly check how one can get the embedding vectors for a list of input texts.
     *
     * @param textFragments
     *      tex fragments to be tested
     */
    private static void testCallEmbedded(String... textFragments) {
        log.info("A test call for embeddings");
        List<Embedding> result = getOpenAI().createEmbeddings(EmbeddingRequest.builder()
              .model(LLM_MODEL_EMBEDDINGS)
              .input(Arrays.asList(textFragments))
              .build()).getData();
        log.info("len(result.data)={}", result.size());
        log.info("result.data[0].embedding={}", result.get(0).getEmbedding().subList(0,50));
        log.info("len(result.data[0].embedding)={}", result.get(0).getEmbedding().size());
    }

    /**
     * Search for quotes
     * You'll now search for quotes using the vector store. You'll use the ann_search method
     * to find the closest quotes to a given query. You'll use the author as a filter to
     * restrict the search to a given author.
     *
     * @param vTable
     *      current table
     * @param  query
     *      similarity query
     * @param recordCount
     *      record count
     */
    private static List<String> findQuotes(MetadataVectorCassandraTable vTable, String query, int recordCount) {
        log.info("Search for quotes:");
        return findQuotesDetailed(vTable, query, null, recordCount, null, null);
    }

    private static void logQuotes(List<String> quotes) {
        if (quotes != null) {
            quotes.stream().forEach(System.out::println);
        }
    }

    /**
     * The vector similarity search generally returns the vectors that are closest to the query, even if that
     *  means results that might be somewhat irrelevant if there's nothing better.
     * To keep this issue under control, you can get the actual "distance" between the query and each result,
     * and then set a cutoff on it, effectively discarding results that are beyond that threshold. Tuning
     * this threshold correctly is not an easy problem: here, we'll just show you the way.
     * To get a feeling on how this works, try the following query and play with the choice of quote and
     * threshold to compare the results.
     * Note (for the mathematically inclined): this "distance" is exactly the cosine difference between the
     * vectors, i.e. the scalar product divided by the product of the norms of the two vectors. As such,
     * it is a number ranging from -1 to +1. Elsewhere (e.g. in the "CQL" version of this example) you
     * will see this quantity rescaled to fit the [0, 1] interval, which means the numerical values and
     * adequate thresholds will be slightly different.
     *
     * @param vTable
     * @param query
     * @param recordCount
     * @param threshold
     */
    private static List<String> findQuotesWithThreshold(MetadataVectorCassandraTable vTable, String query,int recordCount, double threshold) {
        log.info(" Cutting out irrelevant results:");
        return findQuotesDetailed(vTable, query, threshold, recordCount, (String) null, (String[]) null);
    }

    private static List<String> findQuotesWithAuthor(MetadataVectorCassandraTable vTable, String query, int recordCount, String author) {
        log.info("Search restricted to an author:");
        return findQuotesDetailed(vTable, query, null, recordCount, author, (String[])  null);
    }

    private static List<String> findQuotesWithATags(MetadataVectorCassandraTable vTable, String query, int recordCount, String... tags) {
        log.info("Search constrained to a tag (out of those saved earlier with the quotes");
       return findQuotesDetailed(vTable, query, null, recordCount, null, tags);
    }

    private static List<String> findQuotesDetailed(MetadataVectorCassandraTable vTable, String query, Double threshold, int recordCount, String author, String... tags) {
        List<Float> vector = getOpenAI()
                // Invoke OpenAi API
                .createEmbeddings(EmbeddingRequest.builder()
                .model(LLM_MODEL_EMBEDDINGS)
                .input(Collections.singletonList(query))
                .build()).getData().get(0)
                // Mapping as a List<Float>
                .getEmbedding().stream()
                .map(Double::floatValue)
                .collect(Collectors.toList());

        // Build the query
        SimilaritySearchQuery.SimilaritySearchQueryBuilder queryBuilder =
                SimilaritySearchQuery.builder()
                .distance(SimilarityMetric.DOT_PRODUCT)
                .recordCount(recordCount)
                .embeddings(vector);
        if (threshold != null) {
            queryBuilder.threshold(threshold);
        }
        Map<String, String> metaData = new LinkedHashMap<>();
        if (author != null) {
            metaData.put("author", author);
        }
        if (tags != null) {
            for (String tag : tags) {
                metaData.put(tag, "true");
            }
        }
        if (!metaData.isEmpty()) {
            queryBuilder.metaData(metaData);
        }

        return vTable.similaritySearch(queryBuilder.build())
                     .stream()
                     .map(SimilaritySearchResult::getEmbedded)
                     .map(MetadataVectorCassandraTable.Record::getBody)
                     .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    private static LinkedHashMap<String, List<?>> loadQuotes(String filePath) throws IOException {
        LinkedHashMap<String, Object> philo_quotes = new ObjectMapper().readValue(new File(filePath), LinkedHashMap.class);
        System.out.println("Quotes by Author:");
        ((LinkedHashMap<?,?>) philo_quotes.get("quotes")).forEach((k,v) -> {
            System.out.println("   " + k + " (" + ((ArrayList<?>)v).size() + ") ");
        });
        log.info("Sample Quotes");
        ((LinkedHashMap<?, ?>) philo_quotes.get("quotes"))
                .entrySet().stream().limit(2)
                .forEach(e -> {
                    System.out.println("   " + e.getKey() + " : ");
                    Map<String, Object> entry = (Map<String,Object>) ((ArrayList<?>)e.getValue()).get(0);
                    System.out.println("      " + ((String) entry.get("body")).substring(0, 50) + "... (tags: " + entry.get("tags") + ")");
                    entry = (Map<String,Object>) ((ArrayList<?>)e.getValue()).get(1);
                    System.out.println("      " + ((String) entry.get("body")).substring(0, 50) + "... (tags: " + entry.get("tags") + ")");
                });
        return  ((LinkedHashMap<String, List<?>>) philo_quotes.get("quotes"));
    }

    @SuppressWarnings("unchecked")
    private static MetadataVectorCassandraTable.Record mapQuote(AtomicInteger quote_idx, String author, Object q) {
        MetadataVectorCassandraTable.Record record = new MetadataVectorCassandraTable.Record();
        Map<String, Object> quote = (Map<String, Object>) q;
        String body = (String) quote.get("body");
        record.setBody(body);
        record.getMetadata().put("author", author);
        ((ArrayList<String>) quote.get("tags"))
                .forEach(tag -> record.getMetadata().put((String) tag, "true"));
        record.setVector(openai.createEmbeddings(EmbeddingRequest
                        .builder()
                        .model(LLM_MODEL_EMBEDDINGS)
                        .input(Collections.singletonList(body))
                        .build())
                .getData().get(0)
                .getEmbedding().stream()
                .map(Double::floatValue)
                .collect(Collectors.toList()));
        record.setRowId("q_" + author + "_" + quote_idx.getAndIncrement());
        System.out.print("◾");
        return record;
    }

    private MetadataVectorCassandraTable.Record mapEntry(Map.Entry<?, ?> entry) {
        String author = (String) entry.getKey();
        log.info(author + " :");
        AtomicInteger quote_idx = new AtomicInteger(0);
        ((ArrayList<?>) entry.getValue()).forEach(q -> {
                    MetadataVectorCassandraTable.Record record = new MetadataVectorCassandraTable.Record();
                    Map<String, Object> quote = (Map<String, Object>) q;
                    String body = (String) quote.get("body");
                    record.setBody(body);
                    record.getMetadata().put("author", author);
                    ((ArrayList<String>) quote.get("tags"))
                            .forEach(tag -> record.getMetadata().put((String) tag, "true"));
                    record.setVector(openai.createEmbeddings(EmbeddingRequest
                                    .builder()
                                    .model(LLM_MODEL_EMBEDDINGS)
                                    .input(Collections.singletonList(body))
                                    .build())
                            .getData().get(0)
                            .getEmbedding().stream()
                            .map(Double::floatValue)
                            .collect(Collectors.toList()));
                    record.setRowId("q_" + author + "_" + quote_idx.getAndIncrement());
        });
        return null;
    }

    /**
     * Creation of the DB connection.
     * This is how you create a connection to Astra DB. You leverage the SDK that
     * would download missing pieces (secure bundle).
     *
     * @return
     *      a connection to AstraDB
     */
    private static CqlSession getCqlSession() {
        if (astraClient == null) {
             astraClient = AstraClient.builder()
                    .withToken(ASTRA_DB_APPLICATION_TOKEN)
                    .withCqlKeyspace(ASTRA_DB_KEYSPACE)
                    .withDatabaseId(ASTRA_DB_ID)
                    .withDatabaseRegion(ASTRA_DB_REGION)
                    .enableCql()
                    .enableDownloadSecureConnectBundle()
                    .build();
             log.info("Connected to Astra.");
        }
        return astraClient.cqlSession();
    }

    /**
     * Connect to OpenAI.
     *
     * @return
     *      a connection to OpenAI
     */
    private static OpenAiService getOpenAI() {
        if (openai ==null) {
            openai = new OpenAiService(OPENAI_API_KEY);
        }
        return openai;
    }

}
