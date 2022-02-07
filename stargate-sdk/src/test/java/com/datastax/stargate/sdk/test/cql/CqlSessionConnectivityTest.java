package com.datastax.stargate.sdk.test.cql;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;

public class CqlSessionConnectivityTest {
    
    @Test
    // As not 'enabled' no session created
    public void cql_should_not_be_accessible() {
        StargateClient sc = StargateClient.builder()
                .withAuthCredentials("cassandra", "cassandra")
                .withLocalDatacenter("datacenter1")
                .withCqlContactPoints("localhost:9042")
                .withApiNode(new StargateNodeConfig("127.0.0.1"))
                .build();
        Assertions.assertFalse(sc.cqlSession().isPresent());
    }
    
    @Test
    // As 'enabled' session is created
    public void shoudl_enable_cql_only() {
        StargateClient sc = StargateClient.builder()
                //.withAuthCredentials("cassandra", "cassandra")
                //.withLocalDatacenter("datacenter1")
                //.withCqlContactPoints("localhost:9042")
                .enableCql()
                .build();
        Assertions.assertTrue(sc.cqlSession().isPresent());
        Assertions.assertEquals("datacenter1", 
                    sc.cqlSession().get()
                      .execute("select data_center from system.local")
                      .one().getString("data_center"));
    }
    
}
