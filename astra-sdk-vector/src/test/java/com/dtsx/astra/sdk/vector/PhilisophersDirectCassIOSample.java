package com.dtsx.astra.sdk.vector;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.embedding.Embedding;
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
public class PhilisophersDirectCassIOSample {

    /**
     * Settings from Astra Usage
     */
    public static final String  ASTRA_DB_APPLICATION_TOKEN = System.getenv("ASTRA_DB_APPLICATION_TOKEN");
    public static final String ASTRA_DB_ID = "20d1ccb8-44da-491f-bc9d-8ce8bd33a343";
    public static final String ASTRA_DB_REGION = "us-east1";
    public static final String ASTRA_DB_KEYSPACE = "javaml";

    /**
     * Settings from OpenAI Usage
     */
    public static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
    public static final String LLM_MODEL = "text-embedding-ada-002";
    public static final int LLM_MODEL_DIMENSION = 1536;
    public static final String DATASET = "/Users/cedricklunven/dev/datastax/sdk/astra-sdk-java/" +
            "astra-sdk-vector/src/main/resources/philo_quotes.json";

    public static void main(String[] args) {


        // Creation of the DB connection
        try(CqlSession cqlSession = getCqlSession()) {

            /* ---------
             * Creating the destinations table suited for the model to use.
             * ---------
             * Here the dimension come from the LLM_MODEL
             */
            MetadataVectorCassandraTable v_table = new MetadataVectorCassandraTable(
                    cqlSession, ASTRA_DB_KEYSPACE,"philosophers", LLM_MODEL_DIMENSION);
            v_table.createIfNotExist();

            /*
             * ---------
             * A test call for embeddings
             * ---------
             * Quickly check how one can get the embedding vectors for a list of input texts:
            */
            log.info("A test call for embeddings");
            List<Embedding> result = createEmbeddings("This is a sentence","A second sentence");
            System.out.printf("len(result.data)=%s%n", result.size());
            System.out.printf("result.data[0].embedding=%s%n", result.get(0).getEmbedding().subList(0,50));
            System.out.printf("len(result.data[0].embedding)=%s%n", result.get(0).getEmbedding().size());

            /*
             * ---------
             * Load quotes into the Vector Store
             * Insert quotes into vector store
             * ---------
             * You will compute the embeddings for the quotes and save them into the Vector Store,
             * along with the text itself and the metadata planned for later use. Note that the author is added
             * as a metadata field along with the "tags" already found with the quote itself.
             *
             * To optimize speed and reduce the calls, you'll perform batched calls to the embedding OpenAI service,
             * with one batch per author.
             */
            log.info("Loading Dataset");
            loadQuotes(DATASET).forEach((author, quoteList) -> {
                log.info("Processing {} :", author);
                AtomicInteger quote_idx = new AtomicInteger(0);
                quoteList.stream()
                     .map(quote -> mapQuote(quote_idx, author, quote))
                     .forEach(v_table::put);
                System.out.println();
                log.info(" Done (inserted " + quote_idx.get() + " quotes).");
            });
            log.info("Finished inserting.");

            /*
             * ---------
             * Search for quotes
             * ---------
             * You'll now search for quotes using the vector store. You'll use the ann_search method
             * to find the closest quotes to a given query. You'll use the author as a filter to
             * restrict the search to a given author.
             */
            List<String> quotes = findQuoteAndAuthor(v_table, "We struggle all our life for nothing", 3);
            quotes.stream().forEach(System.out::println);

        }
        catch (IOException e) {
            log.error("An Error occured", e);
            throw new RuntimeException(e);
        }
    }

    private static AstraClient astraClient;
    private static OpenAiService openai;

    private static List<String> findQuoteAndAuthor(MetadataVectorCassandraTable vTable, String query, int recordCount) {
        return findQuoteAndAuthor(vTable, query, recordCount, null, null);
    }

    private static List<String> findQuoteAndAuthor(MetadataVectorCassandraTable vTable, String query, int recordCount, String author, String... tags) {
        List<Float> vector = createEmbeddings(query).get(0).getEmbedding().stream()
                .map(Double::floatValue)
                .collect(Collectors.toList());
        return vTable.ann_search(vector, 3, null)
                .stream().map(MetadataVectorCassandraRecord::getBody)
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
    private static MetadataVectorCassandraRecord mapQuote(AtomicInteger quote_idx, String author, Object q) {
        MetadataVectorCassandraRecord record = new MetadataVectorCassandraRecord();
        Map<String, Object> quote = (Map<String, Object>) q;
        String body = (String) quote.get("body");
        record.setBody(body);
        record.getMetadata().put("author", author);
        ((ArrayList<String>) quote.get("tags"))
                .forEach(tag -> record.getMetadata().put((String) tag, "true"));
        record.setVector(openai.createEmbeddings(EmbeddingRequest
                        .builder()
                        .model(LLM_MODEL)
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

    private MetadataVectorCassandraRecord mapEntry(Map.Entry<?, ?> entry) {
        String author = (String) entry.getKey();
        log.info(author + " :");
        AtomicInteger quote_idx = new AtomicInteger(0);
        ((ArrayList<?>) entry.getValue()).forEach(q -> {
                    MetadataVectorCassandraRecord record = new MetadataVectorCassandraRecord();
                    Map<String, Object> quote = (Map<String, Object>) q;
                    String body = (String) quote.get("body");
                    record.setBody(body);
                    record.getMetadata().put("author", author);
                    ((ArrayList<String>) quote.get("tags"))
                            .forEach(tag -> record.getMetadata().put((String) tag, "true"));
                    record.setVector(openai.createEmbeddings(EmbeddingRequest
                                    .builder()
                                    .model(LLM_MODEL)
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
     * Syntax sugar to compute embeddings with OpenAI.
     *
     * @param inputs
     *      text to convert
     * @return
     *      the embeddings as computed by oepenAI
     */
    private static List<Embedding> createEmbeddings(String... inputs) {
        EmbeddingResult result = getOpenAI()
                        .createEmbeddings(EmbeddingRequest.builder()
                        .model(LLM_MODEL)
                        .input(Arrays.asList(inputs))
                        .build());
        return result.getData();
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
