package com.datastax.astra.sdk.stargate;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.NamespaceClient;
import com.datastax.stargate.sdk.doc.StargateDocumentRepository;
import com.datastax.stargate.sdk.doc.test.ApiDocumentRepositoryTest;
import com.datastax.stargate.sdk.doc.test.ApiDocumentTest;
import com.datastax.stargate.sdk.doc.test.ApiDocumentDocumentTest.Person;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentRepositoryAstraTest extends ApiDocumentRepositoryTest {
     
    protected static StargateClient stargateClient;
    
    /**
     * Init
     */
    @BeforeAll
    public static void init() { // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);
        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().get())
                .withKeyspace(ApiDocumentTest.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .withoutCqlSession()
                .build();
        stargateClient = client.getStargateClient();
        
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
        personRepository = new StargateDocumentRepository<Person>(personClient, Person.class);
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
