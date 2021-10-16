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

package com.datastax.astra.sdk.utils;

/**
 * Utility to work with generated CSV
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class TokenCsv {
    
    /**
     * Hide constructor.
     */
    private TokenCsv() {}
    
    /**
     * Parse the CSV file generated.
     * @param file
     *      current file
     * @return
     *      token
     */
    public static final Token readCsvToken(String file) {
        return new Token("", "", "", "");
        
    }

}
