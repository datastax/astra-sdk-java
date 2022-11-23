package io.stargate.test.doc;

import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.test.doc.AbstractDocClientNamespacesTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Work with local stargate
 * 
 * docker run --name stargate \
 * -p 8080:8080 \
 * -p 8081:8081 \
 * -p 8082:8082 \
 * -p 127.0.0.1:9042:9042 \
 * -d \
 *   -e CLUSTER_NAME=stargate \
 *   -e CLUSTER_VERSION=3.11 \
 *   -e DEVELOPER_MODE=true \
 *   stargateio/stargate-3_11:v1.0.35
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DocClientNamespacesDirectTest extends AbstractDocClientNamespacesTest {

   @BeforeAll
   public static void init() {
       // Initialization
       stargateDocumentApiClient = new StargateDocumentApiClient();

       // PreRequisites
       NamespaceClient nsClientTest   = stargateDocumentApiClient.namespace(TEST_NAMESPACE);
       if (nsClientTest.exist()) nsClientTest.delete();

       NamespaceClient nsClientTestBis = stargateDocumentApiClient.namespace(TEST_NAMESPACE+"bis");
       if (nsClientTestBis.exist()) nsClientTestBis.delete();

    }

}
