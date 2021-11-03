package com.datastax.astra.sdk.stargate;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import com.datastax.stargate.sdk.doc.test.ApiDocumentNamespacesTest;
import com.datastax.stargate.sdk.doc.test.ApiDocumentTest;

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
public class ApiDocumentNamespacesAstraTest extends ApiDocumentNamespacesTest {
    
   /*
    * Init
    */
   @BeforeAll
   public static void init() {
       // Default client to create DB if needed
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
    
    @Test
    @Order(1)
    @Override
    @DisplayName("01-Create a namespace java with simple Strategy")
    public void a_should_create_namespace_withSimpleStrategy() {
        // cannot create keyspace in Astra
    }
    
    @Test
    @Order(2)
    @Override
    @DisplayName("02-Create a namespace java with network topology")
    public void b_should_create_namespace_withNetworkTopology() {
        // cannot create keyspace in Astra
    }
    
    @Test
    @Order(3)
    @Override
    @DisplayName("03-Create a namespace already exist")
    public void c_should_create_namespace_already_exist() {
       // cannot create keyspace in Astra
    }
    
    @Test
    @Order(7)
    @Override
    @DisplayName("07-Delete a namespace")
    public void g_should_delete_namespace() {
        // cannot create keyspace in Astra
    }
}
