package com.datastax.stargate.sdk.test.local;

import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.test.AbstractDocumentTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentDocumentStargateTest extends AbstractDocumentTest {
     
    /*
     * Init
     */
    @BeforeAll
    public static void init() {
        apiDocumentClient = new ApiDocumentClient();

        nsClient = apiDocumentClient.namespace(TEST_NAMESPACE);
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }

        personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }
    }

}
