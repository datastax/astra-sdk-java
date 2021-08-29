package com.datastax.astra.sdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;

@TestMethodOrder(OrderAnnotation.class)
public class T09_DevopsStreamingIntegrationTest extends AbstractAstraIntegrationTest {
    
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
        StreamingClient streaming = new StreamingClient(client.getToken().get());
        streaming.tenants()
                 .map(Tenant::getTenantName)
                 .forEach(System.out::println);
    }
    
    @Test
    @Order(3)
    public void should_list_providers() {
        System.out.println(ANSI_YELLOW + "- List Providers" + ANSI_RESET);
        StreamingClient streaming = new StreamingClient(client.getToken().get());
        System.out.println(streaming.providers());
    }
    
    @Test
    @Order(4)
    public void should_create_tenant() {
        System.out.println(ANSI_YELLOW + "- Create a tenant" + ANSI_RESET);
        StreamingClient streaming = new StreamingClient(client.getToken().get());
        CreateTenant ct = new CreateTenant();
        ct.setCloudProvider("aws");
        ct.setCloudRegion("useast2");
        ct.setTenantName("cedrick.lunven@datastax.com");
        ct.setPlan("free");
        ct.setTenantName("sdk_tenant");
        streaming.createTenant(ct);
    }

}
