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
