package com.dtsx.astra.sdk.org;

import com.dtsx.astra.sdk.AbstractApiClient;
import com.dtsx.astra.sdk.org.domain.Key;
import com.dtsx.astra.sdk.org.domain.KeyDefinition;
import com.dtsx.astra.sdk.utils.ApiLocator;
import com.dtsx.astra.sdk.utils.ApiResponseHttp;
import com.dtsx.astra.sdk.utils.Assert;
import com.dtsx.astra.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.stream.Stream;

/**
 * Workshop with key management.
 */
public class KeysClient extends AbstractApiClient {

    /**
     * Constructor.
     *
     * @param token
     *      current token.
     */
    public KeysClient(String token) {
        super(token);
    }

    /**
     * List keys in a Organizations.
     *
     * @return
     *      list of keys in target organization.
     */
    public Stream<Key> findAll() {
        // Invoke endpoint
        ApiResponseHttp res = GET(ApiLocator.getApiDevopsEndpoint() + "/kms");
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
