package io.stargate.sdk.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.stargate.sdk.StargateClient;
import io.stargate.sdk.doc.ApiDocument;
import io.stargate.sdk.doc.ApiDocumentClient;
import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.DocumentClient;
import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.doc.QueryDocument;
import io.stargate.sdk.doc.ResultListPage;
import io.stargate.sdk.rest.DataCenter;
import io.stargate.sdk.test.dto.Person;
import io.stargate.sdk.test.dto.Person.Address;


/**
 * Test operations for the Document API operation
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class DocumentApiTest extends AsbtractStargateTestIt {

    public static final String WORKING_NAMESPACE = "astra_sdk_namespace_test";
    public static final String COLLECTION_PERSON = "person";

    public static ApiDocumentClient clientApiDoc;

    @BeforeAll
    public static void initDocumentAPI() {
        AsbtractStargateTestIt.init();
        clientApiDoc = client.apiDocument();
        Assertions.assertNotNull(clientApiDoc);
    }

    @Test
    @Order(1)
    @DisplayName("Parameters Validation (builder)")
    public void builder_should_fail_if_empty_params() {
        System.out.println(ANSI_YELLOW + "\n#02 Parameters Validation (builder) " + ANSI_RESET);
        Assertions.assertAll("Required parameters", () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().cqlContactPoint(null, 0);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().cqlContactPoint("", 0);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().addCqlContactPoint(null, 0);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().addCqlContactPoint("", 0);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().username("");
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().username(null);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().password("");
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().password(null);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().localDc("");
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().localDc(null);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().authenticationUrl("");
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().documentApiUrl("");
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().documentApiUrl(null);
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().restApiUrl("");
        }), () -> Assertions.assertThrows(IllegalArgumentException.class, () -> {
            StargateClient.builder().restApiUrl(null);
        }));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Tests passed.");
    }

    @Test
    @Order(2)
    @DisplayName("Get authentication token")
    public void token_should_not_be_null() {
        System.out.println(ANSI_YELLOW + "\n#03 Authentication Token" + ANSI_RESET);
        String token = client.apiDocument().getToken();
        Assertions.assertNotNull(token);
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Token retrieved." + token);
    }

    @Test
    @Order(3)
    @DisplayName("Create and delete namespace with replicas")
    public void should_create_tmp_namespace()
    throws InterruptedException {
        System.out.println(ANSI_YELLOW + "\n#04 Working with Namespaces" + ANSI_RESET);
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
            System.out.println("+ ");
        }
        clientApiDoc.namespace("tmp_namespace").delete();
        wait = 0;
        while (wait++ < 5 && clientApiDoc.namespace("tmp_namespace").exist()) {
            Thread.sleep(1000);
            System.out.println("+ ");
        }
    }

    @Test
    @Order(4)
    @DisplayName("Create and delete namespace with datacenter")
    public void should_create_tmp_namespace2() throws InterruptedException {
        // TMP KEYSPACE
        if (clientApiDoc.namespace("tmp_namespace2").exist()) {
            clientApiDoc.namespace("tmp_namespace2").delete();
            int wait = 0;
            while (wait++ < 5 && clientApiDoc.namespace("tmp_namespace2").exist()) {
                Thread.sleep(1000);
                System.out.println("+ ");
            }
        }
        clientApiDoc.namespace("tmp_namespace2").create(new DataCenter(localDc, 1));
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
    }

    @Test
    @Order(5)
    @DisplayName("Create working namespace and check list")
    public void should_create_working_namespace() throws InterruptedException {
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
        System.out.println(ANSI_GREEN + "[POST] Create namespace if needed" + ANSI_RESET);
        
        // When
        Set<String> namespaces = clientApiDoc.namespaceNames().collect(Collectors.toSet());
        // Then
        Assertions.assertTrue(namespaces.contains(WORKING_NAMESPACE));
    }
    
    @Test
    @Order(6)
    @DisplayName("Create working namespace and check list")
    public void should_fail_on_invalid_namespace_params() {
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
            dc.create(new DataCenter(localDc, 1));
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
    }
    
    @Test
    @Order(7)
    @DisplayName("Create collection")
    public void should_create_collection() throws InterruptedException {
        // Operations on collections
        System.out.println(ANSI_YELLOW + "\n#05 Working with Collections" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "[POST] Create a collection" + ANSI_RESET);
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
        Assertions.assertTrue(clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).exist());
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection now exist");
    }

    @Test
    @Order(8)
    @DisplayName("Find Collection")
    public void shoudl_find_collection() throws InterruptedException {
        Thread.sleep(1000);
        Assertions.assertTrue(
                clientApiDoc.namespace(WORKING_NAMESPACE).collectionNames().anyMatch(s -> COLLECTION_PERSON.equals(s)));
        System.out.println(ANSI_GREEN + "[OK]" + ANSI_RESET + " - Collection is available in list");
    }
    
    @Test
    @Order(9)
    @DisplayName("Delete collection")
    public void should_delete_collection() throws InterruptedException {
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        Assertions.assertFalse(clientApiDoc.namespace(WORKING_NAMESPACE).collection(randomCollection).exist());
        // When
        clientApiDoc.namespace(WORKING_NAMESPACE).collection(randomCollection).create();
        Thread.sleep(500);
        // Then
        Assertions.assertTrue(clientApiDoc.namespace(WORKING_NAMESPACE).collection(randomCollection).exist());
        // When
        clientApiDoc.namespace(WORKING_NAMESPACE).collection(randomCollection).delete();
        Thread.sleep(500);
        // Then
        Assertions.assertFalse(clientApiDoc.namespace(WORKING_NAMESPACE).collection(randomCollection).exist());
    }

    @Test
    @Order(10)
    @DisplayName("Create document")
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
    
    @Test
    @Order(11)
    @DisplayName("Order document")
    public void should_upsert_document_create() throws InterruptedException {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("myId").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));

        Thread.sleep(500);
        // Then
        Assertions.assertTrue(collectionPerson.document("myId").exist());
    }

    @Test
    @Order(12)
    @DisplayName("Update document")
    public void should_upsert_document_update() throws InterruptedException {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        String uid = UUID.randomUUID().toString();
        Assertions.assertFalse(collectionPerson.document(uid).exist());
        // When
        collectionPerson.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPerson.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75015)));
        // Then
        Optional<Person> loulou = collectionPerson.document(uid).find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        Assertions.assertEquals(75015, loulou.get().getAddress().getZipCode());
    }

    @Test
    @Order(13)
    @DisplayName("Delete document")
    public void should_delete_document() throws InterruptedException {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        String uid = UUID.randomUUID().toString();
        Assertions.assertFalse(collectionPerson.document(uid).exist());
        // When
        collectionPerson.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertTrue(collectionPerson.document(uid).exist());
        collectionPerson.document(uid).delete();
        Thread.sleep(1000);
        Assertions.assertFalse(collectionPerson.document(uid).exist());
        Assertions.assertTrue(collectionPerson.document(uid).find(String.class).isEmpty());
        Assertions.assertThrows(RuntimeException.class, () -> {
            collectionPerson.document(uid).delete();
        });
    }

    @Test
    @Order(14)
    @DisplayName("Update document")
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

    @Test
    @Order(15)
    @DisplayName("Find All person")
    public void should_find_all_person() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        ResultListPage<Person> results = collectionPerson.findAll(Person.class);
        // Then
        Assert.assertNotNull(results);
        for (ApiDocument<Person> person : results.getResults()) {
            Assert.assertNotNull(person);
        }
    }

    @Test
    @Order(16)
    @DisplayName("Search Query")
    public void should_search_withQuery() {
        // Given
        CollectionClient collectionPerson = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());

        collectionPerson.document("person1").upsert(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        collectionPerson.document("person2").upsert(new Person("person2", "person2", 30, new Address("Paris", 75000)));
        collectionPerson.document("person3").upsert(new Person("person3", "person3", 40, new Address("Melun", 75000)));
        Assertions.assertTrue(collectionPerson.document("person1").exist());
        Assertions.assertTrue(collectionPerson.document("person2").exist());
        Assertions.assertTrue(collectionPerson.document("person3").exist());

        // Create a query
        QueryDocument query = QueryDocument.builder().where("age").isGreaterOrEqualsThan(21).build();

        // Execute q query
        ResultListPage<Person> results = collectionPerson.search(query, Person.class);
        Assert.assertNotNull(results);
        for (ApiDocument<Person> person : results.getResults()) {
            Assert.assertNotNull(person);
        }
    }
    

    @Test
    @Order(17)
    @DisplayName("Invalid parameters")
    public void testInvalidDoc() {
        DocumentClient dc = StargateClient.builder().disableCQL().build().apiDocument().namespace("n").collection("c")
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
       
    }

    @Test
    @Order(18)
    @DisplayName("Find sub doc")
    public void should_find_subdocument() {
        // Given, Collection exist, Document Exist
        CollectionClient cc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
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
        Optional<Address> oa = p1.findSubDocument("address", Address.class);
        Assertions.assertTrue(oa.isPresent());
        Assertions.assertEquals(75000, oa.get().getZipCode());

        // When
        Optional<Integer> oz = p1.findSubDocument("address/zipCode", Integer.class);
        Assertions.assertTrue(oz.isPresent());
        Assertions.assertEquals(75000, oz.get());
    }

    @Test
    @Order(19)
    @DisplayName("Update sub doc")
    public void should_update_subdocument() {

        // Given
        CollectionClient cc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
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

    @Test
    @Order(20)
    @DisplayName("Delete sub doc")
    public void should_delete_subdocument() {
        // Given
        CollectionClient cc = clientApiDoc.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
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
