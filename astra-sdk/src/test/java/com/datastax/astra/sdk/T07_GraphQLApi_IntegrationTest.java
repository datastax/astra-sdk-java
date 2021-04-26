package com.datastax.astra.sdk;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * DATASET
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class T07_GraphQLApi_IntegrationTest extends AbstractAstraIntegrationTest {
  
    private static final String WORKING_KEYSPACE = "sdk_test_ks";
    private static final String WORKING_TABLE    = "videos";
    
    @BeforeAll
    public static void config() {
        System.out.println(ANSI_YELLOW + "[T05_GraphQL_IntegrationTest]" + ANSI_RESET);
        initDb("sdk_test_graphqlApi");
        
    }
    
    
   
}
