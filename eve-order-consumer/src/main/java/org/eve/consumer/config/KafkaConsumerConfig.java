package org.eve.consumer.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eve.consumer.domain.OrdersStatsByIdInLocation;
import org.eve.consumer.domain.OrdersStatsByIdInRegion;
import org.eve.consumer.domain.StructuresByRegion;
import org.eve.consumer.serializer.OrderStatsByIdInLocationDeserializer;
import org.eve.consumer.serializer.OrderStatsByIdInRegionDeserializer;
import org.eve.consumer.serializer.StructureByRegionDeserializer;
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
    public Map<String, Object> consumerConfigsOrderStatsByIdInLocation() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderStatsByIdInLocationDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "5000");
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "1048586");
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "1024000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
        return props;
    }

    @Bean
    public Map<String, Object> consumerConfigsStructuresByRegion() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StructureByRegionDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.FETCH_MIN_BYTES_CONFIG, "5000");
        props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG, "1048586");
        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, "1024000");
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "500");
        return props;
    }

    @Bean
    public ConsumerFactory<String, OrdersStatsByIdInLocation> consumerFactoryOrder() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsOrderStatsByIdInLocation());
    }

    @Bean
    public ConsumerFactory<String, StructuresByRegion> consumerFactoryStructuresByRegion() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsStructuresByRegion());
    }

    @Bean
    public Map<String, Object> consumerConfigsOrderStatsByIdInRegion() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderStatsByIdInRegionDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "myGroupId");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return props;
    }

    @Bean
    public ConsumerFactory<String, OrdersStatsByIdInRegion> consumerFactoryOrderMean() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigsOrderStatsByIdInRegion());
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrdersStatsByIdInRegion> OrdersStatsByIdInRegion() {
        ConcurrentKafkaListenerContainerFactory<String, OrdersStatsByIdInRegion> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryOrderMean());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrdersStatsByIdInLocation> OrdersStatsByIdInLocation() {
        ConcurrentKafkaListenerContainerFactory<String, OrdersStatsByIdInLocation> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryOrder());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StructuresByRegion> StructuresByRegion() {
        ConcurrentKafkaListenerContainerFactory<String, StructuresByRegion> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactoryStructuresByRegion());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }
}