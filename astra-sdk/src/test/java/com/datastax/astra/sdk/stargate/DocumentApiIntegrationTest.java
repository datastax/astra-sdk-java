/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.sdk.stargate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.dto.Address;
import com.datastax.astra.dto.Person;
import com.datastax.astra.sdk.AbstractAstraIntegrationTest;
import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.audit.ApiCallListenerLog;
import com.datastax.stargate.sdk.doc.ApiDocument;
import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.DocumentClient;
import com.datastax.stargate.sdk.doc.domain.DocumentResultPage;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;
import com.datastax.stargate.sdk.utils.HttpApisClient;

/**
 * Test operations for the Document API operation
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class DocumentApiIntegrationTest extends AbstractAstraIntegrationTest {
    
    /** TEST CONSTANTS. */
    private static final String TEST_DBNAME          = "sdk_test_api_stargate";
    private static final String WORKING_NAMESPACE    = "ns1";
    private static final String COLLECTION_PERSON    = "person";
    
    // Client Test
    private static ApiDocumentClient clientApiDoc;
    
    @BeforeAll
    public static void config() {
        printYellow("=======================================");
        printYellow("=     Document Api IntegrationTest    =");
        printYellow("=======================================");
        String dbId = createDbAndKeyspaceIfNotExist(TEST_DBNAME, WORKING_NAMESPACE);
        client.cqlSession().close();
        
        // Connect the client to the new created DB
        client = AstraClient.builder()
                  .appToken(client.getToken().get())
                  .clientId(client.getClientId().get())
                  .clientSecret(client.getClientSecret().get())
                  .keyspace(WORKING_NAMESPACE)
                  .databaseId(dbId)
                  .cloudProviderRegion("us-east-1")
                  .build();
        clientApiDoc = client.apiStargateDocument();
        printOK("Connection established to the DB");
    }
   
    @Test
    @Order(1)
    @DisplayName("Parameter validations should through IllegalArgumentException(s)")
    public void builderParams_should_not_be_empty() {
        printYellow("builderParams_should_not_be_empty");
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
        printOK("Required parameters are tested");
    }
    
    @Test
    @Order(2)
    @DisplayName("Create and delete empty collection")
    public void should_create_empty_collection() throws InterruptedException {
        // Operations on collections
        printYellow("should_create_empty_collection");
        CollectionClient cc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        
        if (cc.exist()) {
            printOK("Collection aleady exists");
            cc.delete();
            printOK("Delete collection request sent");
            Thread.sleep(500);
        }
        // Given
        Assertions.assertFalse(cc.exist());
        printOK("Collection does not exist");
        // When
        cc.create();
        printOK("Creation request sent");
        Thread.sleep(1000);
        // Then
        Assertions.assertTrue(cc.exist());
        printOK("Collection now exist");
    }

    @Test
    @Order(3)
    @DisplayName("Find Collection")
    public void should_find_collection() throws InterruptedException {
        printYellow("should_find_collection");
        
        HttpApisClient.getInstance().registerListener("audit", new ApiCallListenerLog());
        
        Assertions.assertTrue(clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collectionNames()
                .anyMatch(s -> COLLECTION_PERSON.equals(s)));
        printOK("Collection is available in list");
        
        Assertions.assertTrue(clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON)
                .exist());
        printOK("Collection is available find find()");
        
    }
    
    @Test
    @Order(4)
    @DisplayName("Delete collection")
    public void should_delete_collection() throws InterruptedException {
        printYellow("should_delete_collection");
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        CollectionClient rcc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(randomCollection);
        Assertions.assertFalse(rcc.exist());
        // When
        rcc.create();
        Thread.sleep(1000);
        // Then
        Assertions.assertTrue(rcc.exist());
        // When
        rcc.delete();
        Thread.sleep(1000);
        // Then
        Assertions.assertFalse(rcc.exist());
        printOK("Collection deleted");
    }

    @Test
    @Order(5)
    @DisplayName("Create document")
    public void should_create_newDocument() throws InterruptedException {
        printYellow("should_create_newDocument");
        // Given
        CollectionClient collectionPersonAstra = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPersonAstra.exist());
        // When
        String docId = collectionPersonAstra.create(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertNotNull(docId);
        Thread.sleep(1000);
        Assertions.assertTrue(collectionPersonAstra.document(docId).exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document created");
    }
    
    
    @Test
    @Order(5)
    public void testCount() {
        CollectionClient collectionPersonAstra = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        System.out.println(collectionPersonAstra.count());
    }
    
    @Test
    @Order(6)
    @DisplayName("Upsert document")
    public void should_upsert_document_create() throws InterruptedException {
        printYellow("should_upsert_document_createh");
        // Given
        // Given
        CollectionClient collectionPersonAstra = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPersonAstra.exist());
        // When
        collectionPersonAstra.document("myId").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));

        Thread.sleep(500);
        // Then
        Assertions.assertTrue(collectionPersonAstra.document("myId").exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document created");
    }

    @Test
    @Order(7)
    @DisplayName("Update document")
    public void should_upsert_document_update() throws InterruptedException {
        printYellow("should_upsert_document_update");
        // Given
        CollectionClient collectionPersonAstra = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPersonAstra.exist());
        String uid = UUID.randomUUID().toString();
        Assertions.assertFalse(collectionPersonAstra.document(uid).exist());
        // When
        collectionPersonAstra.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPersonAstra.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75015)));
        // Then
        Optional<Person> loulou = collectionPersonAstra.document(uid).find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        Assertions.assertEquals(75015, loulou.get().getAddress().getZipCode());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document updated");
    }
    
    @Test
    @Order(8)
    @DisplayName("Delete document")
    public void should_delete_document() throws InterruptedException {
        printYellow("should_delete_document");
        // Given
        CollectionClient collectionPersonAstra = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        String uid = UUID.randomUUID().toString();
        Assertions.assertFalse(collectionPersonAstra.document(uid).exist());
        printOK("Document does not exist");
        
        // When
        collectionPersonAstra.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertTrue(collectionPersonAstra.document(uid).exist());
        printOK("Document exist");
        
        collectionPersonAstra.document(uid).delete();
        printOK("Deleting....");
        Thread.sleep(5000);
        
        Assertions.assertFalse(collectionPersonAstra.document(uid).exist());
        printOK("Document does not exist");
        
        Assertions.assertFalse(collectionPersonAstra
                .document(uid)
                .find(String.class)
                .isPresent());
      
        Assertions.assertThrows(RuntimeException.class, () -> {
            collectionPersonAstra.document(uid).delete();
        });
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document deleted");
    }

    @Test
    @Order(9)
    @DisplayName("Update document")
    public void should_update_document() {
        printYellow("should_update_document");
        // Given
        CollectionClient collectionPersonAstra = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPersonAstra.exist());
        // When
        collectionPersonAstra.document("AAA").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPersonAstra.document("AAA").update(new Person("a", "b"));
        // Then
        Optional<Person> loulou = collectionPersonAstra.document("AAA").find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        // Then sub fields are still there
        Assertions.assertEquals(75000, loulou.get().getAddress().getZipCode());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document updated");
    }

    @Test
    @Order(10)
    @DisplayName("Find All PersonAstra")
    public void should_find_all_PersonAstra() {
        printYellow("should_find_all_PersonAstra");
        // Given
        CollectionClient collectionPersonAstra = clientApiDoc
                .namespace(WORKING_NAMESPACE)
                .collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPersonAstra.exist());
        // When
        DocumentResultPage<Person> results = collectionPersonAstra.findAllPageable(Person.class);
        // Then
        Assert.assertNotNull(results);
        for (ApiDocument<Person> PersonAstra : results.getResults()) {
            Assert.assertNotNull(PersonAstra);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document list found");
    }

    @Test
    @Order(11)
    @DisplayName("Search Query")
    public void should_search_withQuery() {
        printYellow("should_search_withQuery");
        // Given
        CollectionClient collectionPersonAstra = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPersonAstra.exist());

        collectionPersonAstra.document("PersonAstra1").upsert(new Person("PersonAstra1", "PersonAstra1", 20, new Address("Paris", 75000)));
        collectionPersonAstra.document("PersonAstra2").upsert(new Person("PersonAstra2", "PersonAstra2", 30, new Address("Paris", 75000)));
        collectionPersonAstra.document("PersonAstra3").upsert(new Person("PersonAstra3", "PersonAstra3", 40, new Address("Melun", 75000)));
        Assertions.assertTrue(collectionPersonAstra.document("PersonAstra1").exist());
        Assertions.assertTrue(collectionPersonAstra.document("PersonAstra2").exist());
        Assertions.assertTrue(collectionPersonAstra.document("PersonAstra3").exist());

        // Create a query
        SearchDocumentQuery query = SearchDocumentQuery.builder().where("age").isGreaterOrEqualsThan(21).build();

        // Execute q query
        DocumentResultPage<Person> results = collectionPersonAstra.searchPageable(query, Person.class);
        Assert.assertNotNull(results);
        for (ApiDocument<Person> PersonAstra : results.getResults()) {
            Assert.assertNotNull(PersonAstra);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Document list found");
    }
    
   

    @Test
    @Order(12)
    @DisplayName("Find sub doc")
    public void should_find_subdocument() {
        printYellow("should_find_subdocument");
        // Given, Collection exist, Document Exist
        CollectionClient cc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(cc.exist());
        System.out.println(" + Collection exist");

        // Create doc
        DocumentClient p1 = cc.document("PersonAstra1");
        p1.upsert(new Person("PersonAstra1", "PersonAstra1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());
        System.out.println(" + Document created");

        // When
        Optional<String> os = p1.findSubDocument("firstname", String.class);
        Assertions.assertTrue(os.isPresent());
        Assertions.assertTrue(os.get().length() > 0);
        System.out.println(" + subdoc find");

        // When
        Optional<Integer> oi = p1.findSubDocument("age", Integer.class);
        Assertions.assertTrue(oi.isPresent());
        Assertions.assertEquals(20, oi.get());

        // When
        Optional<Address> oa = p1.findSubDocument("address", Address.class);
        Assertions.assertTrue(oa.isPresent());
        Assertions.assertEquals(75000, oa.get().getZipCode());

        // When
        Optional<Integer> oz = p1.findSubDocument("address/zipCode", Integer.class);
        Assertions.assertTrue(oz.isPresent());
        Assertions.assertEquals(75000, oz.get());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Sub document retrieved");
    }

    @Test
    @Order(13)
    @DisplayName("Update sub doc")
    public void should_update_subdocument() {
        printYellow("should_update_subdocument");
        // Given
        CollectionClient cc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(cc.exist());

        DocumentClient p1 = cc.document("PersonAstra1");
        p1.upsert(new Person("PersonAstra1", "PersonAstra1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());

        // When
        p1.replaceSubDocument("address", new Address("city2", 8000));
        // Then
        Address updated = (Address) p1.findSubDocument("address", Address.class).get();
        Assertions.assertEquals(8000, updated.getZipCode());
        Assertions.assertEquals("city2", updated.getCity());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Sub document updated");
    }

    @Test
    @Order(14)
    @DisplayName("Delete sub doc")
    public void should_delete_subdocument() {
        printYellow("should_delete_subdocument");
        // Given
        CollectionClient cc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(cc.exist());
        DocumentClient p1 = cc.document("PersonAstra1");
        p1.upsert(new Person("PersonAstra1", "PersonAstra1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());
        Assertions.assertTrue(p1.findSubDocument("address", Address.class).isPresent());
        // When
        p1.deleteSubDocument("address");
        // Then
        Assertions.assertFalse(p1.findSubDocument("address", Address.class).isPresent());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Sub document deleted");
    }
    
    @Test
    @Order(15)
    @DisplayName("Invalid parameters")
    public void testInvalidDoc() {
        printYellow("testInvalidDoc");
        DocumentClient dc = StargateClient.builder()
                .disableCQL().build()
                .apiDocument().namespace("n").collection("c")
                .document("??a=&invalid??");

        Assertions.assertAll("Required parameters", () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.exist();
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.delete();
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.upsert("X");
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.update("X");
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.find(String.class);
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.findSubDocument("a", String.class);
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.replaceSubDocument("a", String.class);
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.updateSubDocument("a", String.class);
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.deleteSubDocument("a");
        }));

        Assertions.assertThrows(InvocationTargetException.class, () -> {
            Method method = DocumentClient.class.getDeclaredMethod("marshallDocumentId", String.class);
            method.setAccessible(true);
            method.invoke(dc, "invalid_body");
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Method method = DocumentClient.class.getDeclaredMethod("marshallDocument", String.class, Class.class);
            method.setAccessible(true);
            method.invoke(dc, "invalid_body");
        });
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Validation OK");
    }
    
    
    /* KEYSPACE <=> NAMESPACE
     * manipulations will be done at devops API only 
     
    @Test
    @Order(2)
    @DisplayName("Create and delete namespace with replicas")
    public void should_create_tmp_namespace()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#02 Working with Namespaces" + ANSI_RESET);
        if (clientApiDoc.namespace("tmp_namespace").exist()) {
            clientApiDoc.namespace("tmp_namespace").delete();
            int wait = 0;
            while (wait++ < 5 && clientApiDoc.namespace("tmp_namespace").exist()) {
                Thread.sleep(1000);
                System.out.println("+ ");
            }
        }
        clientApiDoc.namespace("tmp_namespace").createSimple(1);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
        int wait = 0;
        while (wait++ < 5 && !clientApiDoc.namespace("tmp_namespace").exist()) {
            Thread.sleep(1000);
        }
        clientApiDoc.namespace("tmp_namespace").delete();
        wait = 0;
        while (wait++ < 5 && clientApiDoc.namespace("tmp_namespace").exist()) {
            Thread.sleep(1000);
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespace craeted");
    }
    
    @Test
    @Order(3)
    @DisplayName("Create and delete namespace with datacenter")
    public void should_create_tmp_namespace2() throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#03 Working with Namespaces 2" + ANSI_RESET);
        // TMP KEYSPACE
        if (clientApiDoc.namespace("tmp_namespace2").exist()) {
            clientApiDoc.namespace("tmp_namespace2").delete();
            int wait = 0;
            while (wait++ < 5 && clientApiDoc.namespace("tmp_namespace2").exist()) {
                Thread.sleep(1000);
            }
        }
        clientApiDoc.namespace("tmp_namespace2").create(new DataCenter(cloudRegion.get(), 1));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Creation request sent");
        int wait = 0;
        while (wait++ < 5 && !clientApiDoc.namespace("tmp_namespace2").exist()) {
            Thread.sleep(1000);
            System.out.println("+ ");
        }

        clientApiDoc.namespace("tmp_namespace2").delete();
        wait = 0;
        while (wait++ < 5 && clientApiDoc.namespace("tmp_namespace2").exist()) {
            Thread.sleep(1000);
            System.out.println("+ ");
        }
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespaces created");
    }
   
    @Test
    @Order(4)
    @DisplayName("Create working namespace and check list")
    public void should_create_working_namespace() throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#04 Create working namespaces" + ANSI_RESET);
        
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
        Assertions.assertFalse(clientApiDoc.namespace("invalid").exist());
        // When
        Set<String> namespaces = clientApiDoc.namespaceNames().collect(Collectors.toSet());
        // Then
        Assertions.assertTrue(namespaces.contains(WORKING_NAMESPACE));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Namespaces created");
    }
    
     @Test
    @Order(5)
    @DisplayName("Create working namespace and check list")
    public void should_fail_on_invalid_namespace_params() {
        System.out.println(ANSI_YELLOW + "\n#05 Fail on invalid namespaces" + ANSI_RESET);
        NamespaceClient dc = StargateClient.builder().disableCQL().build().apiDocument().namespace("???df.??");
        Assertions.assertAll("Required parameters", () -> Assertions.assertThrows(RuntimeException.class, () -> {
            StargateClient.builder().disableCQL().build().apiDocument().namespace("?AA???").collectionNames();
        }));

        Assertions.assertAll("Required parameters", () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.exist();
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.collectionNames();
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.delete();
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.find();
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.createSimple(1);
        }), () -> Assertions.assertThrows(RuntimeException.class, () -> {
            dc.create(new DataCenter(client.getDatabaseRegion().get(), 1));
        }));
        
        Assertions.assertThrows(InvocationTargetException.class, () -> {
            Method method = NamespaceClient.class.getDeclaredMethod("marshallApiResponseNamespace", String.class);
            method.setAccessible(true);
            method.invoke(dc, (String)null);
        });
        
        Assertions.assertThrows(InvocationTargetException.class, () -> {
            Method method = NamespaceClient.class.getDeclaredMethod("marshallApiResponseCollections", String.class);
            method.setAccessible(true);
            method.invoke(dc,  (String)null);
        });
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Validation OK");
    }
    
    */
}
