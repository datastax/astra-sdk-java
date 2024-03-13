package com.datastax.astra.db;

import io.stargate.sdk.data.internal.DataApiCollectionImpl;

/**
 * Implementation of a Collection within Astra.
 *
 * @param <DOC>
 *     working document class
 */
public class AstraDBCollection<DOC> extends DataApiCollectionImpl<DOC> {

    /**
     * Full constructor.
     *
     * @param db
     *      client namespace http
     * @param collectionName
     *      collection identifier
     * @param clazz
     *      working pojo with this collection.
     */
    protected AstraDBCollection(AstraDBDatabase db, String collectionName, Class<DOC> clazz) {
        super(db, collectionName, clazz);
    }
}
