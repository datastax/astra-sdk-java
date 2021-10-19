package com.datastax.stargate.sdk.doc.test;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.doc.NamespaceClient;
import com.datastax.stargate.sdk.doc.domain.FunctionDefinition;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.utils.AnsiUtils;

/**
 * This class test the document api for namespaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiDocumentNamespacesTest implements ApiDocumentTest {
    
    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    // -----------------------------------------
    //           Operation on Namespaces
    // -----------------------------------------
    
    /**
     * Create a namespace
     * POST /v2​/schemas​/namespaces
     * @throws InterruptedException
     *          error in creation
     */
    @Test
    @Order(1)
    @DisplayName("01-Create a namespace java with simple Strategy")
    public void a_should_create_namespace_withSimpleStrategy() throws InterruptedException {
        // Given
        NamespaceClient nsTemp = stargateClient.apiDocument().namespace(TEST_NAMESPACE);
        Assertions.assertFalse(nsTemp.exist());
        // When
        nsTemp.createSimple(1);
        // Then
        int wait = 0;
        while (wait++ < 10 && ! nsTemp.exist()) {
            Thread.sleep(2000);
            System.out.print(AnsiUtils.green("\u25a0"));
        }
        Assertions.assertTrue(nsTemp.exist());
    }

    /**
     * Create a namespace
     * POST /v2​/schemas​/namespaces
     * @throws InterruptedException
     *          error in creation
     */
    @Test
    @Order(2)
    @DisplayName("02-Create a namespace java with network topology")
    public void b_should_create_namespace_withNetworkTopology() throws InterruptedException {
        // Given
        NamespaceClient nsTemp = stargateClient.apiDocument().namespace(TEST_NAMESPACE + "bis");
        Assertions.assertFalse(nsTemp.exist());
        // When
        nsTemp.create(new DataCenter(stargateClient.getCurrentDatacenter(), 1));
        // Then
        int wait = 0;
        while (wait++ < 10 && ! nsTemp.exist()) {
            Thread.sleep(2000);
            System.out.print(AnsiUtils.green("\u25a0"));
        }
        Assertions.assertTrue(nsTemp.exist());
    }
    
    /**
     * Create a namespace
     * POST /v2​/schemas​/namespaces
     * @throws InterruptedException
     *          error in creation
     */
    @Test
    @Order(3)
    @DisplayName("03-Create a namespace already exist")
    public void c_should_create_namespace_already_exist() throws InterruptedException {
        // Given
        NamespaceClient nsTemp = stargateClient.apiDocument().namespace(TEST_NAMESPACE_BIS);
        Assertions.assertTrue(nsTemp.exist());
        // When
        nsTemp.create(new DataCenter(stargateClient.getCurrentDatacenter(), 1));
    }
    
    /**
     * Get all namespaces
     * GET /v2​/schemas​/namespaces
     */
    @Test
    @Order(4)
    @DisplayName("04-Get all namespaces")
    public void d_should_list_namespaces() {
        // list is present and contains the test namespace
        Assertions.assertTrue(stargateClient.apiDocument()
                .namespaceNames().collect(Collectors.toSet())
                .contains(TEST_NAMESPACE));
        
        // Namespace populated
        Map<String, Namespace> nsList = stargateClient.apiDocument()
                .namespaces().collect(Collectors.toMap(Namespace::getName, Function.identity()));
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE));
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE).getName());
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE_BIS));
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE_BIS).getName());
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE_BIS).getDatacenters());
    }
    
    /**
     * Get a namespace
     * GET ​/v2​/schemas​/namespaces​/{namespace-id}
     */
    @Test
    @Order(5)
    @DisplayName("05-Get a namespace by name (if exist)")
    public void e_should_get_namespace_by_name() {
        // Given
        Assertions.assertTrue(stargateClient.apiDocument().namespace(TEST_NAMESPACE).exist());
        // When
        Optional<Namespace> nsTest = stargateClient.apiDocument().namespace(TEST_NAMESPACE).find();
        // Then
        Assertions.assertNotNull(nsTest);
        Assertions.assertTrue(nsTest.isPresent());
        Assertions.assertNotNull(nsTest.get().getName());
        
        // Given
        Assertions.assertFalse(stargateClient.apiDocument().namespace("invalid").exist());
        Assertions.assertFalse(stargateClient.apiDocument().namespace("invalid").find().isPresent());
    }
    
    /**
     * View all built-in reserved functions
     * GET /v2/schemas/namespaces/{namespace-id}/functions
     */
    @Test
    @Order(6)
    @DisplayName("06-List functions in a namespace")
    public void f_should_list_functions() {
        // Given
        Assertions.assertTrue(stargateClient.apiDocument().namespace(TEST_NAMESPACE).exist());
        // When
        Map<String, String > funcs = stargateClient
                .apiDocument()
                .namespace(TEST_NAMESPACE)
                .functions()
                .collect(Collectors.toMap(FunctionDefinition::getName, FunctionDefinition::getDescription));
        Assertions.assertTrue(funcs.containsKey("$push"));
        Assertions.assertNotNull(funcs.get("$push"));
    }
    
    /**
     * Delete a namespace
     * DELETE ​/v2​/schemas​/namespaces​/{namespace-id}
     */
    @Test
    @Order(7)
    @DisplayName("07-Delete a namespace")
    public void g_should_delete_namespace() {
        NamespaceClient javaBis = stargateClient.apiDocument()
                .namespace(TEST_NAMESPACE_BIS);
        // Given
        Assertions.assertTrue(javaBis.exist());
        // When
        javaBis.delete();
        // Then
        Assertions.assertFalse(javaBis.exist());
    }

}
