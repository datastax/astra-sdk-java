package com.dtsx.astra.sdk.cassio;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.internal.ValidationUtils;
import dev.langchain4j.store.embedding.CosineSimilarity;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.RelevanceScore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

/**
 * Implementation of {@link EmbeddingStore} using Cassandra.
 *
 * @see EmbeddingStore
 */
public class ClusteredMetadataVectorStore implements EmbeddingStore<TextSegment> {

    /** Represents an embedding table in Cassandra, it is a table with a vector column. */
    protected ClusteredMetadataVectorTable embeddingTable;

    /**
     * Embedding Store.
     *
     * @param table
     *      Cassandra Table
     */
    public ClusteredMetadataVectorStore(ClusteredMetadataVectorTable table) {
        this.embeddingTable = table;
    }

    /**
     * Delete the table.
     */
    public void delete() {
        embeddingTable.delete();
    }

    /**
     * Delete all rows.
     */
    public void clear() {
        embeddingTable.clear();
    }

    /**
     * Add a new embedding to the store.
     * - the row id is generated
     * - text and metadata are not stored
     *
     * @param embedding representation of the list of floats
     * @return newly created row id
     */
    @Override
    public String add(@NonNull Embedding embedding) {
        return add(embedding, null);
    }

    /**
     * Add a new embedding to the store.
     * - the row id is generated
     * - text and metadata coming from the text Segment
     *
     * @param embedding   representation of the list of floats
     * @param textSegment text content and metadata
     * @return newly created row id
     */
    @Override
    public String add(@NonNull Embedding embedding, TextSegment textSegment) {
        ClusteredMetadataVectorRecord record = new ClusteredMetadataVectorRecord();
        record.setVector(embedding.vectorAsList());
        record.setPartitionId("default");
        record.setRowId(Uuids.timeBased());
        if (textSegment != null) {
            record.setBody(textSegment.text());
            Map<String, String> metaData = textSegment.metadata().asMap();
            if (metaData != null && !metaData.isEmpty()) {
                if (metaData.containsKey(ClusteredMetadataVectorTable.PARTITION_ID)) {
                    record.setPartitionId(metaData.get(ClusteredMetadataVectorTable.PARTITION_ID));
                    metaData.remove(ClusteredMetadataVectorTable.PARTITION_ID);
                }
                record.setMetadata(metaData);
            }
        }
        embeddingTable.save(record);
        return record.getRowId().toString();
    }

    /**
     * Add a new embedding to the store.
     *
     * @param rowId     the row id
     * @param embedding representation of the list of floats
     */
    @Override
    public void add(@NonNull String rowId, @NonNull Embedding embedding) {
        ClusteredMetadataVectorRecord record = new ClusteredMetadataVectorRecord();
        record.setVector(embedding.vectorAsList());
        record.setPartitionId("default");
        record.setRowId(UUID.fromString(rowId));
        embeddingTable.save(record);
    }

    /**
     * They will all be added in the same partition.
     *
     * @param embeddingList embeddings list
     * @return list of new row if (same order as the input)
     */
    @Override
    public List<String> addAll(List<Embedding> embeddingList) {
        return embeddingList.stream()
                .map(Embedding::vectorAsList)
                .map(ClusteredMetadataVectorRecord::new)
                .peek(embeddingTable::save)
                .map(ClusteredMetadataVectorRecord::getRowId)
                .map(UUID::toString)
                .collect(toList());
    }

    /**
     * Add multiple embeddings as a single action.
     *
     * @param embeddingList   embeddings
     * @param textSegmentList text segments
     * @return list of new row if (same order as the input)
     */
    @Override
    public List<String> addAll(List<Embedding> embeddingList, List<TextSegment> textSegmentList) {
        if (embeddingList == null || textSegmentList == null || embeddingList.size() != textSegmentList.size()) {
            throw new IllegalArgumentException("embeddingList and textSegmentList must not be null and have the same size");
        }
        // Looping on both list with an index
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < embeddingList.size(); i++) {
            ids.add(add(embeddingList.get(i), textSegmentList.get(i)));
        }
        return ids;
    }

    /**
     * Search for relevant.
     *
     * @param embedding  current embeddings
     * @param maxResults max number of result
     * @param minScore   threshold
     * @return list of matching elements
     */
    public List<EmbeddingMatch<TextSegment>> findRelevant(Embedding embedding, int maxResults, double minScore) {
        return findRelevant(embedding, maxResults, minScore, null);
    }

    /**
     * Similarity Search ANN based on the embedding.
     *
     * @param embedding  vector
     * @param maxResults max number of results
     * @param minScore   score minScore
     * @param metadata   map key-value to build a metadata filter
     * @return list of matching results
     */
    public List<EmbeddingMatch<TextSegment>> findRelevant(Embedding embedding, int maxResults, double minScore, Metadata metadata) {
        AnnQuery.AnnQueryBuilder builder = AnnQuery.builder()
                .embeddings(embedding.vectorAsList())
                .metric(CassandraSimilarityMetric.COSINE)
                .recordCount(ValidationUtils.ensureGreaterThanZero(maxResults, "maxResults"))
                .threshold(CosineSimilarity.fromRelevanceScore(ValidationUtils.ensureBetween(minScore, 0, 1, "minScore")));
        if (metadata != null) {
            builder.metaData(metadata.asMap());
        }
        return embeddingTable
                .similaritySearch(builder.build())
                .stream()
                .map(this::mapSearchResult)
                .collect(toList());
    }

    /**
     * Map Search result coming from Astra.
     *
     * @param record current record
     * @return search result
     */
    private EmbeddingMatch<TextSegment> mapSearchResult(AnnResult<ClusteredMetadataVectorRecord> record) {

        TextSegment embedded = null;
        String body = record.getEmbedded().getBody();
        if (body != null
                && !body.isEmpty()
                && record.getEmbedded().getMetadata() != null) {
            embedded = TextSegment.from(record.getEmbedded().getBody(),
                    new Metadata(record.getEmbedded().getMetadata()));
        }
        return new EmbeddingMatch<>(
                // Score
                RelevanceScore.fromCosineSimilarity(record.getSimilarity()),
                // EmbeddingId : unique identifier
                record.getEmbedded().getRowId().toString(),
                // Embeddings vector
                Embedding.from(record.getEmbedded().getVector()),
                // Text segment and metadata
                embedded);
    }
}
