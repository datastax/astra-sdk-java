package io.stargate.sdk.doc.domain;

import io.stargate.sdk.http.domain.Filter;
import io.stargate.sdk.http.domain.FilterCondition;

import java.util.Collection;

/**
 * Helper to build a where clause in natural language (fluent API).
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class PageableQueryBuilderFilter {
    
    /** Required field name. */
    private final String fieldName;
    
    /** Working builder to override the 'where' field and move with builder. */
    private final PageableQueryBuilder builder;
    
    /**
     * Only constructor allowed
     * 
     * @param builder SearchDocumentQueryBuilder
     * @param fieldName String
     */
    protected PageableQueryBuilderFilter(PageableQueryBuilder builder, String fieldName) {
        this.builder   = builder;
        this.fieldName = fieldName;
    }
    
    /**
     * Add a filter
     * @param op
     *      operation
     * @param value
     *      value
     * @return
     *      self reference
     */
    private PageableQueryBuilder addFilter(FilterCondition op, Object value) {
        builder.filters.add(new Filter(fieldName,op, value));
        return builder;
    }
    
    /**
     * Add condition is less than.
     *
     * @param value
     *      value
     * @return
     *      self reference
     */
    public PageableQueryBuilder isLessThan(Object value) {
        return addFilter(FilterCondition.LESS_THAN, value);
    }
    
    /**
     * Add condition is less than.
     *
     * @param value
     *      value
     * @return
     *      self reference
     */
    public PageableQueryBuilder isLessOrEqualsThan(Object value) {
        return addFilter(FilterCondition.LESS_THAN_OR_EQUALS_TO, value);
    }
    
    /**
     * Add condition is less than.
     *
     * @param value
     *      value
     * @return
     *      self reference
     */        
    public PageableQueryBuilder isGreaterThan(Object value) {
        return addFilter(FilterCondition.GREATER_THAN, value);
    }
    
    /**
     * Add condition is greater than.
     *
     * @param value
     *      value
     * @return
     *      self reference
     */        
    public PageableQueryBuilder isGreaterOrEqualsThan(Object value) {
        return addFilter(FilterCondition.GREATER_THAN_OR_EQUALS_TO, value);
    }
    
    /**
     * Add condition is is equals to.
     *
     * @param value
     *      value
     * @return
     *      self reference
     */        
    public PageableQueryBuilder isEqualsTo(Object value) {
        return addFilter(FilterCondition.EQUALS_TO, value);
    }
    
    /**
     * Add condition is not equals to.
     *
     * @param value
     *      value
     * @return
     *      self reference
     */        
    public PageableQueryBuilder isNotEqualsTo(Object value) {
        return addFilter(FilterCondition.NOT_EQUALS_TO, value);
    }
    
    /**
     * Add condition exists.
     *
     * @return
     *      self reference
     */
    public PageableQueryBuilder exists() {
        return addFilter(FilterCondition.EXISTS, null);
    }
    
    /**
     * Add condition is isIn.
     *
     * @param values
     *      values
     * @return
     *      self reference
     */
    public PageableQueryBuilder isIn(Collection<Object> values) {
        return addFilter(FilterCondition.IN, values);
    }

}
