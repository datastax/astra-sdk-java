package com.datastax.astra.sdk.streaming;

import com.datastax.astra.sdk.devops.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.streaming.AstraStreamingClient;
import com.dtsx.astra.sdk.streaming.domain.CreateTenant;
import org.junit.Assert;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Work with tenant.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TenantClientTest extends AbstractDevopsApiTest {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamingClientTest.class);

    private String tmpTenant;

    @Test
    @Order(1)
    public void shouldCreateTenant() throws InterruptedException {
        AstraStreamingClient sc  = new AstraStreamingClient(getToken());
        LOGGER.info("- Create a tenant");
        // Giving
        tmpTenant = "sdk-java-junit-" + UUID.randomUUID().toString().substring(0,7);
        // When
        Assert.assertFalse(sc.exist(tmpTenant));
        LOGGER.info("Tenant " + tmpTenant + " does not exist");
        sc.create( new CreateTenant(tmpTenant, "astra-cli@datastax.com"));
        Thread.sleep(1000);
        Assert.assertTrue(sc.exist(tmpTenant));
        LOGGER.info("Tenant " + tmpTenant + " now exist");
    }



    @Test
    @Order(10)
    public void shouldDeleteTenant() throws InterruptedException {
        //tmpTenant = "sdk_java_junit_32defeb";
        AstraStreamingClient sc  = new AstraStreamingClient(getToken());
        LOGGER.info("Delete a tenant");
        // Giving
        Assert.assertTrue(sc.exist(tmpTenant));
        LOGGER.info("Tenant " + tmpTenant + " exists");
        // When
        sc.delete(tmpTenant);
    }
}
