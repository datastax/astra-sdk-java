package com.datastax.stargate.sdk.test.local;

import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.test.AbstractNamespacesTest;
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
public class NamespacesLocalTest extends AbstractNamespacesTest {

   /*
    * Init
    */
   @BeforeAll
   public static void init() {
       apiDocumentClient = new ApiDocumentClient();
       if (apiDocumentClient.namespace(TEST_NAMESPACE).exist()) {
           apiDocumentClient.namespace(TEST_NAMESPACE).delete();
       }
       if (apiDocumentClient.namespace(TEST_NAMESPACE+"bis").exist()) {
           apiDocumentClient.namespace(TEST_NAMESPACE+"bis").delete();
       }
    }

}
