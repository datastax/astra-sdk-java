package com.datastax.astra.sdk.stargate;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import io.stargate.sdk.test.doc.AbstractDocClientCollectionsTest;
import io.stargate.sdk.test.doc.AbstractDocClientDocumentsTest;
import io.stargate.sdk.test.doc.AbstractDocClientNamespacesTest;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentDocumentAstraTest extends AbstractDocClientDocumentsTest {

    @BeforeAll
    public static void init() {
        // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);

        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().get())
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
