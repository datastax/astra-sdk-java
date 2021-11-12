package com.datastax.spring.security.dao;

import static com.datastax.oss.driver.api.core.type.DataTypes.TEXT;
import static com.datastax.spring.security.dao.User.COLUMN_ID;
import static com.datastax.spring.security.dao.User.COLUMN_RESET_KEY;
import static com.datastax.spring.security.dao.UserByResetKey.TABLE_USER_BY_RESET_KEY;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.data.cassandra.core.cql.PreparedStatementCreator;
import org.springframework.data.cassandra.core.cql.SimplePreparedStatementCreator;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public class UserByResetKeyRepository extends SimpleCassandraRepository<UserByResetKey, String> {
    
    /** Reference to cqlTemplate. */
    private final CassandraOperations cassandraTemplate;
    
    /** Reference to cqlTemplate. */
    private final CqlOperations cqlOperations; 
    
    /** 
     * SELECT id FROM user_by_reset_key where reset_key = ?
     **/
    protected PreparedStatementCreator cqlUserByResetKeyFind = new SimplePreparedStatementCreator(QueryBuilder
            .selectFrom(TABLE_USER_BY_RESET_KEY).column(COLUMN_ID)
            .whereColumn(COLUMN_RESET_KEY)
            .isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /** 
     * INSERT INTO user_by_reset_key (reset_key, id) VALUES (?, ?)
     **/
    protected PreparedStatementCreator cqlUserByResetKeyInsert = new SimplePreparedStatementCreator(QueryBuilder
            .insertInto(TABLE_USER_BY_RESET_KEY)
            .value(COLUMN_RESET_KEY, QueryBuilder.bindMarker())
            .value(COLUMN_ID, QueryBuilder.bindMarker())
            .build());
      
    /** 
     * DELETE FROM user_by_reset_key WHERE reset_key = ?
     **/
    protected PreparedStatementCreator cqlUserByResetKeyDelete = new SimplePreparedStatementCreator(QueryBuilder
            .deleteFrom(TABLE_USER_BY_RESET_KEY)
            .whereColumn(COLUMN_RESET_KEY).isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /**
     * Spring Data repository constructor.
     * 
     * @param ceInfo
     *      entity information.
     * @param operations
     *      cassandra operation.
     */
    public UserByResetKeyRepository(CassandraEntityInformation<UserByResetKey, String> ceInfo, CassandraOperations operations) {
        super(ceInfo, operations);
        this.cassandraTemplate = operations;
        this.cqlOperations     = cassandraTemplate.getCqlOperations();
    }
    

    /**
     * CREATE TABLE IF NOT EXISTS user_by_reset_key (
     *  reset_key text,
     *  id text,
     *  PRIMARY KEY(reset_key)
     * );
     */
    public void createTable() {
        cqlOperations.execute(SchemaBuilder
                .createTable(TABLE_USER_BY_RESET_KEY).ifNotExists()
                .withPartitionKey(COLUMN_RESET_KEY, TEXT)
                .withColumn(COLUMN_ID, TEXT)
                .build());
    }
    
    /**
     * TRUNCATE user_by_reset_key;
     */
    public void truncateTable() {
        cqlOperations.execute(QueryBuilder
                .truncate(TABLE_USER_BY_RESET_KEY)
                .build());
    }
    
    /**
     * DROP TABLE user_by_reset_key;
     */
    public void dropTable() {
        cqlOperations.execute(SchemaBuilder
                .dropTable(TABLE_USER_BY_RESET_KEY).ifExists()
                .build());
    }
    
}
