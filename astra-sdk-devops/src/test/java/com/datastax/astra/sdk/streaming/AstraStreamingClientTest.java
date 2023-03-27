package com.datastax.astra.sdk.streaming;

import com.datastax.astra.sdk.devops.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.stream.Collectors;

@TestMethodOrder(OrderAnnotation.class)
public class AstraStreamingClientTest extends AbstractDevopsApiTest  {

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
        Assert.assertNotNull(tenants);
    }

}