package org.datastax.astra;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.datastax.astra.devops.ApiDevopsClient;
import org.datastax.astra.doc.ApiDocumentClient;
import org.datastax.astra.doc.AstraDocument;
import org.datastax.astra.doc.ResultListPage;
import org.datastax.astra.schemas.DataCenter;
import org.datastax.astra.schemas.Namespace;
import org.datastax.astra.schemas.QueryDocument;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonProcessingException;

public class AstraClientSimpleTest {
    
    // Credentials Doc and Rest API
    private static String dbId;
    private static String dbRegion;
    private static String dbUser;
    private static String dbPasswd;
    
    // Credentials Devops API
    private static String clientId;
    private static String clientName;
    private static String clientSecret;
    
    // --- CONNECTIVITY ---
    
    @Test
    @Disabled("Stargate not installed")
    public void should_connect_to_stargate_with_builder() {
        // Given
        AstraClient client = AstraClient.builder()
                // needed to enable the Doc and Rest Apis
                .baseUrl("http://localhost:8082")
                // Needed to enable the CQL API
                .addContactPoint("localhost", 9042)
                .username(dbUser).password(dbPasswd)
                .build();
        // Then
        assertTrue(client.apiDocument().testConnection());
        assertTrue(client.apiRest().testConnection());
        assertTrue(client.apiCql().testConnection());
    }
     
    
    // --- NAMESPACE ---
    
    
    /*
    
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
        
        // Connectivity
        AstraClient astraClient = AstraClient.builder()
                .astraDatabaseId(dbId)
                .astraDatabaseRegion(dbRegion)
                .username(dbUser)
                .password(dbPasswd)
                .tokenTtl(Duration.ofSeconds(300))
                .build();
        
        // Create a query
        QueryDocument query = QueryDocument.builder()
                     .where("age").isGreaterOrEqualsThan(40)
                     .build();
        
        // Execute q query
        ResultListPage<Person> results = astraClient
                     .namespace(namespace)
                     .collection("person")
                     .search(query, Person.class);
        
        for (AstraDocument<Person> person : results.getResults()) {
            System.out.println(person.getDocumentId() + "=" + person.getDocument().getFirstname());
        }
        
    }*/
    
}
