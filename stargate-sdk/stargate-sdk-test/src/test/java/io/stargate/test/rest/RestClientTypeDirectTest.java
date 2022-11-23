package io.stargate.test.rest;

import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.test.rest.AbstractRestClientTypeTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests against collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RestClientTypeDirectTest extends AbstractRestClientTypeTest {

    @BeforeAll
    public static void initStargateRestApiClient() {
        // Initialization
        stargateRestApiClient = new StargateRestApiClient();

        // Prerequisites = 'java' keyspace must exist
        ksClient = stargateRestApiClient.keyspace(TEST_KEYSPACE);
        Assertions.assertNotNull(ksClient);
        if (!ksClient.exist()) ksClient.createSimple(1);
        Assertions.assertTrue(ksClient.exist());


    }

}
