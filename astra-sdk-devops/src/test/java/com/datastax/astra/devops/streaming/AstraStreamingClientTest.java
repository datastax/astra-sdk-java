package com.datastax.astra.devops.streaming;

import com.datastax.astra.devops.AbstractDevopsApiTest;
import com.datastax.astra.devops.streaming.domain.Tenant;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Set;
import java.util.stream.Collectors;

@TestMethodOrder(OrderAnnotation.class)
public class AstraStreamingClientTest extends AbstractDevopsApiTest {

    @Test
    @Order(1)
    public void shouldFailInvalidParams() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraStreamingClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraStreamingClient((String) null));
    }

    @Test
    @Order(2)
    public void shouldFindAllTenant() {
        // Given
        AstraStreamingClient cli = new AstraStreamingClient(getToken());
        // When
        Set<String> tenants = cli.findAll()
                .map(Tenant::getTenantName)
                .collect(Collectors.toSet());
        // Then
        Assertions.assertNotNull(tenants);
    }

}