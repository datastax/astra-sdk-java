package com.datastax.tutorial;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;
import com.datastax.stargate.sdk.core.ApiTokenProviderSimple;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;
import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.grpc.ApiGrpcClient;
import com.datastax.stargate.sdk.rest.ApiDataClient;

public class FullSampleStargate {
    
    public static void main(String[] args) {
        try (StargateClient stargateClient = configureStargateClientDefault()) {
            testCqlApi(stargateClient);
            testRestApi(stargateClient);
            testDocumentaApi(stargateClient);
            testGraphQLApi(stargateClient);
            testGrpcApi(stargateClient);
        }
    }
    
    public void doc(StargateClient sdk) {
        ApiDocumentClient apiDocument = sdk.apiDocument();
        ApiDataClient apiDataRest = sdk.apiRest();
        ApiGrpcClient apiGrpc= sdk.apiGrpc();
        ApiGraphQLClient apiGraphClient= sdk.apiGraphQL();
        
        
        Stream <Document<String>> familyDoe = sdk.apiDocument().namespace("foo").collection("bar")
            .findAll(SearchDocumentQuery.builder()
                        .select("firstname").where("lastname").isEqualsTo("Doe")
                        .build(), String.class);
    }
    
    public static StargateClient configureStargateClientDefault() {
        return StargateClient.builder()
                
                .withLocalDatacenter("DC1")
                
                .withApiNode(new StargateNodeConfig("dc1s1", "localhost", 8081, 8082, 8080, 8083))
                .withApiNode(new StargateNodeConfig("dc1s2", "localhost", 9091, 9092, 9090, 9093))
                .withApiNodeDC("DC2", new StargateNodeConfig("dc2s1", "localhost", 6061, 6062, 6060, 6063))
                .withApiNodeDC("DC2", new StargateNodeConfig("dc2s2", "localhost", 7071, 7072, 7070, 7073))
                
                .withAuthCredentials("cassandra", "cassandra")
                
                .withApiToken(null) // if provided static token use
                .withApiTokenProvider("url1", "url2") // enforce some node to to the auth in current DC
                .withApiTokenProviderDC("DC2", "url1", "url2") // enforce some node to to the auth in current DC
                .withApiTokenProviderDC("DC1", new ApiTokenProviderSimple("", "", ""))
                
                .withApplicationName("appp")
                
                .withCqlCloudSecureConnectBundle(null) // path  of SCB for current DC
                .withCqlCloudSecureConnectBundleDC("DC1", null) // path  of SCB for current a DC
                
                .withCqlConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
                .withCqlConsistencyLevelDC("DC1", ConsistencyLevel.LOCAL_QUORUM)
                
                .withCqlContactPoints("localhost:9052")
                .withCqlContactPointsDC("DC2", "localhost:9062")
                
                .withCqlDriverConfigurationFile(null)
                
                .withCqlDriverConfigurationLoader(null)
                
                .withCqlDriverOption(null, null)
                
                .withCqlDriverOptionDC("DC2", null, null)
                
                .withCqlKeyspace(null)
                
                .withCqlMetricsRegistry(null)
                
                .withCqlRequestTracker(null)
                
                .withCqlSessionBuilderCustomizer(null)
                
                .withHttpRequestConfig(null)
                
                .withHttpRetryConfig(null)
                
                .withHttpObservers(null)
                
                .withoutCqlSession()
                
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
