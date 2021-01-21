package org.datastax.astra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.datastax.astra.devops.AstraDatabaseInfos;
import org.datastax.astra.devops.ApiDevopsClient;
import org.datastax.astra.devops.DatabaseFilter;
import org.datastax.astra.devops.DatabaseFilter.Include;
import org.datastax.astra.devops.DatabaseFilter.Provider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AstraDevopsApiTest {
    
    // Dataset
   
    
    /**
     * Load properties values from an external file. 
     * Easier than ENV VAR in eclipse and not commited
     * in github, Keys names are just a sample
     */
    @BeforeAll
    public static void initAstraClient() throws FileNotFoundException, IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(new File("/tmp/credentials.properties")));
        clientId      = properties.getProperty("clientId");
        clientName    = properties.getProperty("clientName");
        clientSecret  = properties.getProperty("clientSecret");
    }
    
    @Test
    public void testListDatabases() {
        
        ApiDevopsClient devopsApiClient = AstraClient.devops(clientId,clientName,clientSecret);
        
        
        Stream<AstraDatabaseInfos> results = devopsApiClient.databases(DatabaseFilter.builder()
                .limit(20)
                .provider(Provider.ALL)
                .include(Include.NON_TERMINATED)
                .build());
        for (AstraDatabaseInfos infos : results.collect(Collectors.toList())) {
            System.out.println(infos.getInfo().getRegion());
        }
        
        AstraClient.builder()
                   .clientId("").clientSecret("").clientName("")
                   .build()
                   .devopsAPI()
                   .downloadSecureBundle("dbId");
    }

}
