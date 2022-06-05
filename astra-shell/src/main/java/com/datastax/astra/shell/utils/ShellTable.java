package com.datastax.astra.shell.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.datastax.astra.shell.jansi.Out;
import com.datastax.astra.shell.jansi.TextColor;

/**
 * Standardize output for tables.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShellTable implements Serializable {
    
    /** Serial */
    private static final long serialVersionUID = -2134504321420499395L;

    private TextColor tableColor = TextColor.CYAN;
    
    private TextColor columnTitlesColor = TextColor.YELLOW;

    private TextColor cellColor = TextColor.WHITE;
    
    private TextColor pkColor = TextColor.RED;
    
    private List < String > columnTitlesNames = new ArrayList<>();
    
    private Set < String > pkColumns = new HashSet<>();
    
    private Map < String, Integer > columnSize = new HashMap<>();
    
    private List< Map < String, String > > cellValues = new ArrayList<>();
    
    /** Shell Table */
    public ShellTable() {
    }
    
    /**
     * Display the table in the shell.
     */
    public void show() {
        
        // Compute Columns Width
        cellValues.stream().forEach(myRow -> {
            columnTitlesNames.stream().forEach(colName -> {
                if (!columnSize.containsKey(colName) || 
                     columnSize.get(colName) <  Math.max(colName.length(), myRow.get(colName).length())) {
                    columnSize.put(colName,  Math.max(colName.length(), myRow.get(colName).length()) + 1);
                }
            });
        });
        
        // Compute Table Horizontal Line
        StringBuilder tableLine = new StringBuilder();
        for(String columnName : columnTitlesNames) {
            Integer size = columnSize.get(columnName);
            if (null == size) {
                size = columnName.length() + 1;
            }
            tableLine.append("+" + String.format("%-" + (size+1) + "s", "-").replaceAll(" " , "-"));
        }
        Out.print(tableLine.toString() + "+\n", tableColor);
        
        // Display Column Titles
        for(String columnName : columnTitlesNames) {
            Out.print("| ", tableColor);
            Integer size = columnSize.get(columnName);
            if (null == size) {
                size = columnName.length() + 1;
            }
            Out.print(columnName , true, Optional.of(size), columnTitlesColor);
        }
        Out.print("|\n", tableColor);
        Out.print(tableLine.toString() + "+\n", tableColor);
        
        // Display Data
        for (Map<String, String > res : cellValues) {
            // Keep Orders
            for(String columnName : columnTitlesNames) {
                Out.print("| ", tableColor);
                if (pkColumns.contains(columnName)) {
                    Out.print(res.get(columnName) , 
                            true, Optional.of(columnSize.get(columnName)), pkColor);
                } else {
                    Out.print(res.get(columnName) , 
                            true, Optional.of(columnSize.get(columnName)), cellColor);
                } 
                
               
            }
            Out.print("|\n", tableColor);
        }
        Out.print(tableLine.toString() + "+\n", tableColor);
    }
    
    /**
     * Add property in a table.
     *
     * @param name
     *      property name
     * @param value
     *      property value
     * @return
     *      new row
     */
    public static Map<String, String > addProperty(String name, String value) {
        Map <String, String> rf = new HashMap<>();
        rf.put("Name", name);
        rf.put("Value", value);
        return rf;
    }
    
    /**
     * Add a column.
     *
     * @param colName
     *      name
     * @param colwidth
     *      with
     */
    public void addColumn(String colName, int colwidth) {
        getColumnTitlesNames().add(colName);
        getColumnSize().put(colName, colwidth);
    }
    

    /**
     * Getter accessor for attribute 'tableColor'.
     *
     * @return
     *       current value of 'tableColor'
     */
    public TextColor getTableColor() {
        return tableColor;
    }

    /**
     * Setter accessor for attribute 'tableColor'.
     * @param tableColor
     * 		new value for 'tableColor '
     */
    public void setTableColor(TextColor tableColor) {
        this.tableColor = tableColor;
    }

    /**
     * Getter accessor for attribute 'columnTitlesNames'.
     *
     * @return
     *       current value of 'columnTitlesNames'
     */
    public List<String> getColumnTitlesNames() {
        return columnTitlesNames;
    }

    /**
     * Setter accessor for attribute 'columnTitlesNames'.
     * @param columnTitlesNames
     * 		new value for 'columnTitlesNames '
     */
    public void setColumnTitlesNames(List<String> columnTitlesNames) {
        this.columnTitlesNames = columnTitlesNames;
    }

    /**
     * Getter accessor for attribute 'columnTitlesColor'.
     *
     * @return
     *       current value of 'columnTitlesColor'
     */
    public TextColor getColumnTitlesColor() {
        return columnTitlesColor;
    }

    /**
     * Setter accessor for attribute 'columnTitlesColor'.
     * @param columnTitlesColor
     * 		new value for 'columnTitlesColor '
     */
    public void setColumnTitlesColor(TextColor columnTitlesColor) {
        this.columnTitlesColor = columnTitlesColor;
    }

    /**
     * Getter accessor for attribute 'cellColor'.
     *
     * @return
     *       current value of 'cellColor'
     */
    public TextColor getCellColor() {
        return cellColor;
    }

    /**
     * Setter accessor for attribute 'cellColor'.
     * @param cellColor
     * 		new value for 'cellColor '
     */
    public void setCellColor(TextColor cellColor) {
        this.cellColor = cellColor;
    }

    /**
     * Getter accessor for attribute 'cellValues'.
     *
     * @return
     *       current value of 'cellValues'
     */
    public List<Map<String, String>> getCellValues() {
        return cellValues;
    }

    /**
     * Setter accessor for attribute 'cellValues'.
     * @param cellValues
     * 		new value for 'cellValues '
     */
    public void setCellValues(List<Map<String, String>> cellValues) {
        this.cellValues = cellValues;
    }

    /**
     * Getter accessor for attribute 'pkColumns'.
     *
     * @return
     *       current value of 'pkColumns'
     */
    public Set<String> getPkColumns() {
        return pkColumns;
    }

    /**
     * Setter accessor for attribute 'pkColumns'.
     * @param pkColumns
     * 		new value for 'pkColumns '
     */
    public void setPkColumns(Set<String> pkColumns) {
        this.pkColumns = pkColumns;
    }

    /**
     * Getter accessor for attribute 'pkColor'.
     *
     * @return
     *       current value of 'pkColor'
     */
    public TextColor getPkColor() {
        return pkColor;
    }

    /**
     * Setter accessor for attribute 'pkColor'.
     * @param pkColor
     * 		new value for 'pkColor '
     */
    public void setPkColor(TextColor pkColor) {
        this.pkColor = pkColor;
    }

    /**
     * Getter accessor for attribute 'columnSize'.
     *
     * @return
     *       current value of 'columnSize'
     */
    public Map<String, Integer> getColumnSize() {
        return columnSize;
    }

    /**
     * Setter accessor for attribute 'columnSize'.
     * @param columnSize
     * 		new value for 'columnSize '
     */
    public void setColumnSize(Map<String, Integer> columnSize) {
        this.columnSize = columnSize;
    }
    
}
