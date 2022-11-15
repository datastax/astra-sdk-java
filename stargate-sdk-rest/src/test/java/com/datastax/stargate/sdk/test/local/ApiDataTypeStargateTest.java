package com.datastax.stargate.sdk.test.local;

import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.test.ApiDataTypeTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDataTypeStargateTest extends ApiDataTypeTest {
     
    /**
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateClient = new ApiDataClient();
        ksClient = stargateClient.keyspace(TEST_KEYSPACE);
        if (!ksClient.exist()) {
            ksClient.createSimple(1);
        }
    }

}
