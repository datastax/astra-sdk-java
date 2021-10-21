package com.datastax.stargate.sdk.doc.test;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.Document;
import com.datastax.stargate.sdk.doc.DocumentClient;
import com.datastax.stargate.sdk.doc.NamespaceClient;
import com.datastax.stargate.sdk.doc.domain.DocumentFunction;
import com.datastax.stargate.sdk.doc.domain.DocumentResultPage;
import com.datastax.stargate.sdk.doc.domain.SearchDocumentQuery;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This class test the document api for namespaces
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiDocumentDocumentTest implements ApiDocumentTest {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocumentDocumentTest.class);
    
    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    /** Tested Store. */
    protected static NamespaceClient nsClient;
    
    /** Tested Store. */
    protected static CollectionClient personClient;
    
    // -----------------------------------------
    //           Operation on Namespaces
    // -----------------------------------------
    
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
    
    @Test
    @Order(2)
    @DisplayName("02-Count number of items in a collections")
    public void b_testCount() {
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        int count = personClient.count();
        // Then
        Assertions.assertTrue(count > 0);
    }
    
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
        Assertions.assertEquals(75015, loulou.get().address.zipCode);
        System.out.println( "[OK]"  + " - Document updated");
    }
    
    @Test
    @Order(5)
    @DisplayName("05-Delete document")
    public void e_should_delete_document() throws InterruptedException {
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
      
        Assertions.assertThrows(RuntimeException.class, () -> {
            personClient.document(uid).delete();
        });
        System.out.println( "[OK]"  + " - Document deleted");
    }

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
        Assertions.assertEquals(75000, loulou.get().address.zipCode);
        System.out.println( "[OK]"  + " - Document updated");
    }

    @Test
    @Order(7)
    @DisplayName("07-Find All Person")
    public void g_should_find_all_PersonAstra() {
        LOGGER.info("should_find_all_PersonAstra");
        // Given
        Assertions.assertTrue(personClient.exist());
        // When
        DocumentResultPage<Person> results = personClient.findFirstPage(Person.class);
        // Then
        Assertions.assertNotNull(results);
        for (Document<Person> p : results.getResults()) {
            Assertions.assertNotNull(p);
        }
        System.out.println( "[OK]"  + " - Document list found");
    }

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
        SearchDocumentQuery query = SearchDocumentQuery
                .builder()
                .where("age")
                .isGreaterOrEqualsThan(21).build();

        // Execute q query
        DocumentResultPage<Person> results = personClient.findPage(query, Person.class);
        Assertions.assertNotNull(results);
        Assertions.assertTrue(results.getResults().size() > 0);
        
        for (Document<Person> PersonAstra : results.getResults()) {
            Assertions.assertNotNull(PersonAstra);
        }
        System.out.println( "[OK]"  + " - Document list found");
    }
    
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
        Assertions.assertEquals(75000, oa.get().zipCode);

        // When
        Optional<Integer> oz = p1.findSubDocument("address/zipCode", Integer.class);
        Assertions.assertTrue(oz.isPresent());
        Assertions.assertEquals(75000, oz.get());
        System.out.println( "[OK]"  + " - Sub document retrieved");
    }

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
        Address updated = (Address) p1.findSubDocument("address", Address.class).get();
        Assertions.assertEquals(8000, updated.zipCode);
        Assertions.assertEquals("city2", updated.city);
        System.out.println( "[OK]"  + " - Sub document updated");
    }

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
    
    @Test
    @Order(12)
    @DisplayName("12-Invalid parameters")
    public void l_testInvalidDoc() {
        LOGGER.info("testInvalidDoc");
        DocumentClient dc = StargateClient.builder().withoutCqlSession().build()
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
        System.out.println( "[OK]"  + " - Validation OK");
    }

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
        
        SearchDocumentQuery query = SearchDocumentQuery.builder()
                    .where("age")
                    .isGreaterOrEqualsThan(30)
                    .and("lastname").isEqualsTo("PersonAstra2")
                    .withPageSize(2)
                    .build();
        
        // Get ALL
        personClient.findAll(query, Person.class)
                    .forEach(p -> System.out.println(p.getDocument().getFirstname()));
        
        // Get ony the first 2
        DocumentResultPage<Person> currentPage = personClient.findPage(query,  Person.class);
        if (currentPage.getPageState().isPresent()) {
            query.setPageState(currentPage.getPageState().get());
        }
        
        Assertions.assertNotNull(personClient.findPage(query, Person.class));
    }
    
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
    
    // ============================
    //           Custom Beans
    // ============================
    
    /**
     * POJO for tests
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    @JsonIgnoreProperties
    public static class Person implements Serializable {
        
        /** Serial. */
        private static final long serialVersionUID = 5637323589269092358L;

        /** field. */
        private String firstname;
        
        /** field. */
        private String lastname;
        
        /** field. */
        private int age;
        
        /** field. */
        private Address address;
        
        /**
         * Default constructor.
         */
        public Person() {}
        
        /**
         * Full parameter constructor.
         * @param fn
         *      first name
         * @param ln
         *      last name
         */
        public Person(String fn, String ln) {
            this.firstname = fn;
            this.lastname =ln;
        }
        
        /**
         * Full parameter constructor.
         * @param fn
         *      first name
         * @param ln
         *      last name
         * @param age
         *      age
         * @param ad
         *      address
         */
        public Person(String fn, String ln, int age, Address ad) {
            this.firstname = fn;
            this.lastname =ln;
            this.age = age;
            this.address = ad;
        }
        
        /**
         * Getter accessor for attribute 'firstname'.
         *
         * @return
         *       current value of 'firstname'
         */
        public String getFirstname() {
            return firstname;
        }
        
        /**
         * Setter accessor for attribute 'firstname'.
         * @param firstname
         * 		new value for 'firstname '
         */
        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }
        
        /**
         * Getter accessor for attribute 'lastname'.
         *
         * @return
         *       current value of 'lastname'
         */
        public String getLastname() {
            return lastname;
        }
        
        /**
         * Setter accessor for attribute 'lastname'.
         * @param lastname
         * 		new value for 'lastname '
         */
        public void setLastname(String lastname) {
            this.lastname = lastname;
        }
        
        /**
         * Getter accessor for attribute 'age'.
         *
         * @return
         *       current value of 'age'
         */
        public int getAge() {
            return age;
        }
        
        /**
         * Setter accessor for attribute 'age'.
         * @param age
         * 		new value for 'age '
         */
        public void setAge(int age) {
            this.age = age;
        }
        
        /**
         * Getter accessor for attribute 'address'.
         *
         * @return
         *       current value of 'address'
         */
        public Address getAddress() {
            return address;
        }
        
        /**
         * Setter accessor for attribute 'address'.
         * @param address
         * 		new value for 'address '
         */
        public void setAddress(Address address) {
            this.address = address;
        }
        
    }
    
    /**
     * POJO for tests
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class Address implements Serializable {
        
        /** Serial. */
        private static final long serialVersionUID = 5496004686973233873L;

        /** field. */
        private String city;
        
        /** field. */
        private int zipCode;
        
        /**
         * Default constructor
         */
        public Address() {}
        
        /**
         * Full constructor.
         * @param city
         *      city
         * @param zip
         *      zipcode
         */
        public Address(String city, int zip) {
            this.city = city;
            this.zipCode = zip;
        }

        /**
         * Getter accessor for attribute 'city'.
         *
         * @return
         *       current value of 'city'
         */
        public String getCity() {
            return city;
        }

        /**
         * Setter accessor for attribute 'city'.
         * @param city
         * 		new value for 'city '
         */
        public void setCity(String city) {
            this.city = city;
        }

        /**
         * Getter accessor for attribute 'zipCode'.
         *
         * @return
         *       current value of 'zipCode'
         */
        public int getZipCode() {
            return zipCode;
        }

        /**
         * Setter accessor for attribute 'zipCode'.
         * @param zipCode
         * 		new value for 'zipCode '
         */
        public void setZipCode(int zipCode) {
            this.zipCode = zipCode;
        }
        
    }

}
