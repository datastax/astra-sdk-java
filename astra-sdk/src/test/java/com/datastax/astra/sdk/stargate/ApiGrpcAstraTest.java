package com.datastax.astra.sdk.stargate;

import org.junit.jupiter.api.Assertions;

import com.datastax.astra.sdk.AstraClient;
import io.stargate.sdk.grpc.domain.ResultSetGrpc;

public class ApiGrpcAstraTest {
    
    
    public void testAstra() {
        AstraClient astraClient = AstraClient.builder().build();
        ResultSetGrpc rs = astraClient
                .apiStargateGrpc()
                .execute("SELECT data_center from system.local");
        Assertions.assertNotNull(rs.one().getString("data_center"));
    }

}
