package com.datastax.stargate.sdk.test;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.doc.test.ApiDocumentNamespacesTest;

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
public class StargateApiDocumentNamespacesTest extends ApiDocumentNamespacesTest {
    
   /*
    * Init
    */
   @BeforeAll
   public static void init() {
       stargateClient = ApiStargateTestFactory.createStargateClient();
       if (stargateClient.apiDocument().namespace(TEST_NAMESPACE).exist()) {
           stargateClient.apiDocument().namespace(TEST_NAMESPACE).delete();
       }
       if (stargateClient.apiDocument().namespace(TEST_NAMESPACE+"bis").exist()) {
           stargateClient.apiDocument().namespace(TEST_NAMESPACE+"bis").delete();
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
