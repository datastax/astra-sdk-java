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

import org.springframework.data.cassandra.core.cql.PreparedStatementCreator;
import org.springframework.data.cassandra.core.cql.SimplePreparedStatementCreator;

import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;

/**
 * Group Schema constants and queries.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public interface UserSchema {
    
    /** Table names. */
    String TABLE_USER                   = "user_by_id";
    
    /** Table names. */
    String TABLE_USER_BY_EMAIL          = "user_by_email";
    
    /** Table names. */
    String TABLE_USER_BY_LOGIN          = "user_by_login";
    
    /** Table names. */
    String TABLE_USER_BY_ACTIVATION_KEY = "user_by_activation_key";
    
    /** Table names. */
    String TABLE_USER_BY_RESET_KEY      = "user_by_reset_key";
    
    /** Table names. */
    String TABLE_KEY_BY_CREATION_DATE   = "activation_key_by_creation_date";
    
    // ---------------------------------------------------------
    // TABLE: user_by_id
    // ---------------------------------------------------------
    
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
     *   authorities set<text>,
     *   PRIMARY KEY(id)
     * );
     */
    SimpleStatement cqlUserCreateTable = SchemaBuilder
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
            .build();
    
    /**
     * TRUNCATE user_by_id;
     */
    SimpleStatement cqlUserTruncateTable = QueryBuilder
            .truncate(TABLE_USER).build();
    
    /**
     * DROP TABLE user_by_id;
     */
    SimpleStatement cqlUserDropTable = SchemaBuilder
            .dropTable(TABLE_USER).ifExists()
            .build();
    
    // ---------------------------------------------------------
    // TABLE: user_by_login
    // ---------------------------------------------------------
    
    /**
     * CREATE TABLE IF NOT EXISTS user_by_login (
     *  login text,
     *  id text,
     *  PRIMARY KEY(login)
     * );
     */
    SimpleStatement cqlUserByLoginCreateTable = SchemaBuilder
            .createTable(TABLE_USER_BY_LOGIN).ifNotExists()
            .withPartitionKey(COLUMN_LOGIN, TEXT)
            .withColumn(COLUMN_ID, TEXT)
            .build();
    
    /**
     * DROP TABLE user_by_login;
     */
    SimpleStatement cqlUserByLoginDropTable = SchemaBuilder
            .dropTable(TABLE_USER_BY_LOGIN).ifExists()
            .build();
            
    /**
     * TRUNCATE user_by_login;
     */
    SimpleStatement cqlUserByLoginTruncateTable = QueryBuilder
            .truncate(TABLE_USER_BY_LOGIN).build();
    
    /** 
     * SELECT id FROM user_by_login where login = ?
     **/
    PreparedStatementCreator cqlUserByLoginFind = new SimplePreparedStatementCreator(QueryBuilder
            .selectFrom(TABLE_USER_BY_LOGIN).column(COLUMN_ID)
            .whereColumn(COLUMN_LOGIN)
            .isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /** 
     * INSERT INTO user_by_login (login, id) VALUES (?, ?)
     **/
    PreparedStatementCreator cqlUserByLoginInsert = new SimplePreparedStatementCreator(QueryBuilder
            .insertInto(TABLE_USER_BY_LOGIN)
            .value(COLUMN_LOGIN, QueryBuilder.bindMarker())
            .value(COLUMN_ID, QueryBuilder.bindMarker())
            .build());
      
    /** 
     * DELETE FROM user_by_login WHERE login = ?
     **/
    PreparedStatementCreator cqlUserByLoginDelete = new SimplePreparedStatementCreator(QueryBuilder
            .deleteFrom(TABLE_USER_BY_LOGIN)
            .whereColumn(COLUMN_LOGIN).isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    // ---------------------------------------------------------
    // TABLE: user_by_email
    // ---------------------------------------------------------
    
    /**
     * CREATE TABLE IF NOT EXISTS user_by_email (
     *  email text,
     *  id text,
     *  PRIMARY KEY(email)
     * );
     */
    SimpleStatement cqlUserByEmailCreateTable = SchemaBuilder
            .createTable(TABLE_USER_BY_EMAIL).ifNotExists()
            .withPartitionKey(COLUMN_EMAIL, TEXT)
            .withColumn(COLUMN_ID, TEXT)
            .build();
    
    /**
     * TRUNCATE user_by_email;
     */
    SimpleStatement cqlUserByEmailTruncateTable = QueryBuilder
            .truncate(TABLE_USER_BY_EMAIL).build();
    
    /**
     * DROP TABLE user_by_email;
     */
    SimpleStatement cqlUserByEmailDropTable = SchemaBuilder
            .dropTable(TABLE_USER_BY_EMAIL).ifExists()
            .build();
    
    /** 
     * SELECT id FROM user_by_email where email = ?
     **/
    PreparedStatementCreator cqlUserByEmailFind = new SimplePreparedStatementCreator(QueryBuilder
            .selectFrom(TABLE_USER_BY_EMAIL).column(COLUMN_ID)
            .whereColumn(COLUMN_EMAIL)
            .isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /** 
     * INSERT INTO user_by_email (email, id) VALUES (?, ?)
     **/
    PreparedStatementCreator cqlUserByEmailInsert = new SimplePreparedStatementCreator(QueryBuilder
            .insertInto(TABLE_USER_BY_EMAIL)
            .value(COLUMN_EMAIL, QueryBuilder.bindMarker())
            .value(COLUMN_ID, QueryBuilder.bindMarker())
            .build());
      
    /** 
     * DELETE FROM user_by_email WHERE email = ?
     **/
    PreparedStatementCreator cqlUserByEmailDelete = new SimplePreparedStatementCreator(QueryBuilder
            .deleteFrom(TABLE_USER_BY_EMAIL)
            .whereColumn(COLUMN_EMAIL).isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    // ---------------------------------------------------------
    // TABLE: user_by_activation_key
    // ---------------------------------------------------------
    
    /**
     * CREATE TABLE IF NOT EXISTS user_by_activation_key (
     *  activation_key text,
     *  id text,
     *  PRIMARY KEY(activation_key)
     * );
     */
    SimpleStatement cqlUserByActivationKeyCreateTable = SchemaBuilder
            .createTable(TABLE_USER_BY_ACTIVATION_KEY).ifNotExists()
            .withPartitionKey(COLUMN_ACTIVATION_KEY, TEXT)
            .withColumn(COLUMN_ID, TEXT)
            .build();
    
    /** 
     * TRUNCATE user_by_activation_key
     **/
    SimpleStatement cqlUserByActivationKeyTruncateTable = QueryBuilder
            .truncate(TABLE_USER_BY_ACTIVATION_KEY).build();
    
    /**
     * DROP TABLE user_by_activation_key;
     */
    SimpleStatement cqlUserByActivationKeyDropTable = SchemaBuilder
            .dropTable(TABLE_USER_BY_ACTIVATION_KEY).ifExists()
            .build();
    
    /** 
     * SELECT id FROM user_by_activation_key where activation_key = ?
     **/
    PreparedStatementCreator cqlUserByActivationKeyFind= new SimplePreparedStatementCreator(QueryBuilder
            .selectFrom(TABLE_USER_BY_ACTIVATION_KEY).column(COLUMN_ID)
            .whereColumn(COLUMN_ACTIVATION_KEY)
            .isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /** 
     * INSERT INTO user_by_activation_key (activation_key, id) VALUES (?, ?)
     **/
    PreparedStatementCreator cqlUserByActivationKeyInsert = new SimplePreparedStatementCreator(QueryBuilder
            .insertInto(TABLE_USER_BY_ACTIVATION_KEY)
            .value(COLUMN_ACTIVATION_KEY, QueryBuilder.bindMarker())
            .value(COLUMN_ID, QueryBuilder.bindMarker())
            .build());
      
    /** 
     * DELETE FROM user_by_activation_key WHERE activation_key = ?
     **/
    PreparedStatementCreator cqlUserByActivationKeyDelete = new SimplePreparedStatementCreator(QueryBuilder
            .deleteFrom(TABLE_USER_BY_ACTIVATION_KEY)
            .whereColumn(COLUMN_ACTIVATION_KEY).isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    // ---------------------------------------------------------
    // TABLE: user_by_reset_key
    // ---------------------------------------------------------
    
    /**
     * CREATE TABLE IF NOT EXISTS user_by_reset_key (
     *  reset_key text,
     *  id text,
     *  PRIMARY KEY(reset_key)
     * );
     */
    SimpleStatement cqlUserByResetKeyCreateTable = SchemaBuilder
            .createTable(TABLE_USER_BY_RESET_KEY).ifNotExists()
            .withPartitionKey(COLUMN_RESET_KEY, TEXT)
            .withColumn(COLUMN_ID, TEXT)
            .build();
    
    /**
     * TRUNCATE user_by_reset_key;
     */
    SimpleStatement cqlUserByResetKeyTruncateTable = QueryBuilder
            .truncate(TABLE_USER_BY_RESET_KEY).build();
    
    /**
     * DROP TABLE user_by_reset_key;
     */
    SimpleStatement cqlUserByResetKeyDropTable = SchemaBuilder
            .dropTable(TABLE_USER_BY_RESET_KEY).ifExists()
            .build();
    
    /** 
     * SELECT id FROM user_by_reset_key where reset_key = ?
     **/
    PreparedStatementCreator cqlUserByResetKeyFind = new SimplePreparedStatementCreator(QueryBuilder
            .selectFrom(TABLE_USER_BY_RESET_KEY).column(COLUMN_ID)
            .whereColumn(COLUMN_RESET_KEY)
            .isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    /** 
     * INSERT INTO user_by_reset_key (reset_key, id) VALUES (?, ?)
     **/
    PreparedStatementCreator cqlUserByResetKeyInsert = new SimplePreparedStatementCreator(QueryBuilder
            .insertInto(TABLE_USER_BY_RESET_KEY)
            .value(COLUMN_RESET_KEY, QueryBuilder.bindMarker())
            .value(COLUMN_ID, QueryBuilder.bindMarker())
            .build());
      
    /** 
     * DELETE FROM user_by_reset_key WHERE reset_key = ?
     **/
    PreparedStatementCreator cqlUserByResetKeyDelete = new SimplePreparedStatementCreator(QueryBuilder
            .deleteFrom(TABLE_USER_BY_RESET_KEY)
            .whereColumn(COLUMN_RESET_KEY).isEqualTo(QueryBuilder.bindMarker())
            .build());
    
    // ---------------------------------------------------------
    // TABLE: activation_key_by_creation_date
    // ---------------------------------------------------------
    
    /**
     * CREATE TABLE IF NOT EXISTS activation_key_by_creation_date (
     *   creation_date timeuuid,
     *   activation_key text,
     *   PRIMARY KEY(creation_date)
     * );
     */
    SimpleStatement cqlActivationKeyByCreationDateCreateTable = SchemaBuilder
            .createTable(TABLE_KEY_BY_CREATION_DATE).ifNotExists()
            .withPartitionKey(User.COLUMN_CREATION_DATE, DataTypes.TIMEUUID)
            .withColumn(COLUMN_ACTIVATION_KEY, TEXT)
            .build();
    
    /**
     * TRUNCATE activation_key_by_creation_date;
     */
    SimpleStatement cqlActivationKeyByCreationDateTruncateTable = QueryBuilder
            .truncate(TABLE_KEY_BY_CREATION_DATE).build();
    
    /**
     * DROP TABLE activation_key_by_creation_date;
     */
    SimpleStatement cqlActivationKeyByCreationDateDropTable = SchemaBuilder
            .dropTable(TABLE_KEY_BY_CREATION_DATE).ifExists()
            .build();

}
