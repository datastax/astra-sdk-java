package com.datastax.stargate.sdk.test.graphql;

import org.junit.AfterClass;
import org.junit.jupiter.api.BeforeAll;

import com.datastax.stargate.sdk.gql.test.ApiGraphQLCqlSchemaTest;
import com.datastax.stargate.sdk.test.ApiStargateTestFactory;

/**
 * Execute tests locally agains GraphQL endpoint.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGraphQLCqlSchemaStargateTest extends ApiGraphQLCqlSchemaTest { 

    /*
     * Init
     */
    @BeforeAll
    public static void init() {
        stargateClient = ApiStargateTestFactory.createStargateClient();
     }
     
     /**
      * Close connections when ending
      */
     @AfterClass
     public static void closing() {
         if (stargateClient != null) {
             stargateClient.close();
         }
     }

}
