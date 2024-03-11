package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraSdkTest;
import io.stargate.sdk.grpc.StargateGrpcApiClient;
import io.stargate.sdk.grpc.domain.ResultSetGrpc;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.datastax.astra.devops.utils.TestUtils.TEST_REGION;
import static com.datastax.astra.devops.utils.TestUtils.setupDatabase;

/**
 * Test gRPC service against Astra.
 */
public class ApiGrpcAstraTest implements AstraSdkTest {

    /** Singleton grpc Client. */
    private static StargateGrpcApiClient grpClient;

    @BeforeAll
    public static void init() {
        grpClient =  AstraClient.builder()
                .withDatabaseRegion(TEST_REGION)
                .withDatabaseId(setupDatabase(TEST_DATABASE_NAME, TestDocClientConstants.TEST_NAMESPACE))
                .build().getStargateClient().apiGrpc();
    }

    @Test
    public void testAstra() {
        ResultSetGrpc rs = grpClient.execute("SELECT data_center from system.local");
        Assertions.assertNotNull(rs.one().getString("data_center"));
    }

}
