package com.datastax.stargate.sdk.doc;

/**
 * Extension point for the user to implement its own parser for a record.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DOC>
 *      working bean
 */
@FunctionalInterface
public interface DocumentMapper<DOC> {
    
    /**
     * Extension point for the user to implement its own parser for a record.
     * 
     * @param record
     *      current record
     * @return
     *      the object marshalled
     */
    DOC map(String record);

}
