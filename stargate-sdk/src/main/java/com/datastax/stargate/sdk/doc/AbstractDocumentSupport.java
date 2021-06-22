package com.datastax.stargate.sdk.doc;

import java.lang.reflect.ParameterizedType;

/**
 * 
 * Mutualizing support for dynamic generics.
 * 
 * A super class is need to use  ParameterizedType at runtime.
 * 
 * @param <DOC>
 *      current beans
 */
public class AbstractDocumentSupport<DOC> {
    
    /**
     * Dynamically retrieving document type.
     *
     * @return
     *      current working type
     */
    @SuppressWarnings("unchecked")
    protected Class<DOC> getGenericClass() {
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<DOC>) pt.getActualTypeArguments()[0];
    }
    
    /**
     * Compute the collection id from class name or annotation StargateDocument.
     *
     * @return
     *      collection id
     */
    protected String getCollectionIdFromBean() {
        Collection ann = getGenericClass().getAnnotation(Collection.class);
        if (null != ann && ann.value() !=null && !ann.value().equals("")) {
            return ann.value();
        } else {
            return getGenericClass().getSimpleName().toLowerCase();
        }
    }

}
