package org.eve.consumer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;

@Configuration
public class CassandraConfig {
    @Bean
    public CqlSessionFactoryBean cqlSessionFactoryBean() {

        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints("192.168.68.53");
        session.setKeyspaceName("orders");
        session.setUsername("cassandra");
        session.setPassword("cassandra");
        session.setLocalDatacenter("datacenter1");
        return session;
    }
}
