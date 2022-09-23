package com.datastax.stargate.sdk.doc.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.datastax.stargate.sdk.core.Filter;
import com.datastax.stargate.sdk.rest.domain.SearchTableQuery;
import com.datastax.stargate.sdk.utils.Assert;
import com.datastax.stargate.sdk.utils.Utils;

/**
 * Helper to build queries
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class QueryBuilder {
    
    /** Fields to search. */ 
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
    public Query build() {
        return new Query(this);
    }
    
    /**
     * Only return those fields if provided
     * 
     * @param fields String
     * @return SearchDocumentQueryBuilder
     */
    public QueryBuilder select(String... fields) {
        Assert.notNull(fields, "fields");
        this.fields = new HashSet<>(Arrays.asList(fields));
        return this;
    }
    
    /**
     * Keep fields null but convenient for fluent api.
     * @return
     *      current query
     */
    public QueryBuilder selectAll() {
        this.fields = null;
        return this;
    }
    
    /**
     * Use 'where" to help you create 
     * 
     * @param where String
     * @return SearchDocumentQueryBuilder
     */
    public QueryBuilder jsonWhere(String where) {
        if (this.whereClause != null) {
            throw new IllegalArgumentException("Only a single where clause is allowd in a query");
        }
        Assert.hasLength(where, "where");
        this.whereClause = where;
        return this;
    }
    
    /**
     * Only return those fields if provided.
     *
     * @param fieldName String
     * @return SearchDocumentWhere
     */
    public QueryBuilderFilter where(String fieldName) {
        Assert.hasLength(fieldName, "fieldName");
        if (!filters.isEmpty()) {
            throw new IllegalArgumentException("Invalid query please use and() as a where clause has been provided");
        }
        return new QueryBuilderFilter(this, fieldName);
    }
    
    /**
     * Only return those fields if provided
     * @param fieldName String
     * @return SearchDocumentWhere
     */
    public QueryBuilderFilter and(String fieldName) {
        Assert.hasLength(fieldName, "fieldName");
        if (filters.isEmpty()) {
            throw new IllegalArgumentException("Invalid query please use where() as you first condition");
        }
        return new QueryBuilderFilter(this, fieldName);
    }
    
    /**
     * Build Where Clause based on Filters.
     *
     * @return String
     */
    public String getWhereClause() {
        // Explicit values (withWhereClause(0) will got priority on filters
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
