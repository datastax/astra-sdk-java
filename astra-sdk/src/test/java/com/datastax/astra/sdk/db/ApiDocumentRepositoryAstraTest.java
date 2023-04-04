package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraTestUtils;
import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.doc.StargateDocumentRepository;
import io.stargate.sdk.test.doc.AbstractRepositoryTest;
import io.stargate.sdk.test.doc.TestDocClientConstants;
import io.stargate.sdk.test.doc.domain.Person;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests against collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentRepositoryAstraTest extends AbstractRepositoryTest {

    @BeforeAll
    public static void init() {
        // Default client to create DB if needed
        AstraClient client = AstraClient.builder().build();
        String dbId = AstraTestUtils.createTestDbIfNotExist(client);

        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().orElseThrow(() -> new IllegalStateException("token not found")))
                .withCqlKeyspace(TestDocClientConstants.TEST_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion(AstraTestUtils.TEST_REGION)
                .build();

        StargateDocumentApiClient stargateDocumentApiClient = client.getStargateClient().apiDocument();
        NamespaceClient nsClient = stargateDocumentApiClient.namespace(TEST_NAMESPACE);

        CollectionClient personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }
        
        // Initializing a repository for a bean
        personRepository = new StargateDocumentRepository<>(personClient, Person.class);
    }

}
