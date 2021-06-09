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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Response Wrapper.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class ApiResponseError implements Serializable {

    /**
     * Serial.
     */
    private static final long serialVersionUID = 4340469194989763449L;
    
    /**
     * Error List.
     */
    private List<ApiError> errors = new ArrayList<>();

    /**
     * Getter accessor for attribute 'errors'.
     *
     * @return
     *       current value of 'errors'
     */
    public List<ApiError> getErrors() {
        return errors;
    }

    /**
     * Setter accessor for attribute 'errors'.
     * @param errors
     * 		new value for 'errors '
     */
    public void setErrors(List<ApiError> errors) {
        this.errors = errors;
    }
}
