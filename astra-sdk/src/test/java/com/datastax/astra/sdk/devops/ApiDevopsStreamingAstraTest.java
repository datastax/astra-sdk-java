package com.datastax.astra.sdk.devops;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.pulsar.client.admin.PulsarAdmin;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;

@TestMethodOrder(OrderAnnotation.class)
public class ApiDevopsStreamingAstraTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDevopsStreamingAstraTest.class);
    
    private static AstraClient client;
    
    @BeforeAll
    public static void config() {
        client= AstraClient.builder().build();
    }
    
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
        Set<String> tenants = new StreamingClient(client.getToken().get())
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
        Map<String, List<String>> providers = new StreamingClient(client.getToken().get()).providers();
        Assert.assertTrue(providers.size()>0);
        LOGGER.info(providers.toString());
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
        System.out.println("- Create a tenant");
        // Giving
        tmpTenant = "sdk_java_junit_" + UUID.randomUUID().toString().substring(0,7);
        // When
        Assert.assertFalse(sc.tenant(tmpTenant).exist());
        LOGGER.info("Tenant " + tmpTenant + " does not exist");
        sc.createTenant( new CreateTenant(tmpTenant, "cedrick.lunven@datastax.com"));
        Thread.sleep(1000);
        Assert.assertTrue(sc.tenant(tmpTenant).exist());
        LOGGER.info("Tenant " + tmpTenant + " now exist");
    }
    
    @Test
    @Order(6)
    public void should_work_withTenant() throws Exception {
        System.out.println("- Access pulsar admin");
        
        AstraClient astraClient = client;
        
        try(PulsarAdmin admin = astraClient.streaming()
                .tenant(tmpTenant)
                .pulsarAdmin()) {
            Assert.assertTrue(admin
                 .namespaces()
                 .getNamespaces(tmpTenant).size() > 0);
            LOGGER.info("Pulsar admin is OK");
        }
        /*
        try(Consumer<Person> consumer = astraClient.streaming()
                   .tenant("postman_tenant_2")
                   .pulsarClient()
                   .newConsumer(JSONSchema.of(Person.class))
                   .topic("postman_tenant_2/default/topc1")
                   .subscriptionName("my-subscription")
                   .subscribe()) {
        }*/
    }
    
    @Test
    @Order(7)
    public void should_delete_tenant() throws InterruptedException {
        //tmpTenant = "sdk_java_junit_32defeb";
        StreamingClient sc  = new StreamingClient(client.getToken().get());
        System.out.println("- Delete a tenant");
        // Giving
        Assert.assertTrue(sc.tenant(tmpTenant).exist());
        LOGGER.info("Tenant " + tmpTenant + " exists");
        // When
        sc.tenant(tmpTenant).delete();
        Thread.sleep(1000);
        Assert.assertFalse(sc.tenant(tmpTenant).exist());
        LOGGER.info("Tenant " + tmpTenant + " has been deleted");
    }
    
    @Test
    @Order(7)
    public void should_list_limit() throws InterruptedException {
        // Service not available in private beta
    }
    
   
     

}
