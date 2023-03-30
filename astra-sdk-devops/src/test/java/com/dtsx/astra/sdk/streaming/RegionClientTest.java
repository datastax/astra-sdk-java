package com.dtsx.astra.sdk.streaming;

import com.dtsx.astra.sdk.AbstractDevopsApiTest;
import com.dtsx.astra.sdk.streaming.domain.StreamingRegion;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.stream.Collectors;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegionClientTest extends AbstractDevopsApiTest {

    @Test
    @Order(1)
    @DisplayName("Find all regions for an Organization")
    public void shouldFindAllRegions() {
        // Given
        RegionsClient cli = new AstraStreamingClient(getToken()).regions();
        // When
        List<String> regions = cli
                .findAllServerless()
                .map(StreamingRegion::getName)
                .collect(Collectors.toList());
        // Then
        Assertions.assertNotNull(regions);
        Assertions.assertTrue(regions.contains("useast1"));
    }


}
