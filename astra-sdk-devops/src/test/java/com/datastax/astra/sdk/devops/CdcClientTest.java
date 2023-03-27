package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import com.dtsx.astra.sdk.streaming.domain.CdcDefinition;
import com.dtsx.astra.sdk.streaming.domain.CreateTenant;
import com.dtsx.astra.sdk.utils.TestUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.junit.Assert;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CdcClientTest extends AbstractDevopsApiTest  {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(CdcClientTest.class);

    /** Temporary tenant. */
    private static String tmpTenant = "sdk-java-junit-1";// + UUID.randomUUID().toString().substring(0,7);

    @Test
    @Order(1)
    public void shouldCreateDbAndTenant() throws Exception {
        LOGGER.info("CDC Test Initialization:");
        // Create Tenant
        if (!getStreamingClient().exist(tmpTenant)) {
            getStreamingClient().create(CreateTenant.builder()
                    .tenantName(tmpTenant)
                    .userEmail("astra-cli@datastax.com")
                    .cloudProvider("gcp")
                    .cloudRegion("useast1")
                    .build());
        }
        LOGGER.info("+ Using tenant {}", tmpTenant);
        Assert.assertTrue(getStreamingClient().exist(tmpTenant));

        // Create Db
        DatabaseClient dc = getSdkTestDatabaseClient();
        Database db = dc.get();
        LOGGER.info("+ Using db id={}, region={}", db.getId(), db.getInfo().getRegion());
        TestUtils.waitForDbStatus(dc, DatabaseStatusType.ACTIVE, 500);
        // Create Table
        String urlCreateTable = "https://"
                + dc.get().getId() + "-"
                + dc.get().getInfo().getRegion()
                + ".apps.astra.datastax.com/api/rest/v2/schemas/keyspaces/"
                + SDK_TEST_KEYSPACE + "/tables";
        String body = " { \"name\": \"table1\"," +
                "         \"ifNotExists\": true," +
                "         \"columnDefinitions\": [" +
                "           {\"name\":\"col1\"," +
                "            \"typeDefinition\":\"text\"," +
                "            \"static\":false" +
                "           }, " +
                "           {\"name\":\"col2\"," +
                "            \"typeDefinition\":\"text\"," +
                "            \"static\":false" +
                "           }" +
                "          ]," +
                "          \"primaryKey\":   { \"partitionKey\":[\"col1\"] }," +
                "          \"tableOptions\": { \"defaultTimeToLive\":0 }" +
                "        }";
        HttpUriRequestBase req = new HttpPost(urlCreateTable);
        req.addHeader("Content-Type", "application/json");
        req.addHeader("Accept", "application/json");
        req.addHeader("X-Cassandra-Token", getToken());
        req.setEntity(new StringEntity(body, ContentType.TEXT_PLAIN));
        CloseableHttpResponse res = HttpClients.createDefault().execute(req);
        LOGGER.info("+ Table creation status={}", res.getCode());
        body = body.replaceAll("table1", "table2");
        req.setEntity(new StringEntity(body, ContentType.TEXT_PLAIN));
        LOGGER.info("+ Table creation status={}", HttpClients.createDefault().execute(req).getCode());
    }

    @Test
    @Order(2)
    public void shouldCreateCdcTable1() throws InterruptedException {
        // when
        getSdkTestDatabaseClient()
                .cdc()
                .create(SDK_TEST_KEYSPACE, "table1", tmpTenant, 3);
        // Then
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 500);
        Optional<CdcDefinition> optCdc = getSdkTestDatabaseClient()
                .cdc()
                .findByDefinition(SDK_TEST_KEYSPACE, "table1", tmpTenant);
        Assertions.assertTrue(optCdc.isPresent());
        LOGGER.info("+ Cdc created id={} status={}", optCdc.get().getConnectorName(), optCdc.get().getCodStatus());
    }

    @Test
    @Order(3)
    public void shouldCreateCdcTable2() throws InterruptedException {
        // when
        getSdkTestDatabaseClient()
                .cdc()
                .create(SDK_TEST_KEYSPACE, "table2", tmpTenant, 3);
        // Then
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 500);
        Optional<CdcDefinition> optCdc = getSdkTestDatabaseClient()
                .cdc()
                .findByDefinition(SDK_TEST_KEYSPACE, "table2", tmpTenant);
        Assertions.assertTrue(optCdc.isPresent());
        LOGGER.info("+ Cdc created id={} status={}", optCdc.get().getConnectorName(), optCdc.get().getCodStatus());
    }

    @Test
    @Order(4)
    @DisplayName("List CDC from a DB")
    public void shouldListDbCdc() {
        Assertions.assertEquals(2, getSdkTestDatabaseClient()
                .cdc()
                .findAll()
                .count());
    }

    @Test
    @Order(5)
    public void shouldListTenantCdc() {
       getStreamingClient()
                .tenant(tmpTenant)
                .cdc()
                .list()
                .map(cdc -> cdc.getConnectorName() + " | " + cdc.getDatabaseName() + " | " + cdc.getKeyspace() +  " | " + cdc.getDatabaseTable())
                .forEach(System.out::println);
    }

    @Test
    @Order(6)
    public void shouldDeleteCdcWithDB() {
        getSdkTestDatabaseClient().cdc().delete(getSdkTestDatabaseClient()
                .cdc()
                .findByDefinition(SDK_TEST_KEYSPACE, "table2", tmpTenant)
                .get()
                .getConnectorName());
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 500);
        LOGGER.info("+ Deleting for table2");
    }

    @Test
    @Order(7)
    public void shouldDeleteCdcWithDefinition() {
        getSdkTestDatabaseClient().cdc().delete(SDK_TEST_KEYSPACE, "table1", tmpTenant);
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 500);
        LOGGER.info("+ Deleting for table1 ");
    }

    @Test
    @Order(8)
    public void shouldCreateCdcWithTenant() {
        LOGGER.info("Create CDC from tenant");
        getStreamingClient()
                .tenant(tmpTenant)
                .cdc()
                .create(getSdkTestDatabaseClient().get().getId(), SDK_TEST_KEYSPACE, "table1", 3);
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 500);
    }

    @Test
    @Order(9)
    public void shouldDeleteCdcWithTenant() {
        getStreamingClient()
                .tenant(tmpTenant)
                .cdc()
                .delete(getSdkTestDatabaseClient().get().getId(), SDK_TEST_KEYSPACE, "table1");
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 500);
    }


}
