package io.stargate.sdk.test.rest;

import io.stargate.sdk.core.DataCenter;
import io.stargate.sdk.rest.StargateRestApiClient;
import io.stargate.sdk.rest.KeyspaceClient;
import io.stargate.sdk.rest.domain.Keyspace;
import io.stargate.sdk.utils.AnsiUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class test the data api for Keyspaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractRestClientKeyspacesTest implements TestRestClientConstants {

    /** Tested Store. */
    protected static StargateRestApiClient stargateRestApiClient;

    /**
     * Create a keyspace
     * POST /v2/schemas/keyspaces
     *
     * @throws InterruptedException
     *          error in creation
     */
    @Test
    @Order(1)
    @DisplayName("01-Create a keyspace java with simple Strategy")
    public void createKeyspaceWithSimpleStrategyTest() throws InterruptedException {
        // Given
        KeyspaceClient ksTemp = stargateRestApiClient.keyspace(TEST_KEYSPACE);
        Assertions.assertFalse(ksTemp.exist());
        // When
        ksTemp.createSimple(1);
        // Then
        int wait = 0;
        while (wait++ < 10 && ! ksTemp.exist()) {
            Thread.sleep(2000);
            System.out.print(AnsiUtils.green("\u25a0"));
        }
        Assertions.assertTrue(ksTemp.exist());
    }

    /**
     * Create a Keyspace
     * POST /v2/schemas/Keyspaces
     * @throws InterruptedException
     *          error in creation

    @Test
    @Order(2)
    @DisplayName("02-Create a Keyspace java with network topology")
    public void b_should_create_keyspace_withNetworkTopology() throws InterruptedException {
        // Given
        KeyspaceClient nsTemp = stargateClient.keyspace(TEST_KEYSPACE_BIS);
        Assertions.assertFalse(nsTemp.exist());
        // When
        nsTemp.create(new DataCenter(stargateClient
                .getStargateHttpClient()
                .getDeployment()
                .getLocalDatacenterClient()
                .getDatacenterName(), 1));
        // Then
        int wait = 0;
        while (wait++ < 10 && ! nsTemp.exist()) {
            Thread.sleep(2000);
            System.out.print(AnsiUtils.green("\u25a0"));
        }
        Assertions.assertTrue(nsTemp.exist());
    }*/
    
    /**
     * Create a Keyspace
     */
    @Test
    @Order(3)
    @DisplayName("03-Create a Keyspace already exist")
    public void c_should_create_keyspace_already_exist() {
        // Given
        KeyspaceClient nsTemp = stargateRestApiClient.keyspace(TEST_KEYSPACE_BIS);
        if (!nsTemp.exist()) {
            nsTemp.createSimple(1);
        }
        Assertions.assertTrue(nsTemp.exist());
        // When
        nsTemp.create(new DataCenter(stargateRestApiClient
                .getStargateHttpClient()
                .getDeployment()
                .getLocalDatacenterClient()
                .getDatacenterName(), 1));
    }
    
    /**
     * Get all Keyspaces
     * GET /v2​/schemas​/Keyspaces
     */
    @Test
    @Order(4)
    @DisplayName("04-Get all Keyspaces")
    public void listKeyspacesTest() {
        Assertions.assertTrue(stargateRestApiClient
                .keyspaceNames().collect(Collectors.toSet())
                .contains(TEST_KEYSPACE));
        
        // Keyspace populated
        Map<String, Keyspace> nsList = stargateRestApiClient
                .keyspaces().collect(Collectors.toMap(Keyspace::getName, Function.identity()));
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE));
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE).getName());
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE_BIS));
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE_BIS).getName());
        // The keyspace creation with DC must be passing
        //Assertions.assertNotNull(nsList.get(TEST_KEYSPACE_BIS).getDatacenters());
    }
    
    /**
     * Get a Keyspace
     * GET /v2​/schemas​/Keyspaces​/{Keyspace-id}
     */
    @Test
    @Order(5)
    @DisplayName("05-Get a Keyspace by name (if exist)")
    public void e_should_get_keyspace_by_name() {
        // Given
        Assertions.assertTrue(stargateRestApiClient.keyspace(TEST_KEYSPACE).exist());
        // When
        Optional<Keyspace> nsTest = stargateRestApiClient.keyspace(TEST_KEYSPACE).find();
        // Then
        Assertions.assertNotNull(nsTest);
        Assertions.assertTrue(nsTest.isPresent());
        Assertions.assertNotNull(nsTest.get().getName());
        
        // Given
        Assertions.assertFalse(stargateRestApiClient.keyspace("invalid").exist());
        Assertions.assertFalse(stargateRestApiClient.keyspace("invalid").find().isPresent());
    }
    
    /**
     * Delete a Keyspace
     * DELETE ​/v2​/schemas​/Keyspaces​/{Keyspace-id}
     */
    @Test
    @Order(6)
    @DisplayName("06-Delete a Keyspace")
    public void f_should_delete_Keyspace() {
        KeyspaceClient javaBis = stargateRestApiClient
                .keyspace(TEST_KEYSPACE_BIS);
        // Given
        Assertions.assertTrue(javaBis.exist());
        // When
        javaBis.delete();
        // Then
        Assertions.assertFalse(javaBis.exist());
    }

}
