package org.eve.producer.subscriber;

import org.eve.producer.domain.Ids;
import org.eve.producer.domain.Order;
import org.eve.producer.service.EveService;
import org.eve.producer.service.RateLimiterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class ProcessOrdersSubscriber {

    private static final Logger logger = LoggerFactory.getLogger(ProcessOrdersSubscriber.class);

    @Autowired
    private MessageChannel getOrdersChannel;

    @Autowired
    private EveService eveService;


    public void processOrders(Message<Ids> idsMessage) {
        Ids ids = idsMessage.getPayload();
        sendMessage(eveService.getAllOrdersInRegionByType(ids.regionId(),ids.typeId()));
    }

    private void sendMessage(List<Order> orders) {
        getOrdersChannel.send(MessageBuilder.withPayload(orders).build());
    }

}
