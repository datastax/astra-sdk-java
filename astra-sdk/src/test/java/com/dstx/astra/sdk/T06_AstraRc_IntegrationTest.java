package com.dstx.astra.sdk;

import java.io.File;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.dstx.astra.sdk.utils.AstraRc;

/**
 * TEST Loading databases metadata in .astrarc 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class T06_AstraRc_IntegrationTest extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[T06_AstraRc_IntegrationTest]" + ANSI_RESET);
    }
    
    @Test
    @Order(1)
    public void should_create_astraRc_File() {
        System.out.println(ANSI_YELLOW + "\n#01 Create file " + ANSI_RESET);
        // Given
        new File(System.getProperty("user.home") + "/.astrarc").delete();
        Assert.assertFalse(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // When
        AstraRc.create(client.apiDevops());
        // Then
        Assert.assertTrue(new File(System.getProperty("user.home") + "/.astrarc").exists());
        // Then we should be able to load the file
        AstraRc.load();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Loaded");
    }
    
    @Test
    @Order(2)
    public void should_init_AstraClient_from_File() {
        System.out.println(ANSI_YELLOW + "\n#02 Use file " + ANSI_RESET);
        AstraClient astraClient = AstraClient.builder().build();
        Assertions.assertNotNull(astraClient);
        // Can query with CQL !!
        String dataCenterName = astraClient.cqlSession()
                .execute("select data_center from system.local")
                .one().getString("data_center");
        Assertions.assertNotNull(dataCenterName);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - cql sucess with dc " + dataCenterName);
    }
    
    

}
