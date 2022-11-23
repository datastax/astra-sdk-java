package io.stargate.test.rest;

import io.stargate.sdk.StargateClient;
import io.stargate.sdk.rest.KeyspaceClient;
import io.stargate.sdk.test.rest.AbstractRestClientKeyspacesTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Implementations of test for Data keyspace.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class RestClientKeyspacesStargateClientTest extends AbstractRestClientKeyspacesTest {

    @BeforeAll
    public static void initStargateRestApiClient() {
        // Initialization through stargate client
        stargateRestApiClient = new StargateClient().apiRest();

        // PreRequisites
        KeyspaceClient ksClientTest    = stargateRestApiClient.keyspace(TEST_KEYSPACE);
        if (ksClientTest.exist()) ksClientTest.delete();
        KeyspaceClient ksClientTestBis = stargateRestApiClient.keyspace(TEST_KEYSPACE_BIS);
        if (ksClientTestBis.exist()) ksClientTestBis.delete();
    }


}
