package io.stargate.test.cql;

import io.stargate.sdk.StargateClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CqlSessionConnectivityTest {
    
    @Test
    // As not 'enabled' no session created
    public void cql_should_not_be_accessible() {
        StargateClient sc = StargateClient.builder()
                .withAuthCredentials("cassandra", "cassandra")
                .withLocalDatacenter("datacenter1")
                .withCqlContactPoints("localhost:9042")
                .withCqlContactPoints("localhost:9042")
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
