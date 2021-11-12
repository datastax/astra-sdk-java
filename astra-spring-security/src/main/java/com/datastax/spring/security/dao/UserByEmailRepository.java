package com.datastax.spring.security.dao;

import static com.datastax.oss.driver.api.core.type.DataTypes.TEXT;
import static com.datastax.spring.security.dao.User.COLUMN_EMAIL;
import static com.datastax.spring.security.dao.User.COLUMN_ID;
import static com.datastax.spring.security.dao.UserByEmail.TABLE_USER_BY_EMAIL;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.data.cassandra.core.cql.PreparedStatementCreator;
import org.springframework.data.cassandra.core.cql.SimplePreparedStatementCreator;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;
import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

/**
 * Operations on {@link UserByEmail}.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Repository
public class UserByEmailRepository extends SimpleCassandraRepository<UserByEmail, String> {
    
    /** Reference to cqlTemplate. */
    private final CassandraOperations cassandraTemplate;
    
    /** Reference to cqlTemplate. */
    private final CqlOperations cqlOperations; 
    
    /** 
     * SELECT id FROM user_by_email where email = ?
     **/
    protected PreparedStatementCreator cqlPreparedFind = new SimplePreparedStatementCreator(QueryBuilder
            .selectFrom(TABLE_USER_BY_EMAIL).column(COLUMN_ID)
            .whereColumn(COLUMN_EMAIL)
            .isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /** 
     * INSERT INTO user_by_email (email, id) VALUES (?, ?)
     **/
    protected PreparedStatementCreator cqlPreparedInsert = new SimplePreparedStatementCreator(QueryBuilder
            .insertInto(TABLE_USER_BY_EMAIL)
            .value(COLUMN_EMAIL, QueryBuilder.bindMarker())
            .value(COLUMN_ID, QueryBuilder.bindMarker())
            .build());
      
    /** 
     * DELETE FROM user_by_email WHERE email = ?
     **/
    protected PreparedStatementCreator cqlPreparedDelete = new SimplePreparedStatementCreator(QueryBuilder
            .deleteFrom(TABLE_USER_BY_EMAIL)
            .whereColumn(COLUMN_EMAIL).isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /**
     * Spring Data repository constructor.
     * 
     * @param ceInfo
     *      entity information.
     * @param operations
     *      cassandra operation.
     */
    public UserByEmailRepository(CassandraEntityInformation<UserByEmail, String> ceInfo, CassandraOperations operations) {
        super(ceInfo, operations);
        this.cassandraTemplate = operations;
        this.cqlOperations     = cassandraTemplate.getCqlOperations();
    }
    
    /**
     * Create Table.
     */
    public void createTable() {
        cqlOperations.execute(SchemaBuilder
                .createTable(TABLE_USER_BY_EMAIL).ifNotExists()
                .withPartitionKey(COLUMN_EMAIL, TEXT)
                .withColumn(COLUMN_ID, TEXT)
                .build());
    }
    
    /**
     * Truncate Table.
     */
    public void truncateTable() {
        cqlOperations.execute(QueryBuilder
                .truncate(TABLE_USER_BY_EMAIL)
                .build());
    }
    
    /**
     * Drop Table.
     */
    public void dropTable() {
        cqlOperations.execute(SchemaBuilder
                .dropTable(TABLE_USER_BY_EMAIL).ifExists()
                .build());
    }
    
}

