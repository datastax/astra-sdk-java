package com.datastax.astra.sdk.stargate;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import com.datastax.stargate.sdk.rest.test.ApiDocumentCollectionsTest;
import com.datastax.stargate.sdk.rest.test.ApiDocumentTest;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class AstraApiDocumentCollectionsTest extends ApiDocumentCollectionsTest {
     
    /*
     * Init
     */
    @BeforeAll
    public static void init() {
        // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);
        client.cqlSession().close();
        
        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().get())
                .withKeyspace(ApiDocumentTest.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .withoutCqlSession()
                .build();
        stargateClient = client.getStargateClient();
        nsClient = stargateClient.apiDocument().namespace(TEST_NAMESPACE);
     }
     
     /**
      * Close connections when ending
      */
     @AfterClass
     public static void closing() {
         if (stargateClient != null) {
             stargateClient.close();
         }
     }

}
