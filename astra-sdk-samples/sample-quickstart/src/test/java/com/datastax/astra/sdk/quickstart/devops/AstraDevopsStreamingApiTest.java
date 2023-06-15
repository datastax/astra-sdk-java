package com.datastax.astra.sdk.quickstart.devops;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.quickstart.AbstractSdkTest;
import com.dtsx.astra.sdk.db.domain.Database;
import com.dtsx.astra.sdk.db.domain.DatabaseInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class AstraDevopsStreamingApiTest extends AbstractSdkTest {

    @BeforeAll
    public static void init() {
        loadRequiredEnvironmentVariables();
    }

    @Test
    public void shouldConnectWithGraphQL() {
        // A token is all you need
        try (AstraClient astraClient = AstraClient.builder()
                .withToken(ASTRA_DB_APPLICATION_TOKEN)
                .build()) {
            Map<String, List<String>> clouds = astraClient.apiDevopsStreaming().providers().findAll();
            System.out.println("+ Available Clouds to create tenants=" + clouds);
        }
    }
}

