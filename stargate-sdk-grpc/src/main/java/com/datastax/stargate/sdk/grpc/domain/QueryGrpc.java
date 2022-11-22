package com.datastax.stargate.sdk.grpc.domain;

import io.stargate.proto.QueryOuterClass;

import java.util.List;
import java.util.Map;

public class QueryGrpc {

    public  static final int DEFAULT_PAGE_SIZE = 5000;

    private final String cql;

    private String keyspace;

    private QueryOuterClass.Consistency consistencyLevel = QueryOuterClass.Consistency.LOCAL_QUORUM;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private List<Object> positionalValues;

    private Map<String, Object> namedValues;

    private String pagingState;

    public QueryGrpc(String cql) {
        this.cql = cql;
    }

    public QueryGrpc setConsistencyLevel(QueryOuterClass.Consistency cl) {
        this.consistencyLevel = cl;
        return this;
    }

    public QueryGrpc setKeyspace(String keyspace) {
        this.keyspace = keyspace;
        return this;
    }

    public QueryGrpc setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public QueryGrpc setPositionalValues(List<Object> positionalValues) {
        this.positionalValues = positionalValues;
        return this;
    }

    public QueryGrpc setNamedValues(Map<String, Object> namedValues) {
        this.namedValues = namedValues;
        return this;
    }

    public QueryGrpc setPagingState(String pagingState) {
        this.pagingState = pagingState;
        return this;
    }
}
