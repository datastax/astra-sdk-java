package com.datastax.stargate.sdk.rest.test;

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
import com.datastax.stargate.sdk.rest.KeyspaceClient;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.utils.AnsiUtils;

/**
 * This class test the data api for Keyspaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiDataKeyspacesTest implements ApiDataTest {
    
    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    /**
     * Create a keyspace
     * POST /v2​/schemas​/keyspaces
     * @throws InterruptedException
     *          error in creation
     */
    @Test
    @Order(1)
    @DisplayName("01-Create a keyspace java with simple Strategy")
    public void a_should_create_keyspace_withSimpleStrategy() throws InterruptedException {
        // Given
        KeyspaceClient ksTemp = stargateClient.apiRest().keyspace(TEST_KEYSPACE);
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
     * POST /v2​/schemas​/Keyspaces
     * @throws InterruptedException
     *          error in creation
     */
    @Test
    @Order(2)
    @DisplayName("02-Create a Keyspace java with network topology")
    public void b_should_create_keyspace_withNetworkTopology() throws InterruptedException {
        // Given
        KeyspaceClient nsTemp = stargateClient.apiRest().keyspace(TEST_KEYSPACE_BIS);
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
     * Create a Keyspace
     * POST /v2​/schemas​/Keyspaces
     * @throws InterruptedException
     *          error in creation
     */
    @Test
    @Order(3)
    @DisplayName("03-Create a Keyspace already exist")
    public void c_should_create_keyspace_already_exist() throws InterruptedException {
        // Given
        KeyspaceClient nsTemp = stargateClient.apiRest().keyspace(TEST_KEYSPACE_BIS);
        Assertions.assertTrue(nsTemp.exist());
        // When
        nsTemp.create(new DataCenter(stargateClient.getCurrentDatacenter(), 1));
    }
    
    /**
     * Get all Keyspaces
     * GET /v2​/schemas​/Keyspaces
     */
    @Test
    @Order(4)
    @DisplayName("04-Get all Keyspaces")
    public void d_should_list_keyspaces() {
        // list is present and contains the test Keyspace
        Assertions.assertTrue(stargateClient.apiRest()
                .keyspaceNames().collect(Collectors.toSet())
                .contains(TEST_KEYSPACE));
        
        // Keyspace populated
        Map<String, Keyspace> nsList = stargateClient.apiRest()
                .keyspaces().collect(Collectors.toMap(Keyspace::getName, Function.identity()));
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE));
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE).getName());
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE_BIS));
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE_BIS).getName());
        Assertions.assertNotNull(nsList.get(TEST_KEYSPACE_BIS).getDatacenters());
    }
    
    /**
     * Get a Keyspace
     * GET ​/v2​/schemas​/Keyspaces​/{Keyspace-id}
     */
    @Test
    @Order(5)
    @DisplayName("05-Get a Keyspace by name (if exist)")
    public void e_should_get_keyspace_by_name() {
        // Given
        Assertions.assertTrue(stargateClient.apiRest().keyspace(TEST_KEYSPACE).exist());
        // When
        Optional<Keyspace> nsTest = stargateClient.apiRest().keyspace(TEST_KEYSPACE).find();
        // Then
        Assertions.assertNotNull(nsTest);
        Assertions.assertTrue(nsTest.isPresent());
        Assertions.assertNotNull(nsTest.get().getName());
        
        // Given
        Assertions.assertFalse(stargateClient.apiRest().keyspace("invalid").exist());
        Assertions.assertFalse(stargateClient.apiRest().keyspace("invalid").find().isPresent());
    }
    
    /**
     * Delete a Keyspace
     * DELETE ​/v2​/schemas​/Keyspaces​/{Keyspace-id}
     */
    @Test
    @Order(6)
    @DisplayName("06-Delete a Keyspace")
    public void f_should_delete_Keyspace() {
        KeyspaceClient javaBis = stargateClient.apiRest()
                .keyspace(TEST_KEYSPACE_BIS);
        // Given
        Assertions.assertTrue(javaBis.exist());
        // When
        javaBis.delete();
        // Then
        Assertions.assertFalse(javaBis.exist());
    }

}
