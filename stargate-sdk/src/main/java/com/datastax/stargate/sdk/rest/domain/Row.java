/*
 * Copyright DataStax, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.datastax.stargate.sdk.rest.domain;

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
