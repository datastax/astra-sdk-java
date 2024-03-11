package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraSdkTest;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import io.stargate.sdk.test.gql.AbstractGraphClientTest;
import org.junit.jupiter.api.BeforeAll;

import static com.datastax.astra.devops.utils.TestUtils.TEST_REGION;
import static com.datastax.astra.devops.utils.TestUtils.setupDatabase;

public class ApiGraphQLAstraTest extends AbstractGraphClientTest implements AstraSdkTest {

    @BeforeAll
    public static void init() {
        stargateGraphQLApiClient = AstraClient.builder()
                .withDatabaseRegion(TEST_REGION)
                .withDatabaseId(setupDatabase(TEST_DATABASE_NAME, TestDocClientConstants.TEST_NAMESPACE))
                .build().getStargateClient().apiGraphQL();
    }
}
