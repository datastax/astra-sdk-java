package org.datastax.astra;

import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.datastax.astra.doc.ResultListPage;
import org.datastax.astra.schemas.DataCenter;
import org.datastax.astra.schemas.QueryDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

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
    public void should_createClient_standAloneStargate() {
       Assertions.assertTrue(new AstraClient("http://localhost:8082", dbUser, dbPasswd).connect());
    }
    
    @Test
    public void should_createClient_withConstructor() {
       Assertions.assertTrue(new AstraClient(dbId, dbRegion, dbUser, dbPasswd).connect());
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
        astraClient.namespace(namespace).collection("personi").document(myId)
                   .save(new Person(myId,myId));
;    }
    
    @Test
    public void should_updateDoc() {
        String previousId = "a7811b84-a4ab-4a9e-8fe2-45e13a8f8b19";
        astraClient.namespace(namespace).collection("personi").document(previousId)
                   .save(new Person("loulou", "lala"));
    }
    
    
    @Test
    public void should_exist_doc() {
        String previousId = "1323b239-7192-459b-87d0-a8e994fb2217";
        System.out.println(astraClient
                .namespace(namespace)
                .collection("person")
                .document(previousId)
                .exist());
;    }
    @Test
    public void should_find_doc() throws JsonProcessingException {
        String previousId = "fe5585cd-b2d8-455d-900b-12822b691a37";
        Optional<Person> op = astraClient.namespace(namespace)
                                         .collection("person")
                                         .document(previousId)
                                         .find(Person.class);
        System.out.println(op.isPresent());
        System.out.println(astraClient.getObjectMapper().writeValueAsString(op.get()));
    }
    
    @Test
    public void testFindAll() {
        ResultListPage<Person> results = astraClient
                .namespace(namespace)
                .collection("person")
                .findAll(Person.class);
        for (AstraDocument<Person> person : results.getResults()) {
            System.out.println(
                    person.getDocumentId() + "=" + person.getDocument().getFirstname());
        }
    }
    
    @Test
    public void testSearch() {
        
        QueryDocument query = QueryDocument.builder()
                     .where("age").isGreaterOrEqualsThan(40)
                     .build();
        
        ResultListPage<Person> results = astraClient
                     .namespace(namespace)
                     .collection("person")
                     .search(query, Person.class);
        
        for (AstraDocument<Person> person : results.getResults()) {
            System.out.println(
                    person.getDocumentId() + "=" + person.getDocument().getFirstname());
        }
        
    }
    
    

}
