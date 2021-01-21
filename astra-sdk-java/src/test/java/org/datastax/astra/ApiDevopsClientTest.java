package org.datastax.astra;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Assert.assertTrue(apiDevopsClient.findDatabaseById(dbId).isPresent());
    }
    
    @Test
    public void should_Not_find_databases() {
       Assert.assertFalse(apiDevopsClient.findDatabaseById("i-like-cheese").isPresent());
    }
    
    @Test
    public void should_have_dbid_in_running_databases() {
        DatabaseFilter runningDbQuery = DatabaseFilter.builder()
                .limit(20).provider(Provider.ALL)
                .include(Include.NON_TERMINATED)
                .build();
        Map<String, AstraDatabaseInfos> runningDb = apiDevopsClient
                .databases(runningDbQuery)
                .collect(Collectors.toMap(AstraDatabaseInfos::getId, Function.identity()));
        Assert.assertTrue(runningDb.containsKey(dbId));
    }
    
    @Test
    public void should_create_keyspace() {
        // Given
        String randomKeyspaceName = UUID.randomUUID().toString().replaceAll("-", "");
        Assert.assertFalse(apiDevopsClient.findDatabaseById(dbId)
                .get().getInfo().getKeyspaces()
                .contains(randomKeyspaceName));
        // When
        apiDevopsClient.createKeyspace(dbId, randomKeyspaceName);
        waitForSeconds(5);
        Assert.assertTrue(apiDevopsClient.findDatabaseById(dbId)
                .get().getInfo().getKeyspaces()
                .contains(randomKeyspaceName));
        
    }
    
    @Test
    public void should_download_zip() {
        // Given
        String randomFile = "/tmp/" + UUID.randomUUID().toString().replaceAll("-", "") + ".zip";
        Assert.assertFalse(new File(randomFile).exists());
        // When
        apiDevopsClient.downloadSecureConnectBundle(dbId, randomFile);
        // Then
        Assert.assertTrue(new File(randomFile).exists());
        System.out.println(randomFile);
    }

    
    

}
