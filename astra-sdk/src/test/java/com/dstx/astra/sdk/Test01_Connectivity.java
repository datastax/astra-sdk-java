package com.dstx.astra.sdk;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.datastax.oss.driver.api.core.CqlSession;
import com.dstx.astra.sdk.AstraClient;

/**
 * Multiple Connectivity mode for eacj parameters.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Test01_Connectivity extends AbstractAstraIntegrationTest {
    
    @BeforeAll
    public static void overriding_client() {
     /* 
     client = AstraClient.builder()
       .databaseId("58c6335b-766f-49e0-8e12-ed222c943e35")
       .cloudProviderRegion("europe-west1")
       .appToken("AstraCS:MGJEgIcLhuosUFiYJBtBzCdd:6c728ba45be91e43140f7390d12de5c06419cea602a5fc31a8acac232fcfbe7b")
       .build();
       */
    }
    
    @Test
    public void should_use_cqlSession_with_clientId_clientSecret() {
        Assertions.assertTrue(dbId.isPresent());
        Assertions.assertTrue(cloudRegion.isPresent());
        Assertions.assertTrue(clientId.isPresent());
        Assertions.assertTrue(clientSecret.isPresent());
        System.out.println("should_use_cqlSession_with_clientId_clientSecret " + clientId.get());
        cqlSession_ok(AstraClient.builder()
                .databaseId(dbId.get())
                .cloudProviderRegion(cloudRegion.get())
                .clientId(clientId.get())
                .clientSecret(clientSecret.get())
                .build().cqlSession());
    }
    
    private void cqlSession_ok(CqlSession cqlSession) {
        
        Assertions.assertNotNull(cqlSession.execute("SELECT release_version FROM system.local")
                .one().getString("release_version"));
    }
    
    @Test
    public void should_use_cqlSession_with_appToken() {
        
    }

}
