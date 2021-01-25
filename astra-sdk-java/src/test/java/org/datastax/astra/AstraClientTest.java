package org.datastax.astra;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;
import com.dstx.astra.sdk.cql.ApiCqlClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.document.ApiDocumentClient;
import com.dstx.astra.sdk.rest.ApiRestClient;
import com.dstx.astra.sdk.utils.ApiResponse;

public class AstraClientTest extends ApiSupportTest {

    @Test
    public void testBuilder() {
        AstraClient astraClient = AstraClient.builder()
                .astraDatabaseId(dbId)
                .astraDatabaseRegion(dbRegion)
                .username(dbUser)
                .password(dbPasswd)
                .build();
        
        ApiDevopsClient   apiDevops  = astraClient.apiDevops();
        Optional<Astr > dbInfos = apiDevops.findDatabaseById(dbId);
        ApiRestClient     apiRest    = astraClient.apiRest();
        ApiCqlClient      apiCql      = astraClient.apiCql();
        ApiDocumentClient apiDocument = astraClient.apiDocument();
       
    }
}
