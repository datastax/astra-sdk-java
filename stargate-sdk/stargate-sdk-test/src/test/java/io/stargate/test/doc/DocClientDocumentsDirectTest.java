package io.stargate.test.doc;

import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.test.doc.AbstractDocClientDocumentsTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DocClientDocumentsDirectTest extends AbstractDocClientDocumentsTest {
     
    /*
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateDocumentApiClient = new StargateDocumentApiClient();

        nsClient = stargateDocumentApiClient.namespace(TEST_NAMESPACE);
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }

        personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }
    }

}
