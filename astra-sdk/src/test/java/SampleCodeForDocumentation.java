

import java.io.File;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.utils.AstraRc;
import com.datastax.stargate.sdk.core.DataCenter;
import com.datastax.stargate.sdk.doc.NamespaceClient;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;
import com.datastax.stargate.sdk.rest.domain.ColumnDefinition;

public class SampleCodeForDocumentation {
    
    public void init() {
        
        AstraClient astraClient = AstraClient.builder()
                .withDatabaseId("astra_cluster_id")           // Unique identifier for your database instance
                .withDatabaseRegion("astra_db_region")
                .build();   // Cloud Pr
                
        AstraClient astraClient2 = AstraClient.builder()
                .withDatabaseId("astra_cluster_id")           // Unique identifier for your database instance
                .withDatabaseRegion("astra_db_region")   // Cloud Provider region picked for you instance
                .withKeyspace("ks1")                          // (optional) Set your keyspace
                .withToken("AstraCS:......")               // App Token will be used as ApiKey for Devops, Docs and REST Api.
                .withClientId("TWRvjlcrgfZYfhcxGZhUlAAA")     // Will be used as your username
                .withClientSecret("7xKSrZPLbWxDJ0WXyj..")     // Will be used as your password
                .withSecureConnectBundleFolder("/tmp")      // (optional) if not provided download in ~/.astra
                .build();
        
        AstraRc arc = AstraRc.load(null);
       
        
        // Generate the file in default location (`/.astrarc)
        AstraRc.create("<your_token>");
        
        // Generate the file in a defined location
        AstraRc.create("<your_token>", new File("/tmp/astracrc"));
       
        
        // Working with Namespaces
        NamespaceClient ns1Client = astraClient.apiStargateDocument().namespace("ns1");
        
        // Create if not exist
        if (!ns1Client.exist()) {
            ns1Client.createSimple(3);
        }
        
        // Show datacenters where it lives
        ns1Client.find().get().getDatacenters()
                 .stream().map(DataCenter::getName)
                 .forEach(System.out::println); 
        
        // Delete 
        ns1Client.delete();
        
        ColumnDefinition cd = new ColumnDefinition("col", "text");
        
        
        SearchDocumentQuery query = SearchDocumentQuery.builder().where("age").isGreaterOrEqualsThan(21).build();

        
     
        
    }
    
}
