package org.eve.producer.service;

import org.eve.producer.domain.Ids;
import org.eve.producer.domain.Order;
import org.eve.producer.subscriber.GetOrdersSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);
    private Integer counter = 0;
    private final EveService eveService;
    private final MessageChannel processOrdersChannel;
    @Autowired
    public OrdersService(EveService eveService, MessageChannel processOrdersChannel) {
        this.eveService = eveService;
        this.processOrdersChannel = processOrdersChannel;
    }

    @Scheduled(cron = "* 05 * * * *")
    public void getOrders() {
        List<Long> regionIds = eveService.getRegionIds();
        List<Long> typeIds = eveService.getTypeIds();
        regionIds.forEach(regionId ->
                typeIds.forEach(typeId -> {
                    counter++;
                    logger.info("counter: " + counter);
                    sendMessage(regionId, typeId);
                        }
                )
        );

    }

    private void sendMessage(Long regionId, Long typeId) {
        processOrdersChannel.send(MessageBuilder.withPayload(new Ids(regionId, typeId)).build());
    }

}
