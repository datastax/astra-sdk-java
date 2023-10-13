package com.dtsx.astra.sdk.vector;

import com.dtsx.astra.sdk.AbstractAstraVectorTest;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseInfo;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.AstraRc;
import com.dtsx.astra.sdk.vector.domain.LLMEmbedding;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.UUID;

/**
 * Once upon a time in the vector database wonderland.
 */
@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class VectorClientDbCreationsTest extends AbstractAstraVectorTest {
    public static final String DBNAME_VECTOR_CLIENT = "vector_client_test";
    public static final String DBNAME_VECTOR_CLIENT2 = "vector_client_test2";
    public static final String DEMO_COLLECTION_VECTOR = "demo_vector_store";
    public static final String STORE_WITH_VECTORIZE_TMP = "demo_vector_tmp";

    @Test
    @Order(1)
    @DisplayName("01. Init client")
    @EnabledIfEnvironmentVariable(named = AstraRc.ASTRA_DB_APPLICATION_TOKEN, matches = "Astra.*")
    public void shouldInit() {

        // (1) token retrieve from env var
        AstraVectorClient vectorClient = new AstraVectorClient();

        // (2) Pass the token
        String astraToken = System.getenv(AstraRc.ASTRA_DB_APPLICATION_TOKEN);
        AstraVectorClient vectorClient2 = new AstraVectorClient(astraToken);

        // (3) works with dev
        AstraVectorClient vectorClientDev = new AstraVectorClient(astraToken, AstraEnvironment.DEV);
    }

    @Test
    @Order(2)
    @DisplayName("02. List Databases (devops)")
    @EnabledIfEnvironmentVariable(named = AstraRc.ASTRA_DB_APPLICATION_TOKEN, matches = "Astra.*")
    public void shouldListDatabases() {
        // Init
        AstraVectorClient vectorClient = new AstraVectorClient();

        // Show me all vector databases in my organization
        log.info("Databases currently running in your organization:");
        vectorClient.findAllDatabases()
                .map(Database::getInfo)
                .map(DatabaseInfo::getName)
                .forEach(log::info);
    }

    @Test
    @Order(3)
    @DisplayName("03. Create Database (devops)")
    @EnabledIfEnvironmentVariable(named = AstraRc.ASTRA_DB_APPLICATION_TOKEN, matches = "Astra.*")
    public void shouldCreateDatabases() {

        // Init
        AstraVectorClient vectorClient = new AstraVectorClient();

        // Creating from a name (default cloud region is free tier)
        UUID databaseId = vectorClient.createDatabase(DBNAME_VECTOR_CLIENT);
        log.info("{} is created and active", databaseId);

        // Specify the region (enum for the user to pick, +  explicit FREE_TIER)
        UUID yaDatabaseId = vectorClient.createDatabase(
                DBNAME_VECTOR_CLIENT2,
                AstraVectorClient.FREE_TIER_CLOUD,
                AstraVectorClient.FREE_TIER_CLOUD_REGION);
        log.info("{} is created and active", yaDatabaseId);

        vectorClient.devopsDbClient.database("").delete();
    }

    @Test
    @Order(4)
    @DisplayName("04. Create Store (json)")
    @EnabledIfEnvironmentVariable(named = AstraRc.ASTRA_DB_APPLICATION_TOKEN, matches = "Astra.*")
    public void shouldCreateCollection() {
        // Init
        AstraVectorClient vectorClient = new AstraVectorClient();

        // Assign vectorDb client (but could user fluent everywhere)
        VectorDatabase vectorDb = vectorClient.database(DBNAME_VECTOR_CLIENT);

        // Create a Store with $vector
        vectorDb.createVectorStore(DEMO_COLLECTION_VECTOR, 14);
        log.info("{} is created", DEMO_COLLECTION_VECTOR);

        // [NOT FOR GA] -Create a Store with $vectorize
        vectorDb.createVectorStore(STORE_WITH_VECTORIZE_TMP, LLMEmbedding.ADA_002);
        log.info("{} is created", STORE_WITH_VECTORIZE_TMP);

        log.info("List of Stores");
        vectorDb.findAllStores().forEach(log::info);
    }

    @Test
    @Order(5)
    @DisplayName("05. Delete Store")
    @EnabledIfEnvironmentVariable(named = "ASTRA_DB_APPLICATION_TOKEN", matches = "Astra.*")
    public void shouldDeleteStore() {
        // Assign vectorDb client (but could user fluent everywhere)
        VectorDatabase vectorDb = new AstraVectorClient().database(DBNAME_VECTOR_CLIENT);
        // Given
        Assertions.assertTrue(vectorDb.isStoreExist(STORE_WITH_VECTORIZE_TMP));
        // When Delete a store
        vectorDb.deleteStore(STORE_WITH_VECTORIZE_TMP);
        log.info("{} is deleted", STORE_WITH_VECTORIZE_TMP);
        // Then
        Assertions.assertFalse(vectorDb.isStoreExist(STORE_WITH_VECTORIZE_TMP));
    }

}
