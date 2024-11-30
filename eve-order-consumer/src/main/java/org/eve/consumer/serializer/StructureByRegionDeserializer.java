package org.eve.consumer.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.eve.consumer.domain.StructuresByRegion;

import java.util.Map;

public class StructureByRegionDeserializer implements Deserializer<StructuresByRegion> {

    private final ObjectMapper objectMapper;

    public StructureByRegionDeserializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public StructuresByRegion deserialize(String topic, byte[] data) {
        try {
            if (data == null || data.length == 0) {
                return null;
            }
            return objectMapper.readValue(data, StructuresByRegion.class);
        } catch (Exception e) {
            throw new SerializationException("Error deserializing StructuresByRegionEntity", e);
        }
    }

    @Override
    public void close() {
        // No resources to close
    }
}
