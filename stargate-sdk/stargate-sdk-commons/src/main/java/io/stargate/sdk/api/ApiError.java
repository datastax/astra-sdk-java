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

package io.stargate.sdk.api;

import java.io.Serializable;

/**
 * Specialized error for Stargate.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiError implements Serializable {
    
    /** Serial number. */
    private static final long serialVersionUID = 1312739916968639792L;

    /** Description. */
    private String description;
    
    /** Code. */
    private int code;

    /**
     * Getter accessor for attribute 'description'.
     *
     * @return
     *       current value of 'description'
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter accessor for attribute 'description'.
     * @param description
     * 		new value for 'description '
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Getter accessor for attribute 'code'.
     *
     * @return
     *       current value of 'code'
     */
    public int getCode() {
        return code;
    }

    /**
     * Setter accessor for attribute 'code'.
     * @param code
     * 		new value for 'code '
     */
    public void setCode(int code) {
        this.code = code;
    }
    
    

}
