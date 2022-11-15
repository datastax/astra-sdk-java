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

package com.datastax.stargate.sdk.doc;

import com.datastax.stargate.sdk.StargateClientNode;
import com.datastax.stargate.sdk.ServiceClient;
import com.datastax.stargate.sdk.core.ApiResponse;
import com.datastax.stargate.sdk.doc.domain.Namespace;
import com.datastax.stargate.sdk.rest.domain.Keyspace;
import com.datastax.stargate.sdk.utils.Assert;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.datastax.stargate.sdk.utils.AnsiUtils.green;
import static com.datastax.stargate.sdk.utils.JsonUtils.unmarshallType;

/**
 * Client for the Astra/Stargate document (collections) API.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiDocumentClient {
    
    /** Logger for our Client. */
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiDocumentClient.class);
    
    /** Schema sub level. */
    public static final String PATH_SCHEMA_NAMESPACES = "/namespaces";
    
    /** Schema sub level. */
    public static final String PATH_SCHEMA            = "/schemas";
    
    /** Schema sub level. */
    public static final String PATH_V2                = "/v2";
    
    /** Get Topology of the nodes. */
    protected final ServiceClient stargateHttpClient;
    
    /**
     * Constructor with StargateClient as argument.
     *
     * @param stargateHttpClient
     *      stargate http client
     */
    public ApiDocumentClient(ServiceClient stargateHttpClient) {
        Assert.notNull(stargateHttpClient, "stargate client reference. ");
        this.stargateHttpClient = stargateHttpClient;
        LOGGER.info("+ API Document :[" + green("{}") + "]", "ENABLED");
    }
    
    /**
     * Return list of {@link Namespace}(keyspaces) available.
     * 
     * @see <a href="https://stargate.io/docs/stargate/1.0/attachments/docv2.html#operation/getAllNamespaces">Reference Documentation</a>
     * 
     * @return Stream
     */
    public Stream<Namespace> namespaces() {
        String res = stargateHttpClient.GET(this.namespacesSchemaResource).getBody();
        return unmarshallType(res, new TypeReference<ApiResponse<List<Namespace>>>(){})
              .getData()
              .stream();
    }
    
    /**
     * Return list of Namespace (keyspaces) names available.
     *
     * @return Stream
     *      stream of the namespaces
     */
    public Stream<String> namespaceNames() {
        return namespaces().map(Keyspace::getName);
    }
    
    // ---------------------------------
    // ----    Sub Resources        ----
    // ---------------------------------
    
    /**
     * Move the document API (namespace client)
     * 
     * @param namespace String
     * @return NamespaceClient
     */
    public NamespaceClient namespace(String namespace) {
        return new NamespaceClient(stargateHttpClient, this, namespace);
    }
    
    // ---------------------------------
    // ----   Build Resources URLS  ----
    // ---------------------------------
    
    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<StargateClientNode, String> namespacesSchemaResource = 
            (node) -> node.getApiRestEndpoint() + PATH_V2 + PATH_SCHEMA + PATH_SCHEMA_NAMESPACES;

    /**
     * Mapping from root URL to rest endpoint listing keyspaces definitions.
     */
    public Function<StargateClientNode, String> namespacesResource = 
            (node) -> node.getApiRestEndpoint() + PATH_V2 + PATH_SCHEMA_NAMESPACES;
            
}
