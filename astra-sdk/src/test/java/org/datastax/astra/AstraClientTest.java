package org.datastax.astra;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;
import com.dstx.astra.sdk.cql.ApiCqlClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.devops.AstraDatabaseInfos;
import com.dstx.stargate.client.doc.ApiDocumentClient;
import com.dstx.stargate.client.doc.QueryDocument;
import com.dstx.stargate.client.rest.ApiRestClient;

public class AstraClientTest extends ApiSupportTest {

    @Test
    @SuppressWarnings("unused")
    public void testBuilder() {
        AstraClient astraClient = AstraClient.builder()
                .astraDatabaseId(dbId)
                .astraDatabaseRegion(dbRegion)
                .username(dbUser)
                .password(dbPasswd)
                .build();
        
        QueryDocument.builder().where("age").isLessOrEqualsThan(20).build();
       
        
        ApiDevopsClient   apiDevops  = astraClient.apiDevops();
        Optional<AstraDatabaseInfos > dbInfos = apiDevops.findDatabaseById(dbId);
        
        ApiRestClient     apiRest    = astraClient.apiRest();
         ApiDocumentClient apiDocument = astraClient.apiDocument();
       
    }
}
