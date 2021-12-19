package com.datastax.yasa.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;
import org.springframework.data.cassandra.repository.query.CassandraEntityInformation;
import org.springframework.data.cassandra.repository.support.CassandraRepositoryFactory;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.yasa.user.User;

@Configuration
@EnableCassandraRepositories(basePackages = {"com.datastax.yasa.user"})
public class SpringDataConfiguration {
    
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
    
}
