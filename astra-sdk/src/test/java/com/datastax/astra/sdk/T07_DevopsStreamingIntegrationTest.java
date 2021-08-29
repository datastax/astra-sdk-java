package com.datastax.astra.sdk;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;

@TestMethodOrder(OrderAnnotation.class)
public class T07_DevopsStreamingIntegrationTest extends AbstractAstraIntegrationTest {
    
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        System.out.println(ANSI_YELLOW + "- Parameter validation" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StreamingClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StreamingClient((String) null));
        
    }
    
    @Test
    @Order(2)
    public void should_list_tenants() {
        System.out.println(ANSI_YELLOW + "- List Tenants" + ANSI_RESET);
        Set<String> tenants = new StreamingClient(client.getToken().get())
                .tenants()
                .map(Tenant::getTenantName)
                .collect(Collectors.toSet());
        Assert.assertNotNull(tenants);
        printOK(tenants.toString());
    }
    
    @Test
    @Order(3)
    public void should_list_providers() {
        System.out.println(ANSI_YELLOW + "- List Providers" + ANSI_RESET);
        Map<String, List<String>> providers = new StreamingClient(client.getToken().get()).providers();
        Assert.assertTrue(providers.size()>0);
        printOK(providers.toString());
    }
    
    private static String tmpTenant;
    
    
    @Test
    @Order(4)
    public void should_exist_tenant() throws InterruptedException {
        StreamingClient sc  = new StreamingClient(client.getToken().get());
        tmpTenant = "sdk_java_junit_" + UUID.randomUUID().toString().substring(0,7);
        //Assert.assertTrue(sc.tenant("postman_tenant_2").exist());
        Assert.assertFalse(sc.tenant(tmpTenant).exist());
    }
    
    @Test
    @Order(5)
    public void should_create_tenant() throws InterruptedException {
        StreamingClient sc  = new StreamingClient(client.getToken().get());
        System.out.println(ANSI_YELLOW + "- Create a tenant" + ANSI_RESET);
        // Giving
        tmpTenant = "sdk_java_junit_" + UUID.randomUUID().toString().substring(0,7);
        // When
        Assert.assertFalse(sc.tenant(tmpTenant).exist());
        printOK("Tenant " + tmpTenant + " does not exist");
        sc.createTenant( new CreateTenant(tmpTenant, "cedrick.lunven@datastax.com"));
        Thread.sleep(1000);
        Assert.assertTrue(sc.tenant(tmpTenant).exist());
        printOK("Tenant " + tmpTenant + " now exist");
    }
    
    @Test
    @Order(6)
    public void should_delete_tenant() throws InterruptedException {
        //tmpTenant = "sdk_java_junit_32defeb";
        StreamingClient sc  = new StreamingClient(client.getToken().get());
        System.out.println(ANSI_YELLOW + "- Delete a tenant" + ANSI_RESET);
        // Giving
        Assert.assertTrue(sc.tenant(tmpTenant).exist());
        printOK("Tenant " + tmpTenant + " exists");
        // When
        sc.tenant(tmpTenant).delete();
        Thread.sleep(1000);
        Assert.assertFalse(sc.tenant(tmpTenant).exist());
        printOK("Tenant " + tmpTenant + " has been deleted");
    }
    
    @Test
    @Order(7)
    public void should_list_limit() throws InterruptedException {
        // Service not available in private beta
    }
    

}
