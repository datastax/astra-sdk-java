package com.datastax.astra.sdk.devops;

import com.datastax.astra.sdk.streaming.StreamingClientTest;
import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.DatabasesClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.streaming.StreamingClient;
import com.dtsx.astra.sdk.streaming.TenantClient;
import com.dtsx.astra.sdk.streaming.domain.CdcDefinition;
import com.dtsx.astra.sdk.streaming.domain.CreateCdc;
import com.dtsx.astra.sdk.streaming.domain.DeleteCdc;
import org.junit.Assert;
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
                .cdcs()
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
        getDatabasesClient().name("db2").createCdc("ks2", "users", "clun-gcp-east1", 3);

    }

    @Test
    @Order(3)
    public void shouldDeleteCdcWithDB() {
        getSdkTestDbClient().deleteCdc("9c68f46-foo1");
    }

    @Test
    @Order(3)
    public void shouldDeleteCdcWithDefinition() {
        getSdkTestDbClient().deleteCdc("ks1", "foo1", "clun-gcp-east1");
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
