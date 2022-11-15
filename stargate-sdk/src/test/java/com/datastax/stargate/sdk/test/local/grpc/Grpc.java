package com.datastax.stargate.sdk.test.local.grpc;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;
import com.datastax.stargate.sdk.grpc.domain.ResultSetGrpc;
import com.google.protobuf.InvalidProtocolBufferException;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.StargateGrpc;

public class Grpc {
   
    public void test_gRPC_Stargate() 
    throws IOException {
       
        // To get a token:
        // curl -L -X POST 'http://localhost:8081/v1/auth' -H 'Content-Type: application/json' --data-raw '{ "username": "cassandra", "password": "cassandra" }'
        String stargateToken = "<your_token>";
        String stargateHost  = "localhost";
        int    stargateport  = 8090;
        
        ManagedChannel mc = ManagedChannelBuilder
                            .forAddress(stargateHost, stargateport)
                            .usePlaintext() // <-- Stargate
                            .build();
        
        StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc.newBlockingStub(mc)
                .withDeadlineAfter(10, TimeUnit.SECONDS)
                .withCallCredentials(new StargateBearerToken(stargateToken));
        
        QueryOuterClass.Response response = blockingStub.executeQuery(
                QueryOuterClass.Query
                    .newBuilder()
                    .setCql("SELECT data_center from system.local")
                    .build());
        
        QueryOuterClass.ResultSet rs = response.getResultSet();
        System.out.println(rs.getRowsList().get(0).getValues(0));
    }
    
    
    public void test_gRPC_Stargate_SDK() 
    throws InvalidProtocolBufferException {
        
        StargateClient sc = StargateClient.builder()
                .withAuthCredentials("cassandra", "cassandra")
                .withLocalDatacenter("datacenter1")
                .withApiNode(new StargateNodeConfig("127.0.0.1", 8081, 8082, 8080, 9191))
                .build();
       
        ResultSetGrpc rs = sc.apiGrpc().execute("SELECT data_center from system.local");
        System.out.println(rs.one().getString("data_center"));
    }
   
    public void test_gRPC_Stargate_Astra() 
    throws InvalidProtocolBufferException {
        StargateClient sc = StargateClient.builder()
                .withAuthCredentials("cassandra", "cassandra")
                .withLocalDatacenter("datacenter1")
                .withApiNode(new StargateNodeConfig("127.0.0.1", 8081, 8082, 8080, 9191))
                .build();
       
        ResultSetGrpc rs = sc.apiGrpc().execute("SELECT data_center from system.local");
        System.out.println(rs.one().getString("data_center"));
    }

}
