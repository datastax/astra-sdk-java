package com.datastax.astra.devops.db;

import com.datastax.astra.devops.AbstractDevopsApiTest;
import com.datastax.astra.devops.db.domain.CloudProviderType;
import com.datastax.astra.devops.db.domain.Database;
import com.datastax.astra.devops.db.domain.DatabaseCreationRequest;
import com.datastax.astra.devops.db.domain.DatabaseInfo;
import com.datastax.astra.devops.utils.AstraEnvironment;
import org.junit.jupiter.api.Test;

public class NonProductionEnvironmentTest extends AbstractDevopsApiTest {

    static String tokenDev = System.getenv("ASTRA_DB_APPLICATION_TOKEN_DEV");
    static String tokenDev2 = "AstraCS:ZiWfNzYJtUGszRuGyyTjFIXU:2c5a21a4623c6ee688d4bca4b8e55a269aa3ee864fcd16b26b7f9a82ca57b999";
    static String tokenTest = System.getenv("ASTRA_DB_APPLICATION_TOKEN_TEST");

    @Test
    public void shouldListDatabasesDev() {
        AstraDBDevopsClient opsClient = new AstraDBDevopsClient(tokenDev2, AstraEnvironment.DEV);
        opsClient.findAllNonTerminated().map(Database::getInfo).map(DatabaseInfo::getName).forEach(System.out::println);
        //opsClient.databaseByName("sdk_java_test_vector").accessLists();

        // Create Db in dev
        String dbId = opsClient.create(DatabaseCreationRequest
                .builder()
                .name(SDK_TEST_DB_VECTOR_NAME)
                .keyspace(SDK_TEST_KEYSPACE)
                .cloudProvider(CloudProviderType.AWS)
                .cloudRegion("us-west-2")
                .withVector()
                .build());
        //TestUtils.waitForDbStatus(getDatabasesClient().database(dbId), DatabaseStatusType.ACTIVE, 500);
    }
}
