package com.datastax.astra.devops.org;

import com.datastax.astra.devops.org.domain.KeyDefinition;
import com.datastax.astra.devops.utils.ApiResponseHttp;
import com.datastax.astra.devops.utils.Assert;
import com.datastax.astra.devops.utils.JsonUtils;
import com.datastax.astra.devops.AbstractApiClient;
import com.datastax.astra.devops.org.domain.Key;
import com.datastax.astra.devops.utils.ApiLocator;
import com.datastax.astra.devops.utils.AstraEnvironment;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.stream.Stream;

/**
 * Workshop with key management.
 */
public class KeysClient extends AbstractApiClient {

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param token
     *      authenticated token
     */
    public KeysClient(String token) {
        this(token, AstraEnvironment.PROD);
    }

    /**
     * As immutable object use builder to initiate the object.
     *
     * @param env
     *      define target environment to be used
     * @param token
     *      authenticated token
     */
    public KeysClient(String token, AstraEnvironment env) {
        super(token, env);
    }

    /**
     * List keys in a Organizations.
     *
     * @return
     *      list of keys in target organization.
     */
    public Stream<Key> findAll() {
        // Invoke endpoint
        ApiResponseHttp res = GET(ApiLocator.getApiDevopsEndpoint(environment) + "/kms");
        // Mapping
        return JsonUtils.unmarshallType(res.getBody(), new TypeReference<List<Key>>(){}).stream();
    }

    /**
     * Create a new key.
     *
     * @param keyDef
     *      key definition request
     * @return
     *      new role created
     */
    public Object createKey(KeyDefinition keyDef) {
        Assert.notNull(keyDef, "CreateRole request");
        throw new RuntimeException("This function is not yet implemented");
    }
}
