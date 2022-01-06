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

package com.datastax.stargate.sdk.core;

import com.datastax.stargate.sdk.rest.domain.Ordering;

/**
 * Sorting results.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class Sort {
    
    /** reference to field to sort. */
    private String fieldName;
    
    /** Order. */
    private Ordering order;

    /**
     * Default Constructor.
     */
    public Sort() {}
    
    /**
     * Syntax sugar to do Sort.by("field").ascending();
     * 
     * @param field
     *      field name
     * @return
     *      sort condition
     */
    public static Sort by(String field) {
        Sort s = new Sort(field, Ordering.ASC);
        return s;
    }
    
    /**
     * Constructor with parameters.
     *
     * @param fieldName
     *      current field name
     * @param order
     *      ordering value
     */
    public Sort(String fieldName, Ordering order) {
        super();
        this.fieldName = fieldName;
        this.order = order;
    }

    /**
     * Getter accessor for attribute 'fieldName'.
     *
     * @return
     *       current value of 'fieldName'
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Setter accessor for attribute 'fieldName'.
     * @param fieldName
     * 		new value for 'fieldName '
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Getter accessor for attribute 'order'.
     *
     * @return
     *       current value of 'order'
     */
    public Ordering getOrder() {
        return order;
    }

    /**
     * Setter accessor for attribute 'order'.
     * @param order
     * 		new value for 'order '
     */
    public void setOrder(Ordering order) {
        this.order = order;
    }
    
    /**
     * Sort in ascending mode.
     *
     * @return
     *  current reference.
     */
    public Sort ascending() {
        this.order = Ordering.ASC;
        return this;
    }
    
    /**
     * Sort in descending mode.
     *
     * @return
     *  current reference.
     */
    public Sort descending() {
        this.order = Ordering.ASC;
        return this;
    }
    
     
    
}
