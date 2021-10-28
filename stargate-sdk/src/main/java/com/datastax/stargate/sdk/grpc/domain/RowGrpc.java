package com.datastax.stargate.sdk.grpc.domain;

import io.stargate.proto.QueryOuterClass.Row;
import io.stargate.proto.QueryOuterClass.Value;

/**
 * Wrapper to work with GRPC rows.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class RowGrpc {
    
    /** reference to the resultset. */
    private final ResultSetGrpc rs;
    
    /** Internal technical row. */
    private final Row row;
    
    /**
     * Constructor (used by the result set)
     * @param rs
     *      current result set
     * @param r
     *      current row
     */
    protected RowGrpc(ResultSetGrpc rs, Row r) {
        this.rs  = rs;
        this.row = r;
    }
    
    /**
     * Access String values.
     *
     * @param columnName
     *      column name
     * @return
     *      column value
     */
    public String getString(String columnName) {
        return getValue(columnName).getString();
    }
    
    /**
     * Access String values.
     *
     * @param columnName
     *      column name
     * @return
     *      column value
     */
    public String getDouble(String columnName) {
        return getValue(columnName).getString();
    }
    
    /**
     * Access String values.
     *
     * @param columnName
     *      column name
     * @return
     *      column value
     */
    public Value getValue(String columnName) {
        return row.getValues(rs.getColumnIndex(columnName));
    }

}
