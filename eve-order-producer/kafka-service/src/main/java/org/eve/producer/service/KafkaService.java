package org.eve.producer.service;

import org.eve.producer.domain.Order;
import org.eve.producer.domain.OrdersStatsByIdInRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaService {

    private final KafkaTemplate<String, Order> kafkaTemplateOrder;

    private final KafkaTemplate<String, OrdersStatsByIdInRegion> kafkaTemplateOrdersMean;

    @Autowired
    public KafkaService(KafkaTemplate<String, Order> kafkaTemplate, KafkaTemplate<String, OrdersStatsByIdInRegion> kafkaTemplateOrdersMean) {

        this.kafkaTemplateOrder = kafkaTemplate;
        this.kafkaTemplateOrdersMean = kafkaTemplateOrdersMean;
    }

    public void sendMessage(String topic, String key, Order value) {
        final CompletableFuture<SendResult<String, Order>> future = kafkaTemplateOrder.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
                //System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.toString());
            }
    });
    }

    public void sendMessage(String topic, String key, OrdersStatsByIdInRegion value) {
        final CompletableFuture<SendResult<String, OrdersStatsByIdInRegion>> future = kafkaTemplateOrdersMean.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
               // System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.  toString());
            }
        });
    }
}
