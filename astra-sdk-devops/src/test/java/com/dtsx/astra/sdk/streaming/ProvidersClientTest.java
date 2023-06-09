package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractDevopsApiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;
import java.util.Map;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProvidersClientTest extends AbstractDevopsApiTest {

    @Test
    @Order(1)
    @DisplayName("Find all providers for an Organization")
    public void shouldFindAllProviders() {
        // Given
        AstraStreamingClient cli = new AstraStreamingClient(getToken());
        // When
        Map<String, List<String>> providers = cli.providers().findAll();
        // Then
        Assertions.assertNotNull(providers);
        Assertions.assertTrue(providers.containsKey("gcp"));
        Assertions.assertTrue(providers.get("gcp").contains("useast1"));
    }


}
