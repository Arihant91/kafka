package org.eve.producer.service;

import org.eve.producer.domain.Order;
import org.eve.producer.domain.OrdersStatByIdInLocation;
import org.eve.producer.domain.OrdersStatsByIdInRegion;
import org.eve.producer.domain.StructuresByRegion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaService {

    private final KafkaTemplate<String, Order> kafkaTemplateOrder;

    private final KafkaTemplate<String, OrdersStatsByIdInRegion> kafkaTemplateOrdersByRegion;


    private final KafkaTemplate<String, OrdersStatByIdInLocation> kafkaTemplateOrdersByLocation;

    private final KafkaTemplate<String, StructuresByRegion> kafkaTemplateStructureByLocation;


    @Autowired
    public KafkaService(KafkaTemplate<String, Order> kafkaTemplate, KafkaTemplate<String, OrdersStatsByIdInRegion> kafkaTemplateOrdersByRegion,
                        KafkaTemplate<String, OrdersStatByIdInLocation> kafkaTemplateOrdersByLocation, KafkaTemplate<String, StructuresByRegion> kafkaTemplateStructureByLocation) {

        this.kafkaTemplateOrder = kafkaTemplate;
        this.kafkaTemplateOrdersByRegion = kafkaTemplateOrdersByRegion;
        this.kafkaTemplateOrdersByLocation = kafkaTemplateOrdersByLocation;
        this.kafkaTemplateStructureByLocation = kafkaTemplateStructureByLocation;
    }

    public void sendMessage(String topic, String key, StructuresByRegion value) {
        final CompletableFuture<SendResult<String, StructuresByRegion>> future = kafkaTemplateStructureByLocation.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
                //System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.toString());
            }
        });
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
        final CompletableFuture<SendResult<String, OrdersStatsByIdInRegion>> future = kafkaTemplateOrdersByRegion.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
                // System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.  toString());
            }
        });
    }

    public void sendMessage(String topic, String key, OrdersStatByIdInLocation value) {
        final CompletableFuture<SendResult<String, OrdersStatByIdInLocation>> future = kafkaTemplateOrdersByLocation.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
                // System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.  toString());
            }
        });
    }
}
