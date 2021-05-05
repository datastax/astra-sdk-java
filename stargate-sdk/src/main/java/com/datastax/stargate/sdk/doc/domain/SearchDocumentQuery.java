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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.datastax.stargate.sdk.core.Filter;
import com.datastax.stargate.sdk.core.FilterCondition;
import com.datastax.stargate.sdk.rest.domain.SearchTableQuery;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Build a queyr with filter clause
 *
 * @author Cedrick LUNVEN (@clunven)
 * 
 * QueryDocument.builder()
 *              .withPageSize(in)
 *              .where("age").isGreaterThan(10)
 */
public class SearchDocumentQuery {
    
    /** Limit set for the API. */
    public static final int PAGING_SIZE_MAX     = 20;
    
    /** Number of records to retrieve on a page. MAXIMUM 100. */
    public static final int DEFAULT_PAGING_SIZE = 20;
    
    /** Page sixe. */ 
    private final int pageSize;
    
    /** Cursor for paging. */ 
    private final String pageState;
    
    /** Build where clause. */
    private final String where;
    
    /** If we want to filter on fields. */
    private final Set<String> fieldsToRetrieve;
    
    /**
     * Constructor hidden to enforce builder usage.
     * @param builder
     *      filled builder.
     */
    private SearchDocumentQuery(SearchDocumentQueryBuilder builder) {
        this.pageSize         = builder.pageSize;
        this.pageState        = builder.pageState;
        this.fieldsToRetrieve = builder.fields;
        // Note that the Json Where query is built here but not yet escaped
        this.where            = builder.getWhereClause();
    }
    
    /** static accees to a builder instance. */
    public static SearchDocumentQueryBuilder builder() {
        return new SearchDocumentQueryBuilder(); 
    }
    
    /**
     * Builder pattern.
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class SearchDocumentQueryBuilder {
        
        /** Page size. */ 
        protected int pageSize = DEFAULT_PAGING_SIZE;
        
        protected String pageState = null;
        
        protected Set<String> fields = null;
        
        /** 
         * One can provide the full where clause as a JSON String.
         * If not null it will be used and the filters will be ignored.
         */
        protected String whereClause;
        
        /**
         * Use to build the where Clause as a JsonString if the field 
         * whereClause is not provided.
         * - FieldName + condition + value
         */
        protected List<Filter> filters = new ArrayList<>();
        
        /**
         * Terminal call to build immutable instance of {@link SearchTableQuery}.
         *
         * @return
         *      immutable instance of {@link SearchTableQuery}.
         */
        public SearchDocumentQuery build() {
            return new SearchDocumentQuery(this);
        }
        
        public SearchDocumentQueryBuilder withPageSize(int pageSize) {
            if (pageSize < 1 || pageSize > PAGING_SIZE_MAX) {
                throw new IllegalArgumentException("Page size should be between 1 and 100");
            }
            this.pageSize = pageSize;
            return this;
        }
        
        public SearchDocumentQueryBuilder withPageState(String pageState) {
            Assert.hasLength(pageState, "pageState");
            this.pageState = pageState;
            return this;
        }
        
        /**
         * Only return those fields if provided
         */
        public SearchDocumentQueryBuilder withReturnedFields(String... fields) {
            Assert.notNull(fields, "fields");
            this.fields = new HashSet<>(Arrays.asList(fields));
            return this;
        }
        
        /**
         * Only return those fields if provided
         */
        public SearchDocumentQueryBuilder select(String... fields) {
            return withReturnedFields(fields);
        }
        
        /**
         * Use 'where" to help you create 
         */
        public SearchDocumentQueryBuilder withWhereClauseJson(String where) {
            if (this.whereClause != null) {
                throw new IllegalArgumentException("Only a single where clause is allowd in a query");
            }
            Assert.hasLength(where, "where");
            this.whereClause = where;
            return this;
        }
        
        /**
         * Only return those fields if provided
         */
        public SearchDocumentWhere where(String fieldName) {
            Assert.hasLength(fieldName, "fieldName");
            return new SearchDocumentWhere(this, fieldName);
        }
        
        /**
         * Build Where Clause based on Filters.
         *
         * @return
         */
        public String getWhereClause() {
            // Explicit values will got primer on filters
            if (Utils.hasLength(whereClause)) {
                return whereClause;
            }
            // Use Filters
            return "{" + filters.stream()
                    .map(Filter::toString)
                    .collect(Collectors.joining(",")) 
                    + "}";
        }
        
    }
    
    /**
     * Helper to build a where clause in natural language (fluent API)
     *
     * TODO the WHERE CLAUSE CAN HAVE MULTIPLE CRITERIA FOR A FIELD
     * where("field").greaterThan(40)
     *               .lessThan(50);
     */
    public static class SearchDocumentWhere {
        
        /** Required field name. */
        private final String fieldName;
        
        /** Working builder to override the 'where' field and move with builder. */
        private final SearchDocumentQueryBuilder builder;
        
        /**
         * Only constructor allowed
         */
        protected SearchDocumentWhere(SearchDocumentQueryBuilder builder, String fieldName) {
            this.builder   = builder;
            this.fieldName = fieldName;
        }
        private SearchDocumentQueryBuilder addFilter(FilterCondition op, Object value) {
            builder.filters.add(new Filter(fieldName,op, value));
            return builder;
        }
        
        public SearchDocumentQueryBuilder isLessThan(Object value) {
            return addFilter(FilterCondition.LessThan, value);
        }
        public SearchDocumentQueryBuilder isLessOrEqualsThan(Object value) {
            return addFilter(FilterCondition.LessThanOrEqualsTo, value);
        }
        public SearchDocumentQueryBuilder isGreaterThan(Object value) {
            return addFilter(FilterCondition.GreaterThan, value);
        }
        public SearchDocumentQueryBuilder isGreaterOrEqualsThan(Object value) {
            return addFilter(FilterCondition.GreaterThenOrEqualsTo, value);
        }
        public SearchDocumentQueryBuilder isEqualsTo(Object value) {
            return addFilter(FilterCondition.EqualsTo, value);
        }
        public SearchDocumentQueryBuilder isNotEqualsTo(Object value) {
            return addFilter(FilterCondition.NotEqualsTo, value);
        }
        public SearchDocumentQueryBuilder exists() {
            return addFilter(FilterCondition.Exists, null);
        }
        public SearchDocumentQueryBuilder isIn(Collection<Object> values) {
            return addFilter(FilterCondition.In, values);
        }
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
