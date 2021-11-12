package com.datastax.spring.security.dao;

import java.util.Optional;

import org.springframework.data.cassandra.core.CassandraBatchOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.datastax.oss.driver.api.core.cql.BatchType;

/**
 * Wrapper to interact with Tables related to User.
 * - UserByid
 * - UserByActivationKey
 * - UserByResetKey
 * - UserByLogin
 * - UserByEmail
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
@Repository
public class CassandraUserRepository {
    
    /** Reference to cqlTemplate. */
    private final CassandraOperations cassandraTemplate;
    
    /** work with user_by_id table. */
    private final UserByIdRepository userByIdRepo;
    
    /** work with user_by_login table. */
    private final UserByLoginRepository userByLoginRepo;
    
    /** work with user_by_email table. */
    private final UserByEmailRepository userByEmailRepo;
    
    /** work with user_by_email table. */
    private final UserByResetKeyRepository userByResetKeyRepo;
    
    /** work with user_by_email table. */
    private final UserByActivationtKeyRepository userByActivationKeyRepo;
    
    public CassandraUserRepository(
            CassandraOperations cassandraTemplate, 
            UserByIdRepository userByIdRepository,
            UserByLoginRepository userByLoginRepo,
            UserByEmailRepository userByEmailRepo,
            UserByResetKeyRepository userByResetKeyRepo,
            UserByActivationtKeyRepository userByActivationKeyRepo) {
        this.cassandraTemplate        = cassandraTemplate;
        this.userByIdRepo             = userByIdRepository;
        this.userByLoginRepo          = userByLoginRepo;
        this.userByEmailRepo          = userByEmailRepo;
        this.userByResetKeyRepo       = userByResetKeyRepo;
        this.userByActivationKeyRepo  = userByActivationKeyRepo;
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
        Optional<UserByLogin> opt = userByLoginRepo.findById(login);
        return opt.isPresent() ? 
                userByIdRepo.findById(opt.get().getId()) : 
                Optional.empty();
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
        Optional<UserByEmail> opt = userByEmailRepo.findById(email.toLowerCase());
        return opt.isPresent() ? 
                userByIdRepo.findById(opt.get().getId()) : 
                Optional.empty();
    }
    
    /**
     * Create all tables needed
     */
    public void createSchema() {
        userByIdRepo.createTable();
        userByLoginRepo.createTable();
        userByEmailRepo.createTable();
        userByResetKeyRepo.createTable();
        userByActivationKeyRepo.createTable();
    }
    
    /**
     * Delete tables related to security
     */
    public void dropSchema() {
        userByIdRepo.dropTable();
        userByLoginRepo.dropTable();
        userByEmailRepo.dropTable();
        userByResetKeyRepo.dropTable();
        userByActivationKeyRepo.dropTable();
    }
    
    /**
     * Delete all records.
     */
    public void deleteAll() {
        userByIdRepo.truncateTable();
        userByLoginRepo.truncateTable();
        userByEmailRepo.truncateTable();
        userByResetKeyRepo.truncateTable();
        userByActivationKeyRepo.truncateTable();
    }
    
    /**
     * Save a User.
     *
     * @param user
     *      current user
     */
    public void save(User user) {
        
        // CleanUp
        userByIdRepo.findById(user.getId()).ifPresent(u -> {
            CassandraBatchOperations batchClean = cassandraTemplate.batchOps(BatchType.LOGGED);
            if (!StringUtils.hasLength(u.getEmail()) && 
                !u.getEmail().equals(user.getEmail())) {
                batchClean.delete(new UserByEmail(u.getEmail(), u.getId()));
            }
            if (!StringUtils.hasLength(u.getLogin()) && 
                !u.getLogin().equals(user.getLogin())) {
                batchClean.delete(new UserByLogin(u.getLogin(), u.getId()));
            }
            if (!StringUtils.hasLength(u.getResetKey()) && 
                !u.getResetKey().equals(user.getResetKey())) {
                batchClean.delete(new UserByResetKey(u.getResetKey(), u.getId()));
            }
            if (!StringUtils.hasLength(u.getActivationKey()) && 
                !u.getActivationKey().equals(user.getActivationKey())) {
                batchClean.delete(new UserByActivationKey(u.getActivationKey(), u.getId()));
            }
            batchClean.execute();
        });
        
        // Update
        cassandraTemplate.batchOps(BatchType.LOGGED)
                         .insert(user)
                         .insert(new UserByEmail(user.getEmail(), user.getId()))
                         .insert(new UserByLogin(user.getLogin(), user.getId()))
                         .insert(new UserByResetKey(user.getResetKey(), user.getId()))
                         .insert(new UserByActivationKey(user.getActivationKey(), user.getId()))
                         .execute();
    }
    
    public void deletebyId(String user) {
        
    }
    
    public void delete(User user) {
        
        userByIdRepo.findById(user.getId()).ifPresent(u -> {
            CassandraBatchOperations batchDelete = cassandraTemplate.batchOps(BatchType.LOGGED);
            if (!StringUtils.hasLength(u.getEmail())) {
                batchDelete.delete(new UserByEmail(u.getEmail(), u.getId()));
            }
            if (!StringUtils.hasLength(u.getLogin())) {
                batchDelete.delete(new UserByLogin(u.getLogin(), u.getId()));
            }
            if (!StringUtils.hasLength(u.getResetKey())) {
                batchDelete.delete(new UserByResetKey(u.getResetKey(), u.getId()));
            }
            if (!StringUtils.hasLength(u.getActivationKey()) && 
                !u.getActivationKey().equals(user.getActivationKey())) {
                batchDelete.delete(new UserByActivationKey(u.getActivationKey(), u.getId()));
            }
            batchDelete.delete(user);
            batchDelete.execute();
        });
    }

}
