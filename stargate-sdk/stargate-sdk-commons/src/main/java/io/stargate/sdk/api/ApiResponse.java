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

/**
 * Wrapper for Astra API RESPONSE.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DATA>
 *      returned data by astra
 */
public class ApiResponse<DATA> {
    
    /**
     * Data field is always part of the response
     */
    private DATA data;
    
    /**
     * for Page queries
     */
    private String pageState;
    
    /**
     * Default constructor.
     */
    public ApiResponse() {}
    
    /**
     * Default Constructor.
     * @param t DATA
     */
    public ApiResponse(DATA t) {
        this.data = t;
    }

    /**
     * Getter accessor for attribute 'data'.
     *
     * @return
     *       current value of 'data'
     */
    public DATA getData() {
        return data;
    }

    /**
     * Getter accessor for attribute 'pageState'.
     *
     * @return
     *       current value of 'pageState'
     */
    public String getPageState() {
        return pageState;
    }

    /**
     * Setter accessor for attribute 'pageState'.
     * @param pageState
     * 		new value for 'pageState '
     */
    public void setPageState(String pageState) {
        this.pageState = pageState;
    }

    /**
     * Setter accessor for attribute 'data'.
     * @param data
     * 		new value for 'data '
     */
    public void setData(DATA data) {
        this.data = data;
    }

}
