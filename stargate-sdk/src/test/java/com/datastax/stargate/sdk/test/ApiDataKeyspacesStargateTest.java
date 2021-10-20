package com.datastax.stargate.sdk.test;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.rest.test.ApiDataKeyspacesTest;

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
    public static void init() {
        stargateClient = ApiStargateTestFactory.createStargateClient();
        if (stargateClient.apiRest().keyspace(TEST_KEYSPACE).exist()) {
            stargateClient.apiRest().keyspace(TEST_KEYSPACE).delete();
        }
        if (stargateClient.apiRest().keyspace(TEST_KEYSPACE_BIS).exist()) {
            stargateClient.apiRest().keyspace(TEST_KEYSPACE_BIS).delete();
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
