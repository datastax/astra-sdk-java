package com.datastax.astra.sdk.streaming;

import com.datastax.astra.sdk.devops.AbstractDevopsApiTest;
import org.datastax.astra.sdk.domain.StreamingRegion;
import org.datastax.astra.sdk.streaming.StreamingClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Test regions
 */
public class ListRegionsTest extends AbstractDevopsApiTest {

    @Test
    public void testListRegions() {
        List<StreamingRegion> regions = new StreamingClient(getToken())
                .serverlessRegions()
                .collect(Collectors.toList());
        Assertions.assertFalse(regions.isEmpty());
        regions.stream().map(StreamingRegion::getName).forEach(System.out::println);
    }

}
