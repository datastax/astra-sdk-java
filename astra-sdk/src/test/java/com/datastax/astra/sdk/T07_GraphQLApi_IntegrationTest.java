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
