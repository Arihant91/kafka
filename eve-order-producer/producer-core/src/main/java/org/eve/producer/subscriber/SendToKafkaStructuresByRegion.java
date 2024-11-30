package org.eve.producer.subscriber;

import org.eve.producer.domain.StructuresByRegion;
import org.eve.producer.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendToKafkaStructuresByRegion {

    private static final Logger logger = LoggerFactory.getLogger(SendToKafkaOrdersStatsByLocation.class);

    @Autowired
    private KafkaService kafkaService;

    public void sendToKafkaStructuresByRegion(StructuresByRegion structuresByRegion) {
        kafkaService.sendMessage("StructuresByRegion", null, structuresByRegion);
    }
}
