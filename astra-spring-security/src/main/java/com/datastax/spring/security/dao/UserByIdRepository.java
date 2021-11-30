package com.datastax.spring.security.dao;

import static com.datastax.oss.driver.api.core.type.DataTypes.BOOLEAN;
import static com.datastax.oss.driver.api.core.type.DataTypes.TEXT;
import static com.datastax.oss.driver.api.core.type.DataTypes.TIMESTAMP;
import static com.datastax.oss.driver.api.core.type.DataTypes.setOf;
import static com.datastax.spring.security.dao.User.COLUMN_ACTIVATED;
import static com.datastax.spring.security.dao.User.COLUMN_ACTIVATION_KEY;
import static com.datastax.spring.security.dao.User.COLUMN_AUTHORITIES;
import static com.datastax.spring.security.dao.User.COLUMN_EMAIL;
import static com.datastax.spring.security.dao.User.COLUMN_FIRSTNAME;
import static com.datastax.spring.security.dao.User.COLUMN_ID;
import static com.datastax.spring.security.dao.User.COLUMN_LANG_KEY;
import static com.datastax.spring.security.dao.User.COLUMN_LASTNAME;
import static com.datastax.spring.security.dao.User.COLUMN_LOGIN;
import static com.datastax.spring.security.dao.User.COLUMN_PASSWORD;
import static com.datastax.spring.security.dao.User.COLUMN_RESET_DATE;
import static com.datastax.spring.security.dao.User.COLUMN_RESET_KEY;

import java.util.List;
import java.util.Optional;

import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.cql.CqlOperations;
import org.springframework.data.cassandra.core.cql.PreparedStatementCreator;
import org.springframework.data.cassandra.core.cql.SingleColumnRowMapper;
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
public class UserByIdRepository extends SimpleCassandraRepository<User, String> implements UserSchema {
    
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
    public UserByIdRepository(
            CassandraEntityInformation<User, String> ceInfo, 
            CassandraOperations operations) {
        super(ceInfo, operations);
        this.cassandraTemplate = operations;
        this.cqlOperations     = cassandraTemplate.getCqlOperations();
    }
    
    /**
     * Find a user by its activation key.
     *
     * @param activationKey
     *      current activation key.
     * @return
     *      user
     */
    public Optional<User> findOneByActivationKey(String activationKey) {
        return findUser(cqlUserByActivationKeyFind, activationKey);
    }
    
    /**
     * Find user from a login.
     * 
     * @param login
     *      param login
     * @return
     *      user if exists.
     */
    public Optional<User> findOneByLogin(String login) {
        return findUser(cqlUserByLoginFind, login);
    }
    
    /**
     * Find user from an resetKey.
     * 
     * @param resetKey
     *      param resetKey
     * @return
     *      user if exists.
     */
    public Optional<User> findOneByResetKey(String resetKey) {
        return findUser(cqlUserByResetKeyFind, resetKey);
    }
    
    /**
     * Find user from an email.
     * 
     * @param email
     *      param email
     * @return
     *      user if exists.
     */
    public Optional<User> findOneByEmailIgnoreCase(String email) {
        return findUser(cqlUserByEmailFind,  email.toLowerCase());
    }
    
    /**
     * Retrieve a user based on a criteria.
     *
     * @param cql
     *      cql statement (prepared)
     * @param param
     *      cql param
     * @return
     *      a user of exist
     */
    private Optional<User> findUser(PreparedStatementCreator cql, String param) {
        // Retrieve list of ids with parama
        List<String> ids = cassandraTemplate.getCqlOperations().query(
                // Cql Statement to use
                cql, 
                // Binder to map param to ?
                ps -> ps.bind(param),
                // Result contain only ids mapping as String
                new SingleColumnRowMapper<String>(String.class));
        // We should get a single result
        if (null != ids && ids.size() == 1 ) {
            return findById(ids.get(0));
        }
        return Optional.empty();
    }
    
    /**
     * CREATE TABLE IF NOT EXISTS user_by_id (
     *   id text,
     *   login text,
     *   password text,
     *   firstname text,
     *   lastname text,
     *   email text,
     *   activated boolean,
     *   lang_key text,
     *   activation_key text,
     *   reset_key text,
     *   reset_date timestamp,
     *   authorities set &lt;text&lt;,
     *   PRIMARY KEY(id)
     * );
     */
    public void createTable() {
        cqlOperations.execute(SchemaBuilder
            .createTable(TABLE_USER).ifNotExists()
            .withPartitionKey(COLUMN_ID, TEXT)
            .withColumn(COLUMN_LOGIN, TEXT)
            .withColumn(COLUMN_PASSWORD, TEXT)
            .withColumn(COLUMN_FIRSTNAME, TEXT)
            .withColumn(COLUMN_LASTNAME, TEXT)
            .withColumn(COLUMN_EMAIL, TEXT)
            .withColumn(COLUMN_ACTIVATED, BOOLEAN)
            .withColumn(COLUMN_LANG_KEY, TEXT)
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
        cqlOperations.execute(QueryBuilder
                .truncate(TABLE_USER)
                .build());
    }
    
    /**
     * DROP TABLE user_by_id;
     */
    public void dropTable() {
        cqlOperations.execute(SchemaBuilder
                .dropTable(TABLE_USER).ifExists()
                .build());
    }
    
}
