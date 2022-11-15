package com.datastax.stargate.sdk.test.local;

import com.datastax.stargate.sdk.core.Ordering;
import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.test.ApiDataRecordTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDataRecordStargateTest extends ApiDataRecordTest {
     
    /**
     * Init
     * @throws InterruptedException 
     */
    @BeforeAll
    public static void init() 
    throws InterruptedException {
        stargateClient  = new ApiDataClient();
        workingKeyspace = stargateClient.keyspace(TEST_KEYSPACE);
        videoTable      = workingKeyspace.table(TEST_TABLE);
        if (!workingKeyspace.exist()) {
            workingKeyspace.createSimple(1);
        }
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
            Thread.sleep(500);
        }
    }

}
