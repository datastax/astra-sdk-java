package com.datastax.astra.sdk.stargate;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.astra.sdk.AbstractAstraIntegrationTest;
import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.stargate.domain.PersonEntity;
import com.datastax.stargate.sdk.doc.StargateDocumentRepository;

@TestMethodOrder(OrderAnnotation.class)
public class DocumentRepositoryIntegrationTest extends AbstractAstraIntegrationTest {

    /** TEST CONSTANTS. */
    private static final String TEST_DBNAME          = "sdk_test_api_stargate";
    private static final String WORKING_NAMESPACE    = "ns1";
    
    /**
     * 
     */
    private static StargateDocumentRepository<PersonEntity> personRepository;
    
    @BeforeAll
    public static void config() {
        printYellow("=======================================");
        printYellow("=     Document Api IntegrationTest    =");
        printYellow("=======================================");
        String dbId = createDbAndKeyspaceIfNotExist(TEST_DBNAME, WORKING_NAMESPACE);
        client.cqlSession().close();
        
        // Connect the client to the new created DB
        client = AstraClient.builder()
                  .appToken(client.getToken().get())
                  .clientId(client.getClientId().get())
                  .clientSecret(client.getClientSecret().get())
                  .keyspace(WORKING_NAMESPACE)
                  .databaseId(dbId)
                  .cloudProviderRegion("us-east-1")
                  .build();
        
        personRepository = new StargateDocumentRepository<PersonEntity>(
                client.getStargateClient(), WORKING_NAMESPACE);
        printOK("Connection established to the DB");
    }

    @Test
    @Order(1)
    public void save() {
        personRepository.create(new PersonEntity("cedrick", "lunven"));
        
    }
   

}
