package org.datastax.astra;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.datastax.astra.dto.PersonAstra;
import org.datastax.astra.dto.PersonAstra.Address;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.dstx.astra.sdk.AstraClient;

import io.stargate.sdk.doc.ApiDocumentClient;
import io.stargate.sdk.doc.ApiDocument;
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
    
    // Update those values to ease unit tests wuhen debug
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
        // =====================================================
        // LOAD PARAMETER VALUES FROM COMMANDS (IF AVAILABLE)
        // =====================================================
        if (null != System.getenv(AstraClient.ASTRA_DB_REGION) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_REGION))) {
            cloudRegion = System.getenv(AstraClient.ASTRA_DB_REGION);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_REGION) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_REGION))) {
            cloudRegion = System.getProperty(AstraClient.ASTRA_DB_REGION);
        } 
        if (null != System.getenv(AstraClient.ASTRA_DB_ID) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_ID))) {
            dbId = System.getenv(AstraClient.ASTRA_DB_ID);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_ID) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_ID))) {
            dbId = System.getProperty(AstraClient.ASTRA_DB_ID);
        }
        if (null != System.getenv(AstraClient.ASTRA_DB_CLIENT_ID) && 
           !"".equals(System.getenv(AstraClient.ASTRA_DB_CLIENT_ID))) {
          cliendID = System.getenv(AstraClient.ASTRA_DB_CLIENT_ID);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_CLIENT_ID) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_CLIENT_ID))) {
          cliendID = System.getProperty(AstraClient.ASTRA_DB_CLIENT_ID);
        }
        if (null != System.getenv(AstraClient.ASTRA_DB_CLIENT_SECRET) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_CLIENT_SECRET))) {
          clientSecret = System.getenv(AstraClient.ASTRA_DB_CLIENT_SECRET);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_CLIENT_SECRET) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_CLIENT_SECRET))) {
          clientSecret = System.getProperty(AstraClient.ASTRA_DB_CLIENT_SECRET);
        }
        if (null != System.getenv(AstraClient.ASTRA_DB_APPLICATION_TOKEN) && 
                !"".equals(System.getenv(AstraClient.ASTRA_DB_APPLICATION_TOKEN))) {
            appToken = System.getenv(AstraClient.ASTRA_DB_APPLICATION_TOKEN);
        }
        if (null != System.getProperty(AstraClient.ASTRA_DB_APPLICATION_TOKEN) && 
                !"".equals(System.getProperty(AstraClient.ASTRA_DB_APPLICATION_TOKEN))) {
            appToken = System.getProperty(AstraClient.ASTRA_DB_APPLICATION_TOKEN);
        }
        // =====================================================
        
        
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
        
        System.out.println(ANSI_YELLOW + "\n[] Checking required parameters " + ANSI_RESET);
        builderParams_should_not_be_empty();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Tests passed.");
        
        // Operations on namespaces
        System.out.println(ANSI_YELLOW + "\n[POST] Create namespace if needed" + ANSI_RESET);
        should_create_namespace();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace exists.");
        
        System.out.println(ANSI_YELLOW + "\n[GET] Created namespace is in available list" + ANSI_RESET);
        working_namespace_should_exist();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace founds.");
        working_namespace_should_have_dc();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace datacenters are populated.");
        
        // Operations on collections
        System.out.println(ANSI_YELLOW + "\n[POST] Create a collection" + ANSI_RESET);
        should_create_collection();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection exist");
        
        System.out.println(ANSI_YELLOW + "\n[POST] Collection should now be available" + ANSI_RESET);
        shoudl_find_collection();
        
        System.out.println(ANSI_YELLOW + "\n[DELETE] Delete a collection" + ANSI_RESET);
        should_delete_collection();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection has been deleted");
        
        System.out.println(ANSI_YELLOW + "\n[PUT] Create a new document" + ANSI_RESET);
        should_create_newDocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document has been created");
        
        // Operation on documents
        System.out.println(ANSI_YELLOW + "\n[PUT] Upsert new document" + ANSI_RESET);
        should_upsert_document_create();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document has been created with upsert");
        
        System.out.println(ANSI_YELLOW + "\n[POST] replace a document" + ANSI_RESET);
        should_upsert_document_update();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " -  Document has been updated with upsert");

        System.out.println(ANSI_YELLOW + "\n[POST] update a document" + ANSI_RESET);
        should_update_document();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " -  Document has been updated with update (PATCH)");
        
        System.out.println(ANSI_YELLOW + "\n[GET] Should findAll" + ANSI_RESET);
        should_find_all_person();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected docs retrieved");
        
        System.out.println(ANSI_YELLOW + "\n[GET] Should find with Where clause" + ANSI_RESET);
        should_search_withQuery();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Expected docs retrieved");

        // Operations on subdocuments
        System.out.println(ANSI_YELLOW + "\n[GET] Should find a sub doc" + ANSI_RESET);
        should_find_subdocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Doc found");

        System.out.println(ANSI_YELLOW + "\n[PUT] Should update a sub doc" + ANSI_RESET);
        should_update_subdocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Doc updated");

        System.out.println(ANSI_YELLOW + "\n[DELETE] Should DELETE a sub doc" + ANSI_RESET);
        should_delete_subdocument();
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Doc deleted");

    }
    
    public void should_create_namespace() 
    throws InterruptedException {
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
        String docId = collectionPerson.createNewDocument(new PersonAstra("loulou", "looulou", 20, new Address("Paris", 75000)));
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
                        .upsert(new PersonAstra("loulou", "looulou", 20, new Address("Paris", 75000)));
        
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
        collectionPerson.document("123").upsert(new PersonAstra("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPerson.document("123").upsert(new PersonAstra("loulou", "looulou", 20, new Address("Paris", 75015)));
        // Then
        Optional<PersonAstra> loulou = collectionPerson.document("123").find(PersonAstra.class);
        Assertions.assertTrue(loulou.isPresent());
        Assertions.assertEquals(75015, loulou.get().getAddress().getZipCode());
    }
    
    
    public void should_update_document() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("AAA").upsert(new PersonAstra("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPerson.document("AAA").update(new PersonAstra("a", "b"));
        // Then
        Optional<PersonAstra> loulou = collectionPerson.document("AAA").find(PersonAstra.class);
        Assertions.assertTrue(loulou.isPresent());
        // Then sub fields are still there
        Assertions.assertEquals(75000, loulou.get().getAddress().getZipCode());
    }
    
    
    
    public void should_find_all_person() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        ResultListPage<PersonAstra> results = collectionPerson.findAll(PersonAstra.class);
        // Then
        Assert.assertNotNull(results);
        for (ApiDocument<PersonAstra> person : results.getResults()) {
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
                        .upsert(new PersonAstra("person1", "person1", 20, new Address("Paris", 75000)));
        collectionPerson.document("person2")
                        .upsert(new PersonAstra("person2", "person2", 30, new Address("Paris", 75000)));
        collectionPerson.document("person3")
                        .upsert(new PersonAstra("person3", "person3", 40, new Address("Melun", 75000)));
        Assertions.assertTrue(collectionPerson.document("person1").exist());
        Assertions.assertTrue(collectionPerson.document("person2").exist());
        Assertions.assertTrue(collectionPerson.document("person3").exist());
        
        
        // Create a query
        QueryDocument query = QueryDocument.builder()
                    .where("age")
                    .isGreaterOrEqualsThan(21)
                    .build();
        
        // Execute q query
        ResultListPage<PersonAstra> results = collectionPerson.search(query, PersonAstra.class);
        Assert.assertNotNull(results);
        for (ApiDocument<PersonAstra> person : results.getResults()) {
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
        p1.upsert(new PersonAstra("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(PersonAstra.class).isPresent());

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
        p1.upsert(new PersonAstra("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(PersonAstra.class).isPresent());
        
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
        p1.upsert(new PersonAstra("person1", "person1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(PersonAstra.class).isPresent());
        Assertions.assertFalse(p1.findSubDocument("address", Address.class).isEmpty());
        // When
        p1.deleteSubDocument("address");
        // Then
        Assertions.assertTrue(p1.findSubDocument("address", Address.class).isEmpty());
    }
}
