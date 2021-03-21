package org.datastax.astra;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.datastax.astra.dto.Person;
import org.datastax.astra.dto.Person.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;

import io.stargate.sdk.doc.ApiDocumentClient;
import io.stargate.sdk.doc.AstraDocument;
import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.DocumentClient;
import io.stargate.sdk.doc.Namespace;
import io.stargate.sdk.doc.QueryDocument;
import io.stargate.sdk.doc.ResultListPage;
import io.stargate.sdk.rest.DataCenter;

/**
 * Test operations for the Document API operation
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentWithAstraTest {
    
    private static final String ANSI_RESET           = "\u001B[0m";
    private static final String ANSI_GREEN           = "\u001B[32m";
    private static final String ANSI_YELLOW          = "\u001B[33m";
    
    private static String cliendID     = "ZykcSMWLUDktHMZsYCKWtNQa";
    private static String clientSecret = "uk4ZjvBMyZA075P1jQozwLtNwtb5mELx.bi5n1PDhxZ+U2p+SJAQkg,G9Nf6d_C0cGjt-lub9a2an.agFWnliRc70Kk,+H8wZc_FT4f3K.cUHQH30A1ZMJagC7sunzd.";
    private static String appToken     = "AstraCS:ZykcSMWLUDktHMZsYCKWtNQa:7f95412e3c5014d952febbf5bd4223c74afa95f1f9b205327804c8cac1597e2b";
    private static String dbId         = "9ea35b33-aa48-49fb-88a5-20525aad07fd";
    private static String cloudRegion  = "eu-central-1";
    
    private static final String WORKING_NAMESPACE    = "astra_sdk_namespace_test";
    private static final String COLLECTION_PERSON    = "person";
    
    public static AstraClient client;
    public static ApiDocumentClient clientApiDoc;

    @BeforeAll
    public static void initClient() {
        client = AstraClient.builder()
                .databaseId(dbId)
                .cloudProviderRegion(cloudRegion)
                .clientId(cliendID)
                .clientSecret(clientSecret)
                .appToken(appToken).build();
        clientApiDoc = client.apiDocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Connection with document API");
    }
    
    @Test
    public void testSuite() throws InterruptedException {
        builderParams_should_not_be_empty();
        
        // Operations on namespaces
        should_create_namespace();
        working_namespace_should_exist();
        working_namespace_should_have_dc();
        
        // Operations on collections
        should_create_collection();
        shoudl_find_collection();
        should_delete_collection();
        should_create_newDocument();
        
        // Operation on documents
        should_upsert_document();
        should_update_document();
        should_find_all_person();
        should_search_withQuery();

        // Operations on subdocuments
        should_find_subdocument();
        should_update_subdocument();
        should_delete_subdocument();
    }
    
    public void should_create_namespace() 
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n[POST] Create namespace if needed" + ANSI_RESET);
        if (!clientApiDoc.namespace(WORKING_NAMESPACE).exist()) {
            clientApiDoc.namespace(WORKING_NAMESPACE)
                        .create(new DataCenter(cloudRegion, 3));
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
            int wait = 0;
            while (wait++ < 5 && !clientApiDoc.namespace(WORKING_NAMESPACE).exist()) {
                Thread.sleep(5000);
                System.out.println("+ ");
            }
        }
        Assertions.assertTrue(clientApiDoc.namespace(WORKING_NAMESPACE).exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace exists");
    }
   
    
    public void builderParams_should_not_be_empty() {
        Assertions.assertAll("Required parameters",
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().databaseId(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().databaseId(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().cloudProviderRegion(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().cloudProviderRegion(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().appToken(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { AstraClient.builder().appToken(null); })
        );
    }
    
    public void working_namespace_should_exist() {
        // When
        Set<String> namespaces = clientApiDoc.namespaceNames().collect(Collectors.toSet());
        // Then
        Assertions.assertTrue(namespaces.contains(WORKING_NAMESPACE));
    }
    
    public void working_namespace_should_have_dc() {
        // When
        Map<String, Namespace> namespaces = 
                clientApiDoc.namespaces().collect(
                Collectors.toMap(Namespace::getName,  Function.identity()));
        // Then
        Assertions.assertTrue(namespaces.containsKey(WORKING_NAMESPACE));
        Assertions.assertFalse(namespaces.get(WORKING_NAMESPACE).getDatacenters().isEmpty());
    }
   
    public void should_create_collection() 
    throws InterruptedException {
      System.out.println(ANSI_YELLOW + "\n[POST] Create/Delete collections" + ANSI_RESET);
      // Create working collection is not present
      if (clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).exist()) {
          clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).delete();
          System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Delete request sent");
      }
      // Given
      Assertions.assertFalse(clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).exist());
      // When
      clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).create();
      System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
      // Then
      Assertions.assertTrue(clientApiDoc
              .namespace(WORKING_NAMESPACE)
              .collection(COLLECTION_PERSON)
              .exist());
      System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection exist");
    }
    
    public void shoudl_find_collection() {
        Assertions.assertTrue(clientApiDoc
                  .namespace(WORKING_NAMESPACE)
                  .collectionNames().anyMatch(s -> COLLECTION_PERSON.equals(s)));
    }
   
    public void should_delete_collection() 
    throws InterruptedException {
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        Assertions.assertFalse(clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(randomCollection).exist());
        // When
        clientApiDoc.namespace(WORKING_NAMESPACE)
                    .collection(randomCollection)
                    .create();
        Thread.sleep(2000);
        // Then
        Assertions.assertTrue(clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(randomCollection)
                .exist());
        // When
        clientApiDoc.namespace(WORKING_NAMESPACE)
                    .collection(randomCollection)
                    .delete();
        // Then
        Assertions.assertFalse(clientApiDoc
                    .namespace(WORKING_NAMESPACE)
                    .collection(randomCollection)
                    .exist());
    }
    
    public void should_create_newDocument() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        String docId = collectionPerson.createNewDocument(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertNotNull(docId);
        Assertions.assertTrue(collectionPerson.document(docId).exist());
    }
    
    public void should_upsert_document() {
        // Given
        CollectionClient collectionPerson = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("myId")
                        .save(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertTrue(collectionPerson
                        .document("myId")
                        .exist());
    }
    
    public void should_update_document() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("123").save(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPerson.document("123").save(new Person("loulou", "looulou", 20, new Address("Paris", 75015)));
        // Then
        Optional<Person> loulou = collectionPerson.document("123").find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        Assertions.assertEquals(75015, loulou.get().getAddress().getZipCode());
    }
    
    public void should_find_all_person() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        ResultListPage<Person> results = collectionPerson.findAll(Person.class);
        // Then
        for (AstraDocument<Person> person : results.getResults()) {
            System.out.println(person.getDocumentId() + "=" + person.getDocument().getFirstname());
        }
    }

    public void should_search_withQuery() {
        // Given
        CollectionClient collectionPerson = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        
        collectionPerson.document("person1")
                        .save(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        collectionPerson.document("person2")
                        .save(new Person("person2", "person2", 30, new Address("Paris", 75000)));
        collectionPerson.document("person3")
                        .save(new Person("person3", "person3", 40, new Address("Melun", 75000)));
        Assertions.assertTrue(collectionPerson.document("person1").exist());
        Assertions.assertTrue(collectionPerson.document("person2").exist());
        Assertions.assertTrue(collectionPerson.document("person3").exist());
        
        
        // Create a query
        QueryDocument query = QueryDocument.builder()
                    .where("age")
                    .isGreaterOrEqualsThan(21)
                    .build();
        
        // Execute q query
        ResultListPage<Person> results = collectionPerson.search(query, Person.class);
        
        for (AstraDocument<Person> person : results.getResults()) {
            System.out.println(person.getDocumentId() + "=" + person.getDocument().getAge());
        }
        
    }
    
    public void should_find_subdocument() {
        // Given, Collection exist, Document Exist
        CollectionClient cc = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(cc.exist());
        
        // Create doc
        DocumentClient p1 = cc.document("person1");
        p1.save(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());

        // When
        Optional<String> os = p1.findSubDocument("firstname", String.class);
        Assertions.assertTrue(os.isPresent());
        Assertions.assertTrue(os.get().length() > 0);
        
        // When
        Optional<Integer> oi = p1.findSubDocument("age", Integer.class);
        Assertions.assertTrue(oi.isPresent());
        Assertions.assertEquals(20, oi.get());
        
        // When
        Optional<Address> oa  = p1.findSubDocument("address", Address.class);
        Assertions.assertTrue(oa.isPresent());
        Assertions.assertEquals(75000, oa.get().getZipCode());
        
        // When
        Optional<Integer> oz = p1.findSubDocument("address/zipCode", Integer.class);
        Assertions.assertTrue(oz.isPresent());
        Assertions.assertEquals(75000, oz.get());  
    }
    
    @Test
    public void should_update_subdocument() {
        
        // Given
        CollectionClient cc = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(cc.exist());
        
        DocumentClient p1 = cc.document("person1");
        p1.save(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());
        
        // When
        p1.updateSubDocument("address", new Address("city2", 8000));
        // Then
        Address updated = p1.findSubDocument("address", Address.class).get();
        Assertions.assertEquals(8000, updated.getZipCode());
        Assertions.assertEquals("city2", updated.getCity());
    }
    
    public void should_delete_subdocument() {
        // Given
        CollectionClient cc = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(cc.exist());
        DocumentClient p1 = cc.document("person1");
        p1.save(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());
        Assertions.assertFalse(p1.findSubDocument("address", Address.class).isEmpty());
        // When
        p1.deleteSubDocument("address");
        // Then
        Assertions.assertTrue(p1.findSubDocument("address", Address.class).isEmpty());
    }
}
