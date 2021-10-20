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

/**
 * Sorting results.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class SortField {
    
    /** reference to field to sort. */
    private String fieldName;
    
    /** Order. */
    private Ordering order;

    /**
     * Default Constructor.
     */
    public SortField() {}
    
    /**
     * Constructor with parameters.
     *
     * @param fieldName
     *      current field name
     * @param order
     *      ordering value
     */
    public SortField(String fieldName, Ordering order) {
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
    
     
    
}
