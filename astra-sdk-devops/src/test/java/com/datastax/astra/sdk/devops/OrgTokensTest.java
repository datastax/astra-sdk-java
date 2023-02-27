package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.org.OrganizationsClient;
import com.dtsx.astra.sdk.org.domain.CreateTokenResponse;
import com.dtsx.astra.sdk.org.domain.DefaultRoles;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrgTokensTest extends AbstractDevopsApiTest {

    // -----------------------------------
    // -----------    TOKENS      ---------
    // -----------------------------------

    private static String tmpClientId;

    @Test
    @Order(1)
    public void shouldCreateToken() {
        System.out.println( "- Creating a Token");
        OrganizationsClient iamClient = new OrganizationsClient(getToken());
        CreateTokenResponse res = iamClient.createToken(DefaultRoles.DATABASE_ADMINISTRATOR.getName());
        tmpClientId = res.getClientId();
        System.out.println("Token created " + tmpClientId);
        Assertions.assertTrue(iamClient.token(res.getClientId()).exist());
        System.out.println("Token exist ");
    }

    @Test
    @Order(2)
    @DisplayName("Deleting a Token")
    public void shouldDeleteToken() {
        // Given
        Assertions.assertNotNull(getToken());
        OrganizationsClient iamClient = new OrganizationsClient(getToken());
        // When
        Assertions.assertTrue(iamClient.token(tmpClientId).exist());
        iamClient.token(tmpClientId).delete();
        // Then
        Assertions.assertFalse(iamClient.token(tmpClientId).exist());
    }

}
