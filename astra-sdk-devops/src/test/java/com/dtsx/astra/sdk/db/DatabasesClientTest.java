package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import com.dtsx.astra.sdk.utils.TestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.Optional;

/**
 * Tests Operations on Databases level.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DatabasesClientTest extends AbstractDevopsApiTest {

    @Test
    @Order(1)
    @DisplayName("Initialization with Invalid Parameters")
    public void failInitializationsWithInvalidParamsTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraDbClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> new AstraDbClient(null));
    }

    @Test
    @Order(2)
    @DisplayName("Create DB and test existence")
    public void shouldCreateServerlessDb() {
        String dbId;
        Optional<Database> optDb = getDatabasesClient().databaseByName(SDK_TEST_DB_NAME).find();
        if (!optDb.isPresent()) {
            dbId = getDatabasesClient().create(DatabaseCreationRequest
                    .builder()
                    .name(SDK_TEST_DB_NAME)
                    .keyspace(SDK_TEST_KEYSPACE)
                    .cloudRegion(SDK_TEST_DB_REGION)
                    .build());
        } else {
            dbId = optDb.get().getId();
        }

        // Then
        Assertions.assertTrue(getDatabasesClient().findById(dbId).isPresent());
        Assertions.assertNotNull(getDatabasesClient().database(dbId).get());
        Assertions.assertTrue(getDatabasesClient().findByName(SDK_TEST_DB_NAME).count() > 0);
        // When
        TestUtils.waitForDbStatus(getDatabasesClient().database(dbId), DatabaseStatusType.ACTIVE, 500);
        // Then
        Database db = getDatabasesClient().database(dbId).get();
        Assertions.assertEquals(DatabaseStatusType.ACTIVE, db.getStatus());
    }


    @Test
    @Order(3)
    @DisplayName("findAll() retrieve some data")
    public void shouldFindAll() {
        Assertions.assertTrue(getDatabasesClient().findAll().findAny().isPresent());
    }

    @Test
    @Order(4)
    @DisplayName("Create sdk_classic if possible")
    public void shouldCreateAClassicDb() {
        Assertions.assertEquals(0, getDatabasesClient().findByName("classic20").count());
        // When Creating a DB
        try {
            getDatabasesClient().create(DatabaseCreationRequest
                    .builder()
                    .name("classic20")
                    .keyspace("classic")
                    .cloudProvider(CloudProviderType.AWS)
                    .cloudRegion("us-east-2")
                    .tier("C20")
                    .capacityUnit(1)
                    .build());
        } catch(IllegalArgumentException iex) {
            // Swallowing error if classic is not available.
        }
    }

    @Test
    @Order(5)
    @DisplayName("Create a DB with vector preview enabled")
    public void shouldCreateServerlessWithVector() {
        String dbId;
        Optional<Database> optDb = getDatabasesClient().findFirstByName(SDK_TEST_DB_VECTOR_NAME);
        if (!optDb.isPresent()) {
            dbId = getDatabasesClient().create(DatabaseCreationRequest
                    .builder()
                    .name(SDK_TEST_DB_VECTOR_NAME)
                    .keyspace(SDK_TEST_KEYSPACE)
                    .cloudRegion(SDK_TEST_DB_REGION)
                    .withVector()
                    .build());
        } else {
            dbId = optDb.get().getId();
        }
        // Then
        Assertions.assertTrue(getDatabasesClient().findById(dbId).isPresent());
        Assertions.assertNotNull(getDatabasesClient().database(dbId).get());
        Assertions.assertTrue(getDatabasesClient().findByName(SDK_TEST_DB_VECTOR_NAME).count() > 0);
        // When
        TestUtils.waitForDbStatus(getDatabasesClient().database(dbId), DatabaseStatusType.ACTIVE, 500);
        // Then
        Database finalDb = getDatabasesClient().database(dbId).get();
        Assertions.assertEquals(DatabaseStatusType.ACTIVE, finalDb.getStatus());
        // Engine Type is not populated in details, only in findAll()
        Assertions.assertEquals("vector", finalDb.getInfo().getDbType());
    }

    @Test
    @Order(6)
    @DisplayName("Find database by its name")
    public void shouldFindDatabaseByNameTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getDatabasesClient().databaseByName(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> getDatabasesClient().databaseByName(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> getDatabasesClient().databaseByName("i-like-cheese"));
        Assertions.assertTrue(getDatabasesClient()
                .findAllNonTerminated()
                .anyMatch(db -> SDK_TEST_DB_NAME.equals(db.getInfo().getName())));
        Assertions.assertTrue(getDatabasesClient()
                .findByName(SDK_TEST_DB_NAME)
                .count() > 0);
        Assertions.assertTrue(getDatabasesClient()
                .databaseByName(SDK_TEST_DB_NAME)
                .exist());
    }

    @Test
    @Order(7)
    @DisplayName("Find database by id")
    public void shouldFindDatabaseByIdTest() {
        // --> Getting a valid id
        Assertions.assertTrue(getDatabasesClient().databaseByName(SDK_TEST_DB_NAME).exist());
        String dbId = getDatabasesClient().databaseByName(SDK_TEST_DB_NAME).get().getId();
        // <---
        Assertions.assertThrows(IllegalArgumentException.class, () -> getDatabasesClient().database(""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> getDatabasesClient().database(null));

        Assertions.assertFalse(getDatabasesClient().database("invalid").exist());

        DatabaseClient dbClient = getDatabasesClient().database(dbId);
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

    @Test
    @Order(8)
    @DisplayName("Find accessLists")
    public void shouldFindAllAccessLists() {
        getDatabasesClient()
                .findAllAccessLists()
                .forEach(al -> Assertions.assertNotNull(al.getDatabaseId()));
    }

}
