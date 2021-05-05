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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.datastax.stargate.sdk.core.Filter;
import com.datastax.stargate.sdk.core.FilterCondition;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Build a queyr with filter clause
 *
 * @author Cedrick LUNVEN (@clunven)
 * 
 * QueryDocument.builder()
 *              .withPageSize(in)
 *              .where("").isGreatherThan("")
 *              .where("").isGreatherThan("")
 *              
 */
public class SearchTableQuery {
    
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
    private final List<String> fieldsToRetrieve;
    
    /** List of items to retrieve. */
    private final List<SortField> fieldsToSort;
    
    /**
     * static accees to a builder instance.
     * 
     * @return SearchTableQueryBuilder
     */
    public static SearchTableQueryBuilder builder() {
        return new SearchTableQueryBuilder(); 
    }
    
    /**
     * Constructor hidden to enforce builder usage.
     * @param builder
     *      filled builder.
     */
    private SearchTableQuery(SearchTableQueryBuilder builder) {
        this.pageSize         = builder.pageSize;
        this.pageState        = builder.pageState;
        this.fieldsToRetrieve = builder.fieldsToRetrieve;
        this.fieldsToSort     = builder.fieldsToSort;
        // Note that the Json Where query is built here but not yet escaped
        this.where            = builder.getWhereClause();
    }
    
    /**
     * Builder pattern.
     *
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class SearchTableQueryBuilder {
        
        /** Page size. */ 
        protected int pageSize = DEFAULT_PAGING_SIZE;
        
        /** First page does not need page state. */
        protected String pageState = null;
        
        /** If empty the API retrieve all fields SELECT *. */
        protected List<String> fieldsToRetrieve = new ArrayList<>();
        
        /** Help sorted by result. fieldName + ASC/DESC. */
        protected List<SortField> fieldsToSort = new ArrayList<>();
        
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
        public SearchTableQuery build() {
            return new SearchTableQuery(this);
        }
         
        /**
         * withPageSize
         * 
         * @param pageSize int
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder withPageSize(int pageSize) {
            if (pageSize < 1 || pageSize > PAGING_SIZE_MAX) {
                throw new IllegalArgumentException("Page size should be between 1 and 100");
            }
            this.pageSize = pageSize;
            return this;
        }
        
        /**
         * withPageState
         * 
         * @param pageState String
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder withPageState(String pageState) {
            Assert.hasLength(pageState, "pageState");
            this.pageState = pageState;
            return this;
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fields String
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder withReturnedFields(String... fields) {
            Assert.notNull(fields, "fields");
            this.fieldsToRetrieve = new ArrayList<>(Arrays.asList(fields));
            fieldsToRetrieve.remove("");
            fieldsToRetrieve.remove((String) null);
            return this;
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fields String
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder select(String... fields) {
            return withReturnedFields(fields);
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fields SortField
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder withSortedFields(SortField... fields) {
            Assert.notNull(fields, "fields");
            this.fieldsToSort = new ArrayList<>(Arrays.asList(fields));
            return this;
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fields SortField
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder sortBy(SortField... fields) {
            return withSortedFields(fields);
        }
        
        /**
         * Use 'where" to help you create
         * 
         * @param where String
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder withWhereClauseJson(String where) {
            if (this.whereClause != null) {
                throw new IllegalArgumentException("Only a single where clause is allowd in a query");
            }
            Assert.hasLength(where, "where");
            this.whereClause = where;
            return this;
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fieldName String
         * @return SearchTableWhere
         */
        public SearchTableWhere where(String fieldName) {
            Assert.hasLength(fieldName, "fieldName");
            return new SearchTableWhere(this, fieldName);
        }
        
        /**
         * Build Where Clause based on Filters.
         *
         * @return String
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
    public static class SearchTableWhere {
        
        /** Required field name. */
        private final String fieldName;
        
        /** Working builder to override the 'where' field and move with builder. */
        private final SearchTableQueryBuilder builder;
        
        /**
         * Only constructor allowed
         * 
         * @param builder SearchTableQueryBuilder
         * @param fieldName String
         */
        protected SearchTableWhere(SearchTableQueryBuilder builder, String fieldName) {
            this.builder   = builder;
            this.fieldName = fieldName;
        }
        
        /**
         * addFilter
         * 
         * @param op FilterCondition
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        private SearchTableQueryBuilder addFilter(FilterCondition op, Object value) {
            builder.filters.add(new Filter(fieldName,op, value));
            return builder;
        }
        
        /**
         * isLessThan
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder isLessThan(Object value) {
            return addFilter(FilterCondition.LessThan, value);
        }

        /**
         * isLessOrEqualsThan
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder isLessOrEqualsThan(Object value) {
            return addFilter(FilterCondition.LessThanOrEqualsTo, value);
        }

        /**
         * isGreaterThan
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder isGreaterThan(Object value) {
            return addFilter(FilterCondition.GreaterThan, value);
        }
        
        /**
         * isGreaterOrEqualsThan
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder isGreaterOrEqualsThan(Object value) {
            return addFilter(FilterCondition.GreaterThenOrEqualsTo, value);
        }

        /**
         * isEqualsTo
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder isEqualsTo(Object value) {
            return addFilter(FilterCondition.EqualsTo, value);
        }

        /**
         * isNotEqualsTo
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder isNotEqualsTo(Object value) {
            return addFilter(FilterCondition.NotEqualsTo, value);
        }
        
        /**
         * exists
         * 
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder exists() {
            return addFilter(FilterCondition.Exists, null);
        }

        /**
         * isIn
         * 
         * @param values Collection
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder isIn(Collection<Object> values) {
            return addFilter(FilterCondition.In, values);
        }

        /**
         * contains
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder contains(Object value) {
            return addFilter(FilterCondition.Contains, value);
        }

        /**
         * containsKey
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder containsKey(Object value) {
            return addFilter(FilterCondition.ContainsKey, value);
        }

        /**
         * containsEntry
         * 
         * @param value Object
         * @return SearchTableQueryBuilder
         */
        public SearchTableQueryBuilder containsEntry(Object value) {
            return addFilter(FilterCondition.ContainsEntry, value);
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
    public List<String> getFieldsToRetrieve() {
        return fieldsToRetrieve;
    }

    /**
     * Getter accessor for attribute 'fieldsToSort'.
     *
     * @return
     *       current value of 'fieldsToSort'
     */
    public List<SortField> getFieldsToSort() {
        return fieldsToSort;
    }


}
