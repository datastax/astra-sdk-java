package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import io.stargate.sdk.test.doc.AbstractDocClientDocumentsTest;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests against collections.
 */
public class ApiDocumentDocumentAstraTest extends AbstractDocClientDocumentsTest {

    @BeforeAll
    public static void init() {
        // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);

        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().orElseThrow(() -> new IllegalStateException("token not found")))
                .withCqlKeyspace(TestDocClientConstants.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .build();

        stargateDocumentApiClient = client.getStargateClient().apiDocument();
        nsClient = stargateDocumentApiClient.namespace(TEST_NAMESPACE);

        personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }
    }

}
