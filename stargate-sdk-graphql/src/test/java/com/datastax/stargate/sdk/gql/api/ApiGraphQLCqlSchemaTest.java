package com.datastax.stargate.sdk.gql.api;

import com.datastax.stargate.sdk.gql.ApiGraphQLClient;
import com.datastax.stargate.sdk.gql.CqlSchemaClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Abstract class to work with GraphQL CQLFirst.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class ApiGraphQLCqlSchemaTest implements ApiGraphQLTest {

    /** Tested Store. */
    protected static ApiGraphQLClient gqpClient = new ApiGraphQLClient();
    
    /**
     * Test.
     * @throws InterruptedException
     *      exception
     */
    @Test
    @Order(1)
    @DisplayName("01-should_list_default_keyspaces")
    public void a_should_list_keyspaces() throws InterruptedException {
        // Given
        CqlSchemaClient cqlSchemaClient = gqpClient.cqlSchema();
        // When
        Assertions.assertNotNull(cqlSchemaClient.keyspaces());
    }
    
}
