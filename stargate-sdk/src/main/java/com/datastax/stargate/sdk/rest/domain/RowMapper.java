package com.datastax.stargate.sdk.rest.domain;

/**
 * Row Mapper.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <T>
 *      object to marshall the row
 */
public interface RowMapper<T> {
    
    /**
     * Convert row to bean.
     *
     * @param row
     *      current row sent to API
     * @return
     *      marshalled bean
     */
    T map(Row row);

}
