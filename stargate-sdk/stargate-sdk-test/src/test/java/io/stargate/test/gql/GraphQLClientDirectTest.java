package io.stargate.test.gql;

import io.stargate.sdk.gql.StargateGraphQLApiClient;
import io.stargate.sdk.test.gql.AbstractGraphClientTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Implementation of GraphQL.
 *
 */
public class GraphQLClientDirectTest extends AbstractGraphClientTest {

    @BeforeAll
    public static void init() {
        // Initialization
        stargateGraphQLApiClient = new StargateGraphQLApiClient();

        // PreRequisites
        //NamespaceClient nsClientTest   = stargateDocumentApiClient.namespace(TEST_NAMESPACE);
        //if (nsClientTest.exist()) nsClientTest.delete();

        //NamespaceClient nsClientTestBis = stargateDocumentApiClient.namespace(TEST_NAMESPACE+"bis");
        //if (nsClientTestBis.exist()) nsClientTestBis.delete();

    }

}
