package com.datastax.astra.sdk.streaming;

import com.datastax.astra.sdk.devops.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.streaming.domain.CreateTenant;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import com.dtsx.astra.sdk.streaming.StreamingClient;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@TestMethodOrder(OrderAnnotation.class)
public class ApiDevopsStreamingAstraTest extends AbstractDevopsApiTest  {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDevopsStreamingAstraTest.class);

    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        System.out.println("- Parameter validation");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StreamingClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new StreamingClient((String) null));
    }
    
    @Test
    @Order(2)
    public void should_list_tenants() {
        System.out.println("- List Tenants");
        Set<String> tenants = new StreamingClient(getToken())
                .tenants()
                .map(Tenant::getTenantName)
                .collect(Collectors.toSet());
        Assert.assertNotNull(tenants);
        LOGGER.info(tenants.toString());
    }
    
    @Test
    @Order(3)
    public void should_list_providers() {
        System.out.println("- List Providers");
        Map<String, List<String>> providers = new StreamingClient(getToken()).providers();
        Assert.assertTrue(providers.size()>0);
        LOGGER.info(providers.toString());
    }
    
    private static String tmpTenant;
    
    
    @Test
    @Order(4)
    public void should_exist_tenant() throws InterruptedException {
        StreamingClient sc  = new StreamingClient(getToken());
        tmpTenant = "sdk_java_junit_" + UUID.randomUUID().toString().substring(0,7);
        //Assert.assertTrue(sc.tenant("postman_tenant_2").exist());
        Assert.assertFalse(sc.tenant(tmpTenant).exist());
    }
    
    @Test
    @Order(5)
    public void should_create_tenant() throws InterruptedException {
        StreamingClient sc  = new StreamingClient(getToken());
        System.out.println("- Create a tenant");
        // Giving
        tmpTenant = "sdk-java-junit-" + UUID.randomUUID().toString().substring(0,7);
        // When
        Assert.assertFalse(sc.tenant(tmpTenant).exist());
        LOGGER.info("Tenant " + tmpTenant + " does not exist");
        sc.createTenant( new CreateTenant(tmpTenant, "astra-cli@datastax.com"));
        Thread.sleep(1000);
        Assert.assertTrue(sc.tenant(tmpTenant).exist());
        LOGGER.info("Tenant " + tmpTenant + " now exist");
    }

    @Test
    @Order(7)
    public void should_delete_tenant() throws InterruptedException {
        //tmpTenant = "sdk_java_junit_32defeb";
        StreamingClient sc  = new StreamingClient(getToken());
        System.out.println("- Delete a tenant");
        // Giving
        Assert.assertTrue(sc.tenant(tmpTenant).exist());
        LOGGER.info("Tenant " + tmpTenant + " exists");
        // When
        sc.tenant(tmpTenant).delete();
    }


}
