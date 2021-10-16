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

package com.datastax.astra.sdk.databases.domain;

/**
 * Encoded all values for 'tier'
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public enum DatabaseTierType {
    
    /** 
     * developer.
     */
    developer("developer"),
    
    /** 
     * cloudnative
     */
    cloudnative("cloudnative"),
    
    /** 
     * serverless
     */
    serverless("serverless"),
   
    /** A5. */
    A5("A5"),
    
    /** 
     * A10.
     */
    A10("A10"),
    
    /** 
     * A20.
     */
    A20("A20"),
    
    /** 
     * A40.
     */
    A40("A40"),
    
    /** 
     * C10.
     */
    C10("C10"),
    
    /** 
     * C20.
     */
    C20("C20"),
    
    /** 
     * C40.
     */
    C40("C40"),
    
    /** 
     * D10.
     */
    D10("D10"),
    
    /** 
     * D20.
     */
    D20("D20"),
    
    /** 
     * D40.
     */
    D40("D40");

    /**
     * Datbase code.
     */
    private String code;
    
    /**
     * get the code.
     *
     * @param code
     *      target code
     */
    private DatabaseTierType(String code) {
        this.code = code;
    }
    
    /**
     * Get code value.
     *
     * @return
     *      value of the code
     */
    public String getCode() {
        return code;
    }
    
}
