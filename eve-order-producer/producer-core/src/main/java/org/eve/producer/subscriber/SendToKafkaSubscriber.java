package org.eve.producer.subscriber;

import org.eve.producer.domain.Order;
import org.eve.producer.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendToKafkaSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(SendToKafkaSubscriber.class);

    @Autowired
    private KafkaService kafkaService;

    public void processOrders(Order order){
            kafkaService.sendMessage("orders",  null, order);
            logger.info("sent from SendToKafkaSubscriber");
    }
}
