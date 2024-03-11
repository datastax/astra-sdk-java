package com.datastax.astra.sdk.db;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraSdkTest;
import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.doc.StargateDocumentRepository;
import io.stargate.sdk.test.doc.AbstractRepositoryTest;
import io.stargate.sdk.test.doc.domain.Person;
import org.junit.jupiter.api.BeforeAll;

import static com.datastax.astra.devops.utils.TestUtils.TEST_REGION;
import static com.datastax.astra.devops.utils.TestUtils.setupDatabase;

/**
 * Execute some unit tests against collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentRepositoryAstraTest extends AbstractRepositoryTest  implements AstraSdkTest {

    @BeforeAll
    public static void init() {

        StargateDocumentApiClient stargateDocumentApiClient = AstraClient.builder()
                .withDatabaseRegion(TEST_REGION)
                .withDatabaseId(setupDatabase(TEST_DATABASE_NAME, TEST_NAMESPACE))
                .build().getStargateClient()
                .apiDocument();

        NamespaceClient nsClient = stargateDocumentApiClient.namespace(TEST_NAMESPACE);

        CollectionClient personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }
        
        // Initializing a repository for a bean
        personRepository = new StargateDocumentRepository<>(personClient, Person.class);
    }

}
