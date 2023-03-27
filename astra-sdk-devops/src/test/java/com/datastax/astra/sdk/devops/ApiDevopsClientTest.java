package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.AstraDevopsApiClient;
import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.DatabaseRegion;
import com.dtsx.astra.sdk.org.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@TestMethodOrder(OrderAnnotation.class)
public class ApiDevopsClientTest extends AbstractDevopsApiTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDevopsClientTest.class);

    @Test
    @Order(1)
    public void shouldAccessOrganization() {
        Organization org = getApiDevopsClient().getOrganization();
        Assertions.assertNotNull(org);
        Assertions.assertNotNull(org.getId());
        Assertions.assertNotNull(org.getName());
    }

    @Test
    @Order(2)
    public void shouldListRegions() {
        Assertions.assertTrue(getApiDevopsClient()
                .db()
                .regions()
                .findAll()
                .collect(Collectors.toList())
                .size() > 1);
    }

    @Test
    @Order(3)
    public void shouldFindAwsRegionAvailable() {
        LOGGER.info("AWS Region available");
        // When
        Map <String, Map<CloudProviderType,List<DatabaseRegion>>> available =
                getApiDevopsClient()
                .db()
                .regions()
                .findAllAsMap();
        // Then
        Assertions.assertTrue(available.containsKey("serverless"));
        Assertions.assertTrue(available.get("serverless").containsKey(CloudProviderType.AWS));
        Assertions.assertTrue(available
                .get("serverless")
                .get(CloudProviderType.AWS).stream()
                .anyMatch(db -> "us-east-1".equalsIgnoreCase(db.getRegion())));
        LOGGER.info("Tier `serverless` for region 'aws/us-east-1' is available");
    }

    @Test
    @Order(4)
    public void shouldFailOnInvalidParams() {
        LOGGER.info("Parameter validation");
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> new AstraDevopsApiClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> new AstraDevopsApiClient((String)null));
    }

    @Test
    @Order(5)
    @DisplayName("Listing serverless region for an organization")
    public void shouldListServerlessRegionTest() {
        AstraDevopsApiClient iamClient = new AstraDevopsApiClient(getToken());
        Assertions.assertTrue(iamClient.db().regions().findAllServerless().count() > 0);
    }

}
