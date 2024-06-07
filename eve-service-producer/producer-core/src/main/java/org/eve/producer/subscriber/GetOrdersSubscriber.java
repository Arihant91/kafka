package org.eve.producer.subscriber;

import org.eve.producer.domain.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.messaging.Message;

import java.util.List;

@Component
public class GetOrdersSubscriber {

    @Autowired
    private MessageChannel sendToKafkaChannel;

    public void processOrders(Message<List<Order>> ordersMessage){
        sendMessage(ordersMessage.getPayload());
    }

    private void sendMessage(List<Order> orders){
        sendToKafkaChannel.send(MessageBuilder.withPayload(orders).build());
    }
}
