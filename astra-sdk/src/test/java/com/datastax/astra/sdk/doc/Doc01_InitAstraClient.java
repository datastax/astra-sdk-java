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

package com.datastax.astra.sdk.doc;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.astra.sdk.AstraClient.AstraClientBuilder;

/**
 * This is not a test but code snippet
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Doc01_InitAstraClient {
    
    public void howToInitializeTheAstraClient() {
        AstraClient client = AstraClient.builder()
                // If you want to use Devops API
                .appToken("AstraCS:...")
                // If you need to use CqlSession provide user/password
                .clientId("...").clientSecret("...")
                // Anything targetting a DB (doc API, Rest API, cqlSession)
                .databaseId("astra_cluster_id").cloudProviderRegion("astra_db_region")
                // Optional you can set the CqlSession for a Keyspace
                .keyspace("optional_keyspace")
                // Terminal Call
                .build();
       
    }

}
