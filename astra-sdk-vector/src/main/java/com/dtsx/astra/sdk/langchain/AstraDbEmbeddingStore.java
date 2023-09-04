package com.dtsx.astra.sdk.langchain;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Represents a <a href="https://www.pinecone.io/">Pinecone</a> index as an embedding store.
 * Current implementation assumes the index uses the cosine distance metric.
 * To use PineconeEmbeddingStore, please add the "langchain4j-pinecone" dependency to your project.
 */
@Slf4j
public class AstraDbEmbeddingStore implements EmbeddingStore<TextSegment> {

    /** Concrete Implementation if class is available. */
    private final EmbeddingStore<TextSegment> implementation;

    /**
     * Constructor with default table name.
     *
     * @param token        token
     * @param dbId         database identifier
     * @param dbRegion     database region
     * @param keyspaceName keyspace name
     * @param tableName    table name
     * @param dimension    vector dimension
     */
    @SuppressWarnings("unchecked")
    public AstraDbEmbeddingStore(@NonNull String token,
                                 @NonNull String dbId,
                                 @NonNull String dbRegion,
                                 @NonNull String keyspaceName,
                                 @NonNull String tableName,
                                 @NonNull Integer dimension) {
        try {
            implementation = (EmbeddingStore<TextSegment>) Class
                    .forName("com.dtsx.astra.sdk.langchain.AstraDbEmbeddingStoreImpl")
                    .getConstructor(String.class, String.class, String.class, String.class, String.class, Integer.class)
                    .newInstance(token, dbId, dbRegion, keyspaceName, tableName, dimension);

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getMessage() {
        return "To use AstraDbEmbeddingStore, please add the following dependency to your project:\n\n"
                + "Maven:\n"
                + "<dependency>\n" +
                "    <groupId>dev.langchain4j</groupId>\n" +
                "    <artifactId>langchain4j-astra</artifactId>\n" +
                "    <version>0.21.0</version>\n" +
                "</dependency>\n\n"
                + "Gradle:\n"
                + "implementation 'dev.langchain4j:langchain4j-astra:0.21.0'\n";
    }

    @Override
    public String add(Embedding embedding) {
        return implementation.add(embedding);
    }

    @Override
    public void add(String id, Embedding embedding) {
        implementation.add(id, embedding);
    }

    @Override
    public String add(Embedding embedding, TextSegment textSegment) {
        return implementation.add(embedding, textSegment);
    }

    /**
     * Add a list of embeddings to the store.
     *
     * @param embeddings
     *      list of embeddings
     * @return
     *      list of ids
     */
    @Override
    public List<String> addAll(List<Embedding> embeddings) {
        return implementation.addAll(embeddings);
    }

    /**
     * Add a list of embeddings to the store.
     *
     * @param embeddings
     *      list of embeddings
     * @param textSegments
     *      list of text segments
     * @return
     *      list of ids
     */
    @Override
    public List<String> addAll(List<Embedding> embeddings, List<TextSegment> textSegments) {
        return implementation.addAll(embeddings, textSegments);
    }

    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(Embedding referenceEmbedding, int maxResults) {
        return implementation.findRelevant(referenceEmbedding, maxResults);
    }

    @Override
    public List<EmbeddingMatch<TextSegment>> findRelevant(Embedding referenceEmbedding, int maxResults, double minScore) {
        return implementation.findRelevant(referenceEmbedding, maxResults, minScore);
    }

    /**
     * Builder for AstraDbEmbeddingStore.
     *
     * @return
     *      current builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String token;
        private String databaseId;
        private String databaseRegion;
        private String keyspaceName;
        private String tableName;
        private Integer vectorDimension;

        public AstraDbEmbeddingStore.Builder token(@NonNull  String token) {
            this.token = token;
            return this;
        }

        public AstraDbEmbeddingStore.Builder database(@NonNull  String databaseId, @NonNull  String databaseRegion) {
            this.databaseId = databaseId;
            this.databaseRegion = databaseRegion;
            return this;
        }

        public AstraDbEmbeddingStore.Builder table(@NonNull String keyspaceName, @NonNull  String tableName) {
            this.keyspaceName = keyspaceName;
            this.tableName = tableName;
            return this;
        }

        public AstraDbEmbeddingStore.Builder vectorDimension(@NonNull Integer dimension) {
            this.vectorDimension = dimension;
            return this;
        }

        public AstraDbEmbeddingStore build() {
            return new AstraDbEmbeddingStore(token, databaseId, databaseRegion, keyspaceName, tableName, vectorDimension);
        }
    }

}
