package org.eve.consumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;

@Configuration
public class CassandraConfig {

    @Value("${spring.data.cassandra.contact-points}")
    private String contactPoints;
    @Value("${spring.data.cassandra.keyspace-name}")
    private String keySpace;
    @Value("${spring.data.cassandra.username}")
    private String userName;
    @Value("${spring.data.cassandra.password}")
    private String password;

    @Value("${spring.data.cassandra.datacenter}")
    private String datacenter;
    @Bean
    public CqlSessionFactoryBean cqlSessionFactoryBean() {
        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints(contactPoints);
        session.setKeyspaceName(keySpace);
        session.setUsername(userName);
        session.setPassword(password);
        session.setLocalDatacenter(datacenter);
        return session;
    }
}
