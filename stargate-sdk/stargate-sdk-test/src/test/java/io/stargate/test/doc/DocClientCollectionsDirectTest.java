package io.stargate.test.doc;

import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.test.doc.AbstractDocClientCollectionsTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DocClientCollectionsDirectTest extends AbstractDocClientCollectionsTest {
     
    /**
     * Init
     */
    @BeforeAll
    public static void init() {
        // Initialize
        stargateDocumentApiClient = new StargateDocumentApiClient();

        // Prerequisites
        nsClient = stargateDocumentApiClient.namespace(TEST_NAMESPACE);
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }
    }

}
