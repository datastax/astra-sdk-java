package com.datastax.tutorial;

import java.util.Optional;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.oss.driver.api.core.CqlSession;

public class QuickStartAstra {
    
    public static void main(String[] args) {
        try (AstraClient astraClient = configureAtraClient()) {
            testDevopsDatabaseApi(astraClient);
            //testCqlApi(astraClient);
            //testRestApi(astraClient);
            //testDocumentaApi(astraClient);
            //testGraphQLApi(astraClient);
            //testGrpcApi(astraClient);
        }
    }
    
    public static void testDevopsDatabaseApi(AstraClient astraClient) {
        Optional<Database> db = astraClient.apiDevopsDatabases().databaseByName("quickstart").find();
        System.out.println("databaseId=" + db.get().getId());
        System.out.println("databaseRegion=" +db.get().getInfo().getRegion());
        System.out.println("keyspace=" +db.get().getInfo().getKeyspace());
        
    }
    
    public static AstraClient configureAtraClient() {
        return AstraClient.builder()
         .withToken("AstraCS:BWuMiTGOxJEREJZmfBRfEyla:2275784d9e47827bdc14cd5fddbd897cacf4872ba7bbf8c354e1ef8efc3b0d41")
         .withDatabaseId("d7d49808-9f08-403b-84e5-19ae47d1da69")
         .withDatabaseRegion("eu-central-1")
         .withKeyspace("quickstart")
         .build();
      }
    
    public static void testCqlApi(AstraClient astraClient) {
        CqlSession cqlSession = astraClient.cqlSession();
        System.out.println("Cql Version (cql)   : " + cqlSession
                .execute("SELECT cql_version from system.local")
                .one().getString("cql_version"));
    }
    
    public static void testRestApi(AstraClient astraClient) {
        System.out.println("Keyspaces (rest)    : " + astraClient.apiStargateData()
            .keyspaceNames().collect(Collectors.toList()));
    }
    
    public static void testDocumentaApi(AstraClient astraClient) {
        System.out.println("Namespaces (doc)    : " + astraClient.apiStargateDocument()
            .namespaceNames().collect(Collectors.toList()));
    }
    
    public static void testGraphQLApi(AstraClient astraClient) {
        System.out.println("Keyspaces (graphQL) : " + astraClient.apiStargateGraphQL().cqlSchema().keyspaces());
    }
    
    public static void testGrpcApi(AstraClient astraClient) {
        System.out.println("Cql Version (grpc)  : " + astraClient.apiStargateGrpc().execute("SELECT cql_version from system.local")
                .one().getString("cql_version"));
    }
    
   
    
}
