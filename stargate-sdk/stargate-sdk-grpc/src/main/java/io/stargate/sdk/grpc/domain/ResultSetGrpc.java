package io.stargate.sdk.grpc.domain;

import io.stargate.proto.QueryOuterClass.ColumnSpec;
import io.stargate.proto.QueryOuterClass.ResultSet;
import io.stargate.proto.QueryOuterClass.Row;

import java.util.*;
import java.util.stream.Stream;

/**
 * Helper to parse the grpoc output.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ResultSetGrpc {
    
    /** Object returned by the grpc.*/
    private final ResultSet grpcResponse;
    
    /** Index columns names. */
    private final List<String> columnsNames = new ArrayList<>();
    
    /** Access one column in particular. */
    private final Map<String, ColumnSpec> columnsSpecs = new HashMap<>();
    
    /** Access one column in particular. */
    private final Map<String, Integer> columnsIndexes = new HashMap<>();

    /**
     * Get row counts.
     *
     * @return
     *      rows counts
     */
    public int getRowCount() {
        return grpcResponse.getRowsCount();
    }

    /**
     * Get columns counts.
     *
     * @return
     *      columns counts
     */
    public int getColumnsCount() {
        return grpcResponse.getColumnsCount();
    }

    /**
     * Get paging state
     *
     * @return
     *      paging state
     */
    public Optional<String> getPagingState() {
        Optional<String> pg = Optional.empty();
        if (grpcResponse.hasPagingState()) {
            pg = Optional.ofNullable(grpcResponse.getPagingState().getValue().toStringUtf8());
        }
        return pg;
    }

    /**
     * Constructor for the wrapper.
     * 
     * @param rs
     *      resultset
     */
    public ResultSetGrpc(ResultSet rs) {
        this.grpcResponse = rs;
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
        if (1 != grpcResponse.getRowsCount()) {
            throw new IllegalArgumentException("Resultset contains more than 1 row");
        }
        return getRows().findFirst().get();
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
        if (idx > grpcResponse.getRowsCount()) {
            throw new IllegalArgumentException("Resulset contains only " +  grpcResponse.getRowsCount() + " row(s).");
        }
        return grpcResponse.getRowsList().get(idx);
    }
    
    /**
     * Access Rows.
     * 
     * @return
     *      list if items
     */
    public Stream<RowGrpc> getRows() {
        return grpcResponse.getRowsList()
                 .stream()
                 .map(r -> new RowGrpc(this, r));
    }
    
    /**
     * Accessor for internal object.
     * 
     * @return
     *      internal object
     */
    public ResultSet getGrpcResponse() {
        return grpcResponse;
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
