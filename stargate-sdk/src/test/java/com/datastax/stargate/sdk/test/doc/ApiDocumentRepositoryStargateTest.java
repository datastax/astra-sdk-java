package com.datastax.stargate.sdk.test.doc;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.NamespaceClient;
import com.datastax.stargate.sdk.doc.StargateDocumentRepository;
import com.datastax.stargate.sdk.doc.test.ApiDocumentRepositoryTest;
import com.datastax.stargate.sdk.doc.test.ApiDocumentDocumentTest.Person;
import com.datastax.stargate.sdk.test.ApiStargateTestFactory;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentRepositoryStargateTest extends ApiDocumentRepositoryTest {
    
    protected static StargateClient stargateClient;
    
    /**
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateClient = ApiStargateTestFactory.createStargateClient();
        
        // We need the namespace
        NamespaceClient nsClient = stargateClient.apiDocument().namespace(TEST_NAMESPACE);
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }
        
        // Create empty collection if needed
        CollectionClient personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }
        // Initializing a repository for a bean
        personRepository = 
                new StargateDocumentRepository<Person>(personClient, Person.class);
    }
    
    /**
     * Close connections when ending
     */
    @AfterClass
    public static void closing() {
        if (stargateClient != null) {
            stargateClient.close();
        }
    }

}
