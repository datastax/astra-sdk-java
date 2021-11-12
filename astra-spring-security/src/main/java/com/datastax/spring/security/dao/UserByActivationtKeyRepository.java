package com.datastax.spring.security.dao;

import static com.datastax.oss.driver.api.core.type.DataTypes.TEXT;
import static com.datastax.spring.security.dao.User.COLUMN_ACTIVATION_KEY;
import static com.datastax.spring.security.dao.User.COLUMN_ID;
import static com.datastax.spring.security.dao.UserByActivationKey.TABLE_USER_BY_ACTIVATION_KEY;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.data.cassandra.core.cql.PreparedStatementCreator;
import org.springframework.data.cassandra.core.cql.SimplePreparedStatementCreator;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

public class UserByActivationtKeyRepository extends SimpleCassandraRepository<UserByActivationKey, String> {
    
    /** Reference to cqlTemplate. */
    private final CassandraOperations cassandraTemplate;
    
    /** Reference to cqlTemplate. */
    private final CqlOperations cqlOperations;
    
    /** 
     * SELECT id FROM user_by_activation_key where activation_key = ?
     **/
    protected PreparedStatementCreator cqlUserByActivationKeyFind= new SimplePreparedStatementCreator(QueryBuilder
           .selectFrom(TABLE_USER_BY_ACTIVATION_KEY).column(COLUMN_ID)
           .whereColumn(COLUMN_ACTIVATION_KEY)
           .isEqualTo(QueryBuilder.bindMarker())
           .build());
   
   /** 
    * INSERT INTO user_by_activation_key (activation_key, id) VALUES (?, ?)
    **/
   protected PreparedStatementCreator cqlUserByActivationKeyInsert = new SimplePreparedStatementCreator(QueryBuilder
           .insertInto(TABLE_USER_BY_ACTIVATION_KEY)
           .value(COLUMN_ACTIVATION_KEY, QueryBuilder.bindMarker())
           .value(COLUMN_ID, QueryBuilder.bindMarker())
           .build());
     
   /** 
    * DELETE FROM user_by_activation_key WHERE activation_key = ?
    **/
   protected PreparedStatementCreator cqlUserByActivationKeyDelete = new SimplePreparedStatementCreator(QueryBuilder
           .deleteFrom(TABLE_USER_BY_ACTIVATION_KEY)
           .whereColumn(COLUMN_ACTIVATION_KEY).isEqualTo(QueryBuilder.bindMarker())
           .build());
    
    /**
     * Spring Data repository constructor.
     * 
     * @param ceInfo
     *      entity information.
     * @param operations
     *      cassandra operation.
     */
    public UserByActivationtKeyRepository(CassandraEntityInformation<UserByActivationKey, String> ceInfo, CassandraOperations operations) {
        super(ceInfo, operations);
        this.cassandraTemplate = operations;
        this.cqlOperations     = cassandraTemplate.getCqlOperations();
    }
    

    /**
     * CREATE TABLE IF NOT EXISTS user_by_activation_key (
     *  reset_key text,
     *  id text,
     *  PRIMARY KEY(reset_key)
     * );
     */
    public void createTable() {
        cqlOperations.execute(SchemaBuilder
                .createTable(TABLE_USER_BY_ACTIVATION_KEY).ifNotExists()
                .withPartitionKey(COLUMN_ACTIVATION_KEY, TEXT)
                .withColumn(COLUMN_ID, TEXT)
                .build());
    }
    
    /**
     * TRUNCATE user_by_activation_key;
     */
    public void truncateTable() {
        cqlOperations.execute(QueryBuilder
                .truncate(TABLE_USER_BY_ACTIVATION_KEY)
                .build());
    }
    
    /**
     * DROP TABLE user_by_activation_key;
     */
    public void dropTable() {
        cqlOperations.execute(SchemaBuilder
                .dropTable(TABLE_USER_BY_ACTIVATION_KEY).ifExists()
                .build());
    }
    
}
