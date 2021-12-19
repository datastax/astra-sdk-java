package com.datastax.yasa.user;

import static com.datastax.oss.driver.api.core.type.DataTypes.BOOLEAN;
import static com.datastax.oss.driver.api.core.type.DataTypes.TEXT;
import static com.datastax.oss.driver.api.core.type.DataTypes.TIMESTAMP;
import static com.datastax.oss.driver.api.core.type.DataTypes.setOf;
import static com.datastax.yasa.user.User.COLUMN_ACTIVATED;
import static com.datastax.yasa.user.User.COLUMN_ACTIVATION_KEY;
import static com.datastax.yasa.user.User.COLUMN_AUTHORITIES;
import static com.datastax.yasa.user.User.COLUMN_EMAIL;
import static com.datastax.yasa.user.User.COLUMN_FIRSTNAME;
import static com.datastax.yasa.user.User.COLUMN_LANG_KEY;
import static com.datastax.yasa.user.User.COLUMN_LASTNAME;
import static com.datastax.yasa.user.User.COLUMN_PASSWORD;
import static com.datastax.yasa.user.User.COLUMN_PICTURE;
import static com.datastax.yasa.user.User.COLUMN_RESET_DATE;
import static com.datastax.yasa.user.User.COLUMN_RESET_KEY;
import static com.datastax.yasa.user.User.TABLE_NAME;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.SimpleCassandraRepository;
import org.springframework.stereotype.Repository;

import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

/**
 * Implements operations on users.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Repository
public class UserRepository extends SimpleCassandraRepository<User, String> {
    
    /** Reference to cqlTemplate. */
    private final CassandraOperations cassandraTemplate;
 
    /** Reference to cqlTemplate. */
    private final CqlOperations cqlOperations; 
    
    /**
     * Available constructor.
     *
     * @param ceInfo
     *      entity mapping
     * @param operations
     *      access to cqlSession
     */
    public UserRepository(CassandraEntityInformation<User, String> ceInfo, CassandraOperations operations) {
        super(ceInfo, operations);
        this.cassandraTemplate = operations;
        this.cqlOperations     = cassandraTemplate.getCqlOperations();
    }
    
    /**
     * create user_by_id;
     */
    public void createTable() {
        cqlOperations.execute(SchemaBuilder
                .createTable(TABLE_NAME).ifNotExists()
                .withPartitionKey(COLUMN_EMAIL, TEXT)
                .withColumn(COLUMN_PASSWORD, TEXT)
                .withColumn(COLUMN_FIRSTNAME, TEXT)
                .withColumn(COLUMN_LASTNAME, TEXT)
                .withColumn(COLUMN_ACTIVATED, BOOLEAN)
                .withColumn(COLUMN_LANG_KEY, TEXT)
                .withColumn(COLUMN_PICTURE, TEXT)
                .withColumn(COLUMN_ACTIVATION_KEY, TEXT)
                .withColumn(COLUMN_RESET_KEY, TEXT)
                .withColumn(COLUMN_RESET_DATE, TIMESTAMP)
                .withColumn(COLUMN_AUTHORITIES, setOf(TEXT))
                .build());
    }
    
    /**
     * TRUNCATE user_by_id;
     */
    public void truncateTable() {
        cqlOperations.execute(QueryBuilder.truncate(User.TABLE_NAME).build());
    }
    
    /**
     * DROP TABLE user_by_id;
     */
    public void dropTable() {
        cqlOperations.execute(SchemaBuilder.dropTable(User.TABLE_NAME).ifExists().build());
    }
    
}
