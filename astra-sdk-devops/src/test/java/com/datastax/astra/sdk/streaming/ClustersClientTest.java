package com.datastax.astra.sdk.streaming;

import com.datastax.astra.sdk.devops.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.streaming.domain.Cluster;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ClustersClientTest extends AbstractDevopsApiTest {

    @Test
    @Order(1)
    @DisplayName("Find all cluster of an organization")
    public void shouldFindAllClusters() {
        // Given
        AstraStreamingClient cli = new AstraStreamingClient(getToken());
        // When
        List<String> clusters = cli.clusters().findAll().map(Cluster::getClusterName).collect(Collectors.toList());
        // Then
        Assertions.assertNotNull(clusters);
        Assertions.assertTrue(clusters.contains("pulsar-gcp-useast1"));
        Assertions.assertNotNull(clusters);
    }

    @Test
    @Order(2)
    @DisplayName("Find a cluster from its name")
    public void shouldFindClusters() {
        // Given
        AstraStreamingClient cli = new AstraStreamingClient(getToken());
        // Then
        Assertions.assertTrue(cli.clusters().find("pulsar-gcp-useast1").isPresent());
        Assertions.assertTrue(cli.clusters().exist("pulsar-gcp-useast1"));
        Assertions.assertFalse(cli.clusters().exist("invalid"));
    }

}
