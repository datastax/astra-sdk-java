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
     * Constructor for a collection.
     *
     * @param db
     *      database in use
     * @param collectionName
     *      collection identifier
     * @param clazz
     *      document class to use
     */
    protected AstraDBCollection(AstraDBDatabase db, String collectionName, Class<DOC> clazz) {
        super(db, collectionName, clazz);
    }

}
