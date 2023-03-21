package com.datastax.astra.sdk.stargate;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import io.stargate.sdk.test.gql.AbstractGraphClientTest;
import org.junit.jupiter.api.BeforeAll;

public class ApiGraphQLAstraTest extends AbstractGraphClientTest {

    @BeforeAll
    public static void init() {
        // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);

        client = AstraClient.builder()
                .withToken(client.getToken().orElseThrow(() -> new IllegalStateException("token not found")))
                .withCqlKeyspace(TestDocClientConstants.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .build();

        stargateGraphQLApiClient = client.getStargateClient().apiGraphQL();

    }
}
