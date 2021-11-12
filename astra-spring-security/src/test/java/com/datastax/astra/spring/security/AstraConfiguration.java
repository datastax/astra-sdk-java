package com.datastax.astra.spring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.spring.security.dao.User;
import com.datastax.spring.security.dao.UserByActivationKey;
import com.datastax.spring.security.dao.UserByEmail;
import com.datastax.spring.security.dao.UserByLogin;
import com.datastax.spring.security.dao.UserByResetKey;

@Configuration
public class AstraConfiguration {

    @Value("${astra.applicationToken}")
    private String appToken;
    
    @Value("${astra.databaseId}")
    private String dbId;
    
    @Value("${astra.cloudRegion}")
    private String dbRegion;
    
    @Value("${astra.keyspace}")
    private String keyspace;

    @Bean
    public AstraClient astraClient() {
        return AstraClient.builder()
                .withToken(appToken)
                .withDatabaseId(dbId).withDatabaseRegion(dbRegion)
                .withCqlKeyspace(keyspace)
                .build();
    }
    
    @Bean
    public CqlSession cqlSession(AstraClient cli) {
        return cli.cqlSession();
    }
    
    @Bean
    public CassandraTemplate cassandraOperations(CqlSession cqlSession) {
        CassandraTemplate template= new CassandraTemplate(cqlSession);
        template.setUsePreparedStatements(true);
        return template;
    }
    
    @Bean
    public CassandraRepositoryFactory cassandraRepositoryFactory(CassandraTemplate template) {
        return new CassandraRepositoryFactory(template);
    }
    
    @Bean
    public  CassandraEntityInformation<User, String>  userEntityInformation(CassandraRepositoryFactory repository) {
        return repository.getEntityInformation(User.class);
    }
    
    @Bean
    public  CassandraEntityInformation<UserByLogin, String>  userByLoginEntityInformation(CassandraRepositoryFactory repository) {
        return repository.getEntityInformation(UserByLogin.class);
    }
    
    @Bean
    public  CassandraEntityInformation<UserByEmail, String>  userByEmailEntityInformation(CassandraRepositoryFactory repository) {
        return repository.getEntityInformation(UserByEmail.class);
    }
    
    @Bean
    public  CassandraEntityInformation<UserByActivationKey, String>  userByActivationKeyEntityInformation(CassandraRepositoryFactory repository) {
        return repository.getEntityInformation(UserByActivationKey.class);
    }
    
    @Bean
    public  CassandraEntityInformation<UserByResetKey, String>  userByResetKeyEntityInformation(CassandraRepositoryFactory repository) {
        return repository.getEntityInformation(UserByResetKey.class);
    }
  
}
