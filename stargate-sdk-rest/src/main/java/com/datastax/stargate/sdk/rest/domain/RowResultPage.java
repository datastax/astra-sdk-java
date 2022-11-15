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

import com.datastax.stargate.sdk.core.Page;

import java.util.List;

/**
 * Result of API
 *
 * @author Cedrick LUNVEN (@clunven)s
 */
public class RowResultPage extends Page<Row> {

    /**
     * Default constructor.
     */
    public RowResultPage() {
        super();
    }
    
    /**
     * Full constructor.
     * 
     * @param pageSize int
     * @param pageState String
     * @param results List
     */
    public RowResultPage(int pageSize, String pageState, List<Row> results) {
        super(pageSize,pageState,results);
    }
}
