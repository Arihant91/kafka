package org.eve.producer.service;

import org.eve.producer.domain.Order;
import org.eve.producer.domain.OrdersMean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaService {

    private final KafkaTemplate<String, Order> kafkaTemplateOrder;

    private final KafkaTemplate<String, OrdersMean> kafkaTemplateOrdersMean;

    @Autowired
    public KafkaService(KafkaTemplate<String, Order> kafkaTemplate, KafkaTemplate<String, OrdersMean> kafkaTemplateOrdersMean) {

        this.kafkaTemplateOrder = kafkaTemplate;
        this.kafkaTemplateOrdersMean = kafkaTemplateOrdersMean;
    }

    public void sendMessage(String topic, String key, Order value) {
        final CompletableFuture<SendResult<String, Order>> future = kafkaTemplateOrder.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
                System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.toString());
            }
    });
    }

    public void sendMessage(String topic, String key, OrdersMean value) {
        final CompletableFuture<SendResult<String, OrdersMean>> future = kafkaTemplateOrdersMean.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
                System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.  toString());
            }
        });
    }
}
