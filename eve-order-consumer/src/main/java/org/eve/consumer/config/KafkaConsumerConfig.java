package org.eve.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eve.consumer.domain.Order;
import org.eve.consumer.domain.OrdersStatsByIdInRegion;
import org.eve.consumer.serializer.OrderDeserializer;
import org.eve.consumer.serializer.OrderMeanDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public Map<String, Object> consumerConfigsOrder() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "5000");
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "1048586");
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "1024000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
        return props;
    }

    @Bean
    public ConsumerFactory<String, Order> consumerFactoryOrder() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsOrder());
    }

    @Bean
    public Map<String, Object> consumerConfigsOrderMean() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderMeanDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroupId");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, OrdersStatsByIdInRegion> consumerFactoryOrderMean() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsOrderMean());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrdersStatsByIdInRegion> kafkaListenerContainerFactoryOrderMean() {
        ConcurrentKafkaListenerContainerFactory<String, OrdersStatsByIdInRegion> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryOrderMean());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Order> kafkaListenerContainerFactoryOrder() {
        ConcurrentKafkaListenerContainerFactory<String, Order> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryOrder());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}