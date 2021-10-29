package com.datastax.stargate.sdk.gql.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.gql.CqlSchemaClient;

/**
 * Abstract class to work with GraphQL CQLFirst.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class ApiGraphQLCqlSchemaTest implements ApiGraphQLTest {

    /** Tested Store. */
    protected static StargateClient stargateClient;
    
    @Test
    @Order(1)
    @DisplayName("01-should_list_default_keyspaces")
    public void a_should_list_keyspaces() throws InterruptedException {
        // Given
        CqlSchemaClient cqlSchemaClient = stargateClient.apiGraphQL().cqlSchema();
        // When
        Assertions.assertNotNull(cqlSchemaClient.keyspaces());
    }
    
}
