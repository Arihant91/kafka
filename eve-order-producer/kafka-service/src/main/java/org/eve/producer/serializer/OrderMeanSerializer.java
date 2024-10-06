package org.eve.producer.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.eve.producer.domain.OrdersStatsByIdInRegion;

import java.util.Map;

public class OrderMeanSerializer implements Serializer<OrdersStatsByIdInRegion> {

    private final ObjectMapper objectMapper;

    public OrderMeanSerializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public byte[] serialize(String topic, OrdersStatsByIdInRegion data) {
        try {
            if (data == null) {
                return null;
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error serializing OrdersMean", e);
        }
    }

    @Override
    public void close() {
    }

}
