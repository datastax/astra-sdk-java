package com.datastax.astra.sdk.streaming;

import com.datastax.astra.sdk.devops.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.streaming.domain.Statistics;
import com.dtsx.astra.sdk.streaming.domain.StreamingRegion;
import com.dtsx.astra.sdk.streaming.domain.Tenant;
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
import java.util.stream.Collectors;

@TestMethodOrder(OrderAnnotation.class)
public class StreamingClientTest extends AbstractDevopsApiTest  {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingClientTest.class);

    @Test
    @Order(1)
    public void shouldFailInvalidParams() {
        LOGGER.info("Parameter validation");
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraStreamingClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraStreamingClient((String) null));
    }
    
    @Test
    @Order(2)
    public void shouldListTenants() {
        LOGGER.info("List Tenants");
        Set<String> tenants = new AstraStreamingClient(getToken())
                .findAll()
                .map(Tenant::getTenantName)
                .collect(Collectors.toSet());
        Assert.assertNotNull(tenants);
        LOGGER.info(tenants.toString());
    }
    
    @Test
    @Order(3)
    public void shouldListProviders() {
        LOGGER.info("List Providers");
        Map<String, List<String>> providers = new AstraStreamingClient(getToken()).providers().findAll();
        Assert.assertTrue(providers.size()>0);
        LOGGER.info(providers.toString());
    }

    @Test
    @Order(4)
    public void should_list_clusters() {
        LOGGER.info("List clusters");
        new AstraStreamingClient(getToken()).clusters().findAll().forEach(cluster -> {
            LOGGER.info(cluster.getClusterName());
        });
    }

    @Test
    @Order(4)
    public void shouldListRegions() {
        List<StreamingRegion> regions = new AstraStreamingClient(getToken())
                .regions().findAllServerless()
                .collect(Collectors.toList());
        Assertions.assertFalse(regions.isEmpty());
        regions.stream().map(StreamingRegion::getName).forEach(System.out::println);
    }

    private static String tmpTenant;

    @Test
    @Order(11)
    public void should_statsTenant() {
        new AstraStreamingClient(getToken())
                .tenant("clun-gcp-east1")
                .stats()
                .namespaces()
                .map(Statistics::getName)
                .forEach(System.out::println);
    }

    @Test
    @Order(12)
    public void should_statsNamespace() {
        new AstraStreamingClient(getToken())
                .tenant("clun-gcp-east1")
                .stats()
                .namespace("default")
                .ifPresent(stats -> System.out.println(stats.getName()));
    }

    @Test
    @Order(13)
    public void should_statsTopics() {
        new AstraStreamingClient(getToken())
                .tenant("clun-gcp-east1")
                .stats()
                .topics()
                .map(Statistics::getName)
                .forEach(System.out::println);
    }

    @Test
    @Order(13)
    public void should_statsTopicsNamespace() {
        new AstraStreamingClient(getToken())
                .tenant("clun-gcp-east1")
                .stats()
                .topics("test")
                .map(Statistics::getName)
                .forEach(System.out::println);
    }

}