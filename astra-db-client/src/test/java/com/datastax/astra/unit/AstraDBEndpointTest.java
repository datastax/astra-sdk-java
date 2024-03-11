package com.datastax.astra.unit;

import com.datastax.astra.db.AstraDBEndpoint;
import com.datastax.astra.devops.utils.AstraEnvironment;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Work with the AstraDBEndpoint Object.
 */
public class AstraDBEndpointTest {

    @Test
    public void shouldParseApiEndpoint() {
        String url1 = "https://4391daae-016c-49e3-8d0a-b4633a86082c-us-east1.apps.astra.datastax.com";
        AstraDBEndpoint example1 = AstraDBEndpoint.parse(url1);
        assertThat(example1).isNotNull();
        assertThat(example1.getEnv()).isEqualTo(AstraEnvironment.PROD);
        assertThat(example1.getDatabaseId()).isEqualTo(UUID.fromString("4391daae-016c-49e3-8d0a-b4633a86082c"));
        assertThat(example1.getDatabaseRegion()).isEqualTo("us-east1");
        assertThat(example1.getOriginalEndPoint()).isEqualTo(url1);

        AstraDBEndpoint example2 = AstraDBEndpoint
                .parse("https://4391daae-016c-49e3-8d0a-b4633a86082c-us-west-2.apps.astra.datastax.com");
        assertThat(example2).isNotNull();
        assertThat(example2.getEnv()).isEqualTo(AstraEnvironment.PROD);
        assertThat(example2.getDatabaseId()).isEqualTo(UUID.fromString("4391daae-016c-49e3-8d0a-b4633a86082c"));
        assertThat(example2.getDatabaseRegion()).isEqualTo("us-west-2");

        AstraDBEndpoint example3 = AstraDBEndpoint
                .parse("https://25cf3382-d3d2-45d3-86af-b0b498c79cd7-europe-west4.apps.astra-dev.datastax.com");
        assertThat(example3).isNotNull();
        assertThat(example3.getEnv()).isEqualTo(AstraEnvironment.DEV);
        assertThat(example3.getDatabaseId()).isEqualTo(UUID.fromString("25cf3382-d3d2-45d3-86af-b0b498c79cd7"));
        assertThat(example3.getDatabaseRegion()).isEqualTo("europe-west4");
    }
}
