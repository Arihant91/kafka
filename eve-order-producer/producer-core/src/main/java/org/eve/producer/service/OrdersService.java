package org.eve.producer.service;

import org.eve.producer.domain.RegionIdWithOrders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReentrantLock;

@Service
public class OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);
    private final EveService eveService;
    private final MessageChannel processOrdersChannel;

    @Autowired
    public OrdersService(EveService eveService, MessageChannel processOrdersChannel) {
        this.eveService = eveService;
        this.processOrdersChannel = processOrdersChannel;
    }

    //@Scheduled(cron = "0 0 0-12,14-23 * * *")
    @Scheduled(cron = "0 * 0-12,14-23 * * *")
    public void getOrdersByRegion() {
            eveService.getRegionIds().forEach(regionId ->
                    sendMessage(new RegionIdWithOrders(regionId, eveService.getOrdersByRegion(regionId))));
            logger.info("run finished");
    }

    private void sendMessage(RegionIdWithOrders regionIdWithOrders) {
        processOrdersChannel.send(MessageBuilder.withPayload(regionIdWithOrders).build());
    }

}
