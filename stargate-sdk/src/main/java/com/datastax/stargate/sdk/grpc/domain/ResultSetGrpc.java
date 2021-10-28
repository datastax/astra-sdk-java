package com.datastax.stargate.sdk.grpc.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.stargate.proto.QueryOuterClass.ColumnSpec;
import io.stargate.proto.QueryOuterClass.ResultSet;
import io.stargate.proto.QueryOuterClass.Row;

/**
 * Helper to parse the grpoc output.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ResultSetGrpc {
    
    /** Object returned by the grpc.*/
    private final ResultSet rs;
    
    /** Index columns names. */
    private final List<String> columnsNames = new ArrayList<>();
    
    /** Access one column in particular. */
    private final Map<String, ColumnSpec> columnsSpecs = new HashMap<>();
    
    /** Access one column in particular. */
    private final Map<String, Integer> columnsIndexes = new HashMap<>();
    
    /**
     * Constructor for the wrapper.
     * 
     * @param rs
     *      resultset
     */
    public ResultSetGrpc(ResultSet rs) {
        this.rs = rs;
        for (int i=0; i<rs.getColumnsCount();i++) {
            ColumnSpec cs = rs.getColumns(i);
            this.columnsSpecs.put(cs.getName(), cs);
            this.columnsIndexes.put(cs.getName(), i);
            this.columnsNames.add(cs.getName());
        }
    }
    
    /**
     * You know you do have a single line.
     * 
     * @return
     *      single row
     */
    public RowGrpc one() {
        if (1 != rs.getRowsCount()) {
            throw new IllegalArgumentException("Resultset contains more than 1 row");
        }
        return getRows().get(0);
    }
    
    /**
     * Access a row by its index.
     * 
     * @param idx
     *      row index
     * @return
     *      row value
     */
    public Row getRow(int idx) {
        if (idx > rs.getRowsCount()) {
            throw new IllegalArgumentException("Resulset contains only " +  rs.getRowsCount() + " row(s).");
        }
        return rs.getRowsList().get(idx);
    }
    
    /**
     * Access Rows.
     * 
     * @return
     *      list if items
     */
    public List<RowGrpc> getRows() {
        return rs.getRowsList()
                 .stream()
                 .map(r -> new RowGrpc(this, r))
                 .collect(Collectors.toList());
    }
    
    /**
     * Accessor for internal object.
     * 
     * @return
     *      internal object
     */
    public ResultSet getResultSet() {
        return rs;
    }
    
    /**
     * Access column index based on offset.
     * 
     * @param name
     *      column name
     * @return
     *      column index
     */
    public int getColumnIndex(String name) {
        if (!columnsIndexes.containsKey(name)) {
            throw new IllegalArgumentException("Column '" + name + "' is unknown, use " + columnsNames);
        }
        return columnsIndexes.get(name);
    }
    
    /**
     * Return column name based on index.
     * @param idx
     *      column index
     * @return
     *      column name
     */
    public String getColumnName(int idx) {
        if (idx > columnsNames.size()) {
            throw new IllegalArgumentException("Invalid index, only '" 
                            + columnsNames.size() + "' size available");
        }
        return columnsNames.get(idx);
    }

}
