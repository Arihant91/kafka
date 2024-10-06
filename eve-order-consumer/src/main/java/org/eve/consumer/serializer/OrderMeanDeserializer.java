package org.eve.consumer.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.eve.consumer.domain.OrdersStatsByIdInRegion;

import java.util.Map;

public class OrderMeanDeserializer implements Deserializer<OrdersStatsByIdInRegion> {

    private final ObjectMapper objectMapper;

    public OrderMeanDeserializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public OrdersStatsByIdInRegion deserialize(String topic, byte[] data) {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            return objectMapper.readValue(data, OrdersStatsByIdInRegion.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing OrdersMean", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
