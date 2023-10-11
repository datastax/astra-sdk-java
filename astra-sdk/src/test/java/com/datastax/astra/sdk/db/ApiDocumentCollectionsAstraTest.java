package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraSdkTest;
import io.stargate.sdk.test.doc.AbstractDocClientCollectionsTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static com.dtsx.astra.sdk.utils.TestUtils.TEST_REGION;
import static com.dtsx.astra.sdk.utils.TestUtils.setupDatabase;

/**
 * Execute some unit tests against collections.
 */
public class ApiDocumentCollectionsAstraTest extends AbstractDocClientCollectionsTest implements AstraSdkTest {

    @BeforeAll
    public static void init() {
        stargateDocumentApiClient = AstraClient.builder()
                .withDatabaseRegion(TEST_REGION)
                .withDatabaseId(setupDatabase(TEST_DATABASE_NAME, TEST_NAMESPACE))
                .build().getStargateClient().apiDocument();
        nsClient = stargateDocumentApiClient
                .namespace(TEST_NAMESPACE);
     }

}
