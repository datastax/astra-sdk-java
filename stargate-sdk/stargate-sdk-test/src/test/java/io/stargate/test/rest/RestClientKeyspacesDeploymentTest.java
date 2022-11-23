package io.stargate.test.rest;

import io.stargate.sdk.ServiceDatacenter;
import io.stargate.sdk.ServiceDeployment;
import io.stargate.sdk.api.TokenProvider;
import io.stargate.sdk.http.ServiceHttp;
import io.stargate.sdk.http.auth.TokenProviderHttpAuth;
import io.stargate.sdk.rest.KeyspaceClient;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.test.rest.AbstractRestClientKeyspacesTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Implementations of test for Data keyspace.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class RestClientKeyspacesDeploymentTest extends AbstractRestClientKeyspacesTest {

    @BeforeAll
    public static void initStargateRestApiClient() {

        // 2 Nodes (even if same)
        String endpoint  = "http://localhost:8082";
        String health    = "http://localhost:8082/stargate/health";
        ServiceHttp s1 = new ServiceHttp("dc1-n1", endpoint, health);
        ServiceHttp s2 = new ServiceHttp("dc1-n2", endpoint, health);

        // One DC (with Auth endpoint), 2 nodess
        TokenProvider auth = new TokenProviderHttpAuth("cassandra", "cassandra", "http://localhost:8081");
        ServiceDatacenter<ServiceHttp> dc1 = new ServiceDatacenter<ServiceHttp>("dc1", auth, s1, s2);

        // Initialization
        stargateRestApiClient = new StargateRestApiClient(new ServiceDeployment<ServiceHttp>(dc1));

        // PreRequisites
        KeyspaceClient ksClientTest    = stargateRestApiClient.keyspace(TEST_KEYSPACE);
        if (ksClientTest.exist()) ksClientTest.delete();

        KeyspaceClient ksClientTestBis = stargateRestApiClient.keyspace(TEST_KEYSPACE_BIS);
        if (ksClientTestBis.exist()) ksClientTestBis.delete();
    }


}
