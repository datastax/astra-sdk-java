package com.datastax.astra.sdk;

import java.io.File;

import com.datastax.astra.sdk.utils.AstraRc;

public class SampleCodeForDocumentation {

    
    public void init() {
        AstraClient client = AstraClient.builder()
                .databaseId("astra_cluster_id")           // Unique identifier for your database instance
                .cloudProviderRegion("astra_db_region")   // Cloud Provider region picked for you instance
                .keyspace("ks1")                          // (optional) Set your keyspace
                .astraRc(null, null)
                
                .appToken("AstraCS:......")               // App Token will be used as ApiKey for Devops, Docs and REST Api.
                .clientId("TWRvjlcrgfZYfhcxGZhUlAAA")     // Will be used as your username
                .clientSecret("7xKSrZPLbWxDJ0WXyj..")     // Will be used as your password

                .secureConnectBundle("/tmp/sec.zip")      // (optional) if not provided download in ~/.astra
                .build();
        
        
        AstraRc arc = AstraRc.load(null);
       
        
        // Generate the file in default location (`/.astrarc)
        AstraRc.create("<your_token>");
        
        // Generate the file in a defined location
        AstraRc.create("<your_token>", new File("/tmp/astracrc"));
        
        AstraRc.load("/tmp/astrarc");
        
        
    }
    
}
