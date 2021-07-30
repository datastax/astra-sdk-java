/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.astra.sdk;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestMethodOrder;

import com.datastax.stargate.sdk.graphql.ApiGraphQLClient;

/**
 * DATASET
 * @author Cedrick LUNVEN (@clunven)
 */
@TestMethodOrder(OrderAnnotation.class)
public class T05_StargateGraphQLApiIntegrationTest extends AbstractAstraIntegrationTest {
    
    private static final String TEST_DBNAME      = "sdk_test_api_stargate";
    private static final String WORKING_KEYSPACE = "ks2";
    
    public static ApiGraphQLClient clientGraphQL;
    
    @BeforeAll
    public static void config() {
        printYellow("=======================================");
        printYellow("=   GraphQL Api IntegrationTest       =");
        printYellow("=======================================");
        String dbId = createDbAndKeyspaceIfNotExist(TEST_DBNAME, WORKING_KEYSPACE);
        client.cqlSession().close();
        
        // Connect the client to the new created DB
        client = AstraClient.builder()
                  .appToken(client.getToken().get())
                  .clientId(client.getClientId().get())
                  .clientSecret(client.getClientSecret().get())
                  .keyspace(WORKING_KEYSPACE)
                  .databaseId(dbId)
                  .cloudProviderRegion("us-east-1")
                  .build();
        clientGraphQL = client.apiStargateGraphQL();
        printOK("Connection established to the DB");
    }
    
    
   
}
