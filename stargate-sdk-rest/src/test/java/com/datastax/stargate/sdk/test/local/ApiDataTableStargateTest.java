package com.datastax.stargate.sdk.test.local;

import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.test.ApiDataTableTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests against collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDataTableStargateTest extends ApiDataTableTest {
     
    /**
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateClient = new ApiDataClient();
        ksClient = stargateClient.keyspace(TEST_KEYSPACE);
        // We need the namespace
        if (!ksClient.exist()) {
            ksClient.createSimple(1);
        }
    }
}
