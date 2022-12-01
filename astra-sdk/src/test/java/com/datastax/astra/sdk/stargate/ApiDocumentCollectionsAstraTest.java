package com.datastax.astra.sdk.stargate;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import io.stargate.sdk.test.doc.AbstractDocClientCollectionsTest;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentCollectionsAstraTest extends AbstractDocClientCollectionsTest {

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
     }
     
    @Test
    @Order(5)
    @Override
    @DisplayName("05-Assign a Json Schema")
    public void e_should_set_schema() {
        // Not working in ASTRA
        // https://github.com/stargate/stargate/issues/1352
    }

}
