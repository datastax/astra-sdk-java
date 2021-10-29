package com.datastax.tutorial;

import java.util.stream.Collectors;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;

public class App {
    
    public static void main(String[] args) {
        try (StargateClient stargateClient = configureStargateClientDefault()) {
            testCqlApi(stargateClient);
            testRestApi(stargateClient);
            testDocumentaApi(stargateClient);
            testGraphQLApi(stargateClient);
            testGrpcApi(stargateClient);
        }
    }
    
    public static StargateClient configureStargateClientDefault() {
        return StargateClient.builder()
                .withCqlContactPoints("localhost:9042")
                .withLocalDatacenter("datacenter1")
                .withAuthCredentials("cassandra", "cassandra")
                .withApiNode(new StargateNodeConfig("127.0.0.1", 8081, 8082, 8080, 9191))
                .build();
    }
    
    public static void testCqlApi(StargateClient stargateClient) {
        CqlSession cqlSession = stargateClient.cqlSession().get();
        System.out.println("Cql Version (cql)   : " + cqlSession
                .execute("SELECT cql_version from system.local")
                .one().getString("cql_version"));
    }
    
    public static void testRestApi(StargateClient stargateClient) {
        System.out.println("Keyspaces (rest)    : " + stargateClient.apiRest()
            .keyspaceNames().collect(Collectors.toList()));
    }
    
    public static void testDocumentaApi(StargateClient stargateClient) {
        System.out.println("Namespaces (doc)    : " + stargateClient.apiDocument()
            .namespaceNames().collect(Collectors.toList()));
    }
    
    public static void testGraphQLApi(StargateClient stargateClient) {
        System.out.println("Keyspaces (graphQL) : " + stargateClient.apiGraphQL().cqlSchema().keyspaces());
    }
    
    public static void testGrpcApi(StargateClient stargateClient) {
        System.out.println("Cql Version (grpc)  : " + stargateClient.apiGrpc().execute("SELECT cql_version from system.local")
                .one().getString("cql_version"));
    }
    
}
