package org.datastax.astra;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datastax.astra.devops.ApiDevopsClient;
import org.datastax.astra.devops.AstraDatabaseInfos;
import org.datastax.astra.devops.DatabaseFilter;
import org.datastax.astra.devops.DatabaseFilter.Include;
import org.datastax.astra.devops.DatabaseFilter.Provider;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ApiDevopsClientTest extends ApiTester {
    
    protected static ApiDevopsClient apiDevopsClient;

    @BeforeAll
    public static void should_init_reusable_api_client() {
        // Given
        Assert.assertNotNull(clientId);Assert.assertNotNull(clientName);
        Assert.assertNotNull(clientSecret);
        // When
        apiDevopsClient = new ApiDevopsClient(clientName, clientId, clientSecret);
        // Then
        Assert.assertNotNull(apiDevopsClient);
        Assert.assertTrue(apiDevopsClient.testConnection());
    }
    
    @Test
    public void should_find_databases() {
        Assert.assertNotNull(dbId);
        Optional<AstraDatabaseInfos> astraDbInfo = apiDevopsClient.findDatabaseById(dbId);
        Assert.assertTrue(astraDbInfo.isPresent());
    }
    
    @Test
    public void should_Not_find_databases() {
       Assert.assertFalse(apiDevopsClient.findDatabaseById("i-like-cheese").isPresent());
    }
    
    
    @Test
    public void should_list_running_databases() {
        DatabaseFilter runningDb = DatabaseFilter.builder()
                .limit(20).provider(Provider.ALL)
                .include(Include.NON_TERMINATED)
                .build();
        Stream<AstraDatabaseInfos> streamResults = apiDevopsClient.databases(runningDb);
        Assert.assertNotNull(streamResults);
        List <AstraDatabaseInfos> listResults = streamResults.collect(Collectors.toList());
        Assert.assertNotNull(listResults);
        Assert.assertFalse(listResults.isEmpty());
        System.out.println(listResults.get(0).getInfo().getKeyspaces());
    }
    
    public void should_create_keyspace() {
        apiDevopsClient.createNamespace(dbId, "toto");
    }

    
    

}
