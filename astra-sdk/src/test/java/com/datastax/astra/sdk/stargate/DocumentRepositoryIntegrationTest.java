package com.datastax.astra.sdk.stargate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.dto.Address;
import com.datastax.astra.dto.PersonRepo;
import com.datastax.astra.sdk.AbstractAstraIntegrationTest;
import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.doc.ApiDocument;
import com.datastax.stargate.sdk.doc.CollectionClient;
import com.datastax.stargate.sdk.doc.StargateDocumentRepository;

@TestMethodOrder(OrderAnnotation.class)
public class DocumentRepositoryIntegrationTest extends AbstractAstraIntegrationTest {

    /** TEST CONSTANTS. */
    private static final String TEST_DBNAME = "sdk_test_api_stargate";
    private static final String WORKING_NAMESPACE = "ns1";

    // Client Test
    private static StargateDocumentRepository<PersonRepo> personRepository;
    
    @BeforeAll
    public static void config() {
        printYellow("=======================================");
        printYellow("=     Document Api IntegrationTest    =");
        printYellow("=======================================");
        String dbId = createDbAndKeyspaceIfNotExist(TEST_DBNAME, WORKING_NAMESPACE);
        client.cqlSession().close();

        // Connect the client to the new created DB
        client = AstraClient.builder()
                .withToken(client.getToken().get())
                .withClientId(client.getConfig().getClientId())
                .withClientSecret(client.getConfig().getClientSecret())
                .withKeyspace(WORKING_NAMESPACE)
                .withDatabaseId(dbId)
                .withDatabaseRegion("us-east-1")
                .build();
       
        // Delete the collection if exist
        CollectionClient cc = client.apiStargateDocument()
              .namespace(WORKING_NAMESPACE)
              .collection("personrepo");
        if (cc.exist()) {
            cc.delete();
        }
        
        personRepository = new StargateDocumentRepository<PersonRepo>(
               client.apiStargateDocument().namespace(WORKING_NAMESPACE), 
               PersonRepo.class);

        printOK("Connection established to the DB");
    }
    
    private static String tmpDocId;

    @Test
    @Order(1)
    public void insert_and_count() {
        printYellow("Create a document");
        PersonRepo p1 = new PersonRepo("loulou", "loulou", 22, new Address("Paris", 75000));
        tmpDocId = personRepository.insert(p1);
        
        personRepository.findAll().map(ApiDocument::getDocument).map(PersonRepo::getFirstname).forEach(System.out::println);
        
        Assertions.assertEquals(1, personRepository.count());
        Assertions.assertTrue(personRepository.exists(tmpDocId));
        printOK("Document created " + tmpDocId);
    }
    
    // TODO More unit tests
    
    
        

}
