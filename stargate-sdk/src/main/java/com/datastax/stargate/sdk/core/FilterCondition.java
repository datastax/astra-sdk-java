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

/**
 * Ease process of creating a where clause.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum FilterCondition {
    
    // Greater Than
    GreaterThan("$gt"),
    
    // Greater Than Or Equal To 
    GreaterThenOrEqualsTo("$gte"),

    // Less Than
    LessThan("$lt"),
    
    // Less Than Or Equal To
    LessThanOrEqualsTo("$lte"),
    
    // Equal To
    EqualsTo("$eq"),
    
    // Not Equal To
    NotEqualsTo("$ne"),
    
    // in
    In("$in"),
    
    // Exist
    Exists("$exists"),
    
    // Contains
    Contains("$contains"),
    
    // Contains Key
    ContainsKey("$containsKey"),
    
    // Contains Entry
    ContainsEntry("$containsEntry");
    
    private String operator;
    
    private FilterCondition(String op) {
        this.operator = op;
    }
    
    public String getOperator() {
        return operator;
    }

}
