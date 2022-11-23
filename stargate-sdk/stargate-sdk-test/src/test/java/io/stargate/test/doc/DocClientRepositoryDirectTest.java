package io.stargate.test.doc;

import io.stargate.sdk.doc.CollectionClient;
import io.stargate.sdk.doc.NamespaceClient;
import io.stargate.sdk.doc.StargateDocumentApiClient;
import io.stargate.sdk.doc.StargateDocumentRepository;
import io.stargate.sdk.test.doc.AbstractRepositoryTest;
import io.stargate.sdk.test.doc.domain.Person;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class DocClientRepositoryDirectTest extends AbstractRepositoryTest {

    /**
     * Init
     */
    @BeforeAll
    public static void init() {

        StargateDocumentApiClient apiDocumentClient = new StargateDocumentApiClient();

        NamespaceClient nsClient = apiDocumentClient.namespace(TEST_NAMESPACE);
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }

        CollectionClient personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }

        personRepository = new StargateDocumentRepository<Person>(personClient, Person.class);
    }

}
