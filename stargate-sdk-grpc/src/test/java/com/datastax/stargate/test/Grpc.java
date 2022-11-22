package com.datastax.stargate.test;

import com.datastax.stargate.sdk.grpc.utils.FuturesUtils;
import com.datastax.stargate.sdk.grpc.utils.StreamObserverToReactivePublisher;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.StargateGrpc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Grpc {

    // To get a token:
    // curl -L -X POST 'http://localhost:8081/v1/auth' -H 'Content-Type: application/json' --data-raw '{ "username": "cassandra", "password": "cassandra" }'
    String stargateToken = "09c34ad4-95c2-4d70-8b41-b2335a77659f";
    String stargateHost  = "localhost";
    int    stargateport  = 8090;
    ManagedChannel mc;

    @BeforeEach
    public void init() {
        mc = ManagedChannelBuilder
                .forAddress(stargateHost, stargateport)
                .usePlaintext() // <-- Stargate
                .build();
    }

    @Test
    public void stargateTest() {
        StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc.newBlockingStub(mc)
                .withDeadlineAfter(10, TimeUnit.SECONDS)
                .withCallCredentials(new StargateBearerToken(stargateToken));

        QueryOuterClass.Query q1 = QueryOuterClass.Query
                .newBuilder()
                .setCql("SELECT * from local " +
                        "WHERE data_center=? " +
                        "AND key=? " +
                        "ALLOW FILTERING")
                .setValues(QueryOuterClass.Values.newBuilder()
                        .addValues(QueryOuterClass.Value.newBuilder().setString("datacenter1"))
                        .addValues(QueryOuterClass.Value.newBuilder().setString("local")))
                .setParameters(QueryOuterClass.QueryParameters.newBuilder()
                        .setKeyspace(StringValue.newBuilder().setValue("system").build())
                        .setConsistency(QueryOuterClass.ConsistencyValue.newBuilder().setValue(QueryOuterClass.Consistency.LOCAL_QUORUM).build())
                        .setPageSize(Int32Value.newBuilder().setValue(1))
                        //.setPagingState()
                        //.setSkipMetadata()
                        //.setTracing()
                        //.setTracingConsistency()
                        //.setSerialConsistency()
                        //.setTimestamp()
                        .build())
                .build();



        QueryOuterClass.Response response = blockingStub.executeQuery(q1);
        QueryOuterClass.ResultSet rs = response.getResultSet();
        System.out.println(rs.getColumns(0).getName());
        System.out.println(rs.getRowsList().get(0).getValues(0));
    }

    @Test
    public void executeAsyncTest()
    throws IOException, ExecutionException, InterruptedException {

        StargateGrpc.StargateFutureStub futureStub = StargateGrpc.newFutureStub(mc)
                .withDeadlineAfter(10, TimeUnit.SECONDS)
                .withCallCredentials(new StargateBearerToken(stargateToken));

        QueryOuterClass.Query q2 = QueryOuterClass.Query
                .newBuilder()
                .setCql("SELECT * from system.local " +
                        "WHERE data_center=:dc " +
                        "AND key=:k " +
                        "ALLOW FILTERING")
                .setValues(QueryOuterClass.Values.newBuilder()
                        .addValueNames("dc")
                        .addValues(QueryOuterClass.Value.newBuilder().setString("datacenter1"))
                        .addValueNames("k")
                        .addValues(QueryOuterClass.Value.newBuilder().setString("local")))
                .build();

        ListenableFuture<QueryOuterClass.Response> f = futureStub.executeQuery(q2);
        FuturesUtils.asCompletableFuture(f).get();

    }

    @Test
    public void executeReactiveTest() {
        StargateGrpc.StargateStub reactiveStub = StargateGrpc.newStub(mc)
                .withDeadlineAfter(10, TimeUnit.SECONDS)
                .withCallCredentials(new StargateBearerToken(stargateToken));

        QueryOuterClass.Query q3 = QueryOuterClass.Query
                .newBuilder()
                .setCql("SELECT * from system.local " +
                        "WHERE data_center=:dc " +
                        "AND key=:k " +
                        "ALLOW FILTERING")
                .setValues(QueryOuterClass.Values.newBuilder()
                        .addValueNames("dc")
                        .addValues(QueryOuterClass.Value.newBuilder().setString("datacenter1"))
                        .addValueNames("k")
                        .addValues(QueryOuterClass.Value.newBuilder().setString("local")))
                .build();

        /*
        reactiveStub.executeQuery(q3, new StreamObserver<QueryOuterClass.Response>() {
            @Override
            public void onNext(QueryOuterClass.Response value) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });*/

        StreamObserverToReactivePublisher streamObserverPublisher = new StreamObserverToReactivePublisher<QueryOuterClass.Response>();
        Flux<QueryOuterClass.Response> myflux = Flux.from(streamObserverPublisher);
        reactiveStub.executeQuery(q3, streamObserverPublisher);
        myflux.blockFirst();

    }


}
