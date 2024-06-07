package org.eve.producer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eve.producer.domain.Order;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private final ObjectMapper objectMapper;

    public KafkaConsumerService(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "orders", groupId = "myGroupId")
    public void listen(String message, Acknowledgment ack) {
        try{
            Order order = objectMapper.readValue(message, Order.class);
            ack.acknowledge();
        } catch (Exception e) {
            System.err.println("Failed to deserialize message: " + e.getMessage());
        }

    }
}