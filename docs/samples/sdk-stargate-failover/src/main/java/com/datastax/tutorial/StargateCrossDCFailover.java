package com.datastax.tutorial;

import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.auth.StandardAuthScheme;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.StandardCookieSpec;
import org.apache.hc.core5.util.Timeout;

import com.datastax.oss.driver.api.core.ConsistencyLevel;
import com.datastax.oss.driver.api.core.config.TypedDriverOption;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.audit.AnsiConsoleLogger;
import com.datastax.stargate.sdk.config.StargateNodeConfig;
import com.evanlennick.retry4j.config.RetryConfigBuilder;

public class StargateCrossDCFailover {
    
    public static final int NB_TRY_BEFORE_FAILOVER = 3;
    public static final String DC1 = "DC1";
    public static final String DC2 = "DC2";
    
    public static void main(String[] args) {
        try (StargateClient stargateClient = setupStargate()) {
            while(true) { 
                int idx = 0;
                while(idx++ < NB_TRY_BEFORE_FAILOVER) {
                    Thread.sleep(2000);
                    testCqlApi(stargateClient);
                    testRestApi(stargateClient);
                    testDocumentaApi(stargateClient);
                    testGraphQLApi(stargateClient);
                    System.out.println("---");
                    //testGrpcApi(stargateClient);
                }
                //stargateClient.getStargateHttpClient().failoverDatacenter();
            }
        } catch (InterruptedException e) {}
    }
    
    public static StargateClient setupStargate() {
        return StargateClient.builder()
                .withApplicationName("FullSample")
                // Setup DC1
                .withLocalDatacenter(DC1)
                .withAuthCredentials("cassandra", "cassandra")
                .withCqlContactPoints("localhost:9052")
                .withCqlKeyspace("system")
                .withCqlConsistencyLevel(ConsistencyLevel.LOCAL_QUORUM)
                .withCqlDriverOption(TypedDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOption(TypedDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOption(TypedDriverOption.CONNECTION_SET_KEYSPACE_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOption(TypedDriverOption.CONTROL_CONNECTION_TIMEOUT, Duration.ofSeconds(10))
                .withApiNode(new StargateNodeConfig("dc1s1", "localhost", 8081, 8082, 8080, 8083))
                .withApiNode(new StargateNodeConfig("dc1s2", "localhost", 9091, 9092, 9090, 9093))
                
                // Setup DC2
                .withApiNodeDC(DC2, new StargateNodeConfig("dc2s1", "localhost", 6061, 6062, 6060, 6063))
                .withApiNodeDC(DC2, new StargateNodeConfig("dc2s2", "localhost", 7071, 7072, 7070, 7073))
                .withCqlContactPointsDC(DC2, "localhost:9062")
                .withCqlDriverOptionDC(DC2,TypedDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOptionDC(DC2,TypedDriverOption.CONNECTION_INIT_QUERY_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOptionDC(DC2,TypedDriverOption.CONNECTION_SET_KEYSPACE_TIMEOUT, Duration.ofSeconds(10))
                .withCqlDriverOptionDC(DC2,TypedDriverOption.CONTROL_CONNECTION_TIMEOUT, Duration.ofSeconds(10))
                
                // Setup HTTP
                .withHttpRequestConfig(RequestConfig.custom()
                        .setCookieSpec(StandardCookieSpec.STRICT)
                        .setExpectContinueEnabled(true)
                        .setConnectionRequestTimeout(Timeout.ofSeconds(5))
                        .setConnectTimeout(Timeout.ofSeconds(5))
                        .setTargetPreferredAuthSchemes(Arrays.asList(StandardAuthScheme.NTLM, StandardAuthScheme.DIGEST))
                        .build())
                .withHttpRetryConfig(new RetryConfigBuilder()
                        //.retryOnSpecificExceptions(ConnectException.class, IOException.class)
                        .retryOnAnyException()
                        .withDelayBetweenTries( Duration.ofMillis(100))
                        .withExponentialBackoff()
                        .withMaxNumberOfTries(3)
                        .build())
                .addHttpObserver("logger_light", new AnsiConsoleLogger())
                .build();
    }
    
    public static void testCqlApi(StargateClient stargateClient) {
        //CqlSession cqlSession = stargateClient.cqlSession().get();
        System.out.println("DataCenter Name (cql) : " +
                stargateClient.cqlSession().get()
                .execute("SELECT data_center from system.local")
                .one().getString("data_center"));

    }
    
    public static void testRestApi(StargateClient stargateClient) {
        //System.out.println("Keyspaces (rest)    : " + stargateClient.apiRest()
        //    .keyspaceNames().collect(Collectors.toList()));
        stargateClient.apiRest().keyspaceNames().collect(Collectors.toList());
    }
    
    public static void testDocumentaApi(StargateClient stargateClient) {
       // System.out.println("Namespaces (doc)    : " + stargateClient.apiDocument()
       // .namespaceNames().collect(Collectors.toList()));
       stargateClient.apiDocument().namespaceNames().collect(Collectors.toList());
    }
    
    public static void testGraphQLApi(StargateClient stargateClient) {
        //System.out.println("Keyspaces (graphQL) : " + stargateClient.apiGraphQL().cqlSchema().keyspaces());
        stargateClient.apiGraphQL().cqlSchema().keyspaces();
    }
    
    public static void testGrpcApi(StargateClient stargateClient) {
        System.out.println("Cql Version (grpc)  : " + stargateClient.apiGrpc().execute("SELECT cql_version from system.local")
                .one().getString("cql_version"));
    }
    
}
