package com.datastax.spring.security;

import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.datastax.spring.security.dao.CassandraUserRepository;
import com.datastax.spring.security.exception.UserNotActivatedException;

/**
 * Create a user management system on Cassandra.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Service
public class CassandraUserDetails implements UserDetailsService {
    
    /** Cassandra Connection. */
    private final CassandraUserRepository userRepository;
    
    /**
     * Constructor with params. 
     * 
     * @param userRepository
     *      user repo
     */
    public CassandraUserDetails(CassandraUserRepository userRepository) {
        this.userRepository = userRepository;
    }    

    /** {@inheritDoc} */
    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository.findOneByLogin(lowercaseLogin)
                             .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                             .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }
    
    /**
     * Map as a Spring Security user.
     *
     * @param lowercaseLogin
     *      login
     * @param user
     *      dao user
     * @return
     *      Spring seucrity user
     */
    private User createSpringSecurityUser(String lowercaseLogin, com.datastax.spring.security.dao.User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getLogin(), 
                user.getPassword(), 
                user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
    
}
