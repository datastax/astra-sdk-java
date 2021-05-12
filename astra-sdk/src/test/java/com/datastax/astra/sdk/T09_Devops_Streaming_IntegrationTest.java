package com.datastax.astra.sdk;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.iam.IamClient;
import com.datastax.astra.sdk.streaming.StreamingClient;
import com.datastax.astra.sdk.streaming.domain.CreateTenant;
import com.datastax.astra.sdk.streaming.domain.Tenant;

@TestMethodOrder(OrderAnnotation.class)
public class T09_Devops_Streaming_IntegrationTest extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[Astra DEVOPS ROLES Test Suite]" + ANSI_RESET);
        appToken = Optional.ofNullable("AstraCS:abjIANeldqrcOQmILeACwhOr:5daf79ff81bd667ea29179eac08ae98e047c0f42ad462bf66b2282b372eb641a");
    }
    
    @Test
    @Order(1)
    public void should_fail_on_invalid_params() {
        System.out.println(ANSI_YELLOW + "- Parameter validation" + ANSI_RESET);
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IamClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new IamClient(null));
    }
    
    @Test
    @Order(2)
    public void should_list_tenants() {
        System.out.println(ANSI_YELLOW + "- List Tenants" + ANSI_RESET);
        StreamingClient streaming = new StreamingClient(appToken.get());
        streaming.tenants()
                 .map(Tenant::getTenantName)
                 .forEach(System.out::println);
    }
    
    @Test
    @Order(3)
    public void should_list_providers() {
        System.out.println(ANSI_YELLOW + "- List Providers" + ANSI_RESET);
        StreamingClient streaming = new StreamingClient(appToken.get());
        System.out.println(streaming.providers());
    }
    
    @Test
    @Order(4)
    public void should_create_tenant() {
        System.out.println(ANSI_YELLOW + "- Create a tenant" + ANSI_RESET);
        StreamingClient streaming = new StreamingClient(appToken.get());
        CreateTenant ct = new CreateTenant();
        ct.setCloudProvider("aws");
        ct.setCloudRegion("useast2");
    }

}
