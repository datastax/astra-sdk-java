package com.datastax.astra.devops.streaming;

import com.datastax.astra.devops.AbstractDevopsApiTest;
import com.datastax.astra.devops.streaming.domain.CreateTenant;
import com.datastax.astra.devops.streaming.domain.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantStatsClientTest extends AbstractDevopsApiTest {

    /** Random. */
    private static final String tmpTenant = "sdk-java-stats-" + UUID.randomUUID().toString().substring(0,7);

    @Test
    @Order(1)
    @DisplayName("Create a Tenant for stats")
    public void shouldCreateTenant() throws InterruptedException {
        // Given
        AstraStreamingClient sc  = new AstraStreamingClient(getToken());
        // When
        sc.create(CreateTenant.builder()
                .tenantName(tmpTenant)
                .userEmail("astra-cli@datastax.com").build());
        Thread.sleep(1000);
        // Then
        assertTrue(sc.exist(tmpTenant));
    }

    @Test
    @Order(2)
    @DisplayName("Access namespace stats")
    public void shouldShowStatsNamespaces() {
        // Given
        AstraStreamingClient cli = new AstraStreamingClient(getToken());
        TenantStatsClient statsClient = cli.tenant(tmpTenant).stats();
        // When
        List<String> statsName = statsClient.namespaces()
                .map(Statistics::getName)
                .collect(Collectors.toList());
        // Then
        assertNotNull(statsName);
    }

    @Test
    @Order(3)
    public void shouldShowStatsNamespaces1() {
        // Given
        AstraStreamingClient cli = new AstraStreamingClient(getToken());
        TenantStatsClient statsClient = cli.tenant(tmpTenant).stats();
        // When
        assertFalse(statsClient.namespace("default").isPresent());
    }

    @Test
    @Order(4)
    public void should_statsTopics() {
        new AstraStreamingClient(getToken())
                .tenant(tmpTenant)
                .stats()
                .topics()
                .map(Statistics::getName)
                .forEach(System.out::println);
    }

    @Test
    @Order(5)
    public void should_statsTopicsNamespace() {
        new AstraStreamingClient(getToken())
                .tenant(tmpTenant)
                .stats()
                .topics("test")
                .map(Statistics::getName)
                .forEach(System.out::println);
    }

    @Test
    @Order(6)
    @DisplayName("Delete a Tenant for stats")
    public void shouldDeleteTenant() throws InterruptedException {
        // Given
        AstraStreamingClient cli = new AstraStreamingClient(getToken());
        // When
        cli.delete(tmpTenant);
        // Then
        Thread.sleep(1000);
        assertFalse(cli.exist(tmpTenant));
    }


}
