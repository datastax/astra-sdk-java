package com.datastax.yasa.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.datastax.yasa.security.exception.UserNotActivatedException;
import com.datastax.yasa.user.UserRepository;

/**
 * Create a user management system on Cassandra.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
@Service
public class SecurityUserDetails implements UserDetailsService {
    
    /** Cassandra Connection. */
    private final UserRepository userRepository;
    
    /**
     * Constructor with params. 
     * 
     * @param userRepository
     *      user repo
     */
    public SecurityUserDetails(UserRepository userRepository) {
        this.userRepository = userRepository;
    }    

    /** {@inheritDoc} */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findById(email)
                             .map(user -> createSpringSecurityUser(email, user))
                             .orElseThrow(() -> new UsernameNotFoundException("User " + email + " was not found in the database"));
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
    private User createSpringSecurityUser(String email, com.datastax.yasa.user.User user) {
        if (!user.isActivated()) {
            throw new UserNotActivatedException("User " + email + " was not activated");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), 
                user.getPassword(), 
                user.getAuthorities().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
    }
    
}
