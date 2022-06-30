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

package com.datastax.stargate.sdk.gql;
import java.util.HashMap;
import java.util.Map;

import com.datastax.stargate.client.KeyspacesGraphQLQuery;
import com.datastax.stargate.client.KeyspacesProjectionRoot;
import com.netflix.graphql.dgs.client.codegen.GraphQLQueryRequest;

import io.aexp.nodes.graphql.GraphQLRequestEntity;
import io.aexp.nodes.graphql.GraphQLResponseEntity;
import io.aexp.nodes.graphql.GraphQLTemplate;

/**
 * Sample to invoke DGS Client code for CQL First GraphQL
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGraphQLClient {

    private static final String URL = "https://"
            + "3ed83de7-d97f-4fb6-bf9f-82e9f7eafa23-"
            + "eu-west-1"
            + ".apps.astra.datastax.com/api/"
            + "graphql-schema";
    private static final String TOKEN = 
            "AstraCS:gdZaqzmFZszaBTOlLgeecuPs:edd25600df1c01506f5388340f138f277cece2c93cb70f4b5fa386490daa5d44";
    
    public static <T> GraphQLResponseEntity<T> callGraphQLService(String query, Class<T> clazz)
    throws Exception {
              
        GraphQLTemplate graphQLTemplate = new GraphQLTemplate();
        
        Map<String, String > headers = new HashMap<>();
        headers.put("x-cassandra-token", TOKEN); 
        
        GraphQLRequestEntity requestEntity = GraphQLRequestEntity.Builder()
              .url(URL)
              .request(query)
              .headers(headers)
              .build();
        return graphQLTemplate.query(requestEntity, clazz);
    }
    
    /**
     * Main.
     */
    public static void main(String[] args)  
    throws Exception {
        
        // Building a request (with DSG codegen)
        GraphQLQueryRequest graphQLQueryRequest =
                new GraphQLQueryRequest(
                        new KeyspacesGraphQLQuery.Builder().build(),
                        new KeyspacesProjectionRoot().name());
        
        // Executing query (with American Express Nodes)
        GraphQLResponseEntity<KeyspaceList> res = callGraphQLService(
                graphQLQueryRequest.serialize(), 
                KeyspaceList.class);
        
        System.out.println("Keyspaces");
        res.getResponse().getKeyspaces().stream().forEach(ks -> {
            System.out.println(ks.getName());
        });
                
    }
    

}
