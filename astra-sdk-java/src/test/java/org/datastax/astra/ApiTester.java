package org.datastax.astra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.datastax.astra.devops.ApiDevopsClient;
import org.datastax.astra.doc.ApiDocumentClient;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;

public class ApiTester {
    
    
    // Credentials Doc and Rest API
    public static String dbId;
    public static String dbRegion;
    public static String dbUser;
    public static String dbPasswd;
    
    // Do we want to create a new keyspace
    public static String clientId;
    public static String clientName;
    public static String clientSecret;
    
    public static ApiDocumentClient apiDocClient;
    
    public static ApiDevopsClient   apiDevopsClient;
    
    public static void initApiDevopsClient() {
        Assert.assertNotNull(clientId);
        Assert.assertNotNull(clientName);
        Assert.assertNotNull(clientSecret);
        
        apiDevopsClient = new ApiDevopsClient(clientName, clientId, clientSecret);
        Assert.assertNotNull(apiDevopsClient);
        Assert.assertTrue(apiDevopsClient.testConnection());
    }
    
    public static void initApiDocumentApiClient() {
        Assert.assertNotNull(dbId);Assert.assertNotNull(dbRegion);
        Assert.assertNotNull(dbUser);Assert.assertNotNull(dbPasswd);

        apiDocClient = new ApiDocumentClient(dbId, dbRegion, dbUser, dbPasswd);
        Assert.assertNotNull(apiDocClient);
        Assert.assertTrue(apiDocClient.testConnection());
    }
    
    /**
     * Load properties values from an external file. 
     * Easier than ENV VAR in eclipse and not commited
     * in github, Keys names are just a sample
     * 
     * id=...
     * region=...
     * user=...
     * password=...
     * clientId=...
     * clientName=...
     * clientSecret=...
     */
    @BeforeAll
    public static void initAstraClient() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("/Users/cedricklunven/dev/WORKSPACES/DATASTAX/credentials.properties")));
        dbId          = properties.getProperty("id");
        dbRegion      = properties.getProperty("region");
        dbUser        = properties.getProperty("user");
        dbPasswd      = properties.getProperty("password");
        clientId      = properties.getProperty("clientId");
        clientName    = properties.getProperty("clientName");
        clientSecret  = properties.getProperty("clientSecret");
        
    }
    
    protected static void waitForSeconds(int s) {
        try {Thread.sleep(s * 1000);} catch (InterruptedException e) {}
    }
    
    

}
