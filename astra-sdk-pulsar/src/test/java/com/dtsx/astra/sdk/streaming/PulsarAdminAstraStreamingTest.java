package com.dtsx.astra.sdk.streaming;

import com.datastax.astra.devops.streaming.AstraStreamingClient;
import com.datastax.astra.devops.streaming.domain.CreateTenant;
import com.datastax.astra.devops.streaming.domain.Tenant;
import com.datastax.astra.devops.utils.AstraRc;
import com.datastax.astra.devops.utils.Utils;
import org.apache.pulsar.client.admin.PulsarAdmin;
import org.apache.pulsar.client.admin.PulsarAdminException;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PulsarAdminAstraStreamingTest {

    private static String tmpTenant = "sdk-java-junit-" + UUID.randomUUID().toString().substring(0,7);

    static String token;

    static AstraStreamingClient streamingClient;

    @BeforeAll
    public static void beforeAll() {
        if (AstraRc.isDefaultConfigFileExists()) {
            token = new AstraRc()
                    .getSectionKey(AstraRc.ASTRARC_DEFAULT, AstraRc.ASTRA_DB_APPLICATION_TOKEN)
                    .orElse(null);
        }
        token = Utils.readEnvVariable(AstraRc.ASTRA_DB_APPLICATION_TOKEN).orElse(token);
        streamingClient  = new AstraStreamingClient(token);
        System.out.println("Tenant: " + tmpTenant);
    }


    @Test
    @Order(1)
    @DisplayName("Create a Tenant for stats")
    public void shouldCreateTenant() throws InterruptedException {
        // When
        streamingClient.create(CreateTenant.builder()
                .tenantName(tmpTenant)
                .userEmail("astra-cli@datastax.com").build());
        Thread.sleep(1000);
        // Then
        assertTrue(streamingClient.exist(tmpTenant));
    }


    @Test
    @Order(2)
    @DisplayName("Test Pulsar Client")
    public void testPulsarClient() throws PulsarClientException {
        Tenant tenant = streamingClient.find(tmpTenant).get();
        PulsarClient cli = new PulsarClientProvider(tenant.getBrokerServiceUrl(), tenant.getPulsarToken()).get();
        cli.shutdown();
    }

    @Test
    @Order(3)
    @DisplayName("Test Pulsar ADmin")
    public void testPulsarAdmin() throws PulsarAdminException {
        Tenant tenant = streamingClient.find(tmpTenant).get();
        PulsarAdmin pa = new PulsarAdminProvider(tenant.getWebServiceUrl(), tenant.getPulsarToken()).get();
        System.out.println(pa.tenants().getTenantInfo(tmpTenant).toString());
        pa.namespaces().getNamespaces(tmpTenant).forEach(System.out::println);
    }


    @Test
    @Order(4)
    @DisplayName("Delete a Tenant for stats")
    public void shouldDeleteTenant() throws InterruptedException {
        // When
        streamingClient.delete(tmpTenant);
        // Then
        Thread.sleep(1000);
        assertFalse(streamingClient.exist(tmpTenant));
    }



}
