package org.eve.consumer.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.eve.consumer.domain.OrdersStatsByIdInLocation;

import java.util.Map;

public class OrderStatsByIdInLocationDeserializer implements Deserializer<OrdersStatsByIdInLocation> {

    private final ObjectMapper objectMapper;

    public OrderStatsByIdInLocationDeserializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public OrdersStatsByIdInLocation deserialize(String topic, byte[] data) {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            return objectMapper.readValue(data, OrdersStatsByIdInLocation.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing OrdersStatsByIdInLocation", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
