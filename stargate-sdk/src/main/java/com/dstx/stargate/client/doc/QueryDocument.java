package com.dstx.stargate.client.doc;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.dstx.stargate.client.utils.Assert;
import com.dstx.stargate.client.utils.JsonUtils;

/**
 * Build a queyr with filter clause
 *
 * @author Cedrick LUNVEN (@clunven)
 * 
 * QueryDocument.builder()
 *              .withPageSize(in)
 *              .where("age").isGreaterThan(10)
 */
public class QueryDocument {
    
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
    
    // NOT WORKING
    //private String sort;
    //
    
    private QueryDocument(QueryDocumentBuilder builder) {
        this.pageSize         = builder.pageSize;
        this.pageState        = builder.pageState;
        this.where            = builder.whereClause;
        this.fieldsToRetrieve = builder.fields;
    }
    
    public static QueryDocumentBuilder builder() {
        return new QueryDocumentBuilder(); 
    }
    
    /**
     * Builder pattern.
     * @author Cedrick LUNVEN (@clunven)
     */
    public static class QueryDocumentBuilder {
        
        /** Page size. */ 
        protected int pageSize = DEFAULT_PAGING_SIZE;
        
        protected String pageState = null;
        
        protected Set<String> fields = null;
        
        /** Build where clause. */
        protected String whereClause;
        
        public QueryDocument build() {
            return new QueryDocument(this);
        }
        
        public QueryDocumentBuilder withPageSize(int pageSize) {
            if (pageSize < 1 || pageSize > PAGING_SIZE_MAX) {
                throw new IllegalArgumentException("Page size should be between 1 and 100");
            }
            this.pageSize = pageSize;
            return this;
        }
        
        public QueryDocumentBuilder withPageState(String pageState) {
            Assert.hasLength(pageState, "pageState");
            this.pageState = pageState;
            return this;
        }
        
        /**
         * Only return those fields if provided
         */
        public QueryDocumentBuilder withReturnedFields(String... fields) {
            Assert.notNull(fields, "fields");
            this.fields = new HashSet<>(Arrays.asList(fields));
            return this;
        }
        
        /**
         * Use 'where" to help you create 
         */
        public QueryDocumentBuilder withJsonWhereClause(String where) {
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
        public Where where(String fieldName) {
            Assert.hasLength(fieldName, "fieldName");
            return new Where(this, fieldName);
        }
        
    }
    
    /**
     * Helper to build a where clause in natural language (fluent API)
     *
     * TODO the WHERE CLAUSE CAN HAVE MULTIPLE CRITERIA FOR A FIELD
     * where("field").greaterThan(40)
     *               .lessThan(50);
     */
    public static class Where {
        
        /** Required field name. */
        private final String fieldName;
        
        /** Working builder to override the 'where' field and move with builder. */
        private final QueryDocumentBuilder builder;
        
        /**
         * Only constructor allowed
         */
        protected Where(QueryDocumentBuilder builder, String fieldName) {
            this.builder   = builder;
            this.fieldName = fieldName;
        }
       
        /**
         * Build where clause 'lt' and move back to builder.
         * A builder can only have a single where clause (not AND allowed)
         */
        public QueryDocumentBuilder isGreaterThan(double value) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$gt\": " 
                          + value + "}}");
        }
        public QueryDocumentBuilder isGreaterOrEqualsThan(double value) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$gte\": " 
                          + value + "}}");
        }
        public QueryDocumentBuilder isLessThan(double value) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$lt\": " 
                          + value + "}}");
        }
        public QueryDocumentBuilder isLessOrEqualsThan(double value) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$lte\": " 
                          + value + "}}");
        }
        
        /**
         * No list allow should be a scalar.
         * No contains keyword.
         */
        public QueryDocumentBuilder isEqualsTo(Object value) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$eq\": " 
                          + JsonUtils.valueAsJson(value) + "}}");
        }
        public QueryDocumentBuilder isNotEqualsTo(Object value) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$neq\": " 
                          + JsonUtils.valueAsJson(value) + "}}");
        }
        
        /**
         * No list allow should be a scalar.
         * No contains keyword.
         */
        public QueryDocumentBuilder isIn(Set<Object> values) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$in\": " 
                          + JsonUtils.collectionAsJson(values) + "}}");
        }
        
        /**
         * No list allow should be a scalar.
         * No contains keyword.
         */
        public QueryDocumentBuilder isNotIn(Set<Object> values) {
            return builder.withJsonWhereClause(
                    "{\"" + JsonUtils.escapeJson(fieldName) 
                          + "\": {\"$nin\": " 
                          + JsonUtils.collectionAsJson(values) + "}}");
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
