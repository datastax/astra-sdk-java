package com.datastax.astra.sdk.stargate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.dto.Address;
import com.datastax.astra.dto.Person;
import com.datastax.astra.sdk.AbstractAstraIntegrationTest;
import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.doc.StargateDocumentRepository;

@TestMethodOrder(OrderAnnotation.class)
public class DocumentRepositoryIntegrationTest extends AbstractAstraIntegrationTest {

    /** TEST CONSTANTS. */
    private static final String TEST_DBNAME = "sdk_test_api_stargate";
    private static final String WORKING_NAMESPACE = "ns1";

    // Client Test
    private static StargateDocumentRepository<Person> personRepository;
    
    @BeforeAll
    public static void config() {
        printYellow("=======================================");
        printYellow("=     Document Api IntegrationTest    =");
        printYellow("=======================================");
        String dbId = createDbAndKeyspaceIfNotExist(TEST_DBNAME, WORKING_NAMESPACE);
        client.cqlSession().close();

        // Connect the client to the new created DB
        client = AstraClient.builder()
                .appToken(client.getToken().get()).clientId(client.getClientId().get())
                .clientSecret(client.getClientSecret().get())
                .keyspace(WORKING_NAMESPACE)
                .databaseId(dbId)
                .cloudProviderRegion("us-east-1")
                .build();

        personRepository = new StargateDocumentRepository<Person>(
               client.apiStargateDocument().namespace(WORKING_NAMESPACE), 
                Person.class);

        printOK("Connection established to the DB");
    }

    @Test
    @Order(1)
    public void save() {
        printYellow("Create a document");
        Person p1 = new Person("loulou", "loulou", 22, new Address("Paris", 75000));
        String docId = personRepository.insert(p1);
        printOK("Document created " + docId);

    }
        

}
