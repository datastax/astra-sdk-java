package org.datastax.astra;

import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;

import io.stargate.sdk.StargateClient;

public class ChrisTest {

    private static String appToken     = "AstraCS:ZykcSMWLUDktHMZsYCKWtNQa:7f95412e3c5014d952febbf5bd4223c74afa95f1f9b205327804c8cac1597e2b";
    private static String dbId         = "9ea35b33-aa48-49fb-88a5-20525aad07fd";
    private static String cloudRegion  = "eu-central-1";
    
    @Test
    public void testDocumentApi() {
        AstraClient client = AstraClient.builder()
                .databaseId(dbId)
                .cloudProviderRegion(cloudRegion)
                .appToken(appToken).build();
        
        client.apiDocument().namespaceNames().forEach(System.out::println);
        
        
        
        StargateClient.builder().addCqlContactPoint("localhost", 9042)
                                .authenticationUrl("localhost");
        
        
    }
}
