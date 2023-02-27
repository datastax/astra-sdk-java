package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.db.domain.CloudProviderType;
import com.dtsx.astra.sdk.db.domain.DatabaseRegion;
import com.dtsx.astra.sdk.org.OrganizationsClient;
import com.dtsx.astra.sdk.org.iam.UserClient;
import com.dtsx.astra.sdk.org.domain.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@TestMethodOrder(OrderAnnotation.class)
public class OrgCoreTest extends AbstractDevopsApiTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrgCoreTest.class);

    @Test
    @Order(1)
    public void shouldAccessOrganization() {
        // Given
        OrganizationsClient cli = getOrganizationClient();
        // When
        Organization org = cli.organization();
        // Then
        Assertions.assertNotNull(org);
        Assertions.assertNotNull(org.getId());
        Assertions.assertNotNull(org.getName());
    }

    @Test
    @Order(2)
    public void shouldListRegions() {
        LOGGER.info("Connection with OrganizationsClient");
        // Given
        Assertions.assertNotNull(getToken());
        // When
        OrganizationsClient cli = getOrganizationClient();
        // Then
        Assertions.assertTrue(cli.regions().collect(Collectors.toList()).size() > 1);
        LOGGER.info("Can connect to ASTRA with OrganizationsClient");
    }

    @Test
    @Order(3)
    public void shouldFindAwsRegionAvailable() {
        LOGGER.info("AWS Region available");
        // Given
        OrganizationsClient cli = getOrganizationClient();
        // When
        Map <String, Map<CloudProviderType,List<DatabaseRegion>>> available = cli.regionsMap();
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
                () -> new OrganizationsClient(""));
        Assertions.assertThrows(IllegalArgumentException.class, 
                () -> new OrganizationsClient((String)null));
    }

    @Test
    @Order(5)
    @DisplayName("Listing serverless region for an organization")
    public void shouldListServerlessRegionTest() {
        OrganizationsClient iamClient = new OrganizationsClient(getToken());
        Assertions.assertTrue(iamClient.regionsServerless().count() > 0);
    }

}
