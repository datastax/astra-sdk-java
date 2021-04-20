package io.stargate.sdk.test;


import org.junit.jupiter.api.Test;

import io.stargate.sdk.StargateClient;

public class K8ssandraVmTest {
    
    @Test
    public void connectivity() {
        StargateClient stargate = StargateClient.builder()
                .username("k8ssandra-superuser")
                .password("JxzrPOnvDGqfEOQ0EySQ")
                .endPointAuth("http://wksc272755.cedrick-ajug.datastaxtraining.com:8081")
                .endPointRest("http://wksc272755.cedrick-ajug.datastaxtraining.com:8082")
                .endPointGraphQL("http://wksc272755.cedrick-ajug.datastaxtraining.com:8080")
                .disableCQL()
                .build();
        
        stargate.apiRest().keyspaceNames().forEach(System.out::println);
    }

}
