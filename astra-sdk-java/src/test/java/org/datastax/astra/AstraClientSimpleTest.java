package org.datastax.astra;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.datastax.astra.schemas.DataCenter;
import org.datastax.astra.schemas.Keyspace;
import org.junit.jupiter.api.Assertions;
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
    
    // --- CONNECTIVITY ---
    
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
    
    // --- NAMESPACE ---
    
    @Test
    public void should_list_namespace() {
        astraClient.namespaces().forEach(System.out::println);
    }
    
    @Test
    public void should_list_namespaceNames() {
        astraClient.namespaceNames().forEach(System.out::println);
    }
    
    @Test
    public void should_exist_namespace() {
        System.out.println(astraClient.namespace("namespace1").exist());
    }
    
    @Test
    public void should_create_namespace() {
        astraClient.namespace("namespace2")
                   .create(Arrays.asList(new DataCenter("dc-1", 1)));
    }
    
    @Test
    public void should_delete_namespace() {
        astraClient.namespace("namespace2").delete();
    }
    
    // --- COLLECTION
    
    @Test
    public void testFindAllCollections() {
       astraClient.namespace("namespace1")
                  .collectionNames()
                  .forEach(System.out::println);;
    }
    
    @Test
    public void should_create_collection() {
        astraClient.namespace(namespace)
                   .collection("lololo")
                   .create();
    }
    
    @Test
    public void should_exist_collection() {
        System.out.println(astraClient.namespace(namespace)
                   .collection("lololo")
                   .exist());
    }
    
    @Test
    public void should_delete_collection() {
        astraClient.namespace(namespace)
                   .collection("lololo")
                   .delete();
    }
    
    // --- DOCUMENT
    
    @Test
    public void should_create_document_genId() {
        String docId = astraClient.namespace(namespace).collection("personi")
                   .save(new Person("loulou", "looulou"));
        System.out.println(docId);
    }
    
    @Test
    public void should_create_doc_withMyID() {
        String myId = UUID.randomUUID().toString();
        astraClient.namespace(namespace).collection("personi")
                   .save(new Person(myId,myId), myId);
;    }
    
    @Test
    public void should_updateDoc() {
        String previousId = "a7811b84-a4ab-4a9e-8fe2-45e13a8f8b19";
        astraClient.namespace(namespace).collection("personi")
                   .save(new Person("loulou", "lala"), previousId);
    }
    
    @Test
    public void should_existDocc() {
        String previousId = "a7811b84-a4ab-4a9e-8fe2-45e13a8f8b19";
        //System.out.println(astraClient.namespace(namespace).collection("personi")
        //           .exist(previousId));
        
        Optional<Person> op = astraClient.namespace(namespace)
                                         .collection("personi")
                                         .findById("iddidi", Person.class);
        System.out.println(op.isPresent());
                
                
;    }
    
   
    
    @Test
    public void test() {
        
       // Optional<DocumentPerson> doc = 
       //         astraClient.namespace("namespace1").findById(
        //        "person", "1323b239-7192-459b-87d0-a8e994fb2217", DocumentPerson.class);
        //System.out.println(doc.isEmpty());
        //System.out.println(doc.get().getData().getFirstName());
        
        //astraClient.restApi("keyspaceName");
        
        //astraClient.devOpsApi();
        
        //astraClient.graphQLApi();
        
        //astraClient.getCqlSession();
        
    }
    
    

}
