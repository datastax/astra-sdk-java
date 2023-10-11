package com.dtsx.astra.sdk.vector;

import io.stargate.sdk.core.domain.ObjectMap;
import io.stargate.sdk.json.JsonCollectionClient;
import io.stargate.sdk.json.vector.VectorStore;

public class DefaultVectorStore extends VectorStore<ObjectMap> {
    /**
     * Default constructor.
     *
     * @param col   collection client parent
     * @param clazz working bean class
     */
    public DefaultVectorStore(JsonCollectionClient col, Class<ObjectMap> clazz) {
        super(col, clazz);
    }
}
