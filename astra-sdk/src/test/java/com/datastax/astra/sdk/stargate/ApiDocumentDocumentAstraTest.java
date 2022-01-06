package com.datastax.astra.sdk.stargate;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentDocumentAstraTest /*extends ApiDocumentDocumentTest */ {
    
    /**
     * Init
     *
    @BeforeAll
    public static void init() { // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);
        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().get())
                .withCqlKeyspace(ApiDocumentTest.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .withoutCqlSession()
                .build();
        stargateClient = client.getStargateClient();
        
        nsClient = stargateClient.apiDocument().namespace(TEST_NAMESPACE);
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }
        
        personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }
    }
    
    /**
     * Close connections when ending
     *
    @AfterClass
    public static void closing() {
        if (stargateClient != null) {
            stargateClient.close();
        }
    }

*/
}
