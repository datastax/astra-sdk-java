package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.db.DatabaseClient;
import com.dtsx.astra.sdk.db.DatabasesClient;
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

    // Test Constants
    static final String DB_SERVERLESS   = "sdk_serverless";
    static final String DB_CLASSIC      = "sdk_classic";
    static final String TEST_KS_NAME    = "java";

    // Client to be tested
    private static DatabasesClient dbsClient;

    @Test
    @BeforeEach
    public void initClient() {
        if (dbsClient == null) {
            dbsClient = new DatabasesClient(getToken());
        }
    }

    @Test
    @Order(1)
    @DisplayName("01. Initialization with Invalid Parameters")
    public void failInitializationsWithInvalidParamsTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DatabasesClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new DatabasesClient((String) null));
    }

    @Test
    @Order(2)
    @DisplayName("02. findAll() retrieve some data")
    public void findAllTest() {
        Assertions.assertTrue(dbsClient.findAll().count() > 0);
    }

    @Test
    @Order(3)
    @DisplayName("03. create sdk_serverless if needed")
    public void createServerlessDbTest() {
        String dbId = null;
        if (dbsClient.findByName(DB_SERVERLESS).count() == 0) {
            dbId = dbsClient.create(DatabaseCreationRequest
                    .builder()
                    .name(DB_SERVERLESS)
                    .keyspace(TEST_KS_NAME)
                    .cloudRegion("us-east4")
                    .build());
            System.out.println("- Creating db '" + DB_SERVERLESS + "' (id=" + dbId + ")");
        } else {
            dbId = dbsClient.name(DB_SERVERLESS).find().get().getId();
        }

        // Then
        Assertions.assertFalse( dbsClient.findByName(DB_SERVERLESS).count() == 0);
        Assertions.assertTrue( dbsClient.findById(dbId).isPresent());
        Assertions.assertNotNull(dbsClient.id(dbId).get());
        // When
        System.out.println("- Db Exists, waiting for ACTIVE STATUS");
        TestUtils.waitForDbStatus(dbsClient.id(dbId), DatabaseStatusType.ACTIVE, 300);
        // When
        Assertions.assertEquals(DatabaseStatusType.ACTIVE, dbsClient.id(dbId).get().getStatus());
    }

    @Test
    @Order(4)
    @DisplayName("04. create sdk_classic if possible")
    public void createClassicDbTest() {
        Assertions.assertTrue( dbsClient.findByName(DB_CLASSIC).count() == 0);
        // When Creating a DB
        try {
            String dbId = dbsClient.create(DatabaseCreationRequest
                    .builder()
                    .name(DB_CLASSIC)
                    .keyspace(TEST_KS_NAME)
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
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.name(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.name(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.name("i-like-cheese"));
        Assertions.assertTrue(dbsClient.findAllNonTerminated().anyMatch(db -> DB_SERVERLESS.equals(db.getInfo().getName())));
        Assertions.assertTrue(dbsClient.findAll().anyMatch(db -> DB_SERVERLESS.equals(db.getInfo().getName())));
        Assertions.assertTrue(dbsClient.findByName(DB_SERVERLESS).count() > 0);
        Assertions.assertTrue(dbsClient.name(DB_SERVERLESS).exist());
    }

    @Test
    @Order(6)
    @DisplayName("06. find by id")
    public void shouldFindDatabaseByIdTest() {
        // --> Getting a valid id
        Assertions.assertTrue(dbsClient.name(DB_SERVERLESS).exist());
        String dbId = dbsClient.name(DB_SERVERLESS).get().getId();
        // <---
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.id(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> dbsClient.id(null));

        Assertions.assertFalse(dbsClient.id("invalid").exist());

        DatabaseClient dbClient = dbsClient.id(dbId);
        Assertions.assertNotNull(dbClient);
        Assertions.assertTrue(dbClient.exist());
        Assertions.assertTrue(dbClient.find().isPresent());
        Assertions.assertNotNull(dbClient.get());

        Database db = dbClient.get();
        Assertions.assertEquals(dbId, db.getId());
        Assertions.assertNotNull(db.getMetrics());
        Assertions.assertNotNull(db.getStorage());
        Assertions.assertEquals(TEST_KS_NAME, db.getInfo().getKeyspace());
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
