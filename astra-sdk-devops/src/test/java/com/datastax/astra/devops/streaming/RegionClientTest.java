package com.datastax.astra.devops.streaming;

import com.datastax.astra.devops.AbstractDevopsApiTest;
import com.datastax.astra.devops.streaming.domain.StreamingRegion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

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
