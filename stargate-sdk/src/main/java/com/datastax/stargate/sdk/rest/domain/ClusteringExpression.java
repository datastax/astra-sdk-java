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

import java.io.Serializable;

/**
 * Code for table deifnition ordering.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ClusteringExpression implements Serializable {
    
    /** Serial. */
    private static final long serialVersionUID = -910292385355052561L;

    private Ordering order;
    
    private String column;
    
    /**
     * Default Constructor
     */
    public ClusteringExpression() {
    }
    
    public ClusteringExpression(String column, Ordering order) {
        super();
        this.order = order;
        this.column = column;
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
     *      new value for 'order '
     */
    public void setOrder(Ordering order) {
        this.order = order;
    }
    
    /**
     * Getter accessor for attribute 'column'.
     *
     * @return
     *       current value of 'column'
     */
    public String getColumn() {
        return column;
    }
    
    /**
     * Setter accessor for attribute 'column'.
     * @param column
     *      new value for 'column '
     */
    public void setColumn(String column) {
        this.column = column;
    }        
}
