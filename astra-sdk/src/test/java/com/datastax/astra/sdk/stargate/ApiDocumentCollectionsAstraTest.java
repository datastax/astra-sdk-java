package com.datastax.astra.sdk.stargate;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentCollectionsAstraTest /*extends ApiDocumentCollectionsTest*/ {
     
    /*
     * Init
     *
    @BeforeAll
    public static void init() {
        // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);
        
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
     
    @Test
    @Order(5)
    @Override
    @DisplayName("05-Assign a Json Schema")
    public void e_should_set_schema() {
        // Not working in ASTRA
        // https://github.com/stargate/stargate/issues/1352
    }
    
     /**
      * Close connections when ending
      *
     @AfterClass
     public static void closing() {
         if (stargateClient != null) {
             stargateClient.close();
         }
     }*/

}
