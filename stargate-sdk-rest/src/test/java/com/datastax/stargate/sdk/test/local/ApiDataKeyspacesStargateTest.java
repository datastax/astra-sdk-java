package com.datastax.stargate.sdk.test.local;

import com.datastax.stargate.sdk.rest.ApiDataClient;
import com.datastax.stargate.sdk.test.ApiDataKeyspacesTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Implementations of test for Data keyspace.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDataKeyspacesStargateTest extends ApiDataKeyspacesTest {
    
    /*
     * Init
     */
    @BeforeAll
    public static void createDefaultKeyspaces() {
        if (stargateClient.keyspace(TEST_KEYSPACE).exist()) {
            stargateClient.keyspace(TEST_KEYSPACE).delete();
        }
        if (stargateClient.keyspace(TEST_KEYSPACE_BIS).exist()) {
            stargateClient.keyspace(TEST_KEYSPACE_BIS).delete();
        }
     }

}
