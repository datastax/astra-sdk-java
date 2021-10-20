package com.datastax.stargate.sdk.test;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.rest.domain.CreateTable;
import com.datastax.stargate.sdk.rest.domain.Ordering;
import com.datastax.stargate.sdk.rest.test.ApiDataRecordTest;

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
        stargateClient  = ApiStargateTestFactory.createStargateClient();
        workingKeyspace = stargateClient.apiRest().keyspace(TEST_KEYSPACE);
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
    
    /**
     * Close connections when ending
     */
    @AfterClass
    public static void closing() {
        if (stargateClient != null) {
            stargateClient.close();
        }
    }

}
