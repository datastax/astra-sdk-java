package com.datastax.tutorial;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.databases.domain.Database;
import com.datastax.astra.sdk.organizations.domain.DefaultRoles;
import com.datastax.astra.sdk.organizations.domain.Role;
import com.datastax.oss.driver.api.core.CqlSession;

public class QuickStartAstra {
    
    public static void main(String[] args) {
        try (AstraClient astraClient = configureAtraClient()) {
            // Devops
            testDevopsStreamingApi(astraClient);
            testDevopsOrganizationApi(astraClient);
            testDevopsDatabaseApi(astraClient);
            // Stargate
            testCqlApi(astraClient);
            testRestApi(astraClient);
            testDocumentaApi(astraClient);
            testGraphQLApi(astraClient);
            testGrpcApi(astraClient);
        }
    }
    
    public static AstraClient configureAtraClient() {
        return AstraClient.builder()
         .withToken("AstraCS:RjwrkBwzbcZZDyFZAvuhmCRJ:71cea89b9d3ea45a1c15675ab38f6cd861633bfc521467f525d2f57ced0e6b14")
         .withDatabaseId("d7d49808-9f08-403b-84e5-19ae47d1da69")
         .withDatabaseRegion("eu-central-1")
         .withCqlKeyspace("quickstart")
         .build();
    }
    
    public static void testDevopsOrganizationApi(AstraClient astraClient) {
        System.out.println("\n[DEVOPS/ORGANIZATION]");
        Optional<Role> role = astraClient.apiDevopsOrganizations().findRoleByName(DefaultRoles.ORGANIZATION_ADMINISTRATOR.getName());
        System.out.println("+ Organization Administrator Role id=" + role.get().getId());
    }
    
    public static void testDevopsStreamingApi(AstraClient astraClient) {
        System.out.println("\n[DEVOPS/STREAMING]");
        
        Map<String, List<String>> clouds = astraClient.apiDevopsStreaming().providers();
        System.out.println("+ Available Clouds to create tenants=" + clouds);
    }
    
    public static void testDevopsDatabaseApi(AstraClient astraClient) {
        System.out.println("\n[DEVOPS/DATABASE]");
        Optional<Database> db = astraClient.apiDevopsDatabases().databaseByName("quickstart").find();
        System.out.println("+ databaseId=" + db.get().getId());
        System.out.println("+ databaseRegion=" +db.get().getInfo().getRegion());
        System.out.println("+ keyspace=" +db.get().getInfo().getKeyspace());
    }
    
    public static void testCqlApi(AstraClient astraClient) {
        System.out.println("\n[STARGATE/CQL]");
        CqlSession cqlSession = astraClient.cqlSession();
        System.out.println("+ Cql Version (cql)   : " + cqlSession
                .execute("SELECT cql_version from system.local")
                .one().getString("cql_version"));
    }
    
    public static void testRestApi(AstraClient astraClient) {
        System.out.println("\n[STARGATE/DATA]");
        System.out.println("+ Keyspaces (rest)    : " + astraClient.apiStargateData()
            .keyspaceNames().collect(Collectors.toList()));
    }
    
    public static void testDocumentaApi(AstraClient astraClient) {
        System.out.println("\n[STARGATE/DOCUMENT]");
        System.out.println("+ Namespaces (doc)    : " + astraClient.apiStargateDocument()
            .namespaceNames().collect(Collectors.toList()));
    }
    
    public static void testGraphQLApi(AstraClient astraClient) {
        System.out.println("\n[STARGATE/GRAPHQL]");
        System.out.println("+ Keyspaces (graphQL) : " + astraClient.apiStargateGraphQL().cqlSchema().keyspaces());
    }
    
    public static void testGrpcApi(AstraClient astraClient) {
        System.out.println("\n[STARGATE/GRPC]");
        System.out.println("+ Cql Version (grpc)  : " + astraClient.apiStargateGrpc().execute("SELECT cql_version from system.local")
                .one().getString("cql_version"));
    }
    
   
    
}
