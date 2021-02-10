package org.datastax.astra;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;
import com.dstx.astra.sdk.cql.ApiCqlClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.devops.AstraDatabaseInfos;
import com.dstx.astra.sdk.document.ApiDocumentClient;
import com.dstx.astra.sdk.document.QueryDocument;
import com.dstx.astra.sdk.rest.ApiRestClient;

public class AstraClientTest extends ApiSupportTest {

    @Test
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
        ApiCqlClient      apiCql      = astraClient.apiCql();
        ApiDocumentClient apiDocument = astraClient.apiDocument();
       
    }
}
