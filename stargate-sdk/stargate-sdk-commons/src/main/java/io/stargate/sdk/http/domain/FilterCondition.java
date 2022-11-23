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

package io.stargate.sdk.http.domain;

/**
 * Ease process of creating a where clause.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum FilterCondition {

    /**
     * Greater Than.
     */
    GREATER_THAN("$gt"),
    
    /** 
     * Greater Than Or Equal To.
     */  
    GREATER_THAN_OR_EQUALS_TO("$gte"),

    /** 
     * Less Than. 
     */
    LESS_THAN("$lt"),
    
    /** 
     * Less Than Or Equal To. 
     */
    LESS_THAN_OR_EQUALS_TO("$lte"),
    
    /** 
     * Equal To. 
     */
    EQUALS_TO("$eq"),
    
    /** 
     * Not Equal To.
     */
    NOT_EQUALS_TO("$ne"),
    
    /** 
     * in. 
     */
    IN("$in"),
    
    /** 
     * Exist. 
     */
    EXISTS("$exists"),
    
    /** 
     * Contains. 
     */
    CONTAINS("$contains"),
    
    /** 
     * Contains Key. 
     */
    CONTAIN_KEY("$containsKey"),
    
    /** 
     * Contains Entry. 
     */
    CONTAIN_ENTRY("$containsEntry");
    
    /**
     * Value for the operator
     */
    private String operator;
    
    /**
     * Constructor for the enum.
     * @param op
     *      current operator
     */
    private FilterCondition(String op) {
        this.operator = op;
    }
    
    /**
     * Getter for param 'operator'.
     *
     * @return
     *      value for operator
     */
    public String getOperator() {
        return operator;
    }

}
