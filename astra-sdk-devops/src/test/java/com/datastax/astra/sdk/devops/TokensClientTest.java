package com.datastax.astra.sdk.devops;

import com.dtsx.astra.sdk.org.domain.CreateTokenResponse;
import com.dtsx.astra.sdk.org.domain.DefaultRoles;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TokensClientTest extends AbstractDevopsApiTest {

    private static String createClientId;

    @Test
    @Order(1)
    public void shouldCreateToken() {
        // When creating a token
        CreateTokenResponse res = getApiDevopsClient().tokens().create(DefaultRoles.DATABASE_ADMINISTRATOR);
        // Then it should be found
        Assertions.assertTrue(getApiDevopsClient().tokens().exist(res.getClientId()));
        createClientId = res.getClientId();
    }

    @Test
    @Order(2)
    public void shouldListTokens() {
        Assertions.assertTrue(getApiDevopsClient()
                .tokens().findAll()
                .anyMatch(t-> createClientId.equalsIgnoreCase(t.getClientId())));
    }

    @Test
    @Order(3)
    public void shouldDeleteTokens() {
        // Given
        Assertions.assertTrue(getApiDevopsClient().tokens().exist(createClientId));
        // When
        getApiDevopsClient().tokens().delete(createClientId);
        // Then
        Assertions.assertFalse(getApiDevopsClient().tokens().exist(createClientId));
    }

}
