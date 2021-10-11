package com.datastax.stargate.sdk.test;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.audit.AnsiLoggerObserverLight;
import com.datastax.stargate.sdk.config.StargateNodeConfig;
import com.datastax.stargate.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

public class AAA {
    
    @Test
    public void initCqlSession() {
        try(CqlSession cqlSession = CqlSession.builder()
                .addContactPoint(new InetSocketAddress("localhost", 9045))
                .withAuthCredentials("cassandra", "cassandra")
                .withLocalDatacenter("datacenter1")
                .build()) {
            System.out.println(cqlSession
                       .execute("SELECT data_center from system.local")
                      .one().getString("data_center"));
        }
    }
    
    @Test
    public void initStargateEnvVar() {
        //STARGATE_CONTACTPOINTS=localhost:9045
        //STARGATE_LOCAL_DC=datacenter1
        //STARGATE_PASSWORD=cassandra
        //STARGATE_USERNAME=cassandra
        //STARGATE_APINODES={"datacenter1":[{"name":"dc1Stargate1","restUrl":"http://127.0.0.1:8082","graphqlUrl":"http://127.0.0.1:8080","authUrl":"http://127.0.0.1:8081"},{"name":"dc1Stargate2","restUrl":"http://127.0.0.1:8082","graphqlUrl":"http://127.0.0.1:8080", "authUrl":    "http://127.0.0.1:8081"}]}
        try(StargateClient sc = StargateClient.builder().build()) {
            sc.apiRest().keyspaceNames().forEach(System.out::println);
        }
    }
      
    @Test
    public void initStargate() throws InterruptedException {
        // RETRY and RETRY POOLICY
        // ASTRA MULTI REGION
        int times= 1000;
        try(StargateClient sc = StargateClient.builder()
                // CQL 
                .withAuthCredentials("cassandra","cassandra")
                .withLocalDatacenter("datacenter1")
                //.withContactPoints("127.0.0.1:9045")
                ///.withKeyspace("ks1")
                // API
                //.withCqlOption(null, null)
                .withoutCqlSession()
                //.withApiNode(new StargateNode("localhost"))
                //.withApiNode(new StargateNode("127.0.0.1"))
                .withoutCqlSession()
                .withApiNodeDC("datacenter1", new StargateNodeConfig("node11", "http://localhost:8082",  "http://localhost:8080",  "http://localhost:8081"))
                .withApiNodeDC("datacenter1", new StargateNodeConfig("node12", "http://localhost:9092",  "http://localhost:9090",  "http://localhost:9091"))
                .addHttpObserver("logger", new AnsiLoggerObserverLight())
                .build()) {
            
           for(int idx=0;idx<times;idx++) {
               sc.apiRest().keyspaceNames();
               Thread.sleep(500);
           } 
            // failover
            //sc.useDataCenter("datacenter1");
            
            //sc.apiRest().keyspaceNames().forEach(System.out::println);
        }
        
    }
    
    
   
    
    @Test
    public void test() {
        String var = "{"
                + " \"datacenter1\":["
                + "   {\"name\":\"dc1Stargate1\",\"restUrl\":\"http://127.0.0.1:8082\",\"graphqlUrl\":\"http://127.0.0.1:8080\",\"authUrl\":\"http://127.0.0.1:8081\"},"
                + "   {\"name\":\"dc1Stargate2\",\"restUrl\":\"http://127.0.0.1:8082\",\"graphqlUrl\":\"http://127.0.0.1:8080\",\"authUrl\":\"http://127.0.0.1:8081\"}"
                + " ],"
                + " \"datacenter2\":["
                + "   {\"name\":\"dc1Stargate1\",\"restUrl\":\"http://127.0.0.1:8082\",\"graphqlUrl\":\"http://127.0.0.1:8080\",\"authUrl\":\"http://127.0.0.1:8081\"},"
                + "   {\"name\":\"dc1Stargate2\",\"restUrl\":\"http://127.0.0.1:8082\",\"graphqlUrl\":\"http://127.0.0.1:8080\",\"authUrl\":\"http://127.0.0.1:8081\"}"
                + " ]"
                + "}";
        
        Map<String, List<StargateNodeConfig>> stargateNodes = JsonUtils.unmarshallType(var, 
                    new TypeReference<Map<String, List<StargateNodeConfig>>>(){});
        System.out.println(stargateNodes);
    }
    
}
