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

    private static final String DB_ID     = "<change_me>";
    private static final String DB_REGION = "<change_me>";
    private static final String DB_TOKEN = "<change_me>";
    
    
    private static final String URL = "https://"
            + DB_ID + "-" + DB_REGION 
            + ".apps.astra.datastax.com/api/graphql-schema";
    
    public static <T> GraphQLResponseEntity<T> callGraphQLService(String query, Class<T> clazz)
    throws Exception {
        Map<String, String > headers = new HashMap<>();
        headers.put("x-cassandra-token", DB_TOKEN); 
        return new GraphQLTemplate().query(GraphQLRequestEntity.Builder()
                .url(URL)
                .request(query)
                .headers(headers)
                .build(), clazz);
    }
    
    /**
     * Main.
     */
    public static void main(String[] args)  
    throws Exception {
        
        // List Keyspaces
        GraphQLQueryRequest graphQLQueryRequest =
                new GraphQLQueryRequest(
                        new KeyspacesGraphQLQuery.Builder().build(),
                        new KeyspacesProjectionRoot().name());
        
        // Executing query
        GraphQLResponseEntity<KeyspaceList> res = callGraphQLService(
                graphQLQueryRequest.serialize(), 
                KeyspaceList.class);
        
        // Showing results
        res.getResponse().getKeyspaces().stream().forEach(ks -> {
            System.out.println(ks.getName());
        });
                
    }
    

}
