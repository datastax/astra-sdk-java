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

package io.stargate.sdk.rest.domain;

import io.stargate.sdk.core.Ordering;
import io.stargate.sdk.core.Sort;
import io.stargate.sdk.utils.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Build a queyr with filter clause
 *
 * @author Cedrick LUNVEN (@clunven)
 * 
 * QueryDocument.builder()
 *              .withPageSize(in)
 *              .where("age").isGreaterThan(10)
 */
public class QueryWithKey {
    
    /** Limit set for the API. */
    public static final int PAGING_SIZE_MAX     = 20;
    
    /** Number of records to retrieve on a page. MAXIMUM 100. */
    public static final int DEFAULT_PAGING_SIZE = 20;
    
    /** Page sixe. */ 
    private final int pageSize;
    
    /** Cursor for paging. */ 
    private final String pageState;
    
    /** If we want to filter on fields. */
    private final List<String> fieldsToRetrieve;
    
    /** List of items to retrieve. */
    private final List<Sort> fieldsToSort;
    
    /**
     * builder
     * 
     * @return QueryRowBuilder
     */
    public static QueryRowBuilder builder() {
        return new QueryRowBuilder(); 
    }
    
    /**
     * QueryWithKey
     * 
     * @param builder QueryRowBuilder
     */
    private QueryWithKey(QueryRowBuilder builder) {
        this.pageSize         = builder.pageSize;
        this.pageState        = builder.pageState;
        this.fieldsToRetrieve = builder.fieldsToRetrieve;
        this.fieldsToSort     = builder.fieldsToSort;
    }
    
    /**
     * Builder pattern.
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class QueryRowBuilder {
        
        /** Page size. */ 
        protected int pageSize = DEFAULT_PAGING_SIZE;
        
        /** Page state. */ 
        protected String pageState = null;
        
        /** Fields. */ 
        protected List<String> fieldsToRetrieve = new ArrayList<>();
        
        /** Fields. */ 
        protected List<Sort> fieldsToSort = new ArrayList<>();
        
        /**
         * build
         * 
         * @return QueryWithKey
         */
        public QueryWithKey build() {
            return new QueryWithKey(this);
        }
        
        /**
         * withPageSize
         * 
         * @param pageSize int
         * @return QueryRowBuilder
         */
        public QueryRowBuilder withPageSize(int pageSize) {
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
         * @return QueryRowBuilder
         */
        public QueryRowBuilder withPageState(String pageState) {
            Assert.hasLength(pageState, "pageState");
            this.pageState = pageState;
            return this;
        }
        
        
        /**
         * Only return those fields if provided
         * 
         * @param fields String
         * @return QueryRowBuilder
         */
        public QueryRowBuilder returnedFields(String... fields) {
            Assert.notNull(fields, "fields");
            this.fieldsToRetrieve = new ArrayList<>(Arrays.asList(fields));
            return this;
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fieldname String
         * @return QueryRowBuilder
         */
        public QueryRowBuilder addReturnedField(String fieldname) {
            Assert.hasLength(fieldname, "fieldname");
            this.fieldsToRetrieve.add(fieldname);
            return this;
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fields SortField
         * @return QueryRowBuilder
         */
        public QueryRowBuilder sortedFields(Sort... fields) {
            Assert.notNull(fields, "fields");
            this.fieldsToSort = new ArrayList<>(Arrays.asList(fields));
            return this;
        }
        
        /**
         * Only return those fields if provided
         * 
         * @param fieldname String
         * @param order Ordering
         * @return QueryRowBuilder
         */
        public QueryRowBuilder addSortedField(String fieldname, Ordering order) {
            Assert.hasLength(fieldname, "fieldname");
            Assert.notNull(order, "order");
            this.fieldsToSort.add(new Sort(fieldname, order));
            return this;
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
    public List<Sort> getFieldsToSort() {
        return fieldsToSort;
    }
}
