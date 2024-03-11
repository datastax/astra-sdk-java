package com.datastax.astra.devops.db;

import com.datastax.astra.devops.AbstractDevopsApiTest;
import com.datastax.astra.devops.db.domain.AccessListAddressRequest;
import com.datastax.astra.devops.db.domain.CloudProviderType;
import com.datastax.astra.devops.db.domain.DatabaseStatusType;
import com.datastax.astra.devops.db.domain.Datacenter;
import com.datastax.astra.devops.db.exception.KeyspaceAlreadyExistException;
import com.datastax.astra.devops.db.exception.KeyspaceNotFoundException;
import com.datastax.astra.devops.db.exception.RegionAlreadyExistException;
import com.datastax.astra.devops.db.exception.RegionNotFoundException;
import com.datastax.astra.devops.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
        Assertions.assertFalse(getSdkTestDatabaseClient().keyspaces().exist(SDK_TEST_KEYSPACE2));
        // When
        getSdkTestDatabaseClient().keyspaces().create(SDK_TEST_KEYSPACE2);
        Assertions.assertEquals(DatabaseStatusType.MAINTENANCE, getSdkTestDatabaseClient().find().get().getStatus());
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 300);

        // When
        Assertions.assertEquals(DatabaseStatusType.ACTIVE, getSdkTestDatabaseClient().find().get().getStatus());
        // Then
        Assertions.assertTrue(getSdkTestDatabaseClient().keyspaces().exist(SDK_TEST_KEYSPACE2));
        // Cannot create keyspace that already exist
        Assertions.assertThrows(KeyspaceAlreadyExistException.class,
                () -> getSdkTestDatabaseClient().keyspaces().create(SDK_TEST_KEYSPACE2));
    }

    @Test
    @Order(2)
    @DisplayName("02. Delete a new Keyspace")
    public void shouldDeleteKeyspacesTest() {
        // Givem
        Assertions.assertThrows(KeyspaceNotFoundException.class,
                () -> getSdkTestDatabaseClient().keyspaces().delete("invalid"));
        // Given
        Assertions.assertTrue(getSdkTestDatabaseClient().keyspaces().exist(SDK_TEST_KEYSPACE2));
        // When
        getSdkTestDatabaseClient().keyspaces().delete(SDK_TEST_KEYSPACE2);
        // Then
        TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.ACTIVE, 300);
        Assertions.assertFalse(getSdkTestDatabaseClient().keyspaces().exist(SDK_TEST_KEYSPACE2));
    }

    @Test
    @Order(3)
    @DisplayName("03. Download Default Cloud SecureBundle")
    public void shouldDownloadDefaultScbTest() {
        // Given
        String randomFile = "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
        Assertions.assertFalse(new File(randomFile).exists());
        // When
        getSdkTestDatabaseClient().downloadDefaultSecureConnectBundle(randomFile);
        // Then
        Assertions.assertTrue(new File(randomFile).exists());
        getSdkTestDatabaseClient().downloadSecureConnectBundle(SDK_TEST_DB_REGION, randomFile);
        // When
        byte[] data = getSdkTestDatabaseClient().downloadDefaultSecureConnectBundle();
        // Then
        Assertions.assertTrue(data != null && data.length > 1000);
    }

    @Test
    @Order(4)
    @DisplayName("04. Download Region Cloud SecureBundle")
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
        // When
        byte[] data = getSdkTestDatabaseClient().downloadSecureConnectBundle(SDK_TEST_DB_REGION);
        // Then
        Assertions.assertTrue(data != null && data.length > 1000);
    }


    @Test
    @Order(5)
    @DisplayName("05. Download All Cloud Secured Bundle")
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
    @Order(6)
    @DisplayName("06. Should not PARK Serverless")
    public void shouldNotParkServerlessTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getSdkTestDatabaseClient().park());
    }

    @Test
    @Order(7)
    @DisplayName("07. Should not UNPARK Serverless")
    public void shouldNotUnParkServerlessTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getSdkTestDatabaseClient().unpark());
    }

    @Test
    @Order(8)
    @DisplayName("08. Should not RESIZE Serverless")
    public void shouldNotResizeServerlessTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> getSdkTestDatabaseClient().resize(2));
    }

    @Test
    @Order(9)
    @DisplayName("09. Should not RESET PASSWORD Serverless")
    public void shouldNotResetPasswordTest() {
        Assertions.assertThrows(RuntimeException.class,
                () -> getSdkTestDatabaseClient().resetPassword("token", "cedrick1"));
    }

     @Test
     @Order(10)
     @DisplayName("10. Should List regions")
     public void shouldListRegionsTest() {
         List<Datacenter> regions = getSdkTestDatabaseClient().datacenters().findAll().collect(Collectors.toList());
         Assertions.assertEquals(1, regions.size());
         Assertions.assertEquals(SDK_TEST_DB_REGION, regions.get(0).getRegion());
     }

    @Test
    @Order(11)
    @DisplayName("11. Should find region")
    public void shouldFindRegionsTest() {
        Assertions.assertTrue(getSdkTestDatabaseClient().datacenters().findByRegionName(SDK_TEST_DB_REGION).isPresent());
        Assertions.assertFalse(getSdkTestDatabaseClient().datacenters().findByRegionName("eu-west-1").isPresent());
    }

    @Test
    @Order(12)
    @DisplayName("12. Should not remove invalid region")
    public void shouldNotRemoveRegionsTest() {
        Assertions.assertThrows(RegionNotFoundException.class,
                () -> getSdkTestDatabaseClient().datacenters().delete("eu-west-1"));
    }

    @Test
    @Order(13)
    @DisplayName("13. Should not add existing region")
    public void shouldNotAddRegionsTest() {
        Assertions.assertThrows(RegionAlreadyExistException.class,
                () -> getSdkTestDatabaseClient()
                        .datacenters()
                        .create("serverless", CloudProviderType.GCP, SDK_TEST_DB_REGION));
    }


    @Test
    @Order(14)
    @DisplayName("14. Should add a region")
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
    @Order(15)
    @DisplayName("15. Should delete a region")
    public void shouldDeleteRegionTest() {
//        getDatabasesClient().databaseByName("aws_multiple_regions").datacenters().delete("eu-central-1");
    }

    @Test
    @Order(16)
    @DisplayName("16. Add access list")
    public void shouldAddAccessListsDb() throws InterruptedException {
        AccessListAddressRequest a1 = new AccessListAddressRequest("255.255.255.255", "test2");
        AccessListAddressRequest a2 = new AccessListAddressRequest("254.254.254.254", "test3");
        getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .accessLists().addAddress(a1, a2);
        Thread.sleep(500);
        Assertions.assertTrue(getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .accessLists().get()
                .getAddresses().stream()
                .anyMatch(al -> al.getDescription().equalsIgnoreCase("test2")));
        Thread.sleep(500);
    }

    @Test
    @Order(18)
    @DisplayName("18. Update")
    public void shouldUpdateAccessListsDb() throws InterruptedException {
        AccessListAddressRequest a3 = new AccessListAddressRequest("254.254.254.254/32", "updatedText");
        getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .accessLists()
                .update(a3);
        // Async Operation
        Thread.sleep(500);
        Assertions.assertTrue(getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .accessLists().get()
                .getAddresses().stream()
                .anyMatch(al -> al.getDescription().equalsIgnoreCase("updatedText")));
    }

    @Test
    @Order(19)
    @DisplayName("19. Delete Access List")
    public void shouldDeleteAllAccessListsDb() {
        getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .accessLists().delete();
        Assertions.assertFalse(getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .accessLists().get()
                .getAddresses().stream()
                .anyMatch(al -> al.getDescription().equalsIgnoreCase("updatedText")));
    }

    @Test
    @Order(20)
    @DisplayName("20. Should terminate DB")
    public void shouldTerminateDbTest() {
        Assertions.assertTrue(getSdkTestDatabaseClient().exist());
        //getSdkTestDatabaseClient().delete();
        //TestUtils.waitForDbStatus(getSdkTestDatabaseClient(), DatabaseStatusType.TERMINATED, 300);
        //Assert.assertEquals(0, getDatabasesClient().findByName(SDK_TEST_DB_NAME).count());
    }

}
