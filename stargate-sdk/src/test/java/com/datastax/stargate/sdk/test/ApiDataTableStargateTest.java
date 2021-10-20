package com.datastax.stargate.sdk.test;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.rest.test.ApiDataTableTest;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDataTableStargateTest extends ApiDataTableTest {
     
    /**
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateClient = ApiStargateTestFactory.createStargateClient();
        ksClient = stargateClient.apiRest().keyspace(TEST_KEYSPACE);
        // We need the namespace
        if (!ksClient.exist()) {
            ksClient.createSimple(1);
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
