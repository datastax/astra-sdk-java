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

package com.datastax.stargate.sdk.doc.domain;

import java.util.List;

import com.datastax.stargate.sdk.core.ResultPage;
import com.datastax.stargate.sdk.doc.ApiDocument;

/**
 * Hold results for paging
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <DOC>
 *      document type
 */
public class DocumentResultPage< DOC > extends ResultPage<ApiDocument<DOC>> {
    
    /**
     * Default constructor.
     */
    public DocumentResultPage() {
        super();
    }
    
    
    /**
     * Full constructor.
     * 
     * @param pageSize int
     * @param pageState String
     * @param results List
     */
    public DocumentResultPage(int pageSize, String pageState, List<ApiDocument<DOC>> results) {
        super(pageSize,pageState,results);
    }
    
}
