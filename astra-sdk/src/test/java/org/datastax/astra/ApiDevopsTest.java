package org.datastax.astra;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.devops.CloudProviderType;
import com.dstx.astra.sdk.devops.DatabaseStatusType;
import com.dstx.astra.sdk.devops.DatabaseTierType;
import com.dstx.astra.sdk.devops.req.DatabaseCreationRequest;
import com.dstx.astra.sdk.devops.res.Database;
import com.dstx.astra.sdk.devops.res.DatabaseAvailableRegion;

/**
 * StandAlone test to work with Devops API.
 * 
 * You need to define a variable bearerToken with you DEVOPS token
 * -DbearerToken=...
 * 
 * System.getenv("bearerToken")
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDevopsTest {
    
    private static final String ANSI_RESET           = "\u001B[0m";
    private static final String ANSI_GREEN           = "\u001B[32m";
    private static final String ANSI_YELLOW          = "\u001B[33m";
    
    private static final String SERVERLESS_DB_NAME   = "sdk_serverless2";
    private static final String SERVERLESS_KEYSPACE  = "sdk_ks1";
    private static final String SECOND_KEYSPACE      = "sdk_ks2";
    private static final String SERVERLESS_NAMESPACE = "sdk_ns1";
    
    @Test
    public void testDevopsApiSuite() throws InterruptedException {
        
        // -- (1) - CONNECTION --
        System.out.println(ANSI_YELLOW + "[Astra DEVOPS Api Test Suite]" + ANSI_RESET);
        ApiDevopsClient cli = connectToAstra(System.getenv("bearerToken"));
        
        /* (2) Create a Database
        System.out.println(ANSI_YELLOW + "\n[POST] Create a new database"+ ANSI_RESET);
        String dbId = createServerlessDatabase(cli);
        
        // (3) - FIND DB --
        System.out.println(ANSI_YELLOW + "\n[GET] Returns a list of databases" + ANSI_RESET);
        testFindDatabases(cli, dbId);
        
        // (4) - Working with Keyspaces
        System.out.println(ANSI_YELLOW + "\n[POST] Adds keyspace into database" + ANSI_RESET);
        workingWithKeyspaces(cli, dbId);
        */
        
    }
    
    private void workingWithKeyspaces(ApiDevopsClient cli, String dbId) {
        
        // Check Parameters
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createNamespace(dbId, ""));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createNamespace("", SERVERLESS_NAMESPACE));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createKeyspace(dbId, null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createKeyspace(null, SECOND_KEYSPACE));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Validated parameters are required");
        
        // Given
        Assert.assertFalse(cli.findDatabaseById(dbId).get().getInfo().getKeyspaces().contains(SECOND_KEYSPACE));
        // When
        cli.createKeyspace(dbId, SECOND_KEYSPACE);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Keyspace creation request successful for '" + SECOND_KEYSPACE + "'");
        Assert.assertEquals(DatabaseStatusType.MAINTENANCE, cli.findDatabaseById(dbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Switch in [MAINTENANCE] mode");
        while(DatabaseStatusType.ACTIVE != cli.findDatabaseById(dbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        // When
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli.findDatabaseById(dbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Switch in [ACTIVE] mode");
        
        cli.createNamespace(dbId, SERVERLESS_NAMESPACE);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace creation request successful for '" + SERVERLESS_NAMESPACE + "'");
        Assert.assertEquals(DatabaseStatusType.MAINTENANCE, cli.findDatabaseById(dbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Switch in [MAINTENANCE] mode");
        while(DatabaseStatusType.ACTIVE != cli.findDatabaseById(dbId).get().getStatus() ) {
            waitForSeconds(1);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Switch in [ACTIVE] mode");
        // Then
        Database db = cli.findDatabaseById(dbId).get();
        Assert.assertTrue(db.getInfo().getKeyspaces().contains(SECOND_KEYSPACE));
        Assert.assertTrue(db.getInfo().getKeyspaces().contains(SERVERLESS_NAMESPACE));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected keyspaces and namespaces are now present");
        
        // Cann create keyspace that already exist
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createNamespace(dbId, SERVERLESS_NAMESPACE));
        Assertions.assertThrows(IllegalArgumentException.class, () -> cli.createKeyspace(dbId, SECOND_KEYSPACE));
    }
    
    private void testFindDatabases(ApiDevopsClient cli, String dbId) {
        Assert.assertTrue(cli.findDatabasesNonTerminated().anyMatch(
                db -> SERVERLESS_DB_NAME.equals(db.getInfo().getName())));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - List retrieved and the new created DB is present");
        
        System.out.println(ANSI_YELLOW + "\n[GET] Finds database by ID" + ANSI_RESET);
        Optional<Database> odb = cli.findDatabaseById(dbId);
        Assert.assertTrue(odb.isPresent());
        Assert.assertEquals(DatabaseStatusType.ACTIVE, odb.get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB can be loaded from its id");
        Assert.assertNotNull(odb.get().getInfo());
        Assert.assertEquals(DatabaseTierType.serverless, odb.get().getInfo().getTier());
        Assert.assertNotNull(odb.get().getMetrics());
        Assert.assertNotNull(odb.get().getStorage());
        Assert.assertNotNull(SERVERLESS_KEYSPACE, odb.get().getInfo().getKeyspace());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Fields metrics,storage,info are populated");
    }
    
    private ApiDevopsClient connectToAstra(String bearerToken) {
        Assert.assertNotNull(bearerToken);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Bearer token retrieved " + bearerToken.substring(0,9) + "...");
        ApiDevopsClient cli = new ApiDevopsClient(bearerToken);
        Assert.assertNotNull(cli);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Devops Client initialized");
        Assert.assertTrue(cli.findAllAvailableRegions().collect(Collectors.toList()).size() > 1);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Connectivity OK");
        return cli;
    }
    
    private String createServerlessDatabase(ApiDevopsClient cli) {
        // Given
        Stream<DatabaseAvailableRegion> streamDb = cli.findAllAvailableRegions();
        Map <DatabaseTierType, Map<CloudProviderType,List<DatabaseAvailableRegion>>> available = cli.mapAvailableRegions(streamDb);
        Assert.assertTrue(available.containsKey(DatabaseTierType.serverless));
        Assert.assertTrue(available.get(DatabaseTierType.serverless).containsKey(CloudProviderType.AWS));
        Assert.assertTrue(available.get(DatabaseTierType.serverless).get(CloudProviderType.AWS).stream().anyMatch(db -> "us-east-1".equalsIgnoreCase(db.getRegion())));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Tier `serverless` for region 'aws/us-east-1' is available");
        Assert.assertFalse(cli.findDatabasesNonTerminatedByName(SERVERLESS_DB_NAME).collect(Collectors.toSet()).size()>0);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Instance with name'" + SERVERLESS_DB_NAME + "' does not exist.");
        // When
        DatabaseCreationRequest dcr = new DatabaseCreationRequest();
        dcr.setName(SERVERLESS_DB_NAME);
        dcr.setTier(DatabaseTierType.serverless);
        dcr.setCloudProvider(CloudProviderType.AWS);
        dcr.setRegion("us-east-1");
        dcr.setCapacityUnits(1);
        dcr.setKeyspace(SERVERLESS_KEYSPACE);
        dcr.setUser("cedrick");
        dcr.setPassword("cedrick1");
        String dbId = cli.createDatabase(dcr);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Creation request successful");
        
        // Then
        Assert.assertTrue(cli.databaseExist(dbId));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB now exists id='" + dbId + "'");
        
        Assert.assertEquals(DatabaseStatusType.PENDING, cli.findDatabaseById(dbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Status is [PENDING]");
        while(DatabaseStatusType.PENDING.equals(cli.findDatabaseById(dbId).get().getStatus())) {
            waitForSeconds(1);   
        }
        Assert.assertEquals(DatabaseStatusType.INITIALIZING, cli.findDatabaseById(dbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Status is [INITIALIZING] (should take about 3min)");
        //  When
        while(DatabaseStatusType.INITIALIZING.equals(cli.findDatabaseById(dbId).get().getStatus())) {
            System.out.print(".");
            waitForSeconds(5);
        }
        Assert.assertEquals(DatabaseStatusType.ACTIVE, cli.findDatabaseById(dbId).get().getStatus());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - DB Status is [ACTIVE] your are set.");
        
        return dbId;
    }
    
    
    protected static void waitForSeconds(int s) {
        try {Thread.sleep(s * 1000);} catch (InterruptedException e) {}
    }
    
    
    /*
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
                .limit(20)
                .provider(CloudProvider.ALL)
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
    //@Disabled("Call is successfull but not free hehe")
    public void should_create_database() {
        DatabaseCreationRequest dcr = new DatabaseCreationRequest();
        dcr.setName("db_created_with_sdk");
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
    
   
    }*/
    

}
