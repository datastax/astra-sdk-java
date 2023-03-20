package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.AstraDbClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import com.dtsx.astra.sdk.utils.TestUtils;
import org.junit.jupiter.api.*;

/**
 * Tests Operations on Databases level.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabasesClientTest extends AbstractDevopsApiTest {

    // Client to be tested
    private static AstraDbClient dbsClient;

    @Test
    @BeforeEach
    public void initClient() {
        if (dbsClient == null) {
            dbsClient = new AstraDbClient(getToken());
        }
    }

    @Test
    @Order(1)
    @DisplayName("01. Initialization with Invalid Parameters")
    public void failInitializationsWithInvalidParamsTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraDbClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraDbClient(null));
    }

    @Test
    @Order(2)
    @DisplayName("02. findAll() retrieve some data")
    public void findAllTest() {
        Assertions.assertTrue(dbsClient.findAll().findAny().isPresent());
    }

    @Test
    @Order(3)
    @DisplayName("03. create sdk_serverless if needed")
    public void createServerlessDbTest() {
        String dbId;
        if (!dbsClient.findByName(SDK_TEST_DB_NAME).findAny().isPresent()) {
            dbId = dbsClient.create(DatabaseCreationRequest
                    .builder()
                    .name(SDK_TEST_DB_NAME)
                    .keyspace(SDK_TEST_KEYSPACE)
                    .cloudRegion(SDK_TEST_DB_REGION)
                    .build());
            System.out.println("- Creating db '" + SDK_TEST_DB_NAME + "' (id=" + dbId + ")");
        } else {
            dbId = dbsClient.databaseByName(SDK_TEST_DB_NAME).find().get().getId();
        }

        // Then
        Assertions.assertNotEquals(0, dbsClient.findByName(SDK_TEST_DB_NAME).count());
        Assertions.assertTrue( dbsClient.findById(dbId).isPresent());
        Assertions.assertNotNull(dbsClient.database(dbId).get());
        // When
        System.out.println("- Db Exists, waiting for ACTIVE STATUS");
        TestUtils.waitForDbStatus(dbsClient.database(dbId), DatabaseStatusType.ACTIVE, 300);
        // When
        Assertions.assertEquals(DatabaseStatusType.ACTIVE, dbsClient.database(dbId).get().getStatus());
    }

    @Test
    @Order(4)
    @DisplayName("04. create sdk_classic if possible")
    public void createClassicDbTest() {
        Assertions.assertEquals(0, dbsClient.findByName("classic20").count());
        // When Creating a DB
        try {
            dbsClient.create(DatabaseCreationRequest
                    .builder()
                    .name("classic20")
                    .keyspace("classic")
                    .cloudProvider(CloudProviderType.AWS)
                    .cloudRegion("us-east-2")
                    .tier("C20")
                    .capacityUnit(1)
                    .build());
        } catch(IllegalArgumentException iex) {
            System.out.println(iex.getMessage());
        }
    }

    @Test
    @Order(5)
    @DisplayName("05. find by name")
    public void shouldFindDatabaseByNameTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.databaseByName(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.databaseByName(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.databaseByName("i-like-cheese"));
        Assertions.assertTrue(dbsClient.findAllNonTerminated().anyMatch(db -> SDK_TEST_DB_NAME.equals(db.getInfo().getName())));
        Assertions.assertTrue(dbsClient.findAll().anyMatch(db -> SDK_TEST_DB_NAME.equals(db.getInfo().getName())));
        Assertions.assertTrue(dbsClient.findByName(SDK_TEST_DB_NAME).count() > 0);
        Assertions.assertTrue(dbsClient.databaseByName(SDK_TEST_DB_NAME).exist());
    }

    @Test
    @Order(6)
    @DisplayName("06. find by id")
    public void shouldFindDatabaseByIdTest() {
        // --> Getting a valid id
        Assertions.assertTrue(dbsClient.databaseByName(SDK_TEST_DB_NAME).exist());
        String dbId = dbsClient.databaseByName(SDK_TEST_DB_NAME).get().getId();
        // <---
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.database(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.database(null));

        Assertions.assertFalse(dbsClient.database("invalid").exist());

        DatabaseClient dbClient = dbsClient.database(dbId);
        Assertions.assertNotNull(dbClient);
        Assertions.assertTrue(dbClient.exist());
        Assertions.assertTrue(dbClient.find().isPresent());
        Assertions.assertNotNull(dbClient.get());

        Database db = dbClient.get();
        Assertions.assertEquals(dbId, db.getId());
        Assertions.assertNotNull(db.getMetrics());
        Assertions.assertNotNull(db.getStorage());
        Assertions.assertEquals(SDK_TEST_KEYSPACE, db.getInfo().getKeyspace());
    }

    //@AfterAll
    //public static void cleanup() {
    //    dbsClient.findByName(DB_SERVERLESS).findFirst().ifPresent(db -> {
    //        dbsClient.id(db.getId()).delete();
    //    });
    //    dbsClient.findByName(DB_CLASSIC).findFirst().ifPresent(db -> {
    //        dbsClient.id(db.getId()).delete();
    //    });
    //}

}
