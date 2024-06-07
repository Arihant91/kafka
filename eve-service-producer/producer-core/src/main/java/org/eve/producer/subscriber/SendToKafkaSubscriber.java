package org.eve.producer.subscriber;

import org.eve.producer.domain.Order;
import org.eve.producer.service.KafkaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SendToKafkaSubscriber {

    @Autowired
    private KafkaService kafkaService;

    public void processOrders(Message<List<Order>> messageOrders){

        messageOrders.getPayload().forEach(order -> {
            System.out.println(String.valueOf(order.hashCode()));
            kafkaService.sendMessage("orders",  String.valueOf(order.hashCode()), order.toString());
        });
    }
}
