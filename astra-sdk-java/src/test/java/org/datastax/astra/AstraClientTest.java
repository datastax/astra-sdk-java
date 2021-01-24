package org.datastax.astra;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;

public class AstraClientTest extends ApiSupportTest {

    @Test
    public void testBuilder() {
        AstraClient astraClient = AstraClient.builder()
                .astraDatabaseId(dbId)
                .astraDatabaseRegion(dbRegion)
                .username(dbUser)
                .password(dbPasswd)
                .build();
        
        
        
    }
}
