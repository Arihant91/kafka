package org.eve.producer.service;

import org.eve.producer.domain.Ids;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrdersService {

    private static final Logger logger = LoggerFactory.getLogger(OrdersService.class);

    private static final Logger fileLogger = LoggerFactory.getLogger("FILE_LOGGER");
    private final EveService eveService;
    private final MessageChannel processOrdersChannel;

    @Autowired
    public OrdersService(EveService eveService, MessageChannel processOrdersChannel) {
        this.eveService = eveService;
        this.processOrdersChannel = processOrdersChannel;
    }

    @Scheduled(cron = "0 33 * * * *")
    public void getOrders() {
        Instant startTime = Instant.now();
        AtomicInteger counter = new AtomicInteger();
        List<Long> regionIds = eveService.getRegionIds();
        int idx = 1;
        regionIds.forEach(regionId ->{
                logger.info("doing {} regionid, idx {}", regionId, idx);
                eveService.getRelevantTypesByRegion(regionId).forEach(typeId -> {
                        sendMessage(regionId, typeId);
                        counter.getAndIncrement();
                        logger.info("counter: " + counter);
                        });
        });

        logger.info("Process took in minutes: {}", Duration.between(Instant.now(), startTime).toMinutes());
    }

    private void sendMessage(Long regionId, Long typeId) {
        processOrdersChannel.send(MessageBuilder.withPayload(new Ids(regionId, typeId)).build());
    }

}
