package io.stargate.sdk.test.doc;

import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.doc.domain.Namespace;
import io.stargate.sdk.exception.AlreadyExistException;
import io.stargate.sdk.utils.AnsiUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class test the document api for namespaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractDocClientNamespacesTest implements TestDocClientConstants {
    
    /** Tested Store. */
    protected static StargateDocumentApiClient stargateDocumentApiClient;
    
    // -----------------------------------------
    //           Operation on Namespaces
    // -----------------------------------------
    
    /**
     * Create a namespace
     */
    @Test
    @Order(1)
    @DisplayName("01-Create a namespace java with simple Strategy")
    public void createNamespaceWithSimpleStrategyTest() throws InterruptedException {
        // Given
        NamespaceClient nsTemp = stargateDocumentApiClient.namespace(TEST_NAMESPACE);
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
        NamespaceClient nsTemp = stargateDocumentApiClient.namespace(TEST_NAMESPACE + "bis");
        Assertions.assertFalse(nsTemp.exist());
        // When
        //nsTemp.create(new DataCenter(apiDocumentClient.getCurrentDatacenter(), 1));
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
     */
    @Test
    @Order(3)
    @DisplayName("03-Create a namespace already exist")
    public void c_should_create_namespace_already_exist() {
        // Given
        NamespaceClient nsTemp = stargateDocumentApiClient.namespace(TEST_NAMESPACE_BIS);
        Assertions.assertTrue(nsTemp.exist());
        // When
        Assertions.assertThrows(AlreadyExistException.class, () -> nsTemp.createSimple(1));
    }
    
    /**
     * Get all namespaces
     */
    @Test
    @Order(4)
    @DisplayName("04-Get all namespaces")
    public void d_should_list_namespaces() {
        // list is present and contains the test namespace
        Assertions.assertTrue(stargateDocumentApiClient
                .namespaceNames().collect(Collectors.toSet())
                .contains(TEST_NAMESPACE));
        
        // Namespace populated
        Map<String, Namespace> nsList = stargateDocumentApiClient
                .namespaces().collect(Collectors.toMap(Namespace::getName, Function.identity()));
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE));
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE).getName());
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE_BIS));
        Assertions.assertNotNull(nsList.get(TEST_NAMESPACE_BIS).getName());
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
        Assertions.assertTrue(stargateDocumentApiClient.namespace(TEST_NAMESPACE).exist());
        // When
        Optional<Namespace> nsTest = stargateDocumentApiClient.namespace(TEST_NAMESPACE).find();
        // Then
        Assertions.assertNotNull(nsTest);
        Assertions.assertTrue(nsTest.isPresent());
        Assertions.assertNotNull(nsTest.get().getName());
        
        // Given
        Assertions.assertFalse(stargateDocumentApiClient.namespace("invalid").exist());
        Assertions.assertFalse(stargateDocumentApiClient.namespace("invalid").find().isPresent());
    }

    /**
     * Delete a namespace.
     */
    @Test
    @Order(6)
    @DisplayName("06-Delete a namespace")
    public void g_should_delete_namespace() {
        NamespaceClient javaBis = stargateDocumentApiClient.namespace(TEST_NAMESPACE_BIS);
        // Given
        Assertions.assertTrue(javaBis.exist());
        // When
        javaBis.delete();
        // Then
        Assertions.assertFalse(javaBis.exist());
    }

}
