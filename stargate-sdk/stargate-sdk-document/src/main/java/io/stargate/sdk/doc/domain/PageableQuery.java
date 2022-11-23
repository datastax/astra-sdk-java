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

package io.stargate.sdk.doc.domain;

import java.util.Optional;
import java.util.Set;

/**
 * Build a queyr with filter clause. 
 * Designer choice was to use inheritance over composition to simplify builders + genericity is not mandatory.
 *
 * @author Cedrick LUNVEN (@clunven)
 * 
 * PageableQuery.builder()
 *              .where("age").isGreaterThan(10)  // Regular Query
 *              .withPageSize(in)                // Add Paging
 */
public class PageableQuery {
    
    /** Limit set for the API. */
    public static final int PAGING_SIZE_MAX = 20;
    
    /** Number of records to retrieve on a page. MAXIMUM 100. */
    public static final int DEFAULT_PAGING_SIZE = 20;
    
    /** If we want to filter on fields. */
    protected Set<String> fieldsToRetrieve;
    
    /** Build where clause. */
    protected String where;
    
    /** Page sixe. */ 
    private int pageSize = DEFAULT_PAGING_SIZE;
    
    /** Cursor for paging, not terminal as can be updated for issuing next page */ 
    private String pageState;
    
    /**
     * Constructor from super query.
     *
     * @param query
     *      current query
     */
    public PageableQuery(Query query) {
        this.fieldsToRetrieve = query.fieldsToRetrieve;
        this.where            = query.where;
        this.pageSize         = DEFAULT_PAGING_SIZE;
        this.pageState        = null;
    }
    
    /**
     * Constructor hidden to enforce builder usage.
     *
     * @param builder
     *      filled builder.
     */
    public PageableQuery(PageableQueryBuilder builder) {
        this.fieldsToRetrieve = builder.fields;
        this.where            = builder.getWhereClause();
        this.pageSize   = builder.getPageSize();
        this.pageState  = builder.getPageState();
    }
    
    /**
     * static accees to a builder instance
     * 
     * @return SearchDocumentQueryBuilder
     */
    public static PageableQueryBuilder builder() {
        return new PageableQueryBuilder(); 
    }
    
    /**
     * Getter accessor for attribute 'pageSize'.
     *
     * @return
     *       current value of 'pageSize'
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Getter accessor for attribute 'pageState'.
     *
     * @return
     *       current value of 'pageState'
     */
    public Optional<String> getPageState() {
        return Optional.ofNullable(pageState);
    }
    
    /**
     * Setter accessor for attribute 'pageState'.
     * 
     * @param pageState
     * 		new value for 'pageState '
     */
    public void setPageState(String pageState) {
        this.pageState = pageState;
    }
    
    /**
     * Getter accessor for attribute 'where'.
     *
     * @return
     *       current value of 'where'
     */
    public Optional<String> getWhere() {
        return Optional.ofNullable(where);
    }

    /**
     * Getter accessor for attribute 'fieldsToRetrieve'.
     *
     * @return
     *       current value of 'fieldsToRetrieve'
     */
    public Optional<Set<String>> getFieldsToRetrieve() {
        return Optional.ofNullable(fieldsToRetrieve);
    }
    
}
