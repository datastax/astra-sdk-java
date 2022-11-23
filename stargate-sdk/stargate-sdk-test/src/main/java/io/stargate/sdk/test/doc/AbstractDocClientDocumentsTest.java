package io.stargate.sdk.test.doc;

import io.stargate.sdk.core.Page;
import io.stargate.sdk.doc.domain.DocumentFunction;
import io.stargate.sdk.doc.domain.PageableQuery;
import io.stargate.sdk.doc.domain.Query;
import io.stargate.sdk.doc.*;
import io.stargate.sdk.test.doc.domain.Address;
import io.stargate.sdk.test.doc.domain.Person;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * This class test the document api for namespaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractDocClientDocumentsTest implements TestDocClientConstants {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDocClientDocumentsTest.class);
    
    /** Tested Store. */
    protected static StargateDocumentApiClient stargateDocumentApiClient;

    /** Tested Store. */
    protected static NamespaceClient nsClient;
    
    /** Tested Store. */
    protected static CollectionClient personClient;
    
    // -----------------------------------------
    //           Operation on Namespaces
    // -----------------------------------------
    
    /**
     * Test.
     * 
     * @throws InterruptedException
     *  error
     */
    @Test
    @Order(1)
    @DisplayName("01-Create document in a collection")
    public void a_should_create_newDocument() throws InterruptedException {
        LOGGER.info("should_create_newDocument");
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        String docId = personClient.create(
                new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertNotNull(docId);
        Thread.sleep(2000);
        Assertions.assertTrue(personClient.document(docId).exist());
        System.out.println( "[OK]"  + " - Document created");
    }
    
    /**
     * Test.
     */
    @Test
    @Order(2)
    @DisplayName("02-Count number of items in a collections")
    public void b_testCount() {
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        long count = personClient.count();
        // Then
        Assertions.assertTrue(count > 0);
    }
    
    /**
     * Test.
     * 
     * @throws InterruptedException
     *  error
     */
    @Test
    @Order(3)
    @DisplayName("03-Upsert document")
    public void c_should_upsert_document_create() throws InterruptedException {
        LOGGER.info("should_upsert_document_createh");
        // Given
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        personClient.document("myId").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));

        Thread.sleep(2000);
        // Then
        Assertions.assertTrue(personClient.document("myId").exist());
        System.out.println( "[OK]"  + " - Document created");
    }

    /**
     * Test.
     * 
     * @throws InterruptedException
     *  error
     */
    @Test
    @Order(4)
    @DisplayName("04-Update an existing documenr")
    public void d_should_upsert_document_update() throws InterruptedException {
        LOGGER.info("should_upsert_document_update");
        // Given
        Assertions.assertTrue(personClient.exist());
        String uid = UUID.randomUUID().toString();
        Assertions.assertFalse(personClient.document(uid).exist());
        // When
        personClient.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        personClient.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75015)));
        Thread.sleep(2000);
        // Then
        Optional<Person> loulou = personClient.document(uid).find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        Assertions.assertEquals(75015, loulou.get().getAddress().getZipCode());
        System.out.println( "[OK]"  + " - Document updated");
    }
    
    /**
     * Test.
     * 
     * @throws InterruptedException
     *       exception
     */
    @Test
    @Order(5)
    @DisplayName("05-Delete document")
    public void e_should_delete_document() throws InterruptedException  {
        LOGGER.info("should_delete_document");
        // Given
        String uid = UUID.randomUUID().toString();
        Assertions.assertFalse(personClient.document(uid).exist());
        LOGGER.info("Document does not exist");
        
        // When
        personClient.document(uid).upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertTrue(personClient.document(uid).exist());
        LOGGER.info("Document exist");
        
        personClient.document(uid).delete();
        LOGGER.info("Deleting....");
        Thread.sleep(1000);
        
        Assertions.assertFalse(personClient.document(uid).exist());
        LOGGER.info("Document does not exist");
        
        Assertions.assertFalse(personClient
                .document(uid)
                .find(String.class)
                .isPresent());
      
        Assertions.assertThrows(RuntimeException.class, () -> personClient.document(uid).delete());
        System.out.println( "[OK]"  + " - Document deleted");
    }

    /**
     * Test.
     * 
     * @throws InterruptedException
     *  error
     */
    @Test
    @Order(6)
    @DisplayName("06-Update document")
    public void f_should_update_document() 
    throws InterruptedException {
        LOGGER.info("should_update_document");
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        personClient.document("AAA").upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        personClient.document("AAA").update(new Person("a", "b"));
        Thread.sleep(2000);
        // Then
        Optional<Person> loulou = personClient.document("AAA").find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        // Then sub fields are still there
        Assertions.assertEquals(75000, loulou.get().getAddress().getZipCode());
        System.out.println( "[OK]"  + " - Document updated");
    }

    /**
     * Test.
     */
    @Test
    @Order(7)
    @DisplayName("07-Find all Person")
    public void g_should_find_page_PersonAstra() {
        LOGGER.info("should_find_all_PersonAstra");
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        Stream<Document<Person>> results = personClient.findAll(Person.class);
        // Then
        Assertions.assertNotNull(results);
        results.forEach(Assertions::assertNotNull);
        
        // When
        Stream<Document<String>> resultsRaw = personClient.findAll();
        Assertions.assertNotNull(resultsRaw);
        resultsRaw.forEach(Assertions::assertNotNull);
        
        // When
        Stream<Document<Person>> resultsMapper = personClient.findAll(record -> {
            Person p = new Person();
            p.setAge(10);
            return p;
        });
        Assertions.assertNotNull(resultsMapper);
        resultsMapper.forEach(Assertions::assertNotNull);
    }

    /**
     * Test.
     * 
     * @throws InterruptedException
     *  error
     */
    @Test
    @Order(8)
    @DisplayName("08-Search Document with a filter Query")
    public void h_should_search_withQuery() 
    throws InterruptedException {
        LOGGER.info("should_search_withQuery");
        // Given
        Assertions.assertTrue(personClient.exist());
        personClient.document("PersonAstra1")
                    .upsert(new Person("PersonAstra1", "PersonAstra1", 20, new Address("Paris", 75000)));
        personClient.document("PersonAstra2")
                    .upsert(new Person("PersonAstra2", "PersonAstra2", 30, new Address("Paris", 75000)));
        personClient.document("PersonAstra3")
                    .upsert(new Person("PersonAstra3", "PersonAstra3", 40, new Address("Melun", 75000)));
        Thread.sleep(2000);
        Assertions.assertTrue(personClient.document("PersonAstra1").exist());
        Assertions.assertTrue(personClient.document("PersonAstra2").exist());
        Assertions.assertTrue(personClient.document("PersonAstra3").exist());

        // Create a query
        PageableQuery query = PageableQuery.builder()
                .pageSize(2)
                .where("age")
                .isGreaterOrEqualsThan(21)
                .build();

        // Execute query
        Page<Document<Person>> results = personClient.findPage(query, Person.class);
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.getResults().size() > 0);
        for (Document<Person> PersonAstra : results.getResults()) {
            Assertions.assertNotNull(PersonAstra);
        }
        
        Page<Document<String>> results2 = personClient.findPage(query);
        Assertions.assertNotNull(results2);
        Assertions.assertTrue(results2.getResults().size() > 0);
        for (Document<String> ss : results2.getResults()) {
            Assertions.assertNotNull(ss);
        }
        
        LOGGER.info("[OK]"  + " - Document list found");
    }
    
    /**
     * Test.
     */
    @Test
    @Order(9)
    @DisplayName("09-Find a sub document ")
    public void i_should_find_subdocument() {
        LOGGER.info("should_find_subdocument");
        // Given, Collection exist, Document Exist
        Assertions.assertTrue(personClient.exist());
        System.out.println(" + Collection exist");

        // Create doc
        DocumentClient p1 = personClient.document("PersonAstra1");
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
        System.out.println( "[OK]"  + " - Sub document retrieved");
    }

    /**
     * Test.
     */
    @Test
    @Order(10)
    @DisplayName("10-Update sub doc")
    public void j_should_update_subdocument() {
        LOGGER.info("should_update_subdocument");
        // Given
        Assertions.assertTrue(personClient.exist());

        DocumentClient p1 = personClient.document("PersonAstra1");
        p1.upsert(new Person("PersonAstra1", "PersonAstra1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());

        // When
        p1.replaceSubDocument("address", new Address("city2", 8000));
        // Then
        Address updated = p1.findSubDocument("address", Address.class).get();
        Assertions.assertEquals(8000, updated.getZipCode());
        Assertions.assertEquals("city2", updated.getCity());
        System.out.println( "[OK]"  + " - Sub document updated");
    }

    /**
     * Test.
     */
    @Test
    @Order(11)
    @DisplayName("11-Delete sub doc")
    public void k_should_delete_subdocument() {
        LOGGER.info("should_delete_subdocument");
        // Given
        Assertions.assertTrue(personClient.exist());
        DocumentClient p1 = personClient.document("PersonAstra1");
        p1.upsert(new Person("PersonAstra1", "PersonAstra1", 20, new Address("Paris", 75000)));
        Assertions.assertTrue(p1.find(Person.class).isPresent());
        Assertions.assertTrue(p1.findSubDocument("address", Address.class).isPresent());
        // When
        p1.deleteSubDocument("address");
        // Then
        Assertions.assertFalse(p1.findSubDocument("address", Address.class).isPresent());
        System.out.println( "[OK]"  + " - Sub document deleted");
    }
    
    /**
     * Test.
     */
    @Test
    @Order(12)
    @DisplayName("12-Invalid parameters")
    public void l_testInvalidDoc() {
        LOGGER.info("testInvalidDoc");
        DocumentClient dc = stargateDocumentApiClient.namespace("n").collection("c")
                .document("??a=&invalid??");
        
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
        System.out.println( "[OK]"  + " - Validation OK");
    }

    /**
     * Test.
     */
    @Test
    @Order(13)
    @DisplayName("13-Search Query")
    public void m_should_test_search() {
        // ADD 20 record in a collection 
        for(int i =0; i<20; i++) {
            Address a = new Address("Paris", 75000);
            Person p1 = new Person("PersonAstra" + i, "PersonAstra1", 5*i, a);
            personClient.create(p1);          
        }
        
        Query query = Query.builder()
                    .where("age")
                    .isGreaterOrEqualsThan(30)
                    .and("lastname").isEqualsTo("PersonAstra2")
                    .build();
        
        // Get ALL
        personClient.findAll(query, Person.class)
                    .forEach(p -> System.out.println(p.getDocument().getFirstname()));
        
        PageableQuery pQuery = PageableQuery.builder()
                .where("age")
                .isGreaterOrEqualsThan(30)
                .pageSize(2)
                .and("lastname").isEqualsTo("PersonAstra2")
                .build();
                
        // Get ony the first 2
        Page<Document<Person>> currentPage = personClient.findPage(pQuery,  Person.class);
        if (currentPage.getPageState().isPresent()) {
            pQuery.setPageState(currentPage.getPageState().get());
        }
        
        Assertions.assertNotNull(personClient.findPage(pQuery, Person.class));
    }
    
    /**
     * Test.
     */
    @Test
    @Order(14)
    @DisplayName("14-Execute function")
    public void n_execute_function() {
        String docId = personClient.create(""
                + "{\n"
                + "   \"firstName\":\"cedrick\" ,\n"
                + "   \"lastName\":\"lunven\",\n"
                + "   \"age\": 40,\n"
                + "   \"color\": [\"blue\"]\n"
                + "}");
        String output = personClient.document(docId).executefunction("/color",
                DocumentFunction.PUSH.getOperation(), "red");
        Assertions.assertTrue(output.contains("red"));
        String jsonOutput = personClient.document(docId).find(String.class).get();
        Assertions.assertTrue(jsonOutput.contains("red"));
    }
    
    /**
     * Test.
     * 
     * @throws InterruptedException
     *  error
     */
    @Test
    @Order(15)
    @DisplayName("15-Find a doc document")
    public void o_should_find_document() throws InterruptedException {
        LOGGER.info("o_should_find_document");
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        personClient.document("doc2Retrieve")
                    .upsert(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        Thread.sleep(2000);
        // Then
        Assertions.assertTrue(personClient.document("doc2Retrieve").exist());
        Assertions.assertTrue(personClient.document("doc2Retrieve").find().isPresent());
        Assertions.assertTrue(personClient.document("doc2Retrieve").find(Person.class).isPresent());
        Assertions.assertTrue(personClient.document("doc2Retrieve").find(
                record -> new Person()).isPresent());
        LOGGER.info("Documents Founds.");
    }



}
