package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.org.TokensClient;
import com.dtsx.astra.sdk.org.domain.CreateTokenResponse;
import com.dtsx.astra.sdk.org.domain.DefaultRoles;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class OrgTokensTest extends AbstractDevopsApiTest {

    @Test
    @Order(1)
    public void shouldWorkWithCreateToken() {
        TokensClient tokensClient = getApiDevopsClient().tokens();
        // When creating a token
        CreateTokenResponse res = tokensClient.create(DefaultRoles.DATABASE_ADMINISTRATOR.getName());
        // Then it should be found
        Assertions.assertTrue(tokensClient.exist(res.getClientId()));
        // When deleting a token
        tokensClient.delete(res.getClientId());
        // Then it should not be found anymore
        Assertions.assertFalse(tokensClient.exist(res.getClientId()));
    }

}
