package org.datastax.astra;

import java.time.Duration;
import java.util.Optional;

import org.datastax.astra.utils.MappingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AstraClientSimpleTest {
    
    // Dataset
    static String dbId     = "e92195f2-159f-492e-9777-3dadda3ff1a3";
    static String dbRegion = "europe-west1";
    static String dbUser   = "todouser";
    static String dbPasswd = "todoPassword1";
    
    static String namespace = "namespace1";
    
    static AstraClient astraClient = AstraClient.builder()
            .astraDatabaseId(dbId)
            .astraDatabaseRegion(dbRegion)
            .username(dbUser)
            .password(dbPasswd)
            .tokenTtl(Duration.ofSeconds(300))
            .build();
    
    @Test
    public void should_createClient_withBuilder() {
        Assertions.assertTrue(AstraClient.builder()
                .astraDatabaseId(dbId)
                .astraDatabaseRegion(dbRegion)
                .username(dbUser)
                .password(dbPasswd)
                .tokenTtl(Duration.ofSeconds(300))
                .build().connect());
    }
    
    @Test
    public void should_createClient_withConstructor() {
       Assertions.assertTrue(
               new AstraClient(dbId, dbRegion, dbUser, dbPasswd)
                   .connect());
    }
    
    @Test
    /**
     * Reading... ASTRA_DB_ID, ASTRA_DB_REGION, ASTRA_DB_USERNAME, ASTRA_DB_PASSWORD
     */
    public void should_createClient_withEnvVar() {
        Assertions.assertTrue(AstraClient.builder().build().connect());
    }
    
    @Test
    public void should_create_collection() {
        astraClient.namespace(namespace).createCollection("org_datastax_astra__person");
    }
    
    @Test
    public void testFindAllCollections() {
       astraClient.namespace("namespace1")
                  .findAllCollections()
                  .stream()
                  .forEach(System.out::println);;
    }
    
    @Test
    public void should_create_document_genId() {
        //astraClient.namespace(namespace).create(new Person("Doc", "doc"));
        astraClient.namespace(namespace).create(new Person("Doc", "doc"), "MYID");
        
    }
    
    @Test
    public void test() {
        
        //System.out.println(astraClient.namespace("namespace1").existCollection("person"));
        //astraClient.namespace("namespace1").createCollection("cedrick");
        
        
     // Optional but testing parameters
        //SimplePerson p = new SimplePerson("Cedrick", "Lunven");
        //Document<SimplePerson> doc = new Document<SimplePerson>(p, SimplePerson.class);
        
        //doc.setCollectionName("person");
        
        //System.out.println(doc.getGenericName());
        //System.out.println(astraClient.namespace("namespace1").create(doc));
        
        //System.out.println(astraClient.namespace("namespace1")
        //           .existDocument("person", "1323b239-7192-459b-87d0-a8e994fb2218"));
        
        
        Optional<DocumentPerson> doc = 
                astraClient.namespace("namespace1").findById(
                "person", "1323b239-7192-459b-87d0-a8e994fb2217", DocumentPerson.class);
        System.out.println(doc.isEmpty());
        System.out.println(doc.get().getData().getFirstName());
        
        
        //astraClient.documentApi("namespace").create(new Document<>(val));
        
        //astraClient.restApi("keyspaceName");
        
        //astraClient.devOpsApi();
        
        //astraClient.graphQLApi();
        
        //astraClient.getCqlSession();
        
    }
    
    

}
