package io.stargate.test.rest;

import io.stargate.sdk.core.Ordering;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.rest.domain.CreateTable;
import io.stargate.sdk.test.rest.AbstractRestClientRecordTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RestClientRecordDirectTest extends AbstractRestClientRecordTest {
     
    /**
     * Init
     * @throws InterruptedException 
     */
    @BeforeAll
    public static void init() 
    throws InterruptedException {
        // Initializations
        stargateRestApiClient = new StargateRestApiClient();
        workingKeyspace = stargateRestApiClient.keyspace(TEST_KEYSPACE);
        videoTable      = workingKeyspace.table(TEST_TABLE);

        // Creation if needed
        if (!workingKeyspace.exist()) workingKeyspace.createSimple(1);

        if (!videoTable.exist()) {
            videoTable.create(CreateTable.builder()
                       .ifNotExist(true)
                       .addPartitionKey("genre", "text")
                       .addClusteringKey("year", "int", Ordering.DESC)
                       .addClusteringKey("title", "text", Ordering.ASC)
                       .addColumn("upload", "timestamp")
                       .addColumn("tags", "set<text>")
                       .addColumn("frames", "list<int>")
                       .addColumn("tuples", "tuple<text,text,text>")
                       .addColumn("formats", "frozen<map <text,text>>")
                       .build());
            // Wait for the async op to complete.
            Thread.sleep(500);
        }
    }

}
