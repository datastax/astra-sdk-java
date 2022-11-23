package io.stargate.sdk.doc.domain;

import java.util.Optional;
import java.util.Set;

/**
 * Bean used to create search at collection level (not paged).
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class Query {

    /** If we want to filter on fields. */
    protected Set<String> fieldsToRetrieve;
    
    /** Build where clause. */
    protected String where;
    
    /**
     * Explicit constructor.
     *
     * @param fieldsToRetrieve
     *      fields
     * @param where
     *      filter
     */
    protected Query(Set<String> fieldsToRetrieve, String where) {
        this.fieldsToRetrieve = fieldsToRetrieve;
        this.where            = where;
    }
    
    /**
     * Constructor hidden to enforce builder usage.
     *
     * @param builder
     *      filled builder.
     */
    public Query(QueryBuilder builder) {
        this.fieldsToRetrieve = builder.fields;
        this.where            = builder.getWhereClause();
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
    
    /**
     * static accees to a builder instance
     * 
     * @return SearchDocumentQueryBuilder
     */
    public static QueryBuilder builder() {
        return new QueryBuilder(); 
    }
    
}
