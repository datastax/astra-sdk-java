package io.stargate.sdk.test;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.stargate.sdk.StargateClient;
import io.stargate.sdk.doc.ApiDocumentClient;
import io.stargate.sdk.doc.AstraDocument;
import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.DocumentClient;
import io.stargate.sdk.doc.QueryDocument;
import io.stargate.sdk.doc.ResultListPage;
import io.stargate.sdk.test.dto.Person;
import io.stargate.sdk.test.dto.Person.Address;
/**
 * Test operations for the Document API operation
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DocumentApiTestIT extends AsbtractStargateTestIt {
    
    public static final String WORKING_NAMESPACE    = "astra_sdk_namespace_test";
    public static final String COLLECTION_PERSON    = "person";
    
    public static ApiDocumentClient clientApiDoc;
    
    @BeforeAll
    public static void initDocumentAPI() {
        AsbtractStargateTestIt.init();
        clientApiDoc = client.apiDocument();
        Assertions.assertNotNull(clientApiDoc);
    }
    
    /**
     * This integration test will run through document API endpoint and test all methods
     * 
     * @see http://127.0.0.2:8082/swagger-ui/#/documents
     * 
     * @throws InterruptedException
     */
    @Test
    public void testSuite() throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#02 Parameters Validation (builder) " + ANSI_RESET);
        builder_should_fail_if_empty_params();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Tests passed.");
        
        System.out.println(ANSI_YELLOW + "\n#03 Authentication Token" + ANSI_RESET);
        String token = client.apiDocument().getToken();
        Assertions.assertNotNull(token);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Token retrieved." + token);
        
        // Operations on namespaces
        System.out.println(ANSI_YELLOW + "\n#04 Working with Namespaces" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "[POST] Create namespace if needed" + ANSI_RESET);
        should_create_namespace();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace exists.");
        System.out.println(ANSI_GREEN + "[GET] Created namespace is in available list" + ANSI_RESET);
        working_namespace_should_exist();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace founds.");
       
        // Operations on collections
        System.out.println(ANSI_YELLOW + "\n#05 Working with Collections" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "[POST] Create a collection" + ANSI_RESET);
        should_create_collection();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection exist");
        System.out.println(ANSI_GREEN + "[POST] Collection should now be available" + ANSI_RESET);
        shoudl_find_collection();
        System.out.println(ANSI_GREEN + "[DELETE] Delete a collection" + ANSI_RESET);
        should_delete_collection();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection has been deleted");
        
        // sDocuments
        System.out.println(ANSI_YELLOW + "\n#06 Working with Documents" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "[PUT] Create a new document" + ANSI_RESET);
        should_create_newDocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document has been created");
        System.out.println(ANSI_GREEN + "[PUT] Upsert new document" + ANSI_RESET);
        should_upsert_document_create();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document has been created with upsert");  
        System.out.println(ANSI_GREEN + "[POST] replace a document" + ANSI_RESET);
        should_upsert_document_update();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " -  Document has been updated with upsert");
        System.out.println(ANSI_GREEN + "[POST] update a document" + ANSI_RESET);
        should_update_document();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " -  Document has been updated with update (PATCH)");
        System.out.println(ANSI_GREEN + "[GET] Should findAll" + ANSI_RESET);
        should_find_all_person();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected docs retrieved");
        System.out.println(ANSI_GREEN + "[GET] Should find with Where clause" + ANSI_RESET);
        should_search_withQuery();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected docs retrieved");

        // Operations on subdocuments
        System.out.println(ANSI_YELLOW + "\n#07 Working with SubDocuments" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "[GET] Should find a sub doc" + ANSI_RESET);
        should_find_subdocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Doc found");
        System.out.println(ANSI_GREEN + "[PUT] Should update a sub doc" + ANSI_RESET);
        should_update_subdocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Doc updated");
        System.out.println(ANSI_GREEN + "[DELETE] Should DELETE a sub doc" + ANSI_RESET);
        should_delete_subdocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Doc deleted");
        
    }
    
    public void builder_should_fail_if_empty_params() {
        Assertions.assertAll("Required parameters",
                () -> Assertions.assertThrows(IllegalArgumentException.class,  
                        () -> { StargateClient.builder().cqlContactPoint(null,0); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class,  
                        () -> { StargateClient.builder().cqlContactPoint("",0); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().addCqlContactPoint(null,0); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().addCqlContactPoint("",0); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().username(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().username(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().password(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().password(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().localDc(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().localDc(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().authenticationUrl(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().documentApiUrl(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().documentApiUrl(null); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().restApiUrl(""); }),
                () -> Assertions.assertThrows(IllegalArgumentException.class, 
                        () -> { StargateClient.builder().restApiUrl(null); })
        );
    }
   
    public void should_create_namespace() 
    throws InterruptedException {
        if (!clientApiDoc.namespace(WORKING_NAMESPACE).exist()) {
            clientApiDoc.namespace(WORKING_NAMESPACE).createSimple(1);
            System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
            int wait = 0;
            while (wait++ < 5 && !clientApiDoc.namespace(WORKING_NAMESPACE).exist()) {
                Thread.sleep(1000);
                System.out.println("+ ");
            }
        }
        Assertions.assertTrue(clientApiDoc.namespace(WORKING_NAMESPACE).exist());
    }
   
    public void working_namespace_should_exist() {
        // When
        Set<String> namespaces = clientApiDoc.namespaceNames().collect(Collectors.toSet());
        // Then
        Assertions.assertTrue(namespaces.contains(WORKING_NAMESPACE));
    }
    
    public void should_create_collection() 
    throws InterruptedException {
      // Create working collection is not present
      if (clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).exist()) {
          clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).delete();
          System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Delete collection request sent");
          Thread.sleep(500);
      }
      // Given
      Assertions.assertFalse(clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).exist());
      System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection does not exist");
      // When
      clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).create();
      System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
      Thread.sleep(1000);
      // Then
      Assertions.assertTrue(clientApiDoc
              .namespace(WORKING_NAMESPACE)
              .collection(COLLECTION_PERSON)
              .exist());
      System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection now exist");
    }
    
    public void shoudl_find_collection()
    throws InterruptedException {
        Thread.sleep(1000);
        Assertions.assertTrue(clientApiDoc
                  .namespace(WORKING_NAMESPACE)
                  .collectionNames()
                  .anyMatch(s -> COLLECTION_PERSON.equals(s)));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection is available in list");
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
        Thread.sleep(500);
        // Then
        Assertions.assertTrue(clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(randomCollection)
                .exist());
        // When
        clientApiDoc.namespace(WORKING_NAMESPACE)
                    .collection(randomCollection)
                    .delete();
        Thread.sleep(500);
        // Then
        Assertions.assertFalse(clientApiDoc
                    .namespace(WORKING_NAMESPACE)
                    .collection(randomCollection)
                    .exist());
    }
    
   
    public void should_create_newDocument() throws InterruptedException {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        String docId = collectionPerson.createNewDocument(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertNotNull(docId);
        Thread.sleep(500);
        Assertions.assertTrue(collectionPerson.document(docId).exist());
    }
    
    public void should_upsert_document_create()
    throws InterruptedException {
        // Given
        CollectionClient collectionPerson = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("myId")
                        .upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        
        Thread.sleep(500);
        // Then
        Assertions.assertTrue(collectionPerson
                        .document("myId")
                        .exist());
    }
    
    public void should_upsert_document_update() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("123").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPerson.document("123").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75015)));
        // Then
        Optional<Person> loulou = collectionPerson.document("123").find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        Assertions.assertEquals(75015, loulou.get().getAddress().getZipCode());
    }
    
    
    public void should_update_document() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("AAA").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPerson.document("AAA").update(new Person("a", "b"));
        // Then
        Optional<Person> loulou = collectionPerson.document("AAA").find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        // Then sub fields are still there
        Assertions.assertEquals(75000, loulou.get().getAddress().getZipCode());
    }
    
    
    
    public void should_find_all_person() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        ResultListPage<Person> results = collectionPerson.findAll(Person.class);
        // Then
        Assert.assertNotNull(results);
        for (AstraDocument<Person> person : results.getResults()) {
            Assert.assertNotNull(person);
        }
    }

    public void should_search_withQuery() {
        // Given
        CollectionClient collectionPerson = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        
        collectionPerson.document("person1")
                        .upsert(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        collectionPerson.document("person2")
                        .upsert(new Person("person2", "person2", 30, new Address("Paris", 75000)));
        collectionPerson.document("person3")
                        .upsert(new Person("person3", "person3", 40, new Address("Melun", 75000)));
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
        Assert.assertNotNull(results);
        for (AstraDocument<Person> person : results.getResults()) {
            Assert.assertNotNull(person);
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
        p1.upsert(new Person("person1", "person1", 20, new Address("Paris", 75000)));
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
    
    public void should_update_subdocument() {
        
        // Given
        CollectionClient cc = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(cc.exist());
        
        DocumentClient p1 = cc.document("person1");
        p1.upsert(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());
        
        // When
        p1.replaceSubDocument("address", new Address("city2", 8000));
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
        p1.upsert(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());
        Assertions.assertFalse(p1.findSubDocument("address", Address.class).isEmpty());
        // When
        p1.deleteSubDocument("address");
        // Then
        Assertions.assertTrue(p1.findSubDocument("address", Address.class).isEmpty());
    }
}
