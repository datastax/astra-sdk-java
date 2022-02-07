package com.datastax.stargate.sdk.test;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;

/**
 * Mutualization of client definition in tests
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiStargateTestFactory {
    
    /**
     * Create an instance for test.
     * 
     * @return
     *      a instance for test
     */
    public static StargateClient createStargateClient() {
        
        return StargateClient.builder()
            .withAuthCredentials("cassandra", "cassandra")
            .withLocalDatacenter("datacenter1")
            .withApiNode(new StargateNodeConfig("127.0.0.1"))
            .build();
    }

}
