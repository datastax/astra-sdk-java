package com.datastax.astra.spring.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.spring.security.dao.CassandraUserRepository;
import com.datastax.spring.security.dao.UserByActivationtKeyRepository;
import com.datastax.spring.security.dao.UserByEmailRepository;
import com.datastax.spring.security.dao.UserByIdRepository;
import com.datastax.spring.security.dao.UserByLoginRepository;
import com.datastax.spring.security.dao.UserByResetKeyRepository;

@SuppressWarnings("deprecation")
@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes={
        AstraConfiguration.class, 
        UserByIdRepository.class, UserByLoginRepository.class,
        UserByActivationtKeyRepository.class, UserByResetKeyRepository.class,
        UserByEmailRepository.class, CassandraUserRepository.class
})
@TestPropertySource(locations="/application.properties")
public class TestSpring {
    
    @Autowired
    private AstraClient astraClient;
    
    @Autowired
    private CassandraUserRepository cassandraUserRepository;
    
    @Test
    public void test() {
        cassandraUserRepository.createSchema();
        System.out.println("DONE");
    }

}
