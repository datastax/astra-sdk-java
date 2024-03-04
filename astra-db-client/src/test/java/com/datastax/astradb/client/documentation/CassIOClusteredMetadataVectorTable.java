package com.datastax.astradb.client.documentation;

import com.datastax.astradb.client.AstraDBAdmin;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.astradb.client.cassio.AnnQuery;
import com.datastax.astradb.client.cassio.CassIO;
import com.datastax.astradb.client.cassio.ClusteredMetadataVectorRecord;
import com.datastax.astradb.client.cassio.ClusteredMetadataVectorTable;
import com.dtsx.astra.sdk.utils.TestUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.datastax.astradb.client.cassio.AbstractCassandraTable.PARTITION_ID;

public class CassIOClusteredMetadataVectorTable {
    public static void main(String[] args) {

        // Create db if not exists
        UUID databaseId = new AstraDBAdmin("TOKEN")
                .createDatabase("database");

        // Initializing CqlSession
        try (CqlSession cqlSession = CassIO.init("TOKEN",
                databaseId, TestUtils.TEST_REGION,
                AstraDBAdmin.DEFAULT_KEYSPACE)) {

            // Initializing table with the dimension
            ClusteredMetadataVectorTable vector_Store = CassIO
                    .clusteredMetadataVectorTable("vector_store", 1536);
            vector_Store.create();

            // Insert Vectors
            String partitionId = UUID.randomUUID().toString();
            ClusteredMetadataVectorRecord record = new ClusteredMetadataVectorRecord();
            record.setVector(List.of(0.1f, 0.2f, 0.3f, 0.4f));
            record.setMetadata(Map.of("key", "value"));
            record.setPartitionId(partitionId);
            record.setBody("Sample text fragment");
            record.setAttributes("handy field for special attributes");
            vector_Store.put(record);

            // Semantic Search
            AnnQuery query = AnnQuery
                    .builder()
                   .embeddings(List.of(0.1f, 0.2f, 0.3f, 0.4f))
                   .metaData(Map.of(PARTITION_ID, partitionId))
                   .build();

            vector_Store.similaritySearch(query).forEach(result -> {
                System.out.println("Similarity : " + result.getSimilarity());
                System.out.println("Record : " + result.getEmbedded().getBody());
            });
        }
    }
}
