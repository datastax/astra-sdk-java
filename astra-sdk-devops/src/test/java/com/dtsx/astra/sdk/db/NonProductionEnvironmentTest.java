package com.dtsx.astra.sdk.db;

import com.dtsx.astra.sdk.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseCreationRequest;
import com.dtsx.astra.sdk.db.domain.DatabaseInfo;
import com.dtsx.astra.sdk.db.domain.DatabaseStatusType;
import com.dtsx.astra.sdk.utils.AstraEnvironment;
import com.dtsx.astra.sdk.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class NonProductionEnvironmentTest extends AbstractDevopsApiTest {

    static String tokenDev = System.getenv("ASTRA_DB_APPLICATION_TOKEN_DEV");
    static String tokenDev2 = "AstraCS:ZiWfNzYJtUGszRuGyyTjFIXU:2c5a21a4623c6ee688d4bca4b8e55a269aa3ee864fcd16b26b7f9a82ca57b999";
    static String tokenTest = System.getenv("ASTRA_DB_APPLICATION_TOKEN_TEST");

    @Test
    public void shouldListDatabasesDev() {
        AstraDBOpsClient opsClient = new AstraDBOpsClient(tokenDev2, AstraEnvironment.DEV);
        opsClient.findAllNonTerminated().map(Database::getInfo).map(DatabaseInfo::getName).forEach(System.out::println);
        opsClient.databaseByName("vector_client_test").accessLists();

        // Create Db in dev
        String dbId = opsClient.create(DatabaseCreationRequest
                .builder()
                .name(SDK_TEST_DB_VECTOR_NAME)
                .keyspace(SDK_TEST_KEYSPACE)
                .cloudProvider(CloudProviderType.AWS)
                .cloudRegion("us-east-2")
                .withVector()
                .build());
        TestUtils.waitForDbStatus(getDatabasesClient().database(dbId), DatabaseStatusType.ACTIVE, 500);
    }
}
