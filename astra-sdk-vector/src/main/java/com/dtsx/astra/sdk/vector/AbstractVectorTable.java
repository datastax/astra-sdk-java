package com.dtsx.astra.sdk.vector;

import com.datastax.oss.driver.api.core.CqlSession;

public abstract class AbstractVectorTable {

    public static final String SAI_INDEX_CLASSNAME = "org.apache.cassandra.index.sai.StorageAttachedIndex";

    /** Session to Cassandra. */
    protected final CqlSession cqlSession;

    protected final String keyspaceName;

    protected final String tableName;

    public AbstractVectorTable(CqlSession session, String keyspaceName, String tableName) {
        this.cqlSession = session;
        this.keyspaceName = keyspaceName;
        this.tableName = tableName;
    }

    protected void delete() {
        cqlSession.execute("DROP TABLE IF EXISTS " + tableName);
    }

    protected void clear() {
        cqlSession.execute("TRUNCATE TABLE " + tableName);
    }

}
