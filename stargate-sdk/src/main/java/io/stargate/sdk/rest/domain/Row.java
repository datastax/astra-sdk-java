package io.stargate.sdk.rest.domain;

import java.util.HashMap;

/**
 * Wrapper to parse Rows as an HashMap.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Row extends HashMap<String, Object> {

    /** Serial. */
    private static final long serialVersionUID = 3279531139420446635L;
    
    /**
     * Retrieve value and check existence.
     *
     * @param colName
     *      column name
     * @return
     *      value if exist or error
     */
    public Object get(String colName) {
        if (!containsKey(colName)) {
            throw new IllegalArgumentException("Cannot find column "
                    + "with name '" + colName + "', available columns are " + keySet());
        }
        return super.get(colName);
    }
    
    /**
     * Retrieve a column value as a String.
     */
    public String getString(String colName) {
        return String.valueOf(get(colName));
    }
    
    /**
     * Retrieve a column value as a Double.
     */
    public Double getDouble(String colName) {
        return Double.valueOf(getString(colName));
    }
    
    /**
     * Retrieve a column value as an Integer.
     */
    public Integer getInt(String colName) {
        return getDouble(colName).intValue();
    }
    

}
