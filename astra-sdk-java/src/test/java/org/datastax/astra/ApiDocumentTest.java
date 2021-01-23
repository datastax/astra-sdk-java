package org.datastax.astra;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.datastax.astra.doc.AstraDocument;
import org.datastax.astra.doc.CollectionClient;
import org.datastax.astra.doc.ResultListPage;
import org.datastax.astra.dto.Person;
import org.datastax.astra.dto.Person.Address;
import org.datastax.astra.schemas.DataCenter;
import org.datastax.astra.schemas.Namespace;
import org.datastax.astra.schemas.QueryDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Test operations for the Document API operation
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentTest extends ApiSupportTest {
    
    public static final String WORKING_NAMESPACE = "astra_sdk_namespace_test";
    public static final String COLLECTION_PERSON = "person";
    public static final DataCenter ASTRA_DC      = new DataCenter("dc-1", 1);
    
    @BeforeAll
    public static void should_init_reusable_api_client() {
        initApiDevopsClient();
        initApiDocumentApiClient();
        
        // Create the namespace if not present
        if (!apiDocClient.namespace(WORKING_NAMESPACE).exist()) {
            apiDevopsClient.createKeyspace(dbId, WORKING_NAMESPACE);
            System.out.println("Creating Namespace " + WORKING_NAMESPACE);
            waitForSeconds(5);
        }
        
        // Create working collection is not present
        if (!apiDocClient.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).exist()) {
            apiDocClient.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON).create();
            waitForSeconds(5);
        }
        
        Assertions.assertTrue(apiDocClient.namespace(WORKING_NAMESPACE).exist());
        
    }
    
    @Test
    public void builderParams_should_not_be_empty() {
        assertAll("Required parameters",
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseId(null); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseId(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseRegion(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().astraDatabaseRegion(null); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().username(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().username(null); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().password(""); }),
                () -> assertThrows(IllegalArgumentException.class, () -> { AstraClient.builder().password(null); })
        );
    }
    
    @Test
    public void should_connect_to_astra_with_builder() {
        Assertions.assertTrue(AstraClient.builder()
                .astraDatabaseId(dbId)
                .astraDatabaseRegion(dbRegion)
                .username(dbUser)
                .password(dbPasswd)
                .build().apiDocument().testConnection());
    }
    
    @Test
    @Disabled("Not reproductible on CI/CD but works")
    public void should_connect_to_astra_with_envvar() {
        assertTrue(System.getenv().containsKey("ASTRA_DB_ID"));
        assertTrue(System.getenv().containsKey("ASTRA_DB_REGION"));
        assertTrue(System.getenv().containsKey("ASTRA_DB_USERNAME"));
        assertTrue(System.getenv().containsKey("ASTRA_DB_PASSWORD"));
        assertTrue(AstraClient.builder().build().apiDocument().testConnection());
    }
    
    // -- Working with namespace --
    
    @Test
    @Disabled("An Astra document Api does not have enough permissions, "
            + "to create/delete namespaces you should use devops API.")
    public void should_create_namespace() {
        // Given
        if (apiDocClient.namespace("namespace2").exist() ) {
            apiDocClient.namespace("namespace2").delete();
            Assertions.assertFalse(apiDocClient.namespace("namespace2").exist());
        }
        // When
        apiDocClient.namespace("namespace2").create(ASTRA_DC);
        Assertions.assertTrue(apiDocClient.namespace("namespace2").exist());
    }
    
    @Test
    @Disabled("An Astra document Api does not have enough permissions, "
            + "to create/delete namespaces you should use devops API.")
    public void should_delete_namespace() {
        // Given
        if (!apiDocClient.namespace("namespace2").exist() ) {
            apiDocClient.namespace("namespace2").create(ASTRA_DC);
        }
        Assertions.assertTrue(apiDocClient.namespace("namespace2").exist());
        // When
        apiDocClient.namespace("namespace2").delete();
        // Then
        Assertions.assertFalse(apiDocClient.namespace("namespace2").exist());
    }

    @Test
    public void working_namespace_should_exist() {
        // When
        Set<String> namespaces = apiDocClient.namespaceNames().collect(Collectors.toSet());
        // Then
        Assertions.assertTrue(namespaces.contains(WORKING_NAMESPACE));
    }
    
    @Test
    public void working_namespace_should_have_dc() {
        // When
        Map<String, Namespace> namespaces = apiDocClient.namespaces().collect(
                Collectors.toMap(Namespace::getName,  Function.identity()));
        // Then
        Assertions.assertTrue(namespaces.containsKey(WORKING_NAMESPACE));
        Assertions.assertFalse(namespaces.get(WORKING_NAMESPACE).getDatacenters().isEmpty());
    }
    
    
    // -- Working with Collections --
    
    @Test
    public void testFindAllCollections() {
        apiDocClient.namespace(WORKING_NAMESPACE)
                  .collectionNames()
                  .forEach(System.out::println);;
    }
    
    @Test
    public void should_exist_create_delete_collection() {
        // Given
        String randomCollection = UUID.randomUUID().toString().replaceAll("-", "");
        Assertions.assertFalse(apiDocClient.namespace(WORKING_NAMESPACE).collection(randomCollection).exist());
        // When
        apiDocClient.namespace(WORKING_NAMESPACE).collection(randomCollection).create();
        waitForSeconds(5);
        // Then
        Assertions.assertTrue(apiDocClient.namespace(WORKING_NAMESPACE).collection(randomCollection).exist());
        // When
        apiDocClient.namespace(WORKING_NAMESPACE).collection(randomCollection).delete();
        // Then
        Assertions.assertFalse(apiDocClient.namespace(WORKING_NAMESPACE).collection(randomCollection).exist());
    }
    
    
    // -- Working with Document --
    
    @Test
    public void should_create_document_and_generate_id() {
        // Given
        CollectionClient collectionPerson = apiDocClient.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        String docId = collectionPerson.save(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertNotNull(docId);
        Assertions.assertTrue(collectionPerson.document(docId).exist());
    }
    
    @Test
    public void should_create_document_with_provided_id() {
        // Given
        CollectionClient collectionPerson = apiDocClient.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("myId").save(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        // Then
        Assertions.assertTrue(collectionPerson.document("myId").exist());
    }
    
    @Test
    public void should_update_existing_document() {
        // Given
        CollectionClient collectionPerson = apiDocClient.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        collectionPerson.document("123").save(new Person("loulou", "looulou", 20, new Address("Paris", 75000)));
        collectionPerson.document("123").save(new Person("loulou", "looulou", 20, new Address("Paris", 75015)));
        // Then
        Optional<Person> loulou = collectionPerson.document("123").find(Person.class);
        Assertions.assertTrue(loulou.isPresent());
        Assertions.assertEquals(75015, loulou.get().getAddress().getZipCode());
    }
    
    @Test
    public void should_find_all_person() {
        // Given
        CollectionClient collectionPerson = apiDocClient.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        // When
        ResultListPage<Person> results = collectionPerson.findAll(Person.class);
        // Then
        for (AstraDocument<Person> person : results.getResults()) {
            System.out.println(person.getDocumentId() + "=" + person.getDocument().getFirstname());
        }
    }
    
    @Test
    public void should_search_withQuery() {
        // Given
        CollectionClient collectionPerson = apiDocClient.namespace(WORKING_NAMESPACE).collection(COLLECTION_PERSON);
        Assertions.assertTrue(collectionPerson.exist());
        
        collectionPerson.document("person1").save(new Person("person1", "person1", 20, new Address("Paris", 75000)));
        collectionPerson.document("person2").save(new Person("person2", "person2", 30, new Address("Paris", 75000)));
        collectionPerson.document("person3").save(new Person("person3", "person3", 40, new Address("Melun", 75000)));
        Assertions.assertTrue(collectionPerson.document("person1").exist());
        Assertions.assertTrue(collectionPerson.document("person2").exist());
        Assertions.assertTrue(collectionPerson.document("person3").exist());
        
        
        // Create a query
        QueryDocument query = QueryDocument.builder().where("age")
                    .isGreaterOrEqualsThan(21).build();
        
        // Execute q query
        ResultListPage<Person> results = collectionPerson.search(query, Person.class);
        
        for (AstraDocument<Person> person : results.getResults()) {
            System.out.println(person.getDocumentId() + "=" + person.getDocument().getAge());
        }
        
    }

}
