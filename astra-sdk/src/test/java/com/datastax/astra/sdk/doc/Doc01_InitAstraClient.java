package com.datastax.astra.sdk.doc;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraClient.AstraClientBuilder;

/**
 * This is not a test but code snippet
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Doc01_InitAstraClient {
    
    public void howToInitializeTheAstraClient() {
        AstraClient client = AstraClient.builder()
                // If you want to use Devops API
                .appToken("AstraCS:......")
                // If you need to use CqlSession provide user/password
                .clientId("AAAAAAAAAAA").clientSecret("BBBBBBBBB")
                // Anything targetting a DB (doc API, Rest API, cqlSession)
                .databaseId("astra_cluster_id").cloudProviderRegion("astra_db_region")
                // Optional you can set the CqlSession for a Keyspace
                .keyspace("optional_keyspace")
                // Terminal Call
                .build();
        
        
       
    }

}
