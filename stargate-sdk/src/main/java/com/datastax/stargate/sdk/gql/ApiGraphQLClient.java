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

import static com.datastax.stargate.sdk.utils.AnsiUtils.green;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.StargateHttpClient;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.utils.Assert;
/**
 * Superclass to work with graphQL.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiGraphQLClient {

    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiGraphQLClient.class);
    
    public static final String PATH_CQLFIRST_DDL = "-schema";
    public static final String PATH_CQLFIRST_DML = "/";
    public static final String PATH_GRAPQHFIRST  = "-admin";
    
    /** Get Topology of the nodes. */
    private final StargateHttpClient stargateHttpClient;
    
    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateClient
     *      stargate client
     */
    public ApiGraphQLClient(StargateHttpClient stargateClient) {
        Assert.notNull(stargateClient, "stargate client reference. ");
        this.stargateHttpClient =  stargateClient;
        LOGGER.info("+ API GraphQL  :[" + green("{}") + "]", "ENABLED");
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     * https://docs.datastax.com/en/astra/docs/_attachments/restv2.html#operation/getKeyspaces
     * 
     * @return Keyspace
     */
    public List<Map<String, Object>> keyspaces() {
        stargateHttpClient.GET(null);
        //String query = "";
        // Invoke gql endpoints
        // Parse output
        return null;
    }
    
    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<StargateClientNode, String> getEndpointSchemaKeyspacesGQL = 
            (node) -> node.getApiGraphQLEndpoint() + PATH_CQLFIRST_DDL;
    

}
