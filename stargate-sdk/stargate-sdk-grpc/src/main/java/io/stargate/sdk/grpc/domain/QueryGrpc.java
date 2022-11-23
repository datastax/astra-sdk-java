package io.stargate.sdk.grpc.domain;

import io.stargate.proto.QueryOuterClass;

import java.util.List;
import java.util.Map;

/**
 * Represent a query againt GRPC enpoind
 */
public class QueryGrpc {

    /** query. */
    private final String cql;

    /** Default page size. */
    public  static final int DEFAULT_PAGE_SIZE = 5000;

    /** keyspace. */
    private String keyspace;

    /** consistency level. */
    private QueryOuterClass.Consistency consistencyLevel = QueryOuterClass.Consistency.LOCAL_QUORUM;

    /** page size. */
    private int pageSize = DEFAULT_PAGE_SIZE;

    /** query param 1. */
    private List<Object> positionalValues;

    /** query param 2. */
    private Map<String, Object> namedValues;

    /** query paging state. */
    private String pagingState;

    /**
     * Default constructor.
     *
     * @param cql
     *      current cql query
     */
    public QueryGrpc(String cql) {
        this.cql = cql;
    }

    /**
     * Builder setter.
     * @param cl
     *      consistency level
     * @return
     *      current reference
     */
    public QueryGrpc setConsistencyLevel(QueryOuterClass.Consistency cl) {
        this.consistencyLevel = cl;
        return this;
    }

    /**
     * Builder setter.
     * @param keyspace
     *      keyspace
     * @return
     *      current reference
     */
    public QueryGrpc setKeyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }

    /**
     * Builder setter.
     * @param pageSize
     *      page size
     * @return
     *      current reference
     */
    public QueryGrpc setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    /**
     * Builder setter.
     * @param positionalValues
     *      param values
     * @return
     *      current reference
     */
    public QueryGrpc setPositionalValues(List<Object> positionalValues) {
        this.positionalValues = positionalValues;
        return this;
    }

    /**
     * Builder setter.
     * @param namedValues
     *      param values
     * @return
     *      current reference
     */
    public QueryGrpc setNamedValues(Map<String, Object> namedValues) {
        this.namedValues = namedValues;
        return this;
    }

    /**
     * Builder setter.
     * @param pagingState
     *      value for pagingState
     * @return
     *      current reference
     */
    public QueryGrpc setPagingState(String pagingState) {
        this.pagingState = pagingState;
        return this;
    }
}
