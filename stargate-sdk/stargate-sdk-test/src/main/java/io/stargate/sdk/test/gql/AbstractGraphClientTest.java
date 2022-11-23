package io.stargate.sdk.test.gql;

import com.datastax.stargate.graphql.types.Keyspace;
import io.stargate.sdk.gql.StargateGraphQLApiClient;
import io.stargate.sdk.gql.GraphQLKeyspaceDDLClient;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

/**
 * Abstract class to work with GraphQL CQLFirst.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public abstract class AbstractGraphClientTest implements TestGraphQLClientConstants {

    /** Tested Store. */
    protected static StargateGraphQLApiClient stargateGraphQLApiClient;
    
    /**
     * Test.
     */
    @Test
    @Order(1)
    @DisplayName("01-should_list_default_keyspaces")
    public void listKeyspacesTest() {
        // Given
        GraphQLKeyspaceDDLClient cqlSchemaClient = stargateGraphQLApiClient.keyspaceDDL();
        // When
        Assertions.assertNotNull(cqlSchemaClient.keyspaces());
        cqlSchemaClient.keyspaces().map(Keyspace::getDcs).forEach(System.out::println);
    }

    /**
     * Test.
     */
    @Test
    @Order(2)
    @DisplayName("02-get-keyspace-details")
    public void findKeyspace() {
        // Given
        GraphQLKeyspaceDDLClient cqlSchemaClient = stargateGraphQLApiClient.keyspaceDDL();
        // When
        Assertions.assertNotNull(cqlSchemaClient.keyspaces());
        cqlSchemaClient.keyspaces().map(Keyspace::getDcs).forEach(System.out::println);
    }



}
