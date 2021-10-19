package com.datastax.stargate.sdk.test;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.doc.test.ApiDocumentDocumentTest;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class StargateApiDocumentDocumentTest extends ApiDocumentDocumentTest {
     
    /**
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateClient = ApiStargateTestFactory.createStargateClient();
        nsClient = stargateClient.apiDocument().namespace(TEST_NAMESPACE);
        // We need the namespace
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }
        personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        // Create empty collection if needed
        if (!personClient.exist()) {
            personClient.create();
        }
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
