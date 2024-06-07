package org.eve.producer.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(String topic, String key, String value) {
        final CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, value);

        future.whenComplete((metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending record: " + exception.getMessage());
            } else {
                System.out.println("Record sent to partition " + metadata.toString() + " with offset " + metadata.toString());
            }
    });
    }
}
