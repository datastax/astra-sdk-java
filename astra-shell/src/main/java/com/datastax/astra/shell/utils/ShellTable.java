package com.datastax.astra.shell.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fusesource.jansi.Ansi;

/**
 * Standardize output for tables.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ShellTable implements Serializable {
    
    /** Serial */
    private static final long serialVersionUID = -2134504321420499395L;

    /**
     * Color of table. 
     */
    private Ansi.Color tableColor = Ansi.Color.CYAN;
    
    /**
     * Color of title
     */
    private Ansi.Color  columnTitlesColor = Ansi.Color.YELLOW;

    /**
     * Color of cell
     */
    private Ansi.Color  cellColor = Ansi.Color.WHITE;
    
    /**
     * Color of PK
     */
    private Ansi.Color  pkColor = Ansi.Color.RED;
    
    /**
     * Title column names
     */
    private List < String > columnTitlesNames = new ArrayList<>();
    
    /**
     * Partition keys columns
     */
    private Set < String > pkColumns = new HashSet<>();
    
    /**
     * Columns sizes
     */
    private Map < String, Integer > columnSize = new HashMap<>();
    
    /**
     * Cell values
     */
    private List< Map < String, String > > cellValues = new ArrayList<>();
    
    /** Shell Table */
    public ShellTable() {
        // Astra Shell defaults
        setColumnTitlesColor(Ansi.Color.YELLOW);
        setCellColor(Ansi.Color.WHITE);
        setTableColor(Ansi.Color.CYAN);
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
        LoggerShell.print(tableLine.toString() + "+\n", tableColor);
        
        // Display Column Titles
        for(String columnName : columnTitlesNames) {
            LoggerShell.print("| ", tableColor);
            Integer size = columnSize.get(columnName);
            if (null == size) {
                size = columnName.length() + 1;
            }
            LoggerShell.print(columnName , columnTitlesColor, size);
        }
        LoggerShell.print("|\n", tableColor);
        LoggerShell.print(tableLine.toString() + "+\n", tableColor);
        
        // Display Data
        for (Map<String, String > res : cellValues) {
            // Keep Orders
            for(String columnName : columnTitlesNames) {
                LoggerShell.print("| ", tableColor);
                if (pkColumns.contains(columnName)) {
                    LoggerShell.print(res.get(columnName), pkColor, columnSize.get(columnName));
                } else {
                    LoggerShell.print(res.get(columnName), cellColor, columnSize.get(columnName));
                }
            }
            LoggerShell.print("|\n", tableColor);
        }
        LoggerShell.print(tableLine.toString() + "+\n", tableColor);
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
    public Ansi.Color getTableColor() {
        return tableColor;
    }

    /**
     * Setter accessor for attribute 'tableColor'.
     * @param tableColor
     * 		new value for 'tableColor '
     */
    public void setTableColor(Ansi.Color tableColor) {
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
    public Ansi.Color getColumnTitlesColor() {
        return columnTitlesColor;
    }

    /**
     * Setter accessor for attribute 'columnTitlesColor'.
     * @param columnTitlesColor
     * 		new value for 'columnTitlesColor '
     */
    public void setColumnTitlesColor(Ansi.Color columnTitlesColor) {
        this.columnTitlesColor = columnTitlesColor;
    }

    /**
     * Getter accessor for attribute 'cellColor'.
     *
     * @return
     *       current value of 'cellColor'
     */
    public Ansi.Color getCellColor() {
        return cellColor;
    }

    /**
     * Setter accessor for attribute 'cellColor'.
     * @param cellColor
     * 		new value for 'cellColor '
     */
    public void setCellColor(Ansi.Color cellColor) {
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
    public Ansi.Color getPkColor() {
        return pkColor;
    }

    /**
     * Setter accessor for attribute 'pkColor'.
     * @param pkColor
     * 		new value for 'pkColor '
     */
    public void setPkColor(Ansi.Color pkColor) {
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
