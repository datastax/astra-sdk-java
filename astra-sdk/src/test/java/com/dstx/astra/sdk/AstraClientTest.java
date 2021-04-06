package com.dstx.astra.sdk;

import java.util.Optional;

import org.datastax.astra.dto.PersonAstra;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;
import com.dstx.astra.sdk.devops.ApiDevopsClient;
import com.dstx.astra.sdk.devops.CloudProviderType;
import com.dstx.astra.sdk.devops.DatabaseStatusType;
import com.dstx.astra.sdk.devops.DatabaseTierType;
import com.dstx.astra.sdk.devops.req.DatabaseCreationRequest;
import com.dstx.astra.sdk.devops.res.Database;
import com.dstx.astra.sdk.devops.res.DatabaseInfo;

public class AstraClientTest {

    // Change parameters here
    String astraToken = "<YOUR_TOKEN>";
    
    // Some parameters, Astra is available on the 3 main CloudProviders
    CloudProviderType cloudProvider = CloudProviderType.AWS;
    
    // I provide here the region
    String cloudProviderRegion = "us-east-1";
    
    
    @Test
    public void testDevopsAPI() throws InterruptedException {
        
        /** 
         * Astra Provides a devops API
         * 
         * The SDK provide a client with class `ApiDevopsClient` only need a JWT token
         */
        ApiDevopsClient devopsClient = new ApiDevopsClient(astraToken);
        
        /**
         * The client expose all methods with fluent approach
         * devopsClient.findAllAvailableRegions()
         * devopsClient.findAllDatabases()
         * devopsClient.findAllDatabasesNonTerminated()
         * devopsClient.findDatabaseById(dbId)
         * devopsClient.createKeyspace(dbId, keyspace);
         * devopsClient.createNamespace(dbId, namespace);
         * devopsClient.databaseExist(dbId)
         * devopsClient.downloadSecureConnectBundle(dbId, destination);
         * 
         * Here we are listing existing DB for a user 
         */
        devopsClient.findAllDatabasesNonTerminated()
                    .map(Database::getInfo)
                    .map(DatabaseInfo::getName)
                    .forEach(System.out::println);
    }
    
    @Test
    public void testDevopsApiWithAstraClient() throws InterruptedException {
        
        /**
         * AstraClient is the main and only class to interact with ASTRA.
         * 
         * It is wapping multiple APIs. It will initialized it expected parameters are provided
         */
        AstraClient astraClient =  AstraClient.builder().appToken(astraToken).build();
        
        /**
         * Devops API make no exeption
         */
        astraClient.apiDevops()
                   .findAllDatabasesNonTerminated()
                   .map(Database::getInfo)
                   .map(DatabaseInfo::getName)
                   .forEach(System.out::println);
        
        /**
         * We create a new serverless database
         */
        String dbId = astraClient.apiDevops().createDatabase(DatabaseCreationRequest.builder()
                .tier(DatabaseTierType.serverless)
                .cloudProvider(cloudProvider)
                .cloudRegion(cloudProviderRegion)
                .name("josh_db")
                .keyspace("josh_db")
                .username("josh")
                .password("joshlong1")
                .build());
        
        /**
         * Instance creation take about 3min
         */
        System.out.println("Starting new instance '" + dbId + "' (about 3min)");
        while(DatabaseStatusType.ACTIVE != astraClient.apiDevops().findDatabaseById(dbId).get().getStatus()) {
            Thread.sleep(1000);
            System.out.println("+ Initializing....");
        }
        System.out.println("Ready");
    }
    
    
    @Test
    public void testCqlSessionWithAstraClient() throws InterruptedException {
        
        /**
         * Fill the id with what you got
         */
        String yourDbId      = "a24145e2-7846-4461-9f4d-ab2c29f32040";
        String yourNamespace = "joshns";
        
        /**
         * With an Instance we can do more.
         *
         * Astra works with regular Cassandra drivers.
         * AstraClient simplify configuration of the CqlSession
         */
        AstraClient astraClientFull =  AstraClient.builder()
                .appToken(astraToken)
                .databaseId(yourDbId)
                .cloudProviderRegion(cloudProviderRegion)
                .build();
      
        /**
         * Let's use CqlSession as is.
         */
        System.out.println("dataCenter:" + astraClientFull.cqlSession()
                .execute("SELECT data_center from system.local")
                .one()
                .getString("data_center"));
        
        /**
         * Let's use Document API
         */
        
        // Creating new collections
        astraClientFull.apiDocument()
                       .namespace(yourNamespace)
                       .collection("videos")
                       .create();
        
        // Inserting a document
        String documentId = astraClientFull.apiDocument()
                       .namespace(yourNamespace)
                       .collection("videos")
                       .createNewDocument(new PersonAstra("cedrick", "lunven"));
        System.out.println("Document created:" + documentId);
        
        // Reading a document from its id
        Optional<PersonAstra> p = astraClientFull.apiDocument()
                        .namespace(yourNamespace)
                        .collection("videos")
                        .document(documentId).find(PersonAstra.class);
        System.out.println(p.get().getFirstname());
    }
    
}
