package org.eve.producer.subscriber;

import org.eve.producer.domain.Order;
import org.eve.producer.domain.OrdersMean;
import org.eve.producer.service.KafkaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SendProcessedOrdersToKafkaSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(SendProcessedOrdersToKafkaSubscriber.class);
    @Autowired
    private KafkaService kafkaService;

    public void processOrders(OrdersMean ordersMean){
        kafkaService.sendMessage("ordersMean",  null, ordersMean);
    }
}
