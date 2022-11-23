package io.stargate.test.rest;

import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.test.rest.AbstractRestClientTableTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests against collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RestClientTableDirectTest extends AbstractRestClientTableTest {
     
    /**
     * Init
     */
    @BeforeAll
    public static void init() {

        // Initializations
        stargateRestApiClient = new StargateRestApiClient();
        ksClient = stargateRestApiClient.keyspace(TEST_KEYSPACE);

        // Prerequisites
        if (!ksClient.exist()) ksClient.createSimple(1);
    }
}
