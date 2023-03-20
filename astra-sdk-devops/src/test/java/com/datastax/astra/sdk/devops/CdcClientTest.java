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
        getSdkTestDatabaseClient()
                .cdc()
                .findAll()
                .map(CdcDefinition::getConnectorName)
                .forEach(System.out::println);
    }

    @Test
    @Order(2)
    public void shouldListTenantCdc() {
       getStreamingClient()
                .tenant("clun-gcp-east1")
                .cdc()
                .list()
                .map(cdc -> cdc.getConnectorName() + " | " + cdc.getDatabaseName() + " | " + cdc.getKeyspace() +  " | " + cdc.getDatabaseTable())
                .forEach(System.out::println);
    }

    @Test
    @Order(3)
    public void shouldCreateCdcWithDB() {
        getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .cdc()
                .create(SDK_TEST_KEYSPACE2, "users", "clun-gcp-east1", 3);

    }

    @Test
    @Order(3)
    public void shouldDeleteCdcWithDB() {
        getSdkTestDatabaseClient().cdc().delete("9c68f46-foo1");
    }

    @Test
    @Order(3)
    public void shouldDeleteCdcWithDefinition() {
        getSdkTestDatabaseClient().cdc().delete("ks1", "foo1", "clun-gcp-east1");
    }

    @Test
    @Order(4)
    public void shouldCreateCdcWithTenant() {
        LOGGER.info("Create CDC");
        getStreamingClient()
                .tenant("clun-gcp-east1")
                .cdc()
                .create(getSdkTestDatabaseClient().get().getId(), SDK_TEST_KEYSPACE, "foo2", 3);
    }

    @Test
    @Order(9)
    public void shouldDeleteCdcWithTenant() {
        getStreamingClient()
                .tenant("clun-gcp-east1")
                .cdc()
                .delete(getSdkTestDatabaseClient().get().getId(), SDK_TEST_KEYSPACE, "foo2");
    }

}
