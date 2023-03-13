package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.streaming.domain.CdcDefinition;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CdcClientTest extends AbstractDevopsApiTest  {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CdcClientTest.class);

    @Test
    @Order(1)
    @DisplayName("List CDC from a DB")
    public void shouldListDbCdc() {
        getSdkTestDbClient()
                .cdc().findAll()
                .map(CdcDefinition::getConnectorName)
                .forEach(System.out::println);
    }

    @Test
    @Order(2)
    public void shouldListTenantCdc() {
       getStreamingClient()
                .tenant("clun-gcp-east1")
                .cdc().list()
                .map(cdc -> cdc.getConnectorName() + " | " + cdc.getDatabaseName() + " | " + cdc.getKeyspace() +  " | " + cdc.getDatabaseTable())
                .forEach(System.out::println);
    }

    @Test
    @Order(3)
    public void shouldCreateCdcWithDB() {
        //getSdkTestDbClient().createCdc(SDK_TEST_KEYSPACE, "foo1", "clun-gcp-east1", 3);
        getDatabasesClient().name("db2").cdc().create("ks2", "users", "clun-gcp-east1", 3);

    }

    @Test
    @Order(3)
    public void shouldDeleteCdcWithDB() {
        getSdkTestDbClient().cdc().delete("9c68f46-foo1");
    }

    @Test
    @Order(3)
    public void shouldDeleteCdcWithDefinition() {
        getSdkTestDbClient().cdc().delete("ks1", "foo1", "clun-gcp-east1");
    }

    @Test
    @Order(4)
    public void shouldCreateCdcWithTenant() {
        LOGGER.info("Create CDC");
        getStreamingClient()
                .tenant("clun-gcp-east1")
                .cdc()
                .create(getSdkTestDbClient().get().getId(), SDK_TEST_KEYSPACE, "foo2", 3);
    }

    @Test
    @Order(9)
    public void shouldDeleteCdcWithTenant() {
        getStreamingClient()
                .tenant("clun-gcp-east1")
                .cdc()
                .delete(getSdkTestDbClient().get().getId(), SDK_TEST_KEYSPACE, "foo2");
    }

}
