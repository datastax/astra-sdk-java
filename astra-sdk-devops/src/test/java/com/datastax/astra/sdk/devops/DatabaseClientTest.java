package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.domain.*;
import com.dtsx.astra.sdk.db.exception.KeyspaceAlreadyExistException;
import com.dtsx.astra.sdk.db.exception.RegionAlreadyExistException;
import com.dtsx.astra.sdk.db.exception.RegionNotFoundException;
import com.dtsx.astra.sdk.utils.TestUtils;
import org.junit.Assert;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Tests Operations on Databases level.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabaseClientTest extends AbstractDevopsApiTest {

    @Test
    @Order(1)
    @DisplayName("01. Create a new Keyspace")
    public void shouldCreateKeyspacesTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getSdkTestDatabaseClient().keyspaces().create(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> getSdkTestDatabaseClient().keyspaces().create(null));
        // Given
        Assertions.assertFalse(getSdkTestDatabaseClient().find().get().getInfo().getKeyspaces().contains(SDK_TEST_KEYSPACE2));
        // When
        getSdkTestDatabaseClient().keyspaces().create(SDK_TEST_KEYSPACE2);
        Assertions.assertEquals(DatabaseStatusType.MAINTENANCE, getSdkTestDatabaseClient().find().get().getStatus());
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 300);

        // When
        Assertions.assertEquals(DatabaseStatusType.ACTIVE, getSdkTestDatabaseClient().find().get().getStatus());
        // Then
        Database db = getSdkTestDatabaseClient().get();
        Assertions.assertTrue(db.getInfo().getKeyspaces().contains(SDK_TEST_KEYSPACE2));
        // Cannot create keyspace that already exist
        Assertions.assertThrows(KeyspaceAlreadyExistException.class,
                () -> getSdkTestDatabaseClient().keyspaces().create(SDK_TEST_KEYSPACE2));
    }

    @Test
    @Order(2)
    @DisplayName("02. Download Default Cloud SecureBundle")
    public void shouldDownloadDefaultScbTest() {
        // Given
        String randomFile = "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
        Assertions.assertFalse(new File(randomFile).exists());
        // When
        getSdkTestDatabaseClient().downloadDefaultSecureConnectBundle(randomFile);
        // Then
        Assertions.assertTrue(new File(randomFile).exists());
        getSdkTestDatabaseClient().downloadSecureConnectBundle(SDK_TEST_DB_REGION, randomFile);
    }

    @Test
    @Order(3)
    @DisplayName("03. Download Region Cloud SecureBundle")
    public void shouldDownloadRegionScbTest() {
        // Given
        String randomFile = "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
        Assertions.assertFalse(new File(randomFile).exists());
        // When
        Assertions.assertThrows(RegionNotFoundException.class, () ->
                getSdkTestDatabaseClient().downloadSecureConnectBundle("eu-west-1", randomFile));
        // When
        getSdkTestDatabaseClient().downloadSecureConnectBundle(SDK_TEST_DB_REGION, randomFile);
        Assertions.assertTrue(new File(randomFile).exists());
    }

    @Test
    @Order(4)
    @DisplayName("04. Download All Cloud Secured Bundle")
    public void shouldDownloadAllScbTest() {
        // When
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                getSdkTestDatabaseClient().downloadAllSecureConnectBundles("/invalid"));
        // Given
        String randomFolder = "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "");
        File targetFolder = new File(randomFolder);
        targetFolder.mkdirs();
        Assertions.assertTrue(targetFolder.exists());
        Assertions.assertEquals(0, targetFolder.listFiles().length);
        // When
        getSdkTestDatabaseClient().downloadAllSecureConnectBundles(randomFolder);
        Assertions.assertEquals(1, targetFolder.listFiles().length);
    }

    @Test
    @Order(5)
    @DisplayName("05. Should not PARK Serverless")
    public void shouldNotParkServerlessTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getSdkTestDatabaseClient().park());
    }

    @Test
    @Order(6)
    @DisplayName("06. Should not UNPARK Serverless")
    public void shouldNotUnParkServerlessTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getSdkTestDatabaseClient().unpark());
    }

    @Test
    @Order(7)
    @DisplayName("07. Should not RESIZE Serverless")
    public void shouldNotResizeServerlessTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> getSdkTestDatabaseClient().resize(2));
    }

    @Test
    @Order(8)
    @DisplayName("08. Should not RESET PASSWORD Serverless")
    public void shouldNotResetPasswordTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> getSdkTestDatabaseClient().resetPassword("token", "cedrick1"));
    }

     @Test
     @Order(9)
     @DisplayName("09. Should List regions")
     public void shouldListRegionsTest() {
         List<Datacenter> regions = getSdkTestDatabaseClient().datacenters().findAll().collect(Collectors.toList());
         Assertions.assertEquals(1, regions.size());
         Assertions.assertEquals(SDK_TEST_DB_REGION, regions.get(0).getRegion());
     }

    @Test
    @Order(10)
    @DisplayName("10. Should find region")
    public void shouldFindRegionsTest() {
        Assertions.assertTrue(getSdkTestDatabaseClient().datacenters().findByRegionName(SDK_TEST_DB_REGION).isPresent());
        Assertions.assertFalse(getSdkTestDatabaseClient().datacenters().findByRegionName("eu-west-1").isPresent());
    }

    @Test
    @Order(11)
    @DisplayName("11. Should not remove invalid region")
    public void shouldNotRemoveRegionsTest() {
        Assertions.assertThrows(RegionNotFoundException.class,
                () -> getSdkTestDatabaseClient().datacenters().delete("eu-west-1"));
    }

    @Test
    @Order(12)
    @DisplayName("12. Should not add existing region")
    public void shouldNotAddRegionsTest() {
        Assertions.assertThrows(RegionAlreadyExistException.class,
                () -> getSdkTestDatabaseClient()
                        .datacenters()
                        .create("serverless", CloudProviderType.GCP, SDK_TEST_DB_REGION));
    }


    @Test
    @DisplayName("Should add a region")
    public void shouldAddRegionTest() {
        // create an AWS DB
//        if (getDatabasesClient().findByName("aws_multiple_regions").count() == 0) {
//            getDatabasesClient().create(DatabaseCreationRequest
//                    .builder()
//                    .name("aws_multiple_regions")
//                    .keyspace("ks")
//                    .cloudRegion("us-east-1")
//                    .build());
//        }
//        DatabaseClient dbClientAws = getDatabasesClient().databaseByName("aws_multiple_regions");
//        TestUtils.waitForDbStatus(dbClientAws, DatabaseStatusType.ACTIVE, 300);
//        dbClientAws.datacenters().create("serverless", CloudProviderType.AWS, "eu-central-1");
    }

    @Test
    @DisplayName("Should delete a region")
    public void shouldDeleteRegionTest() {
//        getDatabasesClient().databaseByName("aws_multiple_regions").datacenters().delete("eu-central-1");
    }

    @Test
    @Order(13)
    @DisplayName("13. Should terminate DB")
    public void shouldTerminateDbTest() {
        Assert.assertTrue(getSdkTestDatabaseClient().exist());
        //getSdkTestDatabaseClient().delete();
        //TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.TERMINATED, 300);
        //Assert.assertEquals(0, getDatabasesClient().findByName(SDK_TEST_DB_NAME).count());
    }

}
