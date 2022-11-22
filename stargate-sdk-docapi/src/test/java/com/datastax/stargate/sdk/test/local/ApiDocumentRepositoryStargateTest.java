package com.datastax.stargate.sdk.test.local;

import com.datastax.stargate.sdk.doc.ApiDocumentClient;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.NamespaceClient;
import com.datastax.stargate.sdk.doc.StargateDocumentRepository;
import com.datastax.stargate.sdk.test.AbstractDocumentTest;
import com.datastax.stargate.sdk.test.AbstractRepositoryTest;
import org.junit.jupiter.api.BeforeAll;

/**
 * Execute some unit tests agains collections.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentRepositoryStargateTest extends AbstractRepositoryTest {

    /**
     * Init
     */
    @BeforeAll
    public static void init() {

        ApiDocumentClient apiDocumentClient = new ApiDocumentClient();

        NamespaceClient nsClient = apiDocumentClient.namespace(TEST_NAMESPACE);
        if (!nsClient.exist()) {
            nsClient.createSimple(1);
        }

        CollectionClient personClient = nsClient.collection(TEST_COLLECTION_PERSON);
        if (!personClient.exist()) {
            personClient.create();
        }

        personRepository = new StargateDocumentRepository<>(personClient, AbstractDocumentTest.Person.class);
    }

}
