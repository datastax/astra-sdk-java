package org.datastax.astra;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.datastax.astra.devops.ApiDevopsClient;
import org.datastax.astra.devops.AstraDatabaseInfos;
import org.datastax.astra.devops.CloudProvider;
import org.datastax.astra.devops.DatabaseAvailableRegion;
import org.datastax.astra.devops.DatabaseCreationRequest;
import org.datastax.astra.devops.DatabaseFilter;
import org.datastax.astra.devops.DatabaseFilter.Include;
import org.datastax.astra.devops.DatabaseTier;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ApiDevopsTest extends ApiSupportTest {
    
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
                .limit(20).provider(CloudProvider.ALL)
                .include(Include.NON_TERMINATED)
                .build();
        Map<String, AstraDatabaseInfos> runningDb = apiDevopsClient
                .findDatabases(runningDbQuery)
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

    @Test
    @Disabled("Call is successfull but not free hehe")
    public void should_create_database() {
        DatabaseCreationRequest dcr = new DatabaseCreationRequest();
        dcr.setName("dbCreateApi");
        dcr.setTier(DatabaseTier.A5);
        dcr.setCloudProvider(CloudProvider.AWS);
        dcr.setRegion("us-east-1");
        dcr.setCapacityUnits(1);
        dcr.setKeyspace("ks_demo");
        dcr.setUser("cedrick");
        dcr.setPassword("cedrick1");
        apiDevopsClient.createDatabase(dcr);
    }
    
    @Test
    public void should_park_db() {
        apiDevopsClient.parkDatabase("dc27b2d9-2505-427a-b34d-f924483be9c2");
    }
    
    @Test
    public void should_unpark_db() {
        apiDevopsClient.unparkDatabase("dc27b2d9-2505-427a-b34d-f924483be9c2");
    }
    
    @Test
    public void should_resize_db() {
        apiDevopsClient.resizeDatase("dc27b2d9-2505-427a-b34d-f924483be9c2", 2);
    }
    
    @Test
    public void should_terminate_db() {
        apiDevopsClient.terminateDatabase("c091592b-84fc-4144-a10f-c135fc25bbeb");
    }
    
    @Test
    public void should_reset_password() {
        apiDevopsClient.resetPassword(dbId, "astraUser", "astraPassword1");
    }
    
    @Test
    public void should_list_available_regions() {
        apiDevopsClient.listAvailableRegion().stream()
            .map(DatabaseAvailableRegion::getDescription)
            .forEach(System.out::println);
    }
    

}
